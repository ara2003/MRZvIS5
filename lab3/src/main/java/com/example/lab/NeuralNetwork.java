package com.example.lab;

import java.io.Serializable;

public class NeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final NNMatrix[] layers;
	
	public NeuralNetwork(int... sizes) {
		layers = new NNMatrix[sizes.length - 1];
		for(var i = 0; i < layers.length; i++)
			layers[i] = new NNMatrix(sizes[i], sizes[i + 1]);
	}
	
	public float activation(float x) {
		return x;
		//		return (float) (1 / (1 + Math.exp(-x)));
	}
	
	public float derivative(float y) {
		return 1;
		//		return (1 - y) * y;
	}
	
	public float backpropagation(float learningRate, NNVector inputs, NNVector targets) {
		final var neurons = new NNVector[layers.length + 1];
		neurons[0] = inputs;
		for(var i = 1; i < neurons.length; i++) {
			final var l = layers[i - 1];
			neurons[i] = new NNVector(neurons[i - 1]);
			neurons[i] = l.mult(neurons[i]);
			activation(neurons[i]);
		}
		final var output = neurons[neurons.length - 1];
		var errors = sub(targets, output);
		final var res = errors.sqrLength();
		for(var k = layers.length - 1; k >= 0; k--) {
			final var l = layers[k];
			var gradients = new NNVector(neurons[k + 1]);
			derivative(gradients);
			gradients.mul(errors);
			gradients.mul(learningRate);
			
			errors = l.transposeMult(errors);
			
			l.addMultMatrix(neurons[k], gradients);
		}
		return res;
	}
	
	public NNVector feedForward(NNVector neurons) {
		for(var l : layers) {
			neurons = l.mult(neurons);
			activation(neurons);
		}
		return neurons;
	}
	
	private NNVector activation(NNVector vec) {
		return activation(vec, vec);
	}
	
	private NNVector activation(NNVector vec, NNVector dest) {
		NNVector.checkSize(vec, dest);
		for(var i = 0; i < vec.size(); i++)
			dest.set(i, activation(vec.get(i)));
		return dest;
	}
	
	private NNVector derivative(NNVector vec) {
		return derivative(vec, vec);
	}
	
	private NNVector derivative(NNVector vec, NNVector dest) {
		NNVector.checkSize(vec, dest);
		for(var i = 0; i < vec.size(); i++)
			dest.set(i, derivative(vec.get(i)));
		return dest;
	}
	
	private NNVector sub(NNVector a, NNVector b) {
		final var result = new NNVector(a.size());
		a.sub(b, result);
		return result;
	}
	
}
