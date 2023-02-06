package com.example.lab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.lab.GRUNeuralNetwork.TestContext;

public final class NNSequence {
	
	private final List<? extends Float> sequence;
	private final GRUNeuralNetwork nn;
	
	public NNSequence(GRUNeuralNetwork nn, List<? extends Float> sequence) {
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
	
	public void learn(float errorLimit, float alpha, boolean log) {
		learn(errorLimit, 100000, alpha, log);
	}
		
	public void learn(float errorLimit, int iterationLimit, float alpha, boolean log) {
		TestContext context = null;
		final var tests = split(sequence, nn.size(), nn.size());
		
		int iteration = 0;
		float e;
		while(iteration < iterationLimit) {
			e = 0;
			{
    			final var iter = tests.iterator();
    			if(iter.hasNext()) {
    				final var test = iter.next();
    				context = nn.context(test.second());
    			}
    			while(iter.hasNext()) {
    				final var test = iter.next();
    				e += context.get_error(test.first(), test.second());
    			}
			}
			if(log)
				System.out.println("error: " + (e / errorLimit) + " iteration:" + iteration);
			context.clear();
			if(e > errorLimit) 
			{
				iteration++;
    			final var iter = tests.iterator();
    			if(iter.hasNext()) {
    				final var test = iter.next();
    				context = nn.context(test.second());
    			}
    			while(iter.hasNext()) {
    				final var test = iter.next();
    				context.learn(alpha, test.first(), test.second());
    			}
			}else break;
		}
	}
	
	public List<Float> sequence(int size) {
		final var list = new ArrayList<Float>();
		final TestContext context;
		{
			final var init_contex = new NNVector(nn.size());
			for(int i = 0; i < nn.size() + nn.size(); i++) {
				final var e = sequence.get(i);
				list.add(e);
			}
			for(int i = 0; i < nn.size(); i++) {
				final var e = sequence.get(nn.size() + i);
				init_contex.set(i, e);
			}
			context = nn.context(init_contex);
		}
		
		final var test = new NNVector(nn.size());
		
		for(int i = 0; i < nn.size(); i++)
			test.set(i, sequence.get(i + 1));
		
		while(list.size() <= size) {
			var e = context.test(test).get(0);
			//			e = (float) Math.round(e);
			slide(test, e);
			list.add(e);
		}
		return list;
	}
	
	private void slide(NNVector vector, float x) {
		for(int i = 0; i < vector.size() - 1; i++) {
			vector.set(i, vector.get(i + 1));
		}
		vector.set(vector.size() - 1, x);
	}
	
	public void learn(float errorlimit, float alpha) {
		learn(errorlimit, alpha, false);
	}
	
}
