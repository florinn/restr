package com.github.florinn.restr.error;

import org.springframework.util.ObjectUtils;
import org.springframework.http.HttpStatus;

/**
 * The {@code RestError} class is the error template used in the exception mapping process
 * Contains the HTTP status value, error code, end user message and developer message
 */
public class RestError {

	private final HttpStatus status;
	private final int code;
	private final String message;
	private final String developerMessage;
	private final Throwable throwable;

	/**
	 * @param status the HTTP status
	 * @param code error specific code (useful to discriminate between multiple errors mapped to same HTTP status)
	 * @param message end user friendly information about the error
	 * @param developerMessage developer oriented information about the error
	 * @param throwable the exception to be mapped
	 */
	private RestError(HttpStatus status, int code, String message, String developerMessage, Throwable throwable) {
		if (status == null) {
			throw new IllegalArgumentException("HttpStatus argument cannot be null");
		}
		
		this.status = status;
		this.code = code;
		this.message = message;
		this.developerMessage = developerMessage;
		this.throwable = throwable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof RestError) {
			RestError re = (RestError)o;
			return ObjectUtils.nullSafeEquals(getStatus(), re.getStatus()) &&
					ObjectUtils.nullSafeEquals(getCode(), re.getCode()) &&
					ObjectUtils.nullSafeEquals(getMessage(), re.getMessage()) &&
					ObjectUtils.nullSafeEquals(getDeveloperMessage(), re.getDeveloperMessage()) &&
					ObjectUtils.nullSafeEquals(getThrowable(), re.getThrowable());

		}
		return false;
	}

	@Override
	public int hashCode() {
		Object[] params = {getStatus(), getCode(), getMessage(), getDeveloperMessage(), getThrowable()};
		return ObjectUtils.nullSafeHashCode(params);
	}

	@Override
	public String toString() {
		return append(new StringBuilder(), getStatus()).append(", message: ").append(getMessage()).toString();
	}

	private StringBuilder append(StringBuilder buf, HttpStatus status) {
		buf.append(status.value()).append(" (").append(status.getReasonPhrase()).append(")");
		return buf;
	}

	public HttpStatus getStatus() {
		return status;
	}
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public String getDeveloperMessage() {
		return developerMessage;
	}
	public Throwable getThrowable() {
		return throwable;
	}

	public static class Builder {

		private HttpStatus status;
		private int code;
		private String message;
		private String developerMessage;
		private Throwable throwable;

		public Builder setStatus(HttpStatus status) {
			this.status = status;
			return this;
		}
		
		public Builder setStatus(int statusCode) {
			this.status = HttpStatus.valueOf(statusCode);
			return this;
		}
		
		public Builder setCode(int code) {
			this.code = code;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setDeveloperMessage(String developerMessage) {
			this.developerMessage = developerMessage;
			return this;
		}

		public Builder setThrowable(Throwable throwable) {
			this.throwable = throwable;
			return this;
		}

		public RestError build() {
			return new RestError(this.status, this.code, this.message, this.developerMessage, this.throwable);
		}
	}
}