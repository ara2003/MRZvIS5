package com.exapmle.lab;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.example.lab.JordanNeuralNetwork;
import com.example.lab.NNSequence;
import com.example.lab.NNVector;

public class JordanNeuralNetworkTest {
	
	private static final int INPUT = 2;
	private static final int OUTPUT = 1;
	
	@Test
	void learnTest() {
		//		final var list = sequence(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
		final var list = sequence(1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1);
		//				final var list = sequence(1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0);
		final var nn = new JordanNeuralNetwork(INPUT, OUTPUT, OUTPUT);
		final var seq = new NNSequence(nn, list);
		seq.learn(.001f, .01f);
		final var result1 = nn.context().test(new NNVector(1, -1));
		System.out.println(result1);
		final var result2 = nn.context().test(new NNVector(-1, 1));
		System.out.println(result2);
	}
	
	private Collection<Float> sequence(Number... numbers) {
		final var result = new ArrayList<Float>();
		for(var v : numbers)
			result.add(v.floatValue());
		return result;
	}
	
}
