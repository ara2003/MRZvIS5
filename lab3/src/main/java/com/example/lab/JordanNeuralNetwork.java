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
		weight2.mult(hidden, out);
		
		final var errorOut = new NNVector(sizeOutput());
		final var errorHidden = new NNVector(sizeHidden());
		
		output.sub(out, errorOut);
		
		final var error = errorOut.sqrLength();
		if(error > 1E18) {
			reset();
			return learn(alpha, input, context, output);
		}
		weight2.transposeMult(errorOut, errorHidden);
		
		errorOut.mul(alpha);
		errorHidden.mul(alpha);
		
		weight1.addMultMatrix(in, errorHidden);
		weight2.addMultMatrix(hidden, errorOut);
		
		//		System.out.println(output);
		//		System.out.println(in);
		//		System.out.println(hidden);
		//		System.out.println(out);
		//		System.out.println(errorOut);
		//		System.out.println(errorHidden);
		//		System.out.println(weight1);
		//		System.out.println(weight2);
		//		System.out.println();
		
		return error;
	}
	
	private void reset() {
		weight1.reset();
		weight2.reset();
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
		weight2.mult(hidden, output);
	}
	
	public TestContext context() {
		return context(new NNVector(sizeContext()));
	}
	
	public TestContext context(NNVector context) {
		return new TestContext(context);
	}
	
	public final class TestContext {
		
		private final NNVector context = new NNVector(sizeContext());
		
		public TestContext(NNVector context) {
			this.context.set(context);
		}
		
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
