package com.example.lab;


public class ImageUtil {

	public static float[] to_floats(byte[] bytes) {
		final var fs = new float[bytes.length];
		for(int i = 0; i < bytes.length; i++)
			fs[i] = to_float(bytes[i]);
		return fs;
	}
	
	public static float to_float(byte b) {
		float d = Byte.toUnsignedInt(b);
		d += 0.5;
		d /= 255;
		return d;
	}

	public static byte[] to_bytes(float[] floats) {
		final var bs = new byte[floats.length];
		for(int i = 0; i < bs.length; i++)
			bs[i] = to_byte(floats[i]);
		return bs;
	}

	public static byte to_byte(float x) {
		x *= 255f;
		return (byte) x;
	}
	
}
