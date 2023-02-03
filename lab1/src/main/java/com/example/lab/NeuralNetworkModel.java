package com.example.lab;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class NeuralNetworkModel implements AutoCloseable {
	
	private static final int RGB_COMPONENTS = 3;
	
	public final int blockWidth, blockHeight, hiddenBlockWidth, hiddenBlockHeight, hiddenBlockSize;
	private final NeuralNetwork nn;
	
	public NeuralNetworkModel(int blockWidth, int blockHeight, int hiddenBlockSize) {
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.hiddenBlockSize = hiddenBlockSize;
		
		hiddenBlockWidth = minDivider(hiddenBlockSize);
		hiddenBlockHeight = hiddenBlockSize / hiddenBlockWidth;
		
		final var file = new File(getFileName());
		
		if(file.exists())
			nn = deSerializetionOrDefault(file, blockWidth, blockHeight, hiddenBlockSize);
		else
			nn = new NeuralNetwork(blockWidth * blockHeight * RGB_COMPONENTS,
					hiddenBlockSize * RGB_COMPONENTS);
	}
	
	public static byte[] bytes(NNVector vector) {
		final var arr = new float[vector.size()];
		for(int i = 0; i < arr.length; i++)
			arr[i] = vector.get(i);
		unnormalize(arr);
		return ImageUtil.to_bytes(arr);
	}
	
	public static NNVector floats(byte[] values) {
		final var float_values = ImageUtil.to_floats(values);
		normalize(float_values);
		return new NNVector(float_values);
	}
	
	public static Image reduce(Iterable<Image> blocks, int resultWidth, int resultHeight) {
		final int blockWidth, blockHeight;
		{
			final var firstBlock = blocks.iterator().next();
			blockWidth = firstBlock.getWidth(null);
			blockHeight = firstBlock.getHeight(null);
		}
		final var resultBytes = new byte[resultWidth * resultHeight * RGB_COMPONENTS];
		final var iter = blocks.iterator();
		for(int resultX = 0; resultX < resultWidth; resultX += blockWidth)
			for(int resultY = 0; resultY < resultHeight; resultY += blockHeight) {
				final var blockBytes = getData(iter.next());
				for(int inputX = 0; inputX < blockWidth; inputX++)
					for(int inputY = 0; inputY < blockHeight; inputY++)
						for(int c = 0; c < RGB_COMPONENTS; c++) {
							final var out = (inputX + inputY * blockWidth) * RGB_COMPONENTS + c;
							final var in = (inputX + resultX + (inputY + resultY) * resultWidth)
									* RGB_COMPONENTS + c;
							resultBytes[in] = blockBytes[out];
						}
			}
		return image(resultBytes, resultWidth, resultHeight);
	}
	
	public static Iterable<Image> split(Image image, int blockWidth, int blockHeight) {
		final var result = new ArrayList<Image>();
		for(int x = 0; x < image.getWidth(null) / blockWidth; x++)
			for(int y = 0; y < image.getHeight(null) / blockHeight; y++) {
				final var i0 = subImage(image, x * blockWidth, y * blockHeight, blockWidth,
						blockHeight);
				result.add(i0);
			}
		return result;
	}
	
	private static int minDivider(int n) {
		for(int i = (int) Math.sqrt(n); i >= 2; i--)
			if(n % i == 0)
				return i;
		return 1;
	}
	
	private static byte[] getData(Image image) {
		final var bImage = (BufferedImage) image;
		return ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
	}
	
	private static BufferedImage image(byte[] imageBytes, int width, int height) {
		final var result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		byte[] imageArray = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < imageBytes.length; i++)
			imageArray[i] = imageBytes[i];
		
		return result;
	}
	
	private static float normalize(float x) {
		return 2 * x - 1;
	}
	
	private static void normalize(float[] values) {
		for(int i = 0; i < values.length; i++)
			values[i] = normalize(values[i]);
	}
	
	private static Image subImage(Image img, int x, int y, int w, int h) {
		final var data = getData(img);
		final var bytes = new byte[w * h * RGB_COMPONENTS];
		for(int j = 0; j < h; j++)
			for(int i = 0; i < w; i++)
				for(int c = 0; c < RGB_COMPONENTS; c++) {
					final var out = (i + j * w) * RGB_COMPONENTS + c;
					final var in = (i + x + (j + y) * img.getWidth(null)) * RGB_COMPONENTS + c;
					bytes[out] = data[in];
				}
		return image(bytes, w, h);
	}
	
	private static float unnormalize(float x) {
		if(x < -1)
			x = -1;
		if(x > 1)
			x = 1;
		x = (x + 1) / 2;
		return x;
	}
	
	private static void unnormalize(float[] values) {
		for(int i = 0; i < values.length; i++)
			values[i] = unnormalize(values[i]);
	}
	
	@Override
	public void close() throws FileNotFoundException, IOException {
		final var file = new File(getFileName());
		synchronized(nn) {
			serializetion(file, nn);
		}
	}
	
	public void learn(Image img, float e_limit) {
		final var input_vectors = new ArrayList<NNVector>();
		for(var block : split(img, blockWidth, blockHeight))
			input_vectors.add(floats(getData(block)));
		e_limit *= .5f / (Byte.MAX_VALUE - Byte.MIN_VALUE) * getData(img).length;
		float error = getError(input_vectors);
		int iteration = 0;
		while(error > e_limit) {
			System.out.println(
					"iteration: " + iteration++ + " error/error_limit: " + error / e_limit);
			learnOnIterable(input_vectors);
			error = getError(input_vectors);
		}
	}
	
	public Image run(Image img) {
		final var s = new ArrayList<Image>();
		for(var i : split(img, blockWidth, blockHeight))
			s.add(runBlock(i));
		return reduce(s, img.getWidth(null), img.getHeight(null));
		//		return w2(w1(img));
	}
	
	public Image compress(Image image) {
		final var s = new ArrayList<Image>();
		for(var i : split(image, blockWidth, blockHeight))
			s.add(compressBlock(i));
		return reduce(s, image.getWidth(null) * hiddenBlockWidth / blockWidth,
				image.getHeight(null) * hiddenBlockHeight / blockHeight);
	}
	
	public Image deCompress(Image image) {
		final var s = new ArrayList<Image>();
		for(var i : split(image, hiddenBlockWidth, hiddenBlockHeight))
			s.add(deCompressBlock(i));
		return reduce(s, image.getWidth(null) * blockWidth / hiddenBlockWidth,
				image.getHeight(null) * blockHeight / hiddenBlockHeight);
	}
	
	private Image runBlock(Image img) {
		final var X = floats(getData(img));
		final var Y = nn.run(X);
		return image(bytes(Y), blockWidth, blockHeight);
	}
	
	private Image compressBlock(Image img) {
		final var X = floats(getData(img));
		final var Y = nn.compress(X);
		return image(bytes(Y), hiddenBlockWidth, hiddenBlockHeight);
	}
	
	private Image deCompressBlock(Image img) {
		final var X = floats(getData(img));
		final var Y = nn.decompress(X);
		return image(bytes(Y), blockWidth, blockHeight);
	}
	
	private static NeuralNetwork deSerializetion(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		try(final var fin = new FileInputStream(file)) {
			try(final var oin = new ObjectInputStream(fin)) {
				return (NeuralNetwork) oin.readObject();
			}
		}
	}
	
	private static NeuralNetwork deSerializetionOrDefault(File file, int inputBlockWidth,
			int inputBlockHeight, int hiddenBlockSize) {
		try {
			return deSerializetion(file);
		}catch(Exception e) {
			e.printStackTrace();
			return new NeuralNetwork(inputBlockWidth * inputBlockHeight * RGB_COMPONENTS,
					hiddenBlockSize * RGB_COMPONENTS);
		}
	}
	
	private float getError(Iterable<? extends NNVector> inputVectors) {
		float E = 0;
		for(var vec : inputVectors)
			E = Math.max(E, nn.getError(vec));
		return E;
	}
	
	private String getFileName() {
		return "res/nn/" + blockWidth + " " + blockHeight + " " + hiddenBlockSize;
	}
	
	private void learnOnIterable(Iterable<? extends NNVector> inputVectors) {
		for(var X : inputVectors)
			nn.learn(X);
	}
	
	private static void serializetion(File file, NeuralNetwork nn)
			throws FileNotFoundException, IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
		try(final var fout = new FileOutputStream(file)) {
			try(final var oout = new ObjectOutputStream(fout)) {
				oout.writeObject(nn);
			}
		}
	}
	
}
