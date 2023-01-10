package com.example.lab;

import static com.example.lab.Input.input;
import static com.example.lab.Input.inputFloat;
import static com.example.lab.Input.inputInt;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	private static final int BLOCK_WEIGHT_DEFAULT = 8;
	private static final int BLOCK_HEIGHT_DEFAULT = 8;
	private static final int COMPRESS_SIZE_DEFAULT = 36;
	private static final String IMAGE_DEFAULT = "a";
	private static final String OUT_IMAGE_DEFAULT = "result";
	private static final float ERROR_DEFAULT = 0.01f;

	private static final int WIDTH = 400;
	private static final int HEIGHT = WIDTH;
	public static Image getImageFromFile(String file) throws FileNotFoundException, IOException {
		return ImageIO.read(new File(file));
	}

	public static void main(String[] args) throws Exception {
		final var m = input("L - learn\nC - compress\nD - decompress\nmain type", "F");
		final var t = MainType.get(m);
		t.inputMain();
	}

	private enum MainType {

		LEARN() {

			@Override
			void inputMain() throws FileNotFoundException, IOException {
				final var imageFile = input("image", IMAGE_DEFAULT);

				final var r = inputInt("block weight", BLOCK_WEIGHT_DEFAULT);
				final var m = inputInt("block height", BLOCK_HEIGHT_DEFAULT);
				final var p = inputInt("compress size", COMPRESS_SIZE_DEFAULT);
				final var e = inputFloat("error limit", ERROR_DEFAULT);

				final var img = getImageFromFile("res/img/" + imageFile + ".bmp");
				try(final var nn = new NeuralNetworkModel(r, m, p);) {
					nn.learn(img, e);
					
					final var d = new NeuralNetworkDraw(nn, WIDTH, HEIGHT);
					d.startRenderingForvard(img);
				}
			}
		},
		FORWARD() {

			@Override
			void inputMain() throws FileNotFoundException, IOException {
				final var imageFile = input("image", IMAGE_DEFAULT);

				final var r = inputInt("block weight", BLOCK_WEIGHT_DEFAULT);
				final var m = inputInt("block height", BLOCK_HEIGHT_DEFAULT);
				final var p = inputInt("compress size", COMPRESS_SIZE_DEFAULT);

				final var img = getImageFromFile("res/img/" + imageFile + ".bmp");
				try(final var nn = new NeuralNetworkModel(r, m, p);) {
					final var d = new NeuralNetworkDraw(nn, WIDTH, HEIGHT);
					d.startRenderingForvard(img);
				}
			}
		},
		COMPRESS() {

			@Override
			void inputMain() throws FileNotFoundException, IOException {
				final var inFile = input("in image", IMAGE_DEFAULT);
				final var outFile = input("out image", OUT_IMAGE_DEFAULT);

				final var r = inputInt("block weight", BLOCK_WEIGHT_DEFAULT);
				final var m = inputInt("block height", BLOCK_HEIGHT_DEFAULT);
				final var p = inputInt("compress size", COMPRESS_SIZE_DEFAULT);
				
				final var img = getImageFromFile("res/img/" + inFile + ".bmp");
				try(final var nn = new NeuralNetworkModel(r, m, p);) {
					{
						final var result = nn.compress(img);
						try(final var res = new FileOutputStream("res/img/"+ outFile +".bmp")) {
							ImageIO.write((RenderedImage) result, "bmp", res);
						}
					}

					final var d = new NeuralNetworkDraw(nn, WIDTH, HEIGHT);
					d.startRenderingCompress(img);
				}
			}
			
		},
		DECOMPRESS() {

			@Override
			void inputMain() throws FileNotFoundException, IOException {
				final var imageFile = input("image", OUT_IMAGE_DEFAULT);

				final var r = inputInt("block weight", BLOCK_WEIGHT_DEFAULT);
				final var m = inputInt("block height", BLOCK_HEIGHT_DEFAULT);
				final var p = inputInt("compress size", COMPRESS_SIZE_DEFAULT);

				final var img = getImageFromFile("res/img/"+ imageFile +".bmp");
				try(final var nn = new NeuralNetworkModel(r, m, p);) {
					final var d = new NeuralNetworkDraw(nn, WIDTH, HEIGHT);
					d.startRenderingDeCompress(img);
				}
			}


		},
		;

		static MainType get(String m) {
			m = m.toUpperCase();
			switch(m) {
				case "C" -> {return COMPRESS;}
				case "D" -> {return DECOMPRESS;}
				case "F" -> {return FORWARD;}
				case "L" -> {return LEARN;}
			}
			return valueOf(m);
		}

		abstract void inputMain() throws FileNotFoundException, IOException;

	}

}
