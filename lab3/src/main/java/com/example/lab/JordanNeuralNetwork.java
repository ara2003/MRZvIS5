package com.example.lab;

import java.io.Serializable;

public final class JordanNeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final NNMatrix weight1, weight2;
	private final int input, hidden, output;
	
	
	public JordanNeuralNetwork(int input, int hidden, int output) {
		this.input = input;
		this.hidden = hidden;
		this.output = output;
		weight1 = new NNMatrix(input + output, hidden);
		weight2 = new NNMatrix(hidden, output);
	}
	
	
	public static float activation(float f) {
		return f;
	}
	
	public static float derivative(float f) {
		return 1;
	}
	
	//	public static float activation(float f) {
	//		return (float) (1 / (1 + Math.exp(-f)));
	//	}
	//	public static float derivative(float f) {
	//		return (1 - f) * f;
	//	}
	
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
		final var in = NNVector.concat(input, context);
		final var hidden = new NNVector(sizeHidden());
		final var out = new NNVector(sizeOutput());
		weight1.mult(in, hidden);
		activation(hidden);
		weight2.mult(hidden, out);
		
		final var errorOut = new NNVector(sizeOutput());
		final var errorHidden = new NNVector(sizeHidden());
		
		output.sub(out, errorOut);
		weight2.transposeMult(errorOut, errorHidden);
		
		final var gradientOut = new NNVector(out);
		final var gradientHidden = new NNVector(hidden);
		
		derivative(gradientHidden);
		
		gradientOut.mul(errorOut);
		gradientHidden.mul(errorHidden);
		
		gradientOut.mul(-alpha);
		gradientHidden.mul(-alpha);
		
		weight1.addMultMatrix(in, gradientHidden);
		weight2.addMultMatrix(hidden, gradientOut);
		
		//		normalize(weight1);
		//		normalize(weight2);
		
		System.out.println(output);
		System.out.println(in);
		System.out.println(hidden);
		System.out.println(out);
		System.out.println(errorOut);
		System.out.println(errorHidden);
		System.out.println(gradientOut);
		System.out.println(gradientHidden);
		System.out.println(weight1);
		System.out.println(weight2);
		System.out.println();
		
		return errorOut.sqrLength();
	}
	
	private void normalize(NNMatrix matrix) {
		for(int i = 0; i < matrix.output(); i++) {
			float sum = 0;
			for(int j = 0; j < matrix.input(); j++)
				sum += matrix.get(j).get(i) * matrix.get(j).get(i);
			sum = (float) Math.sqrt(sum);
			for(int j = 0; j < matrix.input(); j++)
				matrix.get(j).set(i, matrix.get(j).get(i) / sum);
		}
	}
	
	
	private NNVector up(NNVector vector, float x) {
		final var result = new NNVector(vector.size() + 1);
		for(int i = 0; i < vector.size(); i++)
			result.set(i, vector.get(i));
		result.set(result.size() - 1, x);
		return result;
	}
	
	private NNVector down(NNVector vector) {
		final var result = new NNVector(vector.size() - 1);
		for(int i = 0; i < result.size(); i++)
			result.set(i, vector.get(i));
		return result;
	}
	
	
	public NNVector learn(float alpha, NNVector input, NNVector context) {
		final var out = new NNVector(sizeOutput());
		learn(alpha, input, context, out);
		return out;
	}
	
	public void test(NNVector input, NNVector context, NNVector output) {
		input = NNVector.concat(input, context);
		final var hidden = new NNVector(sizeHidden());
		weight1.mult(input, hidden);
		activation(hidden);
		weight2.mult(hidden, output);
	}
	
	private void activation(NNVector vector) {
		for(int i = 0; i < vector.size(); i++)
			vector.set(i, activation(vector.get(i)));
	}
	
	private void derivative(NNVector vector) {
		for(int i = 0; i < vector.size(); i++)
			vector.set(i, derivative(vector.get(i)));
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
		
		public void setContext(NNVector context) {
			this.context.set(context);
		}
		
	}
	
}
