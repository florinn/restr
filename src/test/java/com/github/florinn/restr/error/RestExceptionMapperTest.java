package com.github.florinn.restr.error;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.github.florinn.restr.error.sample.CustomIllegalArgumentException;

import static org.assertj.core.api.Assertions.assertThat;

public class RestExceptionMapperTest {

	@Test
	public void mapKnownNoParamException() throws Exception {
		
		Throwable t = new java.lang.IllegalAccessException();
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(1);
		assertThat(error.getMessage()).isEqualTo("Bad request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("Bad request.");
	}
	
	@Test
	public void explicitMapKnownException() throws Exception {
		
		Throwable t = new java.lang.InstantiationError("instantiation error");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(2);
		assertThat(error.getMessage()).isEqualTo("Bad request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("Request is missing header x.");
	}
	
	@Test
	public void mapKnownException() throws Exception {
		
		Throwable t = new java.lang.IllegalArgumentException("arg x is required");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(3);
		assertThat(error.getMessage()).isEqualTo("Bad request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("arg x is required");
	}
	
	@Test
	public void mapKnownNestedException() throws Exception {
		
		Throwable t = new CustomIllegalArgumentException("arg x is required");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(3);
		assertThat(error.getMessage()).isEqualTo("Bad request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("arg x is required");
	}
	
	@Test
	public void mapKnownExceptionAndErrorCode() throws Exception {
		
		Throwable t = new java.lang.IllegalStateException("arg x must be greater than 0");
		RestError error = RestExceptionMapper.getRestError(t, 123);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(123);
		assertThat(error.getMessage()).isEqualTo("Bad request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("arg x must be greater than 0");
	}
	
	@Test
	public void mapKnownExceptionAndReturnMessage() throws Exception {
		
		Throwable t = new java.lang.IllegalMonitorStateException("monitor x must be greater than y");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(400));
		assertThat(error.getCode()).isEqualTo(0);
		assertThat(error.getMessage()).isEqualTo("monitor x must be greater than y");
		assertThat(error.getDeveloperMessage()).isEqualTo(null);
	}
	
	@Test
	public void mapKnownExceptionUsingBacklashEscaping() throws Exception {
		
		Throwable t = new java.lang.SecurityException();
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(403));
		assertThat(error.getCode()).isEqualTo(5);
		assertThat(error.getMessage()).isEqualTo("Credentials authenticating this request are not authorized to run this operation.");
		assertThat(error.getDeveloperMessage()).isEqualTo("Credentials authenticating this request are not authorized to run this operation.");
	}
	
	@Test
	public void mapKnownExceptionDisableMessage() throws Exception {
		
		Throwable t = new java.lang.OutOfMemoryError("out of memory");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(412));
		assertThat(error.getCode()).isEqualTo(1);
		assertThat(error.getMessage()).isEqualTo(null);
		assertThat(error.getDeveloperMessage()).isEqualTo(null);
	}
	
	@Test
	public void mapUnknownException() throws Exception {
		
		Throwable t = new java.lang.UnknownError("this is an unknown error");
		RestError error = RestExceptionMapper.getRestError(t);
		
		assertThat(error.getStatus()).isEqualTo(HttpStatus.valueOf(500));
		assertThat(error.getCode()).isEqualTo(0);
		assertThat(error.getMessage()).isEqualTo("Server unexpected error, retry request.");
		assertThat(error.getDeveloperMessage()).isEqualTo("this is an unknown error");
	}
}
