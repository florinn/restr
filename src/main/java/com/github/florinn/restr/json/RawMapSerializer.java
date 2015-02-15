package com.github.florinn.restr.json;

import java.io.IOException;
import java.util.Map;

import net.sf.json.util.JSONUtils;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class RawMapSerializer extends JsonSerializer<Map<String, Object>> {

	@Override
	public void serialize(Map<String, Object> value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jgen.writeStartObject();
		for (Map.Entry<String, Object> e : value.entrySet()) {
			jgen.writeFieldName(e.getKey());

			if (e.getValue() instanceof String) {
				String stringValue = (String) e.getValue();

				// Write value as raw data, since it's already JSON text
				if (JSONUtils.mayBeJSON(stringValue)) {
					jgen.writeRawValue(stringValue);
				} else {
					jgen.writeRawValue(String.format("\"%s\"", stringValue));
				}
			} else {
				jgen.writeObject(e.getValue());
			}
		}
		jgen.writeEndObject();

	}

}
