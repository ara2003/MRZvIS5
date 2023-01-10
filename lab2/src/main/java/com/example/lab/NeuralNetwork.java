package com.example.lab;

import java.io.Serializable;
import java.util.Collection;

public class NeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private NNMatrix weight;
	private final int size;
	
	public NeuralNetwork(int size) {
		this.size = size;
	}
	
	public static NNVector activation(NNVector vec) {
		return activation(vec, vec);
	}
	
	
	public static NNVector activation(NNVector vec, NNVector dest) {
		NNVector.checkSize(vec, dest);
		for(var i = 0; i < vec.size(); i++)
			dest.set(i, vec.get(i) >= 0 ? 1 : -1);
		return dest;
	}
	
	private static int size(Iterable<?> iterable) {
		if(iterable instanceof Collection<?> c)
			return c.size();
		var size = 0;
		final var iter = iterable.iterator();
		while(iter.hasNext()) {
			size++;
			iter.next();
		}
		return size;
	}
	
	public void learn(Iterable<? extends NNVector> images) {
		weight = new NNMatrix(size, size);
		for(var img : images)
			weight.addMultMatrix(img, img);
		
		for(var i = 0; i < size; i++)
			weight.get(i).set(i, 0);
		
		final var size = size(images);
		weight.mult(1f / size);
	}
	
	
	public NNVector test(NNVector img) {
		var temp1 = new NNVector(img.size());
		var temp2 = new NNVector(img.size());
		
		weight.mult(img, temp1);
		activation(temp1);
		
		while(!temp1.equals(temp2)) {
			weight.mult(temp1, temp2);
			activation(temp2);
			final var temp = temp1;
			temp1 = temp2;
			temp2 = temp;
		}
		
		return temp1;
	}
	
	public int size() {
		return size;
	}
	
	
	
}
