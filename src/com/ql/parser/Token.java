package com.ql.parser;

public class Token {

	private TokenType _type;

	private String _value;

	public Token(TokenType type, String value) {
		_type = type;
		_value = value;
	}

	public boolean equals(Token token) {
		return (token.getType() == getType()) &&
				token.getValue().equals(getValue());
	}

	public TokenType getType() {
		return _type;
	}

	public String getValue() {
		return _value;
	}

	public void setType(TokenType type) {
		this._type = type;
	}

	public void setValue(String value) {
		this._value = value;
	}
	@Override
	public String toString() {
		return "{ type: " + getType() + ", value: \"" + getValue() + "\" }";
	}

}
