package com.ovi.analyser;

public class CharToken extends Token{
	private char value;

	public CharToken(AL code, int line, char value) {
		super(code, line);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getCode().toString() + ":" + value;
	}
}
