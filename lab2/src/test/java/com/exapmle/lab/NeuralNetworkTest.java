package com.exapmle.lab;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.lab.NNVector;
import com.example.lab.NeuralNetwork;

public class NeuralNetworkTest {
	
	@Test
	void learnTest() {
		final var img1 = new NNVector(1, -1, 1, -1);
		final var img2 = new NNVector(-1, 1, 1, -1);
		
		final var nn = new NeuralNetwork(img1.size());
		
		nn.learn(List.of(img1, img2));
		
		final var res1 = nn.test(img1);
		final var res2 = nn.test(img2);
		
		assertEquals(img1, res1);
		assertEquals(img2, res2);
	}
	
}
