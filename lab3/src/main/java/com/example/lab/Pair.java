package com.example.lab;


public record Pair<T1, T2>(T1 first, T2 second) {
	
	@Override
	public String toString() {
		return "Pair [" + first + ", " + second + "]";
	}
	
}
