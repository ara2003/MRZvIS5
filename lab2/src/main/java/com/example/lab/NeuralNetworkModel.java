package com.example.lab;

import static com.example.lab.Input.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class NeuralNetworkModel implements AutoCloseable {
	
	private final NeuralNetwork nn;
	
	private final int size;
	
	public NeuralNetworkModel(int size) {
		this.size = size;
		final var file = new File(getFileName());
		
		if(file.exists())
			nn = deSerializetionOrDefault(file, size);
		else
			nn = new NeuralNetwork(size);
	}
	
	public static Image getImageFromFile(String file) throws FileNotFoundException, IOException {
		String str;
		try(final var in = new FileInputStream(file)) {
			str = new String(in.readAllBytes());
		}
		str = str.replace("\t", "");
		str = str.replace("\r", "");
		str = str.replace(" ", "");
		final var size = str.indexOf("\n");
		str = str.replace("\n", "");
		final var fs = new float[str.length()];
		for(var i = 0; i < fs.length; i++)
			fs[i] = str.charAt(i) == '1' ? 1 : -1;
		return new Image(new NNVector(fs), size);
	}
	
	private static NeuralNetwork deSerializetion(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		try(final var fin = new FileInputStream(file)) {
			try(final var oin = new ObjectInputStream(fin)) {
				return (NeuralNetwork) oin.readObject();
			}
		}
	}
	
	private static NeuralNetwork deSerializetionOrDefault(File file, int size) {
		try {
			return deSerializetion(file);
		}catch(Exception e) {
			e.printStackTrace();
			return new NeuralNetwork(size);
		}
	}
	
	private static void serializetion(File file, NeuralNetwork nn)
			throws FileNotFoundException, IOException {
		try(final var fout = new FileOutputStream(file)) {
			try(final var oout = new ObjectOutputStream(fout)) {
				oout.writeObject(nn);
			}
		}
	}
	
	@Override
	public void close() throws FileNotFoundException, IOException {
		final var file = new File(getFileName());
		synchronized(nn) {
			serializetion(file, nn);
		}
	}
	
	
	public void run() throws FileNotFoundException, IOException {
		loop :
		while(true) {
			final var c = input("task(LEARN, TEST, EXIT)", "L").substring(0, 1).toUpperCase();
			switch(c) {
				case "L" -> run_learn();
				case "T" -> run_test();
				case "E" -> {
					break loop;
				}
			}
		}
	}
	
	private String getFileName() {
		return "res/nn/" + size;
	}
	
	private Image inputImage() throws FileNotFoundException, IOException {
		final var file = input("image file");
		return getImageFromFile("res/img/" + file + ".txt");
	}
	
	private void printImage(NNVector res, int lineSize) {
		for(var i = 0; i < res.size(); i++) {
			System.out.print(res.get(i) >= 0 ? "1" : "0");
			if(i % lineSize == lineSize - 1)
				System.out.println();
		}
	}
	
	private void run_learn() throws FileNotFoundException, IOException {
		final var count = inputInt("images count", 2);
		final var images = new ArrayList<NNVector>();
		final var sizes = new ArrayList<Integer>();
		for(var i = 0; i < count; i++) {
			final var img = inputImage();
			images.add(img.vector);
			sizes.add(img.lineSize);
		}
		nn.learn(images);
		for(int i = 0; i < sizes.size(); i++) {
			final var res = nn.test(images.get(i));
			printImage(res, sizes.get(i));
		}
	}
	
	private void run_test() throws FileNotFoundException, IOException {
		final var img = inputImage();
		final var res = nn.test(img.vector);
		printImage(res, img.lineSize);
	}
	
	public record Image(NNVector vector, int lineSize) {
	}
	
}
