package com.example.lab;

import static com.example.lab.Input.input;
import static com.example.lab.Input.inputFloat;
import static com.example.lab.Input.inputInt;
import static com.example.lab.Input.inputVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class NeuralNetworkModel implements AutoCloseable {
	
	private final GRUNeuralNetwork nn;
	
	
	private final int size;
	
	private final List<Float> sequence;
	
	public NeuralNetworkModel(List<Float> sequence, int size) {
		this.sequence = sequence;
		this.size = size;
		final var file = new File(getFileName());
		
		if(file.exists())
			nn = deSerializetionOrDefault(file, size);
		else
			nn = new GRUNeuralNetwork(size);
	}
	
	private static GRUNeuralNetwork deSerializetion(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		try(final var fin = new FileInputStream(file)) {
			try(final var oin = new ObjectInputStream(fin)) {
				return (GRUNeuralNetwork) oin.readObject();
			}
		}
	}
	
	private static GRUNeuralNetwork deSerializetionOrDefault(File file, int size) {
		try {
			return deSerializetion(file);
		}catch(Exception e) {
			e.printStackTrace();
			return new GRUNeuralNetwork(size);
		}
	}
	
	private static void serializetion(File file, GRUNeuralNetwork nn)
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
	
	public int size() {
		return size;
	}
	
	private String getFileName() {
		//		return "res/nn/file1";
		return "res/nn/" + size;
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
		final var input = inputVector("previous values for prediction (length:" + nn.size() + ")");
		final var init_context = new NNVector(input);
		final var output = new NNVector(nn.size());
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
