package com.ovi.analyser;


public class IntToken extends Token {

	private int value;
	
	public IntToken(AL code, int line, int value) {
		super(code, line);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
