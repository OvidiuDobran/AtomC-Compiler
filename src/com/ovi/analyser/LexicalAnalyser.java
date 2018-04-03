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
