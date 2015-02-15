package com.github.florinn.restr.error.sample;

@SuppressWarnings("serial")
public class CustomIllegalArgumentException extends IllegalArgumentException {

	public CustomIllegalArgumentException() {
		super();
	}

	public CustomIllegalArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomIllegalArgumentException(String s) {
		super(s);
	}

	public CustomIllegalArgumentException(Throwable cause) {
		super(cause);
	}

}
