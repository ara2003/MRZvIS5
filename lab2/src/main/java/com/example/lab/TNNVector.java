package com.example.lab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class TNNVector implements Iterable<TernarFloat>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final TernarFloat[] values;
	
	public TNNVector(TernarFloat... values) {
		this.values = values.clone();
	}
	
	public TNNVector(int size) {
		values = new TernarFloat[size];
	}
	
	public static void checkSize(TNNVector a, TNNVector b) {
		if(a.size() != b.size())
			throw new IllegalArgumentException(a.size() + " != " + b.size());
	}
	
	public float dot(TNNVector vec) {
		checkSize(this, vec);
		float result = 0;
		for(int i = 0; i < size(); i++) {
			final var a = get(i);
			final var b = vec.get(i);
			result += a.mult(b).toFloat();
		}
		return result;
	}
	
	public TernarFloat get(int index) {
		return values[index];
	}
	
	@Override
	public Iterator<TernarFloat> iterator() {
		final var list = new ArrayList<TernarFloat>();
		for(var x : values)
			list.add(x);
		return list.iterator();
	}
	
	public TNNVector mul(TernarFloat k) {
		return mul(k, this);
	}
	
	public TNNVector mul(TernarFloat k, TNNVector dest) {
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, get(i).mult(k));
		return dest;
	}
	
	public TNNVector mul(TNNVector vec, TNNVector dest) {
		checkSize(this, vec);
		checkSize(this, dest);
		for(int i = 0; i < size(); i++)
			dest.set(i, get(i).mult(vec.get(i)));
		return dest;
	}
	
	public void set(int index, TernarFloat value) {
		values[index] = value;
	}
	
	public int size() {
		return values.length;
	}
	
}
