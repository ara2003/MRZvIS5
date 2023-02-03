package com.example.lab;

import java.io.Serializable;
import java.util.Arrays;

public class NNMatrix implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final NNVector[] values;
	
	public NNMatrix(float[][] values) {
		this.values = new NNVector[values.length];
		for(var i = 0; i < this.values.length; i++) {
			final var fs = new float[values[i].length];
			for(var j = 0; j < fs.length; j++)
				fs[j] = values[i][j];
			this.values[i] = new NNVector(fs);
		}
	}
	
	public NNMatrix(int input, int output) {
		this(input, output, 1);
	}
	
	public NNMatrix(int input, int output, float border) {
		values = new NNVector[input];
		final var fs = new float[output];
		for(var i = 0; i < values.length; i++) {
			for(var j = 0; j < fs.length; j++)
				fs[j] = (float) ((Math.random() * 2 - 1) * border);
			values[i] = new NNVector(fs);
			
		}
	}
	
	public NNMatrix(int input, int output, float... values) {
		this.values = new NNVector[input];
		final var fs = new float[output];
		for(var i = 0; i < this.values.length; i++) {
			for(var j = 0; j < fs.length; j++)
				fs[j] = values[i * output + j];
			this.values[i] = new NNVector(fs);
		}
	}
	
	public NNMatrix(NNVector... values) {
		this.values = values.clone();
	}
	
	public void addMultMatrix(NNVector a, NNVector b) {
		if(a.size() != input())
			throw new IllegalArgumentException(input() + " != " + a.size());
		if(b.size() != output())
			throw new IllegalArgumentException(output() + " != " + b.size());
		
		for(var i = 0; i < a.size(); i++) {
			final var l = get(i);
			final var ai = a.get(i);
			for(var j = 0; j < b.size(); j++)
				l.set(j, l.get(j) + ai * b.get(j));
		}
	}
	
	public NNVector get(int i) {
		return values[i];
	}
	
	public int input() {
		return values.length;
	}
	
	public NNMatrix mult(float k) {
		return mult(k, this);
	}
	
	public NNMatrix mult(float k, NNMatrix dest) {
		for(var i = 0; i < input(); i++)
			for(var j = 0; j < output(); j++)
				dest.get(i).set(j, k * get(i).get(j));
		return dest;
	}
	
	public NNVector mult(NNVector vector) {
		return mult(vector, new NNVector(output()));
	}
	
	public NNVector mult(NNVector vector, NNVector dest) {
		if(vector.size() != input())
			throw new IllegalArgumentException(input() + " != " + vector.size());
		if(dest.size() != output())
			throw new IllegalArgumentException(output() + " != " + dest.size());
		for(var i = 0; i < input(); i++) {
			final var l = get(i);
			for(var j = 0; j < output(); j++)
				dest.set(j, dest.get(j) + vector.get(i) * l.get(j));
		}
		return dest;
	}
	
	public int output() {
		return values[0].size();
	}
	
	@Override
	public String toString() {
		return Arrays.toString(values);
	}
	
	public NNMatrix transpose() {
		return transpose(new NNMatrix(output(), input()));
	}
	
	public NNMatrix transpose(NNMatrix dest) {
		if(dest.input() != output())
			throw new IllegalArgumentException(output() + " != " + dest.input());
		if(dest.output() != input())
			throw new IllegalArgumentException(input() + " != " + dest.output());
		for(var x = 0; x < dest.input(); x++)
			for(var y = 0; y < dest.output(); y++)
				dest.values[x].set(y, get(y).get(x));
		return dest;
	}
	
	public NNVector transposeMult(NNVector vector) {
		return transposeMult(vector, new NNVector(input()));
	}
	
	public NNVector transposeMult(NNVector vector, NNVector dest) {
		if(vector.size() != output())
			throw new IllegalArgumentException(output() + " != " + vector.size());
		if(dest.size() != input())
			throw new IllegalArgumentException(input() + " != " + dest.size());
		for(var i = 0; i < input(); i++) {
			final var l = get(i);
			for(var j = 0; j < output(); j++)
				dest.set(i, dest.get(i) + vector.get(j) * l.get(j));
		}
		return dest;
	}
	
	public void reset() {
		for(int i = 0; i < input(); i++)
			for(int j = 0; j < output(); j++)
				get(i).set(j, (float) (Math.random() * 2 - 1));
	}
	
}
