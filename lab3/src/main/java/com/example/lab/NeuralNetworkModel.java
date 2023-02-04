package com.example.lab;

import static com.example.lab.Input.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class NeuralNetworkModel implements AutoCloseable {
	
	private final JordanNeuralNetwork nn;
	
	
	private final int input, hidden, output;
	
	private final List<Float> sequence;
	
	public NeuralNetworkModel(List<Float> sequence, int input, int hidden, int output) {
		this.sequence = sequence;
		this.input = input;
		this.hidden = hidden;
		this.output = output;
		final var file = new File(getFileName());
		
		if(file.exists())
			nn = deSerializetionOrDefault(file, input, hidden, output);
		else
			nn = new JordanNeuralNetwork(input, hidden, output);
	}
	
	private static JordanNeuralNetwork deSerializetion(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		try(final var fin = new FileInputStream(file)) {
			try(final var oin = new ObjectInputStream(fin)) {
				return (JordanNeuralNetwork) oin.readObject();
			}
		}
	}
	
	private static JordanNeuralNetwork deSerializetionOrDefault(File file, int input, int hidden,
			int output) {
		try {
			return deSerializetion(file);
		}catch(Exception e) {
			e.printStackTrace();
			return new JordanNeuralNetwork(input, hidden, output);
		}
	}
	
	private static void serializetion(File file, JordanNeuralNetwork nn)
			throws FileNotFoundException, IOException {
		file.getParentFile().mkdirs();
		file.createNewFile();
		try(final var fout = new FileOutputStream(file)) {
			try(final var oout = new ObjectOutputStream(fout)) {
				oout.writeObject(nn);
			}
		}
	}
	
	@Override
	public void close() throws FileNotFoundException, IOException {
		final var file = new File(getFileName());
		synchronized(nn) {
			serializetion(file, nn);
		}
	}
	
	public int sizeContext() {
		return output;
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
	
	private String getFileName() {
		//		return "res/nn/file1";
		return "res/nn/" + sizeInput() + " " + sizeHidden() + " " + sizeOutput();
	}
	
	public void run() {
		loop :
		while(true) {
			final var c = input("task(LEARN, TEST, SEQUENCE, EXIT)", "L").substring(0, 1)
					.toUpperCase();
			switch(c) {
				case "L" -> run_learn();
				case "T" -> run_test();
				case "S" -> run_sequence();
				case "E" -> {
					break loop;
				}
			}
		}
	}
	
	private void run_sequence() {
		final var s = new NNSequence(nn, sequence);
		final var size = inputInt("size", 10);
		final var sequence = s.sequence(size);
		System.out.println("sequence: " + sequence);
	}
	
	private void run_test() {
		final var input = inputVector("previous values for prediction (length:" + nn.sizeInput() + ")");
		final var init_context = new NNVector(input.get(input.size() - 1));
		final var output = new NNVector(nn.sizeOutput());
		nn.test(input, init_context, output);
		System.out.println("output: " + output.get(0));
	}
	
	private void run_learn() {
		final var s = new NNSequence(nn, sequence);
		final var errorlimit = inputFloat("error limit", 1) * .000001f;
		final var alpha = inputFloat("alpha", .1f) * .01f;
		s.learn(errorlimit, alpha, true);
	}
	
}
