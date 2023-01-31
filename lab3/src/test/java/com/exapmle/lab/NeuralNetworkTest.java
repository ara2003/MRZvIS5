package com.exapmle.lab;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.lab.NNVector;
import com.example.lab.NeuralNetwork;
import com.example.lab.Pair;

public class NeuralNetworkTest {
	
	@Test
	void learnTest() {
		final var nn = new NeuralNetwork(2, 1, 1);
		
		final var tests = List.of(new Pair<>(new NNVector(1, 1), new NNVector(2f)),
				new Pair<>(new NNVector(1, 2), new NNVector(3f)),
				new Pair<>(new NNVector(2, 3), new NNVector(5f)),
				new Pair<>(new NNVector(3, 5), new NNVector(8f)));
		
		float errorLimit = 0.0000001f;
		float e;
		do {
			e = 0;
			for(var test : tests)
				e += nn.backpropagation(0.001f, test.first(), test.second());
		}while(e > errorLimit);
		
		for(var test : tests) {
			final var result = nn.feedForward(test.first());
			result.sub(test.second());
			assertTrue(result.sqrLength() < errorLimit);
		}
		
	}
	
}
