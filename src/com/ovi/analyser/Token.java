package com.ovi.analyser;

public class Token {
	private AL code;
	private int line;

	public Token(AL code, int line) {
		this.line = line;
		this.setCode(code);
	}

	public AL getCode() {
		return code;
	}

	public void setCode(AL code) {
		this.code = code;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return code.toString();
	}
}
