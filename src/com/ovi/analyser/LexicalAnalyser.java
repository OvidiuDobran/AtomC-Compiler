package com.ovi.analyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyser {

	List<String> textLines = new ArrayList<String>();
	// List<String> linesOfCode;
	String code = "";
	List<Token> tokens = new ArrayList<Token>();
	private int currentIndex;

	public static void main(String[] args) throws IOException {
		LexicalAnalyser analyser = new LexicalAnalyser();
		analyser.run();
	}

	private void run() throws IOException {
		readFile();
		// linesOfCode = new ArrayList<String>(textLines);
		formatTextToCode();
		System.out.println(code);
		getTokens();
		System.out.println(tokens);
	}

	private void getTokens() {
		int state = 0;
		int index = 0;
		int startIndex = 0;
		int lineNumber = 1;
		while (index < code.length()) {
			if (code.charAt(index) == '\n') {
				lineNumber++;
				index++;
			}
			switch (state) {
			case 0:
				startIndex = index;
				if (code.charAt(index) == '0') {
					index++;
					state = 1;
				} else if ("123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 3;
				} else if (code.charAt(index) == '\'') {
					index++;
					state = 13;
				} else if (code.charAt(index) == '\"') {
					index++;
					state = 18;
				} else if (code.charAt(index) == '/') {
					index++;
					state = 23;
				} else if (code.charAt(index) == '+') {
					index++;
					state = 27;
				} else if (code.charAt(index) == '-') {
					index++;
					state = 28;
				} else if (code.charAt(index) == '*') {
					index++;
					state = 29;
				} else if (code.charAt(index) == '.') {
					index++;
					state = 30;
				} else if (code.charAt(index) == '&') {
					index++;
					state = 32;
				} else if (code.charAt(index) == '|') {
					index++;
					state = 33;
				} else if (code.charAt(index) == '!') {
					index++;
					state = 35;
				} else if (code.charAt(index) == '=') {
					index++;
					state = 38;
				} else if (code.charAt(index) == '<') {
					index++;
					state = 41;
				} else if (code.charAt(index) == '>') {
					index++;
					state = 44;
				} else if (code.charAt(index) == ';') {
					index++;
					state = 54;
				} else if (code.charAt(index) == '(') {
					index++;
					state = 55;
				} else if (code.charAt(index) == ')') {
					index++;
					state = 56;
				} else if (code.charAt(index) == '[') {
					index++;
					state = 57;
				} else if (code.charAt(index) == ']') {
					index++;
					state = 58;
				} else if (code.charAt(index) == '{') {
					index++;
					state = 59;
				} else if (code.charAt(index) == '}') {
					index++;
					state = 60;
				} else if (Character.isLetter(code.charAt(index)) || (code.charAt(index) == '_')) {
					index++;
					state = 61;
				}
				break;
			case 1:
				if (code.charAt(index) == 'x') {
					index++;
					state = 4;
				} else {// FIXME There may be an escape char here. Not sure.
					index++;
					state = 2;
				}
				break;
			case 2:
				if ("01234567".contains(code.charAt(index) + "")) {
					index++;
					state = 2;
				} else if ("89".contains(code.charAt(index) + "")) {
					index++;
					state = 66;
				} else if (code.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(code.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 6;
				}
				break;
			case 3:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 3;
				} else if (code.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(code.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 6;
				}
				break;
			case 4:
				if (Character.isLetter(code.charAt(index)) || Character.isDigit(code.charAt(index))) {
					index++;
					state = 5;
				}
				break;
			case 5:
				if (Character.isLetter(code.charAt(index)) || Character.isDigit(code.charAt(index))) {
					index++;
					state = 5;
				} else {
					state = 6;
				}
				break;
			case 6:// CT_INT
				String valueInt = code.substring(startIndex, index);
				IntToken tkInt;
				if (valueInt.contains("0x")) {
					int hexVal = Integer.parseInt(valueInt.substring(2), 16);
					tkInt = new IntToken(AL.CT_INT, lineNumber, hexVal);
				} else if (valueInt.startsWith("0")) {
					int octVal = Integer.parseInt(valueInt.substring(1), 8);
					tkInt = new IntToken(AL.CT_INT, lineNumber, octVal);
				} else {
					tkInt = new IntToken(AL.CT_INT, lineNumber, Integer.parseInt(valueInt));
				}
				tokens.add(tkInt);
				state = 0;
				break;
			case 7:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 9;
				}
				break;
			case 8:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 11;
				} else if ("+-".contains(code.charAt(index) + "")) {
					index++;
					state = 12;
				}
				break;
			case 9:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 9;
				} else if ("eE".contains(code.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 10;
				}
				break;
			case 10:
				String valueReal = code.substring(startIndex, index);
				RealToken tkReal = new RealToken(AL.CT_REAL, lineNumber, Integer.parseInt(valueReal));
				tokens.add(tkReal);
				state = 0;
				break;
			case 11:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 11;
				} else {
					state = 10;
				}
				break;
			case 12:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 11;
				}

				break;
			case 13:
				if (!"\\\\'".contains(code.charAt(index) + "")) {
					index++;
					state = 14;
				} else if (!"\\\\".contains(code.charAt(index) + "")) {
					index++;
					state = 15;
				} else {
					index++;
					state = 53;
				}
				break;
			case 14:
				if (code.charAt(index) == '\'') {
					index++;
					state = 17;
				}
			case 15:
				if (!"abfnrtv'?\"\\\\0".contains(code.charAt(index) + "")) {
					index++;
					state = 14;
				}
				break;
			case 16:
				if (code.charAt(index) == '\'') {
					index++;
					state = 17;
				}
				break;
			case 17:
				String valueChar = code.substring(startIndex, index);
				RealToken tkChar = new RealToken(AL.CT_CHAR, lineNumber, Integer.parseInt(valueChar));
				tokens.add(tkChar);
				state = 0;
				break;
			case 18:
				if (code.charAt(index) == '"') {
					index++;
					state = 22;
				} else if ("\\\\".contains(code.charAt(index) + "")) {
					index++;
					state = 20;
				} else if (!"\"\\".contains(code.charAt(index) + "")) {
					index++;
					state = 19;
				}
				break;
			case 19:
				if (code.charAt(index) == '"') {
					index++;
					state = 22;
				} else {
					state = 18;
				}
				break;
			case 20:
				if (!"abfnrtv'?\"\\\\0".contains(code.charAt(index) + "")) {
					index++;
					state = 21;
				}
				break;
			case 21:
				if (code.charAt(index) == '"') {
					index++;
					state = 22;
				} else {
					state = 18;
				}
				break;
			case 22:
				String valueString = code.substring(startIndex, index);
				RealToken tkString = new RealToken(AL.CT_STRING, lineNumber, Integer.parseInt(valueString));
				tokens.add(tkString);
				state = 0;
				break;
			case 23:
				state = 26;
				break;
			case 26:
				Token tkDiv = new Token(AL.DIV, lineNumber);
				tokens.add(tkDiv);
				state = 0;
				break;
			case 27:
				Token tkAdd = new Token(AL.ADD, lineNumber);
				tokens.add(tkAdd);
				state = 0;
				break;
			case 28:
				Token tkSub = new Token(AL.SUB, lineNumber);
				tokens.add(tkSub);
				state = 0;
				break;
			case 29:
				Token tkMul = new Token(AL.MUL, lineNumber);
				tokens.add(tkMul);
				state = 0;
				break;
			case 30:
				Token tkDot = new Token(AL.DOT, lineNumber);
				tokens.add(tkDot);
				state = 0;
				break;
			case 31:
				Token tkAnd = new Token(AL.AND, lineNumber);
				tokens.add(tkAnd);
				state = 0;
				break;
			case 32:
				if (code.charAt(index) == '&') {
					index++;
					state = 31;
				}
				break;
			case 33:
				if (code.charAt(index) == '|') {
					index++;
					state = 34;
				}
				break;
			case 34:
				Token tkOr = new Token(AL.OR, lineNumber);
				tokens.add(tkOr);
				state = 0;
				break;
			case 35:
				if (code.charAt(index) == '=') {
					index++;
					state = 37;
				} else {
					index++;
					state = 36;
				}
				break;
			case 36:
				Token tkNot = new Token(AL.NOT, lineNumber);
				tokens.add(tkNot);
				state = 0;
				break;
			case 37:
				Token tkNotEq = new Token(AL.NOTEQ, lineNumber);
				tokens.add(tkNotEq);
				state = 0;
				break;
			case 38:
				if (code.charAt(index) == '=') {
					index++;
					state = 40;
				} else {
					// index++;
					state = 39;
				}
				break;
			case 39:
				Token tkAssign = new Token(AL.ASSIGN, lineNumber);
				tokens.add(tkAssign);
				state = 0;
				break;
			case 40:
				Token tkEq = new Token(AL.EQUAL, lineNumber);
				tokens.add(tkEq);
				state = 0;
				break;
			case 41:
				if (code.charAt(index) == '=') {
					index++;
					state = 43;
				} else {
					index++;
					state = 42;
				}
				break;
			case 42:
				Token tkLess = new Token(AL.LESS, lineNumber);
				tokens.add(tkLess);
				state = 0;
				break;
			case 43:
				Token tkLEq = new Token(AL.LESSEQ, lineNumber);
				tokens.add(tkLEq);
				state = 0;
				break;
			case 44:
				if (code.charAt(index) == '=') {
					index++;
					state = 46;
				} else {
					index++;
					state = 45;
				}
				break;
			case 45:
				Token tkGre = new Token(AL.GREATER, lineNumber);
				tokens.add(tkGre);
				state = 0;
				break;
			case 46:
				Token tkGEq = new Token(AL.GREATEREQ, lineNumber);
				tokens.add(tkGEq);
				state = 0;
				break;
			case 53:
				Token tkComma = new Token(AL.COMMA, lineNumber);
				tokens.add(tkComma);
				state = 0;
				break;
			case 54:
				Token tkSemic = new Token(AL.SEMICOLON, lineNumber);
				tokens.add(tkSemic);
				state = 0;
				break;
			case 55:
				Token tkLPar = new Token(AL.LPAR, lineNumber);
				tokens.add(tkLPar);
				state = 0;
				break;
			case 56:
				Token tkRPar = new Token(AL.RPAR, lineNumber);
				tokens.add(tkRPar);
				state = 0;
				break;
			case 57:
				Token tkLBra = new Token(AL.LBRACKET, lineNumber);
				tokens.add(tkLBra);
				state = 0;
				break;
			case 58:
				Token tkRBra = new Token(AL.RBRACKET, lineNumber);
				tokens.add(tkRBra);
				state = 0;
				break;
			case 59:
				Token tkLAcc = new Token(AL.LACC, lineNumber);
				tokens.add(tkLAcc);
				state = 0;
				break;
			case 60:
				Token tkRAcc = new Token(AL.RACC, lineNumber);
				tokens.add(tkRAcc);
				state = 0;
				break;
			case 61:
				if (Character.isLetter(code.charAt(index)) || Character.isDigit(code.charAt(index))
						|| code.charAt(index) == '_') {
					index++;
					state = 61;
				} else {
					// index++;
					state = 62;
				}
			case 62:
				String valueID = code.substring(startIndex, index);
				StringToken tkID = new StringToken(AL.ID, lineNumber, valueID);
				tokens.add(tkID);
				state = 0;
				break;
			case 66:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 66;
				} else if (code.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(code.charAt(index) + "")) {
					index++;
					state = 8;
				}
				break;
			}
		}
	}

	private void formatTextToCode() {
		for (String textLine : textLines) {
			code += (textLine + '\n');
		}
	}

	private void readFile() throws FileNotFoundException, IOException {
		File file = new File("input.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			textLines.add(line);
		}
		br.close();
	}

}
