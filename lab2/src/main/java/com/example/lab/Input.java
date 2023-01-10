package com.example.lab;

import java.util.Scanner;

public class Input {

	private Input() {
	}
	
	private static final Scanner sc = new Scanner(System.in);
	
	public static String input(String name, String def) {
		final var v = input(name);
		if(v.isBlank())
			return def;
		return v;
	}
	
	public static String input(String name) {
		System.out.print(name + ": ");
		return sc.nextLine();
	}

	public static float inputFloat(String name) {
		return Float.parseFloat(input(name));
	}

	public static int inputInt(String name) {
		return Integer.parseInt(input(name));
	}
	
	public static float inputFloat(String name, Object def) {
		return Float.parseFloat(input(name, def.toString()));
	}

	public static int inputInt(String name, Object def) {
		return Integer.parseInt(input(name, def.toString()));
	}
	
}
