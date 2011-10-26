package com.ql.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.ql.parser.exception.SyntaxException;

public class Parser {

	private final LinkedList<Token> _tokensRPN;

	public Parser(String query) throws SyntaxException {
		_tokensRPN = toReversePolishNotation(Tokenizer.getTokens(query));
	}

	protected ArrayList<String> applyOperand(
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

	protected ArrayList<String> applyOperator(
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
		else if (value.equals(ReservedWords.NAND)) {
			result.addAll(words);

			for (String string : operand1) {
				if (operand2.contains(string)) {
					result.remove(string);
				}
			}
		}
		else if (value.equals(ReservedWords.XOR)) {
			for (String string : operand1) {
				if (!operand2.contains(string)) {
					result.add(string);
				}
			}

			for (String string : operand2) {
				if (!operand1.contains(string)) {
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

	public ArrayList<String> filter(ArrayList<String> words) {
		LinkedList<ArrayList<String>> result =
			new LinkedList<ArrayList<String>>();

		LinkedList<Token> rpntokensCopy = new LinkedList<Token>();

		rpntokensCopy.addAll(_tokensRPN);

		while (rpntokensCopy.size() > 0) {
			Token curToken = rpntokensCopy.poll();
			TokenType type = curToken.getType();

			if (type == TokenType.STRING) {
				result.add(applyOperand(words, curToken));
			}
			else if (type == TokenType.OPERATOR) {
				result.add(applyOperator(
					words, result.pollLast(), result.pollLast(), curToken));
			}
		}

		if (result.size() == 0) {
			return new ArrayList<String>();
		}

		return result.pollLast();
	}

	protected int getPrecedence(Token operator) {
		String value = operator.getValue();

		if (value.equals(ReservedWords.AND)) {
			return 2;
		}
		else if (value.equals(ReservedWords.NAND)) {
			return 2;
		}
		else if (value.equals(ReservedWords.OR)) {
			return 1;
		}
		else if (value.equals(ReservedWords.XOR)) {
			return 1;
		}
		else {
			return -1;
		}
	}

	protected LinkedList<Token> toReversePolishNotation(
		LinkedList<Token> tokens) throws SyntaxException {

		LinkedList<Token> outputQueue = new LinkedList<Token>();
		LinkedList<Token> operatorStack = new LinkedList<Token>();

		Iterator<Token> it = tokens.iterator();

		while (it.hasNext()) {
			Token curToken = it.next();
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
						throw new SyntaxException("Parenthesis unbalanced");
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
				throw new SyntaxException("Parenthesis unbalanced");
			}
		}

		return outputQueue;
	}

}
