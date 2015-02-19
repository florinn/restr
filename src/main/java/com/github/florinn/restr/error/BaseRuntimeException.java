package com.github.florinn.restr.error;

@SuppressWarnings("serial")
public abstract class BaseRuntimeException extends RuntimeException {

	private int errorCode;

	public BaseRuntimeException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public BaseRuntimeException(String message, Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public BaseRuntimeException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public BaseRuntimeException(Throwable cause, int errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}