package com.example.lab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class NNVector implements Iterable<Float>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final float[] values;
	
	public NNVector(float... values) {
		this.values = values.clone();
	}
	
	public NNVector inverse() {
		return inverse(this);
	}
	
	public NNVector inverse(NNVector dest) {
		checkSize(dest, this);
		for(int i = 0; i < size(); i++)
			dest.set(i, 1 - get(i));
		return dest;
	}

	@Override
	public String toString() {
		return Arrays.toString(values);
	}
	
	public NNVector(int size) {
		values = new float[size];
	}
	
	public NNVector(NNVector vector) {
		this.values = vector.values.clone();
	}
	
	public static void checkSize(NNVector a, NNVector b) {
		if(a.size() != b.size())
			throw new IllegalArgumentException(a.size() + " != " + b.size());
	}
	
	public float dot(NNVector vec) {
		checkSize(this, vec);
		float result = 0;
		for(int i = 0; i < size(); i++) {
			final var a = get(i);
			final var b = vec.get(i);
			result += a * b;
		}
		return result;
	}
	
	public float get(int index) {
		return values[index];
	}
	
	@Override
	public Iterator<Float> iterator() {
		final var list = new ArrayList<Float>();
		for(var x : values)
			list.add(x);
		return list.iterator();
	}
	
	public NNVector mul(float k) {
		return mul(k, this);
	}
	
	public NNVector mul(float k, NNVector dest) {
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, get(i) * k);
		return dest;
	}
	
	public NNVector mul(NNVector vec) {
		return mul(vec, this);
	}
	
	public NNVector mul(NNVector vec, NNVector dest) {
		checkSize(this, vec);
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, get(i) * vec.get(i));
		return dest;
	}
	
	public void set(int index, float value) {
		values[index] = value;
	}
	
	public int size() {
		return values.length;
	}
	
	public NNVector sub(NNVector vec, NNVector dest) {
		checkSize(this, vec);
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, get(i) - vec.get(i));
		return dest;
	}
	
	public NNVector sub(NNVector vec) {
		return sub(vec, this);
	}
	
	public NNVector set(NNVector vec) {
		checkSize(vec, this);
		for(int i = 0; i < size(); i++)
			set(i, vec.get(i));
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof NNVector)) {
			return false;
		}
		NNVector other = (NNVector) obj;
		return Arrays.equals(values, other.values);
	}
	
	public NNVector normalize() {
		return normalize(this);
	}
	
	public NNVector normalize(NNVector dest) {
		return mul(1 / length(), dest);
	}
	
	public float length() {
		return (float) Math.sqrt(sqrLength());
	}
	
	public float sqrLength() {
		float length = 0;
		for(var x : this)
			length += x * x;
		return length;
	}
	
	public static NNVector concat(NNVector a, NNVector b) {
		final var result = new NNVector(a.size() + b.size());
		for(int i = 0; i < a.size(); i++)
			result.set(i, a.get(i));
		for(int i = 0; i < b.size(); i++)
			result.set(i + a.size(), b.get(i));
		return result;
	}
	
	public void clear() {
		for(int i = 0; i < values.length; i++)
			values[i] = 0;
		
	}

	public NNVector add(NNVector vec, NNVector dest) {
		checkSize(vec, dest);
		checkSize(vec, this);
		for(int i = 0; i < vec.size(); i++)
			dest.set(i, get(i) + vec.get(i));
		return dest;
	}

	public NNVector sqrt(NNVector dest) {
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, (float)Math.sqrt(get(i)));
		return dest;
	}
	
}
