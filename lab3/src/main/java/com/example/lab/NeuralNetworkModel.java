package com.example.lab;

import static com.example.lab.Input.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
		return "res/nn/" + sizeInput() + " " + sizeHidden() + " " + sizeOutput();
	}
	
}
