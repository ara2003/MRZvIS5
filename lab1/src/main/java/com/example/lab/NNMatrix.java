package com.example.lab;

import java.io.Serializable;
import java.util.Arrays;

public class NNMatrix implements Serializable {
	private static final long serialVersionUID = 1L;

	private final NNVector[] values;

	public NNMatrix(NNVector...values) {
		this.values = values.clone();
	}

	public NNMatrix(float[][] values) {
		this.values = new NNVector[values.length];
		for(int i = 0; i < this.values.length; i++) {
			final var fs = new float[values[i].length];
			for(int j = 0; j < fs.length; j++)
				fs[j] = values[i][j];
			this.values[i] = new NNVector(fs);
		}
	}

	public NNMatrix(int input, int output) {
		this(input, output, 1);
	}
	
	public NNMatrix(int input, int output, float border) {
		this(input, output, MathUtil.random(input*output, -border, border));
	}

	public NNMatrix(int input, int output, float...values) {
		this.values = new NNVector[input];
		for(int i = 0; i < this.values.length; i++) {
			final var fs = new float[output];
			for(int j = 0; j < fs.length; j++)
				fs[j] = values[i * output + j];
			this.values[i] = new NNVector(fs);
		}
	}
	public void addMultMatrix(NNVector a, NNVector b) {
		if(a.size() != input()) throw new IllegalArgumentException(input() + " != " + a.size());
		if(b.size() != output()) throw new IllegalArgumentException(output() + " != " + b.size());

		for (int i = 0; i < a.size(); i++) {
			final var l = get(i);
			final var ai = a.get(i);
			for (int j = 0; j < b.size(); j++) l.set(j, l.get(j) + ai * b.get(j));
		}
	}
	
	public NNVector get(int i) {
		return values[i];
	}

	public int input() {
		return values.length;
	}

	public NNVector mult(NNVector vector) {
		return mult(vector, new NNVector(output()));
	}

	public NNVector mult(NNVector vector, NNVector dest) {
		if(vector.size() != input()) throw new IllegalArgumentException(input() + " != " + vector.size());
		if(dest.size() != output()) throw new IllegalArgumentException(output() + " != " + dest.size());
		for(int i = 0; i < input(); i++) {
			final var l = get(i);
			for(int j = 0; j < output(); j++)
				dest.set(j, dest.get(j) + vector.get(i) * l.get(j));
		}
		return dest;
	}

	public int output() {
		return values[0].size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NNMatrix [");
		builder.append(Arrays.toString(values));
		builder.append("]");
		return builder.toString();
	}

	public NNVector transposeMult(NNVector vector) {
		return transposeMult(vector, new NNVector(input()));
	}

	public NNVector transposeMult(NNVector vector, NNVector dest) {
		if(vector.size() != output()) throw new IllegalArgumentException(output() + " != " + vector.size());
		if(dest.size() != input()) throw new IllegalArgumentException(input() + " != " + dest.size());
		for(int i = 0; i < input(); i++) {
			final var l = get(i);
			for(int j = 0; j < output(); j++)
				dest.set(i, dest.get(i) + vector.get(j) * l.get(j));
		}
		return dest;
	}

	public NNMatrix transpose() {
		return transpose(new NNMatrix(output(), input()));
	}

	public NNMatrix transpose(NNMatrix dest) {
		if(dest.input() != output()) throw new IllegalArgumentException(output() + " != " + dest.input());
		if(dest.output() != input()) throw new IllegalArgumentException(input() + " != " + dest.output());
		for(int x = 0; x < dest.input(); x++)
			for(int y = 0; y < dest.output(); y++) {
				dest.values[x].set(y, get(y).get(x));
			}
		return dest;
	}

}
