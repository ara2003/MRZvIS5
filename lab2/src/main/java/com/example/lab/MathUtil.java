package com.example.lab;

import java.util.function.Function;

public class MathUtil {
	
	public static float[] random(int len, float min, float max) {
		final var result = new float[len];
		for(int i = 0; i < result.length; i++)
			result[i] = random(min, max);
		return result;
	}
	
	public static TernarFloat[] random(int len) {
		final var result = new TernarFloat[len];
		for(int i = 0; i < result.length; i++)
			result[i] = TernarFloat.randomONE();
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
	
	public static TNNVector map(TNNVector v, Function<? super TernarFloat, ? extends TernarFloat> function, TNNVector dest) {
		TNNVector.checkSize(v, dest);
		for(int i = 0; i < v.size(); i++) {
			final var value = v.get(i);
			dest.set(i, function.apply(value));
		}
		return dest;
	}
	
	public static TNNVector sqr(TNNVector v, TNNVector dest) {
		return map(v, x->x.mult(x), dest);
	}
	
	public static TNNVector sqr(TNNVector v) {
		return sqr(v, new TNNVector(v.size()));
	}
	
}
