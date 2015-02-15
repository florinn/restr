package com.github.florinn.restr.error;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.florinn.restr.util.OrderPreservingProperties;
import com.github.florinn.restr.util.Utils;

public class RestExceptionMapper {

	private static final Logger log = LoggerFactory.getLogger(RestExceptionMapper.class);
	
    private static final String DEFAULT_EXCEPTION_MESSAGE_VALUE = "<exmsg>";
    private static final String EXCEPTION_CONFIG_DELIMITER = "|";
    private static final String PROP_NULL_TOKEN = "null";
    private static final String PROP_EQ_TOKEN = "=";
    private static final String STATUS_PROP_NAME = "status";
    private static final String CODE_PROP_NAME = "code";
    private static final String MESSAGE_PROP_NAME = "msg";
    private static final String DEVELOPER_MESSAGE_PROP_NAME = "devMsg";

    private static Map<String, RestError> exceptionMappings = Collections.emptyMap();
    
    static {
    	InputStream is = Utils.getResourceAsStream("rest-errors.config");
    	OrderPreservingProperties props = new OrderPreservingProperties();
    	props.load(is);
    	exceptionMappings = toRestErrors(props);
    }
    
    /**
     * @param t the exception to be mapped
     * @return the matching {@code RestError}
     */
    public static RestError getRestError(Throwable t) {
    	return getRestError(t, null);
    }
    
    /**
     * @param t the exception to be mapped
     * @param errorCode error specific code (useful to discriminate between multiple errors mapped to same HTTP status)
     * @return the matching {@code RestError}
     */
    public static RestError getRestError(Throwable t, Integer errorCode) {

        RestError template = getRestErrorTemplate(t);
        if (template == null) {
            return null;
        }

        RestError.Builder builder = new RestError.Builder();
        builder.setStatus(template.getStatus());
        
        if(errorCode != null)
        	builder.setCode(errorCode);
        else
        	builder.setCode(template.getCode());
        
        builder.setThrowable(t);

        String msg = getMessage(template.getMessage(), t);
        if (msg != null) {
            builder.setMessage(msg);
        }
        msg = getMessage(template.getDeveloperMessage(), t);
        if (msg != null) {
            builder.setDeveloperMessage(msg);
        }

        return builder.build();
    }

    private static String getMessage(String msg, Throwable t) {

        if (msg != null) {
            if (msg.equalsIgnoreCase(PROP_NULL_TOKEN)) {
                return null;
            }
            if (msg.equalsIgnoreCase(DEFAULT_EXCEPTION_MESSAGE_VALUE)) {
                msg = t.getMessage();
            }
        }

        return msg;
    }

    private static RestError getRestErrorTemplate(Throwable t) {
        Map<String,RestError> mappings = exceptionMappings;
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }
        RestError template = null;
        String dominantMapping = null;
        int deepest = Integer.MAX_VALUE;
        for (Map.Entry<String, RestError> entry : mappings.entrySet()) {
            String key = entry.getKey();
            int depth = getDepth(key, t);
            if (depth >= 0 && depth < deepest) {
                deepest = depth;
                dominantMapping = key;
                template = entry.getValue();
            }
        }
        if (template != null) {
            log.debug("Resolving to RestError template '{}' for exception of type [{}], based on exception mapping [{}]", 
            		template, t.getClass().getName(), dominantMapping);
        }
        return template;
    }

    private static int getDepth(String exceptionMapping, Throwable t) {
        return getDepth(exceptionMapping, t.getClass(), 0);
    }

    private static int getDepth(String exceptionMapping, Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            return depth;
        }
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    private static int getRequiredInt(String key, String value) {
        try {
            int anInt = Integer.valueOf(value);
            return Math.max(-1, anInt);
        } catch (NumberFormatException e) {
            String msg = String.format(
            		"Configuration element '{}' requires an integer value. The value specified: {}", key, value);
            throw new IllegalArgumentException(msg, e);
        }
    }

    private static int getInt(String key, String value) {
        try {
            return getRequiredInt(key, value);
        } catch (IllegalArgumentException iae) {
            return 0;
        }
    }

    private static Map<String, RestError> toRestErrors(Map<String, String> smap) {
        if (smap == null || smap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, RestError> map = new LinkedHashMap<String, RestError>(smap.size());

        for (Map.Entry<String, String> entry : smap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            RestError template = toRestError(value);
            map.put(key, template);
        }

        return map;
    }

    private static RestError toRestError(String exceptionConfig) {
        String[] values = StringUtils.delimitedListToStringArray(exceptionConfig, EXCEPTION_CONFIG_DELIMITER);
        if (values == null || values.length == 0) {
            throw new IllegalStateException(
            		"Invalid config mapping. Exception names must map to a string configuration.");
        }
        if (values.length > 4) {
            throw new IllegalStateException(
            		"Invalid config mapping. Mapped values must not contain more than 4 values (exception=http status, code=int val, msg=string val, devMsg=string val)");
        }

        RestError.Builder builder = new RestError.Builder();

        boolean statusSet = false;
        boolean codeSet = false;
        boolean msgSet = false;
        boolean devMsgSet = false;

        for (String value : values) {

            String trimmedVal = StringUtils.trimWhitespace(value);

            //check to see if the value is an explicitly named key/value pair:
            String[] pair = StringUtils.split(trimmedVal, PROP_EQ_TOKEN);
            if (pair != null) {
                //explicit attribute set:
                String pairKey = StringUtils.trimWhitespace(pair[0]);
                if (!StringUtils.hasText(pairKey)) {
                    pairKey = null;
                }
                String pairValue = StringUtils.trimWhitespace(pair[1]);
                if (!StringUtils.hasText(pairValue)) {
                    pairValue = null;
                }
                if (STATUS_PROP_NAME.equalsIgnoreCase(pairKey)) {
                    int statusCode = getRequiredInt(pairKey, pairValue);
                    builder.setStatus(statusCode);
                    statusSet = true;
                } else if (CODE_PROP_NAME.equalsIgnoreCase(pairKey)) {
                    int code = getRequiredInt(pairKey, pairValue);
                    builder.setCode(code);
                    codeSet = true;
                } else if (MESSAGE_PROP_NAME.equalsIgnoreCase(pairKey)) {
                    builder.setMessage(pairValue);
                    msgSet = true;
                } else if (DEVELOPER_MESSAGE_PROP_NAME.equalsIgnoreCase(pairKey)) {
                    builder.setDeveloperMessage(pairValue);
                    devMsgSet = true;
                }
            } else {
                //not a key/value pair - use heuristics to determine what value is being set:
                int val;
                if (!statusSet) {
                    val = getInt(STATUS_PROP_NAME, trimmedVal);
                    if (val > 0) {
                        builder.setStatus(val);
                        statusSet = true;
                        continue;
                    }
                }
                if (!codeSet) {
                    val = getInt(CODE_PROP_NAME, trimmedVal);
                    if (val > 0) {
                        builder.setCode(val);
                        codeSet = true;
                        continue;
                    }
                }
                if (!msgSet) {
                    builder.setMessage(trimmedVal);
                    msgSet = true;
                    continue;
                }
                if (!devMsgSet) {
                    builder.setDeveloperMessage(trimmedVal);
                    devMsgSet = true;
                    continue;
                }

            }
        }

        return builder.build();
    }
	
}
