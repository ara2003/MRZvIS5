package com.example.lab;

import static com.example.lab.MathUtil.*;

import java.io.Serializable;

public final class NeuralNetwork implements Serializable {
	private static final long serialVersionUID = 1L;

	private NNMatrix weight1, wweight2;

	private void reset() {
		reset(weight1.input(), weight1.output());
	}
	
	private void reset(int originalSize, int copressSize) {
		this.weight1 = new NNMatrix(originalSize, copressSize, 1f);
		this.wweight2 = weight1.transpose();
	}
	
	public NeuralNetwork(int originalSize, int copressSize) {
		reset(originalSize, copressSize);
	}

	public NNVector compress(NNVector x) {
		return weight1.mult(x);
	}
	
	public NNVector decompress(NNVector x) {
		return wweight2.mult(x);
	}

	public float getError(NNVector original) {
		final var compress = compress(original);
		final var result   = decompress(compress);
		
		final var dx = sub(result, original);
		
		return sum(sqr(dx));
	}
	
	public float learn(NNVector original) {
		final var compressed   = compress(original);
		final var decompressed = decompress(compressed);
		
		final var decompressedDelta = sub(decompressed, original);		
		
		final var error = sum(sqr(decompressedDelta));
		
		if(error > 1E18) {
			reset();
			return learn(original);
		}
		
		final var compressedDelta = wweight2.transposeMult(decompressedDelta);

		float alpha;
		
		alpha = 1f / compressed.dot(compressed);
		decompressedDelta.mul(-alpha);
		wweight2.addMultMatrix(compressed, decompressedDelta);

		alpha = 1f / decompressed.dot(decompressed);
		compressedDelta.mul(-alpha);
		weight1.addMultMatrix(original, compressedDelta);
		
		return error;
	}

	public NNVector run(NNVector v) {
		return decompress(compress(v));
	}
}
