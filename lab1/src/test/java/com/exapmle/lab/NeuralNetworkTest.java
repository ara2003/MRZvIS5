package com.exapmle.lab;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.lab.MathUtil;
import com.example.lab.NNVector;
import com.example.lab.NeuralNetwork;

public class NeuralNetworkTest {
	
	private static final float ACCURACY = 1E-5f;
	
	@Test
	void test1() {
		final var nn = new NeuralNetwork(3, 2);

		final var vs = new NNVector[] {
			new NNVector(1, 1, 0),
			new NNVector(1, 0, 1),
		};
		
		learn(nn, ACCURACY, vs);

		for(var v : vs) {
			final var e = MathUtil.sum(MathUtil.sqr(MathUtil.sub(v, nn.run(v))));
			assertTrue(e*e < ACCURACY, e+"");
		}
	}

	private void learn(NeuralNetwork nn, float elim, NNVector...vs) {
		float e;
		do {
			e = 0;
			for(var v : vs) {
				final var e0 = nn.learn(v);
				if(e0 > e) e = e0;
			}
		} while(e > elim);
	}
	
}
