package com.exapmle.lab;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.example.lab.JordanNeuralNetwork;
import com.example.lab.NNSequence;
import com.example.lab.NNVector;

public class JordanNeuralNetworkTest {
	
	private static final float EPS = 1E-3f;
	private static final float ERROR_LIMIT = 0.0000001f;
	private static final float ALPHA = 0.001f;
	
	private void assertSoftEquals(float a, float b, float eps) {
		final var s = Math.abs(a - b);
		assertTrue(s < eps, "sub: " + s / eps);
	}
	
	private void assertSoftEquals(float a, float b) {
		assertSoftEquals(a, b, EPS);
	}
	
	private Collection<Float> sequence(Number... numbers) {
		final var result = new ArrayList<Float>();
		for(var v : numbers)
			result.add(v.floatValue());
		return result;
	}
	
	@Test
	void learnTest_fib() {
		final var INPUT = 2;
		final var OUTPUT = 1;
		
		final var list = sequence(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
		final var nn = new JordanNeuralNetwork(INPUT, OUTPUT + INPUT, OUTPUT);
		final var seq = new NNSequence(nn, list);
		seq.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(2f)).test(new NNVector(1, 2)).get(0);
		assertSoftEquals(result1, 3);
		final var result2 = nn.context(new NNVector(3f)).test(new NNVector(2, 3)).get(0);
		assertSoftEquals(result2, 5);
		final var result3 = nn.context(new NNVector(55f)).test(new NNVector(34, 55)).get(0);
		assertSoftEquals(result3, 89);
	}
	
	@Test
	void learnTest_fib3() {
		final var INPUT = 3;
		final var OUTPUT = 1;
		
		final var list = sequence(1, 1, 1, 3, 5, 9, 17, 31);
		final var nn = new JordanNeuralNetwork(INPUT, OUTPUT + INPUT, OUTPUT);
		final var seq = new NNSequence(nn, list);
		seq.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(3f)).test(new NNVector(1, 1, 3)).get(0);
		assertSoftEquals(result1, 5);
		final var result2 = nn.context(new NNVector(5f)).test(new NNVector(1, 3, 5)).get(0);
		assertSoftEquals(result2, 9);
		final var result3 = nn.context(new NNVector(9f)).test(new NNVector(3, 5, 9)).get(0);
		assertSoftEquals(result3, 17);
		final var result4 = nn.context(new NNVector(105f)).test(new NNVector(31, 57, 105)).get(0);
		assertSoftEquals(result4, 193, EPS * 10);
	}
	
	@Test
	void learnTest_non_zero() {
		final var INPUT = 2;
		final var OUTPUT = 1;
		
		final var list = sequence(1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1);
		final var nn = new JordanNeuralNetwork(INPUT, OUTPUT + INPUT, OUTPUT);
		final var seq = new NNSequence(nn, list);
		seq.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(-1f)).test(new NNVector(1, -1)).get(0);
		assertSoftEquals(result1, 1);
		final var result2 = nn.context(new NNVector(1f)).test(new NNVector(-1, 1)).get(0);
		assertSoftEquals(result2, -1);
	}
	
	@Test
	void learnTest_zero() {
		final var INPUT = 2;
		final var OUTPUT = 1;
		
		final var list = sequence(1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0);
		final var nn = new JordanNeuralNetwork(INPUT, OUTPUT + INPUT, OUTPUT);
		final var seq = new NNSequence(nn, list);
		seq.learn(ERROR_LIMIT, ALPHA);
		final var result1 = nn.context(new NNVector(0f)).test(new NNVector(1, 0)).get(0);
		assertSoftEquals(result1, -1);
		final var result2 = nn.context(new NNVector(0f)).test(new NNVector(-1, 0)).get(0);
		assertSoftEquals(result2, 1);
		final var result3 = nn.context(new NNVector(1f)).test(new NNVector(0, 1)).get(0);
		assertSoftEquals(result3, 0);
		final var result4 = nn.context(new NNVector(-1f)).test(new NNVector(0, -1)).get(0);
		assertSoftEquals(result4, 0);
	}
	
}
