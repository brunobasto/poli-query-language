package com.ql.parser.exception;

public class SyntaxException extends Exception {

	private final String _message;

	public SyntaxException(String message) {
		_message = message;
	}

	@Override
	public String getMessage() {
		return _message;
	}

}
