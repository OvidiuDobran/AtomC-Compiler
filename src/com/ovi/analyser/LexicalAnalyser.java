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
	List<String> linesOfCode;
	List<Token> tokens = new ArrayList<Token>();
	private int currentIndex;

	public static void main(String[] args) throws IOException {
		LexicalAnalyser analyser = new LexicalAnalyser();
		analyser.run();
	}

	private void run() throws IOException {
		readFile();
		linesOfCode = new ArrayList<String>(textLines);
		removeEmptyLines();
		printCodeLines();
		getTokens();
		System.out.println(tokens);
	}

	private void getTokens() {
		for (int i = 0; i < linesOfCode.size(); i++) {
			getTokensFromLine(i);
		}
	}

	private void getTokensFromLine(int lineIndex) {
		int state = 0;
		String line = linesOfCode.get(lineIndex);
		int index = 0;
		int startIndex = 0;
		while (index < line.length()) {
			switch (state) {
			case 0:
				startIndex = index;
				if (line.charAt(index) == '0') {
					index++;
					state = 1;
				} else if ("123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 3;
				} else if (line.charAt(index) == '\'') {
					index++;
					state = 13;
				} else if (line.charAt(index) == '\"') {
					index++;
					state = 18;
				} else if (line.charAt(index) == '/') {
					index++;
					state = 23;
				} else if (line.charAt(index) == '+') {
					index++;
					state = 27;
				} else if (line.charAt(index) == '-') {
					index++;
					state = 28;
				} else if (line.charAt(index) == '*') {
					index++;
					state = 29;
				} else if (line.charAt(index) == '.') {
					index++;
					state = 30;
				} else if (line.charAt(index) == '&') {
					index++;
					state = 32;
				} else if (line.charAt(index) == '|') {
					index++;
					state = 33;
				} else if (line.charAt(index) == '!') {
					index++;
					state = 35;
				} else if (line.charAt(index) == '=') {
					index++;
					state = 38;
				} else if (line.charAt(index) == '<') {
					index++;
					state = 41;
				} else if (line.charAt(index) == '>') {
					index++;
					state = 44;
				} else if (line.charAt(index) == ';') {
					index++;
					state = 54;
				} else if (line.charAt(index) == '(') {
					index++;
					state = 55;
				} else if (line.charAt(index) == ')') {
					index++;
					state = 56;
				} else if (line.charAt(index) == '[') {
					index++;
					state = 57;
				} else if (line.charAt(index) == ']') {
					index++;
					state = 58;
				} else if (line.charAt(index) == '{') {
					index++;
					state = 59;
				} else if (line.charAt(index) == '}') {
					index++;
					state = 60;
				} else if (Character.isLetter(line.charAt(index)) || (line.charAt(index) == '_')) {
					index++;
					state = 61;
				}
				break;
			case 1:
				if (line.charAt(index) == 'x') {
					index++;
					state = 4;
				} else {// FIXME There may be an escape char here. Not sure.
					index++;
					state = 2;
					;
				}
				break;
			case 2:
				if ("01234567".contains(line.charAt(index) + "")) {
					index++;
					state = 2;
				} else if ("89".contains(line.charAt(index) + "")) {
					index++;
					state = 66;
				} else if (line.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(line.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 6;
				}
				break;
			case 3:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 3;
				} else if (line.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(line.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 6;
				}
				break;
			case 4:
				if (Character.isLetter(line.indexOf(index)) || Character.isDigit(line.indexOf(index))) {
					index++;
					state = 5;
				}
				break;
			case 5:
				if (Character.isLetter(line.indexOf(index)) || Character.isDigit(line.indexOf(index))) {
					index++;
					state = 5;
				} else {
					state = 6;
				}
				break;
			case 6:// CT_INT
				String valueInt = line.substring(startIndex, index);
				IntToken tkInt = new IntToken(AL.CT_INT, lineIndex, Integer.parseInt(valueInt));
				tokens.add(tkInt);
				state = 0;
				break;
			case 7:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 9;
				}
				break;
			case 8:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 11;
				} else if ("+-".contains(line.charAt(index) + "")) {
					index++;
					state = 12;
				}
				break;
			case 9:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 9;
				} else if ("eE".contains(line.charAt(index) + "")) {
					index++;
					state = 8;
				} else {
					state = 10;
				}
				break;
			case 10:
				String valueReal = line.substring(startIndex, index);
				RealToken tkReal = new RealToken(AL.CT_REAL, lineIndex, Integer.parseInt(valueReal));
				tokens.add(tkReal);
				state = 0;
				break;
			case 11:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 11;
				} else {
					state = 10;
				}
				break;
			case 12:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 11;
				}

				break;
			case 13:
				if (!"\\\\'".contains(line.charAt(index) + "")) {
					index++;
					state = 14;
				} else if (!"\\\\".contains(line.charAt(index) + "")) {
					index++;
					state = 15;
				} else {
					index++;
					state = 53;
				}
				break;
			case 14:
				if (line.charAt(index) == '\'') {
					index++;
					state = 17;
				}
			case 15:
				if (!"abfnrtv'?\"\\\\0".contains(line.charAt(index) + "")) {
					index++;
					state = 14;
				}
				break;
			case 16:
				if (line.charAt(index) == '\'') {
					index++;
					state = 17;
				}
				break;
			case 17:
				String valueChar = line.substring(startIndex, index);
				RealToken tkChar = new RealToken(AL.CT_CHAR, lineIndex, Integer.parseInt(valueChar));
				tokens.add(tkChar);
				state = 0;
				break;
			case 18:
				if (line.charAt(index) == '"') {
					index++;
					state = 22;
				} else if ("\\\\".contains(line.charAt(index) + "")) {
					index++;
					state = 20;
				} else if (!"\"\\".contains(line.charAt(index) + "")) {
					index++;
					state = 19;
				}
				break;
			case 19:
				if (line.charAt(index) == '"') {
					index++;
					state = 22;
				} else {
					state = 18;
				}
				break;
			case 20:
				if (!"abfnrtv'?\"\\\\0".contains(line.charAt(index) + "")) {
					index++;
					state = 21;
				}
				break;
			case 21:
				if (line.charAt(index) == '"') {
					index++;
					state = 22;
				} else {
					state = 18;
				}
				break;
			case 22:
				String valueString = line.substring(startIndex, index);
				RealToken tkString = new RealToken(AL.CT_STRING, lineIndex, Integer.parseInt(valueString));
				tokens.add(tkString);
				state = 0;
				break;
			case 23:
				state = 26;
				break;
			case 26:
				Token tkDiv = new Token(AL.DIV, lineIndex);
				tokens.add(tkDiv);
				state = 0;
				break;
			case 27:
				Token tkAdd = new Token(AL.ADD, lineIndex);
				tokens.add(tkAdd);
				state = 0;
				break;
			case 28:
				Token tkSub = new Token(AL.SUB, lineIndex);
				tokens.add(tkSub);
				state = 0;
				break;
			case 29:
				Token tkMul = new Token(AL.MUL, lineIndex);
				tokens.add(tkMul);
				state = 0;
				break;
			case 30:
				Token tkDot = new Token(AL.DOT, lineIndex);
				tokens.add(tkDot);
				state = 0;
				break;
			case 31:
				Token tkAnd = new Token(AL.AND, lineIndex);
				tokens.add(tkAnd);
				state = 0;
				break;
			case 32:
				if (line.charAt(index) == '&') {
					index++;
					state = 31;
				}
				break;
			case 33:
				if (line.charAt(index) == '|') {
					index++;
					state = 34;
				}
				break;
			case 34:
				Token tkOr = new Token(AL.OR, lineIndex);
				tokens.add(tkOr);
				state = 0;
				break;
			case 35:
				if (line.charAt(index) == '=') {
					index++;
					state = 37;
				} else {
					index++;
					state = 36;
				}
				break;
			case 36:
				Token tkNot = new Token(AL.NOT, lineIndex);
				tokens.add(tkNot);
				state = 0;
				break;
			case 37:
				Token tkNotEq = new Token(AL.NOTEQ, lineIndex);
				tokens.add(tkNotEq);
				state = 0;
				break;
			case 38:
				if (line.charAt(index) == '=') {
					index++;
					state = 40;
				} else {
					index++;
					state = 39;
				}
				break;
			case 39:
				Token tkAssign = new Token(AL.ASSIGN, lineIndex);
				tokens.add(tkAssign);
				state = 0;
				break;
			case 40:
				Token tkEq = new Token(AL.EQUAL, lineIndex);
				tokens.add(tkEq);
				state = 0;
				break;
			case 41:
				if (line.charAt(index) == '=') {
					index++;
					state = 43;
				} else {
					index++;
					state = 42;
				}
				break;
			case 42:
				Token tkLess = new Token(AL.LESS, lineIndex);
				tokens.add(tkLess);
				state = 0;
				break;
			case 43:
				Token tkLEq = new Token(AL.LESSEQ, lineIndex);
				tokens.add(tkLEq);
				state = 0;
				break;
			case 44:
				if (line.charAt(index) == '=') {
					index++;
					state = 46;
				} else {
					index++;
					state = 45;
				}
				break;
			case 45:
				Token tkGre = new Token(AL.GREATER, lineIndex);
				tokens.add(tkGre);
				state = 0;
				break;
			case 46:
				Token tkGEq = new Token(AL.GREATEREQ, lineIndex);
				tokens.add(tkGEq);
				state = 0;
				break;
			case 53:
				Token tkComma = new Token(AL.COMMA, lineIndex);
				tokens.add(tkComma);
				state = 0;
				break;
			case 54:
				Token tkSemic = new Token(AL.SEMICOLON, lineIndex);
				tokens.add(tkSemic);
				state = 0;
				break;
			case 55:
				Token tkLPar = new Token(AL.LPAR, lineIndex);
				tokens.add(tkLPar);
				state = 0;
				break;
			case 56:
				Token tkRPar = new Token(AL.RPAR, lineIndex);
				tokens.add(tkRPar);
				state = 0;
				break;
			case 57:
				Token tkLBra = new Token(AL.LBRACKET, lineIndex);
				tokens.add(tkLBra);
				state = 0;
				break;
			case 58:
				Token tkRBra = new Token(AL.RBRACKET, lineIndex);
				tokens.add(tkRBra);
				state = 0;
				break;
			case 59:
				Token tkLAcc = new Token(AL.LACC, lineIndex);
				tokens.add(tkLAcc);
				state = 0;
				break;
			case 60:
				Token tkRAcc = new Token(AL.RACC, lineIndex);
				tokens.add(tkRAcc);
				state = 0;
				break;
			case 61:
				if(Character.isLetter(line.charAt(index))||Character.isDigit(line.charAt(index))||line.charAt(index)=='_') {
					index++;
					state=61;
				}else {
					index++;
					state=62;
				}
			case 62:
				String valueID=line.substring(startIndex,index);
				StringToken tkID = new StringToken(AL.ID, lineIndex, valueID);
				tokens.add(tkID);
				state = 0;
				break;
			case 66:
				if ("0123456789".contains(line.charAt(index) + "")) {
					index++;
					state = 66;
				} else if (line.charAt(index) == '.') {
					index++;
					state = 7;
				} else if ("eE".contains(line.charAt(index) + "")) {
					index++;
					state = 8;
				}
				break;
			}
		}
	}

	private void printCodeLines() {
		for (String line : linesOfCode) {
			System.out.println(line);
		}
	}

	private void removeEmptyLines() {
		for (int i = 0; i < linesOfCode.size();) {
			if (linesOfCode.get(i).length() == 0) {
				linesOfCode.remove(i);
			} else {
				i++;
			}
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
