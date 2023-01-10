package com.example.lab;

import static com.example.lab.Input.*;


public class Main {
	
	private static final int IMAGE_SIZE_DEFAULT = 30;
	
	public static void main(String[] args) throws Exception {
		final var inputSize = inputInt("image size", IMAGE_SIZE_DEFAULT);
		try(final var nn = new NeuralNetworkModel(inputSize);) {
			nn.run();
		}
	}
	
}
