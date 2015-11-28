package org.nahe.proto.test1;

public class App {
	
	private final static int k = 8;
	private final int d = 8;
	
	public static void main(String[] args) {
		int d = 6, rf = 3;
		printsom(1, 2);
		printsom(d, rf);
		printmult(d, rf);
		printsqrt(d, rf);
		printsom(7, 9);
		printsom(2, 17);
		byte c = 127;
		byte e = 1;
		c = (byte) (c + e);
		System.out.println("byte =" + (c));
	}

	public static void printsom(int a, int b) {
		int som = Calculator.telop(a, b+k);
		System.out.println("som = " + som);
	}
	
	public void printsom1(int a, int b) {
		int som = Calculator.telop(a, b+d);
		System.out.println("som = " + som);
	}
	
	public static void printsqrt(int a, int b) {
		double sqrt = Calculator.sqrt(a, b);
		System.out.println("square root =" + sqrt);
	}

	public static void printmult(int a, int b) {
		int mult = Calculator.multi(a, b);
		System.out.println("multiplication = " + mult);
	}
}