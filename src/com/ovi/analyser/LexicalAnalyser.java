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
	private Token crtTk;
	private Token consumedTk;

	public static void main(String[] args) throws IOException {
		LexicalAnalyser analyser = new LexicalAnalyser();
		analyser.run();
	}

	private void run() throws IOException {
		readFile();
		// linesOfCode = new ArrayList<String>(textLines);
		formatTextToCode();
		//System.out.println(code);
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

//			try {
//				System.out.println("#" + state + " " + code.charAt(index) + " (" + (int) code.charAt(index) + "):"
//						+ code.substring(index));
//			} catch (Exception e) {
//				System.err.println(tokens);
//			}

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
				} else if ((code.charAt(index) == '\n') || (code.charAt(index) == '\r') || (code.charAt(index) == '\t')
						|| (code.charAt(index) == ' ')) {
					index++;
					state = 0;
				} else {
					err("invalid character");
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
				} else {
					err("invalid character");
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
				} else {
					err("invalid character");
				}
				break;
			case 8:
				if ("0123456789".contains(code.charAt(index) + "")) {
					index++;
					state = 11;
				} else if ("+-".contains(code.charAt(index) + "")) {
					index++;
					state = 12;
				} else {
					err("invalid character");
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
				RealToken tkReal = new RealToken(AL.CT_REAL, lineNumber, Double.parseDouble(valueReal));
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
				} else {
					err("invalid character");
				}

				break;
			case 13:
				if (!"\'\\\\".contains(code.charAt(index) + "")) {
					index++;
					state = 14;
				} else if ("\\\\".contains(code.charAt(index) + "")) {
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
				break;
			case 15:
				if ("abfnrtv\'?\"\\\0".contains(code.charAt(index) + "")) {
					index++;
					state = 16;
				} else {
					err("invalid character");
				}
				break;
			case 16:
				if (code.charAt(index) == '\'') {
					index++;
					state = 17;
				} else {
					err("invalid character");
				}
				break;
			case 17:
				String valueChar = code.substring(startIndex, index);
				StringToken tkChar = new StringToken(AL.CT_CHAR, lineNumber, valueChar);
				tokens.add(tkChar);
				state = 0;
				break;
			case 18:
				if (code.charAt(index) == '\"') {
					index++;
					state = 22;
				} else if ("\\\\".contains(code.charAt(index) + "")) {
					index++;
					state = 20;
				} else if (!"\"\\\\".contains(code.charAt(index) + "")) {
					index++;
					state = 19;
				} else {
					err("invalid character");
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
				if ("abfnrtv\'?\"\\\0".contains(code.charAt(index) + "")) {
					index++;
					state = 21;
				} else {
					err("invalid character");
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
				StringToken tkString = new StringToken(AL.CT_STRING, lineNumber, valueString);
				tokens.add(tkString);
				state = 0;
				break;
			case 23:
				if (code.charAt(index) == '/') {
					index++;
					state = 24;
				} else if (code.charAt(index) == '*') {
					index++;
					state = 47;
				} else {
					state = 26;
				}
				break;
			case 24:
				if (index < code.length()) { // FIXME if suspect
					if ((code.charAt(index) != '\n') && (code.charAt(index) != '\r') && (code.charAt(index) != '\t')) {
						index++;
						state = 24;
					} else {
						index++;
						state = 0;
					}
				} else {
					err("invalid character");
				}
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
				} else {
					err("invalid character");
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
			case 47:
				if (code.charAt(index) == '*') {
					index++;
					state = 48;
				} else {
					index++;
					state = 49;
				}
				break;
			case 48:
				if (code.charAt(index) == '*') {
					index++;
					state = 48;
				} else if ((code.charAt(index) != '*') || (code.charAt(index) != '/')) {
					index++;
					state = 50;
				} else {
					err("invalid character");
				}
				break;
			case 49:
				if (code.charAt(index) == '*') {
					index++;
					state = 51;
				} else {
					index++;
					state = 49;
				}
				break;
			case 50:
				if (code.charAt(index) == '*') {
					index++;
					state = 51;
				} else {
					err("invalid character");
				}
				break;
			case 51:

				if (code.charAt(index) == '*') {
					index++;
					state = 51;
				} else if (code.charAt(index) == '/') {
					index++;
					state = 0;
				} else {
					index++;
					state = 47;
				}
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
				break;
			case 62:
				Token tkID;
				String valueID = code.substring(startIndex, index);
				if (valueID.equals("struct")) {
					tkID = new Token(AL.STRUCT, lineNumber);
				} else if (valueID.equals("break")) {
					tkID = new Token(AL.BREAK, lineNumber);
				} else if (valueID.equals("char")) {
					tkID = new Token(AL.CHAR, lineNumber);
				} else if (valueID.equals("double")) {
					tkID = new Token(AL.DOUBLE, lineNumber);
				} else if (valueID.equals("else")) {
					tkID = new Token(AL.ELSE, lineNumber);
				} else if (valueID.equals("for")) {
					tkID = new Token(AL.FOR, lineNumber);
				} else if (valueID.equals("if")) {
					tkID = new Token(AL.IF, lineNumber);
				} else if (valueID.equals("int")) {
					tkID = new Token(AL.INT, lineNumber);
				} else if (valueID.equals("struct")) {
					tkID = new Token(AL.STRUCT, lineNumber);
				} else if (valueID.equals("void")) {
					tkID = new Token(AL.VOID, lineNumber);
				} else if (valueID.equals("whle")) {
					tkID = new Token(AL.WHILE, lineNumber);
				} else {
					tkID = new StringToken(AL.ID, lineNumber, valueID);
				}
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

	private boolean consume(AL code) {
		if (crtTk.getCode().equals(code)) {
			consumedTk = crtTk;
			crtTk = tokens.get(tokens.indexOf(crtTk));
			return true;
		}
		return false;
	}

	boolean typeName() {
		if (!typeBase()) {
			return false;
		}
		arrayDecl();
		return true;
	}

	private boolean arrayDecl() {
		Token startToken = crtTk;
		if (!consume(AL.LBRACKET)) {
			return false;
		}
		expr();
		if (consume(AL.RBRACKET)) {
			return true;
		}

		return false;
	}

	private boolean expr() {
		if (exprAssign()) {
			return true;
		}
		return false;
	}

	private boolean exprAssign() {
		if (exprUnary()) {
			if (consume(AL.ASSIGN)) {
				if (exprAssign()) {
					return true;
				}
				if (exprOr()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean exprOr() {
		if (exprOr()) {
			if (consume(AL.OR)) {
				if (exprAnd()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean exprAnd() {
		return false;
	}

	private boolean exprUnary() {
		return false;
	}

	private boolean typeBase() {
		Token startToken = crtTk;
		if (consume(AL.INT)) {
			return true;
		}
		if (consume(AL.DOUBLE)) {
			return true;
		}
		if (consume(AL.CHAR)) {
			return true;
		}
		if (consume(AL.STRUCT)) {
			if (consume(AL.ID)) {
				return true;
			}
		}
		return false;
	}

	private void formatTextToCode() {
		for (String textLine : textLines) {
			code += (textLine + '\n');
		}
	}

	private void readFile() throws FileNotFoundException, IOException {
		File file = new File("input.c");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			textLines.add(line);
		}
		br.close();
	}

	private void err(String message) {
		System.err.println(message);
		System.exit(0);
	}

	private void tkerr(String message, Token token) {
		System.err.println("Error in line " + token.getLine() + ":" + message);
		System.exit(1);
	}

}
