package com.example.lab;

import java.util.function.Function;

public class MathUtil {
	
	public static float[] random(int len, float min, float max) {
		final var result = new float[len];
		for(int i = 0; i < result.length; i++)
			result[i] = random(min, max);
		return result;
	}
	
	public static float random(float min, float max) {
		var r = (float) Math.random();
		r *= (max - min);
		r += min;
		return r;
	}
	
	public static float sum(NNVector vec) {
		var sum = 0f;
		for(var x : vec)
			sum += x;
		return sum;
	}

	public static NNVector sub(NNVector a, NNVector b) {
		return a.sub(b, new NNVector(a.size()));
	}

	public static NNVector map(NNVector v, Function<? super Float, ? extends Float> function, NNVector dest) {
		NNVector.checkSize(v, dest);
		for(int i = 0; i < v.size(); i++) {
			final var value = v.get(i);
			dest.set(i, function.apply(value));
		}
		return dest;
	}

	public static NNVector sqr(NNVector v, NNVector dest) {
		return map(v, x -> x*x, dest);
	}
	
	public static NNVector sqr(NNVector v) {
		return sqr(v, new NNVector(v.size()));
	}
	
}
