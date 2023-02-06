package com.example.lab;

import static com.example.lab.Input.*;

import java.util.ArrayList;
import java.util.List;


public class Main {
	
	public static void main(String[] args) throws Exception {
		System.out.println("1) 1 1 2 3 5 8 13 21 34 55");
		System.out.println("2) 1 -1 1 -1 1 -1 1 -1 1 -1");
		System.out.println("3) 1 0 -1 0 1 0 -1 0 1 0 -1");
		System.out.println("4) other");
		final var sequence_type = inputInt("sequence_type", 1);
		final List<Float> sequence = switch(sequence_type) {
			case 1 -> list(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89);
			case 2 -> list(1, -1, 1, -1, 1, -1, 1, -1, 1, -1);
			case 3 -> list(1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1);
			default -> inputFloatList("sequence");
		};
		final var size = inputInt("size", 2);
		try(final var model = new NeuralNetworkModel(sequence, size)) {
			model.run();
		}
	}
	
	private static List<Float> list(Number... numbers) {
		final var result = new ArrayList<Float>();
		for(var n : numbers)
			result.add(n.floatValue());
		return result;
	}
	
}
