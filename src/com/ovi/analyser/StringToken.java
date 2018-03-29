package com.ovi.analyser;

public class StringToken extends Token{

	private String value;
	
	public StringToken(AL code, int line, String value) {
		super(code, line);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
