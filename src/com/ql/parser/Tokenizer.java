package com.ql.parser;

import java.util.LinkedList;

import com.ql.parser.exception.SyntaxException;

public class Tokenizer {

	public static LinkedList<Token> getTokens(String input)
		throws SyntaxException {

		char[] chars = input.toCharArray();

		LinkedList<Token> tokens = new LinkedList<Token>();

		String value = "";

		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case ' ':
					break;
				case Punctuation.L_PARENT:
					tokens.add(
						new Token(TokenType.L_PARENT, String.valueOf(chars[i]))
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
					while (i < chars.length) {
						if (++i > (chars.length - 1)) {
							throw new SyntaxException("Unterminated string");
						}
						else if (chars[i] == Punctuation.QUOTE) {
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
				case 'X':
					value += chars[i];

					if (lookupOperator(value)) {
						tokens.add(
							new Token(TokenType.OPERATOR, value)
						);

						value = "";
					}

					break;
				default:
					throw new SyntaxException(
						"Unkown operator \"" + String.valueOf(chars[i]) + "\"");
			}
		}

		return tokens;
	}

	private static boolean lookupOperator(String value) {
		return (value.equals(ReservedWords.AND) ||
				value.equals(ReservedWords.NAND) ||
				value.equals(ReservedWords.OR) ||
				value.equals(ReservedWords.XOR));
	}

}
