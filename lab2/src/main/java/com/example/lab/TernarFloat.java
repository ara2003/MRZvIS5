package com.example.lab;

import java.util.Random;

public enum TernarFloat{
	
	P_ONE(){
		
		@Override
		public TernarFloat mult(TernarFloat other) {
			return switch(other) {
				case P_ONE -> P_ONE;
				case N_ONE -> N_ONE;
				case ZERO -> ZERO;
			};
		}
		
		@Override
		public TernarFloat negative() {
			return P_ONE;
		}
		
		@Override
		public float toFloat() {
			return 1;
		}
	},
	N_ONE(){
		
		@Override
		public TernarFloat mult(TernarFloat other) {
			return switch(other) {
				case P_ONE -> N_ONE;
				case N_ONE -> P_ONE;
				case ZERO -> ZERO;
			};
		}
		
		@Override
		public TernarFloat negative() {
			return P_ONE;
		}
		
		@Override
		public float toFloat() {
			return -1;
		}
	},
	ZERO(){
		
		@Override
		public TernarFloat mult(TernarFloat other) {
			return switch(other) {
				case P_ONE -> ZERO;
				case N_ONE -> ZERO;
				case ZERO -> ZERO;
			};
		}
		
		@Override
		public TernarFloat negative() {
			return ZERO;
		}
		
		@Override
		public float toFloat() {
			return 0;
		}
	},;
	
	public abstract TernarFloat mult(TernarFloat other);
	public abstract TernarFloat negative();
	
	private static final Random rand = new Random();
	
	public static TernarFloat randomONE() {
		return rand.nextBoolean() ? P_ONE : N_ONE;
	}
	
	public abstract float toFloat();
	
	
}
