package com.ql.parser;

public class Token {
	
	public Token(TokenType type, String value) {
		_type = type;
		_value = value;
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
	
	private TokenType _type;
	private String _value;
	
}
