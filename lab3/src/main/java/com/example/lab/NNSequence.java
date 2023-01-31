package com.example.lab;

import java.util.ArrayList;
import java.util.Collection;

public final class NNSequence {
	
	private final Collection<? extends Float> sequence;
	private final JordanNeuralNetwork nn;
	
	public NNSequence(JordanNeuralNetwork nn, Collection<? extends Float> sequence) {
		this.sequence = sequence;
		this.nn = nn;
	}
	
	public static Collection<NNVector> split(Collection<? extends Float> vector, int size) {
		final var result = new ArrayList<NNVector>();
		final var list = new ArrayList<>(vector);
		final var vec = new float[size];
		for(int i = 0; i < list.size() - size + 1; i++) {
			for(int j = 0; j < size; j++) {
				vec[j] = list.get(i + j);
			}
			result.add(new NNVector(vec));
		}
		return result;
	}
	
	public static Collection<Pair<NNVector, NNVector>> split(Collection<? extends Float> vector,
			int input, int output) {
		final var result = new ArrayList<Pair<NNVector, NNVector>>();
		final var list = new ArrayList<>(vector);
		final var in = new float[input];
		final var out = new float[output];
		for(int i = 0; i < list.size() - input - output + 1; i++) {
			for(int j = 0; j < input; j++) {
				in[j] = list.get(i + j);
			}
			for(int j = 0; j < output; j++) {
				out[j] = list.get(i + j + input);
			}
			result.add(new Pair<>(new NNVector(in), new NNVector(out)));
		}
		return result;
	}
	
	public void learn(float errorLimit, float alpha) {
		final var context = nn.context();
		final var tests = split(sequence, nn.sizeInput(), nn.sizeOutput());
		
		float e;
		do {
			e = 0;
			context.clear();
			for(var test : tests) {
				e += context.learn(alpha, test.first(), test.second());
			}
			//			System.out.println("error: " + (e / errorLimit));
		}while(e > errorLimit);
		
	}
	
}
