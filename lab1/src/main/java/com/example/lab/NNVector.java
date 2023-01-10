package com.example.lab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class NNVector implements Iterable<Float>, Serializable {
	private static final long serialVersionUID = 1L;

	private final float[] values;

	public NNVector(float...values) {
		this.values = values.clone();
	}

	public NNVector(int size) {
		values = new float[size];
	}

	public static void checkSize(NNVector a, NNVector b) {
		if(a.size() != b.size())
			throw new IllegalArgumentException(a.size() +" != "+ b.size());
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

}
