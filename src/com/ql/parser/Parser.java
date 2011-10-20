package com.ql.parser;

import java.util.LinkedList;

public class Parser {
	
	public static void main(String[] args) {
		new Parser("\"bruno\" AND (\"bruna\" OR \"julia\")");
	}
	
	public Parser(String query) {
		_tokens = Tokenizer.getTokens(query);

		System.out.println(_tokens);

		query();
	}

	private boolean accept(TokenType type) {
		if (type == getToken().getType()) {
			nextToken();

			return true;
		}
		
		return false;
	}
	
	private void error() {
		System.out.println("Unexpected token" + getToken());
	}

	private boolean expect(TokenType type) {
		if (type == getToken().getType()) {
			nextToken();
			
			return true;
		}

		error();

		return false;
	}

	public void query() {
		while (nextToken().getType() != TokenType.EOF) {
			orExpression();
		}

		end();
	}
	
	public void end() {
		System.out.println("end");
	}
	
	public void orExpression() {
		andExpression();
		
		System.out.println("or " + getToken());

		while (getToken().getType() == TokenType.OPERATOR && getToken().getValue().equals(ReservedWords.OR)) {
			nextToken();
			andExpression();
		}
	}

	public void andExpression() {
		operand();

		System.out.println("and " + getToken());

		while (getToken().getType() == TokenType.OPERATOR && getToken().getValue().equals(ReservedWords.AND)) {
			nextToken();
			operand();
		}
	}

	public void operand() {
		if (accept(TokenType.STRING)) {
			System.out.println("string");
		}
		else if (accept(TokenType.L_PAREN)) {
			orExpression();

			expect(TokenType.R_PARENT);
		}
		else {
			error();

			nextToken();
		}
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
