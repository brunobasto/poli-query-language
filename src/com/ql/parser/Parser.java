package com.ql.parser;

import java.util.ArrayList;
import java.util.LinkedList;

public class Parser {
	
	public static void main(String[] args) {
		new Parser("\"bruno\" OR (\"murilo\" AND \"junior\")");
	}

	public Parser(String query) {
		ArrayList<String> words = new ArrayList<String>();
		words.add("bruno");
		words.add("eduardo");
		words.add("carlos eduardo junior");
		words.add("joao");
		words.add("marcellus");
		words.add("antonio");
		words.add("murilo junior");
		words.add("thiago");

		System.out.println(filter(words, query));
	}
	
	public ArrayList<String> filter(ArrayList<String> words, String query) {
		_tokens = Tokenizer.getTokens(query);

		LinkedList<Token> rpnTokens = toReversePolishNotation();

		System.out.println(rpnTokens);

		LinkedList<ArrayList<String>> result =
			new LinkedList<ArrayList<String>>();

		while (rpnTokens.size() > 0) {
			Token curToken = rpnTokens.poll();
			TokenType type = curToken.getType();

			if (type == TokenType.STRING) {
				result.add(applyOperand(words, curToken));
			}
			else if (type == TokenType.OPERATOR) {
				result.add(applyOperator(
					words, result.pollLast(), result.pollLast(), curToken));
			}
		}

		return result.pollLast();
	}

	private ArrayList<String> applyOperand(
		ArrayList<String> words, Token operand) {

		ArrayList<String> result = new ArrayList<String>();

		String value = operand.getValue();

		for (String string : words) {
			if (string.indexOf(value) > -1) {
				result.add(string);
			}
		}

		return result;
	}

	private ArrayList<String> applyOperator(
		ArrayList<String> words, ArrayList<String> operand1,
		ArrayList<String> operand2, Token operator) {

		if ((operand1 == null) || (operand2 == null)) {
			return words;
		}

		String value = operator.getValue();

		ArrayList<String> result = new ArrayList<String>();

		if (value.equals(ReservedWords.AND)) {
			for (String string : operand1) {
				if (operand2.contains(string)) {
					result.add(string);
				}
			}
		}
		else if (value.equals(ReservedWords.OR)) {
			result.addAll(operand1);
			result.addAll(operand2);
		}

		return result;
	}

	private void error(String message) {
		System.err.println(message);
	}

	protected int getPrecedence(Token operator) {
		String value = operator.getValue();

		if (value.equals(ReservedWords.AND)) {
			return 2;
		}
		else if (value.equals(ReservedWords.OR)) {
			return 1;
		}
		else {
			return -1;
		}
	}

	protected LinkedList<Token> toReversePolishNotation() {
		LinkedList<Token> outputQueue = new LinkedList<Token>();
		LinkedList<Token> operatorStack = new LinkedList<Token>();

		while (nextToken().getType() != TokenType.EOF) {
			Token curToken = getToken();
			TokenType type = curToken.getType();

			if (type == TokenType.STRING) {
				outputQueue.add(curToken);
			}
			else if (type == TokenType.OPERATOR) {
				while ((operatorStack.size() > 0) &&
					   (getPrecedence(curToken) <=  getPrecedence(operatorStack.getLast()))) {

					outputQueue.add(operatorStack.pollLast());
				}

				operatorStack.add(curToken);
			}
			else if (type == TokenType.L_PARENT) {
				operatorStack.add(curToken);
			}
			else if (type == TokenType.R_PARENT) {
				while ((operatorStack.size() > 0) &&
					   (operatorStack.getLast().getType() != TokenType.L_PARENT)) {

					if (operatorStack.size() == 0) {
						error("Parenthesis unbalanced 1");

						return null;
					}

					outputQueue.add(operatorStack.pollLast());
				}

				operatorStack.pollLast();
			}
		}

		while (operatorStack.size() != 0) {
			if (operatorStack.getLast().getType() != TokenType.L_PARENT &&
			    operatorStack.getLast().getType() != TokenType.R_PARENT) {

				outputQueue.add(operatorStack.pollLast());
			}
			else {
				error("Parenthesis unbalanced 2");
				
				return null;
			}
		}

		return outputQueue;
	}

	private Token getToken() {
		if (_curTokenIndex >= _tokens.size()) {
			return new Token(TokenType.EOF, null);
		}

		return _tokens.get(_curTokenIndex);
	}

	private Token nextToken() {
		int index = ++_curTokenIndex;

		if (index >= _tokens.size()) {
			return new Token(TokenType.EOF, null);
		}

		return _tokens.get(index);
	}

	private int _curTokenIndex = -1;

	private LinkedList<Token> _tokens = null;

}
