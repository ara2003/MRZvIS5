package com.example.lab;

import java.io.Serializable;

public final class JordanNeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final NeuralNetwork nn;
	private final int input, hidden, output;
	
	
	public JordanNeuralNetwork(int input, int hidden, int output) {
		this.input = input;
		this.hidden = hidden;
		this.output = output;
		this.nn = new NeuralNetwork(input + output, hidden, output);
	}
	
	
	public static float activation(float f) {
		return (float) (1 / (1 + Math.exp(-f)));
	}
	
	public static float derivative(float f) {
		return (1 - f) * f;
	}
	//	public static float activation(float f) {
	//		return (float) Math.sin(Math.atan(f));
	//	}
	//	public static float derivative(float f) {
	//		return (float) (1 / Math.pow(f * f + 1, 3f / 2));
	//	}
	
	public int sizeHidden() {
		return hidden;
	}
	
	public int sizeInput() {
		return input;
	}
	
	public int sizeOutput() {
		return output;
	}
	
	public int sizeContext() {
		return output;
	}
	
	public float learn(float alpha, NNVector input, NNVector context, NNVector output) {
		input = NNVector.concat(input, context);
		return nn.backpropagation(alpha, input, output);
	}
	
	public NNVector learn(float alpha, NNVector input, NNVector context) {
		final var out = new NNVector(sizeOutput());
		learn(alpha, input, context, out);
		return out;
	}
	
	public void test(NNVector input, NNVector context, NNVector output) {
		input = NNVector.concat(input, context);
		final var result = nn.feedForward(input);
		output.set(result);
	}
	
	public TestContext context() {
		return new TestContext();
	}
	
	public final class TestContext {
		
		private final NNVector context = new NNVector(sizeContext());
		
		public NNVector test(NNVector input) {
			final var out = new NNVector(sizeContext());
			test(input, out);
			return out;
		}
		
		public void test(NNVector input, NNVector output) {
			JordanNeuralNetwork.this.test(input, context, output);
			context.set(output);
		}
		
		public void clear() {
			context.clear();
		}
		
		public float learn(float alpha, NNVector input, NNVector output) {
			final var error = JordanNeuralNetwork.this.learn(alpha, input, context, output);
			context.set(output);
			return error;
		}
		
	}
	
}
