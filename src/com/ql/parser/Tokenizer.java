package com.ql.parser;

import java.util.LinkedList;

public class Tokenizer {

	public static void main(String[] args) {
		LinkedList<Token> tokens = getTokens("(\"bruno\" AND r & (\"marcellus\" OR \"murilo\")) AND \"eduardo\"");

		System.out.println(tokens);
	}

	public static LinkedList<Token> getTokens(String input) {
		char[] chars = input.toCharArray();
		
		LinkedList<Token> tokens = new LinkedList<Token>();
		
		String value = "";

		for (int i = 0; i < chars.length; i++) {
			
			switch (chars[i]) {
				case ' ':
					break;
				case Punctuation.L_PARENT:
					tokens.add(
						new Token(TokenType.L_PAREN, String.valueOf(chars[i]))
					);
					
					value = "";

					break;
				case Punctuation.R_PARENT:
					tokens.add(
						new Token(TokenType.R_PARENT, String.valueOf(chars[i]))
					);

					value = "";

					break;
				case Punctuation.QUOTE:
					while (++i < chars.length) {
						if (chars[i] == Punctuation.QUOTE) {
							tokens.add(
								new Token(TokenType.STRING, value)
							);

							break;
						}

						value += chars[i];
					}

					value = "";
					
					break;
				case 'A':
				case 'D':
				case 'O':
				case 'N':
				case 'R':
					value += chars[i];

					if (lookupOperator(value)) {
						tokens.add(
							new Token(TokenType.OPERATOR, value)
						);

						value = "";
					}
					
					break;
				default:
					tokens.add(
						new Token(TokenType.UNKOWN, String.valueOf(chars[i]))
					);

					value = "";

					break;
			}
		}
		
		return tokens;
	}
	
	private static boolean lookupOperator(String value) {
		return value.equals(ReservedWords.AND) || value.equals(ReservedWords.OR);
	}

}
