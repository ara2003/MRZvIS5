package com.example.lab;

import java.io.Serializable;

public final class GRUNeuralNetwork implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final NNMatrix weight_r, weight_z, weight_h;
	private final int size;

	public static float dir_tanh(float x) {
		final var t = tanh(x);
		return 1 - t*t;
	}
	public static void dir_tanh(NNVector vec, NNVector dest) {
		NNVector.checkSize(vec, dest);
		for(int i = 0; i < vec.size(); i++)
			dest.set(i, dir_tanh(vec.get(i)));
	}
	public static float tanh(float x) {
		return (float)Math.tanh(x);
	}
	public static void tanh(NNVector vec) {
		for(int i = 0; i < vec.size(); i++)
			vec.set(i, tanh(vec.get(i)));
	}

	public static float dir_sigm(float x) {
		return sigm(x) * (1 - sigm(x));
	}
	public static void dir_sigm(NNVector vec, NNVector dest) {
		NNVector.checkSize(vec, dest);
		for(int i = 0; i < vec.size(); i++)
			dest.set(i, dir_sigm(vec.get(i)));
	}
	public static float sigm(float x) {
		return (float)(1 / (1f + Math.exp(-x)));
	}
	public static void sigm(NNVector vec) {
		for(int i = 0; i < vec.size(); i++)
			vec.set(i, sigm(vec.get(i)));
	}
	
	public GRUNeuralNetwork(int size) {
		this.size = size;
		weight_z = new NNMatrix(2*size + 1, size);
		weight_r = new NNMatrix(2*size + 1, size);
		weight_h = new NNMatrix(2*size + 1, size);
	}
	
	public int size() {
		return size;
	}

	public float get_error(NNVector input, NNVector previous_output, NNVector output) {
		final var in_z = NNVector.concat(input, previous_output);
		final var z = new NNVector(size);
		weight_z.mult(up(in_z, 1), z);
		sigm(z);

		final var in_r = in_z;
		final var r = new NNVector(size);
		weight_r.mult(up(in_r, 1), z);
		sigm(r);

		final var rh = new NNVector(size);
		r.mul(previous_output, rh);
		final var in_h = NNVector.concat(input, rh);

		final var h = new NNVector(size);
		weight_h.mult(up(in_h, 1), h);
		tanh(h);

		final var out = new NNVector(size);
		final var int_z_previous_output= new NNVector(size);
		
		h.mul(z, out);
		z.inverse(int_z_previous_output);
		int_z_previous_output.mul(previous_output);
		out.add(int_z_previous_output, out);
		
		final var errorOut = new NNVector(size);
		output.sub(out, errorOut);
		return errorOut.sqrLength();
	}
	
	public void learn(float alpha, NNVector input, NNVector previous_output, NNVector output) {
		final var in_z = NNVector.concat(input, previous_output);
		final var z = new NNVector(size);
		weight_z.mult(up(in_z, 1), z);
		sigm(z);

		final var in_r = in_z;
		final var r = new NNVector(size);
		weight_r.mult(up(in_r, 1), z);
		sigm(r);

		final var rh = new NNVector(size);
		r.mul(previous_output, rh);
		final var in_h = NNVector.concat(input, rh);

		final var h = new NNVector(size);
		weight_h.mult(up(in_h, 1), h);
		tanh(h);

		final var out = new NNVector(size);
		final var int_z_previous_output= new NNVector(size);
		
		h.mul(z, out);
		z.inverse(int_z_previous_output);
		int_z_previous_output.mul(previous_output);
		out.add(int_z_previous_output, out);
		
		final var errorOut = new NNVector(size);
		output.sub(out, errorOut);
		errorOut.mul(alpha);

		final var error_z = new NNVector(size);
		final var error_r = new NNVector(size);
		final var error_h = new NNVector(size);

		dir_sigm(r, error_z);
		dir_sigm(z, error_r);
		dir_tanh(h, error_h);
		
		error_z.mul(errorOut.mul(-1));
		error_h.mul(errorOut.mul(-1));
		
		final var t = new NNVector(2*size+1);
		weight_h.transposeMult(h, t);
		for(int i = 0; i < error_r.size(); i++)
			error_r.set(i, error_r.get(i) * t.get(i + size));
		
		weight_r.addMultMatrix(up(in_r, 1), error_r);
		weight_z.addMultMatrix(up(in_z, 1), error_z);
		weight_h.addMultMatrix(up(in_h, 1), error_h);
		
//		System.out.println(output);
//		System.out.println(out);
//    	System.out.println(weight_z);
//    	System.out.println(weight_h);
//    	System.out.println(weight_r);
//    	System.out.println();
	}
	
	private void reset() {
		weight_z.reset();
		weight_r.reset();
		weight_h.reset();
	}

	public void test(NNVector input, NNVector previous_output, NNVector output) {
		final var in_z = NNVector.concat(input, previous_output);
		final var z = new NNVector(size);
		weight_z.mult(up(in_z, 1), z);
		sigm(z);

		final var in_r = in_z;
		final var r = new NNVector(size);
		weight_r.mult(up(in_r, 1), z);
		sigm(r);

		final var rh = new NNVector(size);
		r.mul(previous_output, rh);
		final var in_h = NNVector.concat(input, rh);

		final var h = new NNVector(size);
		weight_h.mult(up(in_h, 1), h);
		tanh(h);

		final var int_z_previous_output= new NNVector(size);
		
		h.mul(z, output);
		z.inverse(int_z_previous_output);
		int_z_previous_output.mul(previous_output);
		output.add(int_z_previous_output, output);
	}
	
	private NNVector up(NNVector vector) {
		return up(vector, 1);
	}

	private NNVector up(NNVector input, float x) {
		final var result = new NNVector(input.size() + 1);
		for(int i = 0; i < input.size(); i++)
			result.set(i, input.get(i));
		result.set(input.size(), x);
		return result;
	}
	private NNVector down(NNVector input) {
		final var result = new NNVector(input.size() - 1);
		for(int i = 0; i < input.size() - 1; i++)
			result.set(i, input.get(i));
		return result;
	}
	public TestContext context() {
		return context(new NNVector(size()));
	}
	
	public TestContext context(NNVector context) {
		return new TestContext(context);
	}
	
	public final class TestContext {
		
		private final NNVector context = new NNVector(size());
		
		public TestContext(NNVector context) {
			this.context.set(context);
		}
		
		public NNVector test(NNVector input) {
			final var out = new NNVector(size());
			test(input, out);
			return out;
		}
		
		public void test(NNVector input, NNVector output) {
			GRUNeuralNetwork.this.test(input, context, output);
			context.set(output);
		}
		
		public void clear() {
			context.clear();
		}
		
		public float get_error(NNVector input, NNVector output) {
			final var error = GRUNeuralNetwork.this.get_error(input, context, output);
			context.set(output);
			return error;
		}
		
		public void learn(float alpha, NNVector input, NNVector output) {
			GRUNeuralNetwork.this.learn(alpha, input, context, output);
			context.set(output);
		}
		
	}
	
}
