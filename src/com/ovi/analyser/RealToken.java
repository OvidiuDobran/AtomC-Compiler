package com.ovi.analyser;

public class RealToken extends Token{
	
	private double value;

	public RealToken(AL code, int line, double value) {
		super(code, line);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return getCode().toString() + ":" + value;
	}

}
