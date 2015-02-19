package com.github.florinn.restr.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Throwables;

@Provider
public class RestExceptionMapperProvider implements ExceptionMapper<Throwable> {

	public Response toResponse(Throwable exception) {
		Throwable cause = Throwables.getRootCause(exception);

    	RestError error;

    	if(BaseException.class.isAssignableFrom(cause.getClass())) {
    		error = RestExceptionMapper.getRestError(cause, ((BaseException)cause).getErrorCode());
    	} else if(BaseRuntimeException.class.isAssignableFrom(cause.getClass())) {
    		error = RestExceptionMapper.getRestError(cause, ((BaseRuntimeException)cause).getErrorCode());
    	} else {
    		error = RestExceptionMapper.getRestError(cause);
    	}

    	Object entity = error.toMap();

    	Response response = Response.status(error.getStatus().value()).type(MediaType.APPLICATION_JSON_TYPE).entity(entity).build();

    	return response;
	}

}
