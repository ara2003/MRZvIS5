package com.exapmle.lab;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.example.lab.GRUNeuralNetwork;
import com.example.lab.NNSequence;
import com.example.lab.NNVector;

public class GRUNeuralNetworkTest {
	
	private static final float EPS = 1E-2f;
	private static final float ERROR_LIMIT = 0.00000001f;
	private static final float ALPHA = 0.001f;
	
	private void assertSoftEquals(float a, float b) {
		assertSoftEquals(a, b, EPS);
	}
	
	private void assertSoftEquals(float a, float b, float eps) {
		final var s = Math.abs(a - b);
		assertTrue(s < eps, "sub/eps: " + s / eps);
	}
	
	private NNSequence sequence(GRUNeuralNetwork nn, Number... numbers) {
		final var result = new ArrayList<Float>();
		for(var v : numbers)
			result.add(v.floatValue());
		return new NNSequence(nn, result);
	}
	
//	@Test
	void learnTest_fib() {
		final var nn = new GRUNeuralNetwork(2);
		final var sequence = sequence(nn, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
		sequence.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(1, 2f)).test(new NNVector(1, 2)).get(0);
		assertSoftEquals(result1, 3);
		final var result2 = nn.context(new NNVector(2, 3f)).test(new NNVector(2, 3)).get(0);
		assertSoftEquals(result2, 5);
		final var result3 = nn.context(new NNVector(34, 55f)).test(new NNVector(34, 55)).get(0);
		assertSoftEquals(result3, 89);
	}
	
//	@Test
	void learnTest_fib3() {
		final var nn = new GRUNeuralNetwork(3);
		final var sequence = sequence(nn, 1, 1, 1, 3, 5, 9, 17, 31);
		sequence.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(1, 1, 3f)).test(new NNVector(1, 1, 3)).get(0);
		assertSoftEquals(result1, 5);
		final var result2 = nn.context(new NNVector(1, 3, 5f)).test(new NNVector(1, 3, 5)).get(0);
		assertSoftEquals(result2, 9);
		final var result3 = nn.context(new NNVector(3, 5, 9f)).test(new NNVector(3, 5, 9)).get(0);
		assertSoftEquals(result3, 17);
		final var result4 = nn.context(new NNVector(31, 57, 105f)).test(new NNVector(31, 57, 105)).get(0);
		assertSoftEquals(result4, 193);
	}
	
//	@Test
	void learnTest_non_zero() {
		final var nn = new GRUNeuralNetwork(2);
		final var sequence = sequence(nn, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1);
		sequence.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(1, -1f)).test(new NNVector(1, -1)).get(0);
		assertSoftEquals(result1, 1);
		final var result2 = nn.context(new NNVector(-1, 1f)).test(new NNVector(-1, 1)).get(0);
		assertSoftEquals(result2, -1);
	}
	
	@Test
	void learnTest_zero() {
		final var nn = new GRUNeuralNetwork(1);
		final var sequence = sequence(nn, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0);
		sequence.learn(ERROR_LIMIT, ALPHA, true);
		final var result1 = test(nn, new NNVector(1, 0));
		assertSoftEquals(result1, -1);
		final var result2 = test(nn, new NNVector(-1, 0f));
		assertSoftEquals(result2, 1);
		final var result3 = test(nn, new NNVector(0, 1f));
		assertSoftEquals(result3, 0);
		final var result4 = test(nn, new NNVector(0, 1f));
		assertSoftEquals(result4, 0);
	}

	private float test(GRUNeuralNetwork nn, NNVector input) {
		return nn.context(input).test(input).get(0);
	}
	
}
