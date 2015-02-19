package com.github.florinn.restr.error;

@SuppressWarnings("serial")
public abstract class BaseException extends Exception {

	private int errorCode;
	
	public BaseException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public BaseException(String message, Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public BaseException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public BaseException(Throwable cause, int errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
