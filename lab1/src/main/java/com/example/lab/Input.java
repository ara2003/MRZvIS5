package com.example.lab;

import java.util.Arrays;
import java.util.Scanner;

public class Input {

	private static final Scanner sc = new Scanner(System.in);

	private Input() {
	}

	public static String input(String name, String[] values) {
		System.out.print(name + ": ");
		final var res = sc.nextLine();
		if(contains(res, values))
			return res;
		if(res.isBlank())
			return res;
		System.err.println("not correct");
		return input(name, values);
	}

	public static String input(String name, String def, String[] values) {
		if(contains(def, values))
			throw new IllegalArgumentException(Arrays.toString(values) +" not contains "+ def);
		final var v = input(name, values);
		if(v.isBlank())
			return def;
		return v;
	}
	
	@SafeVarargs
	private static <T> boolean contains(T value, T...values) {
		for(var v : values)
			if(value.equals(v))
				return true;
		return false;
	}

	public static String input(String name) {
		System.out.print(name + ": ");
		return sc.nextLine();
	}

	public static String input(String name, String def) {
		final var v = input(name);
		if(v.isBlank())
			return def;
		return v;
	}

	public static float inputFloat(String name) {
		try {
			return Float.parseFloat(input(name));
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return inputFloat(name);
		}
	}

	public static float inputFloat(String name, float def) {
		final var v = input(name);
		if(v.isBlank())
			return def;
		try {
			return Float.parseFloat(v);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return inputFloat(name, def);
		}
	}

	public static int inputInt(String name) {
		try {
			return Integer.parseInt(input(name));
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return inputInt(name);
		}
	}

	public static int inputInt(String name, int def) {
		final var v = input(name);
		if(v.isBlank())
			return def;
		try {
			return Integer.parseInt(v);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return inputInt(name, def);
		}
	}


}
