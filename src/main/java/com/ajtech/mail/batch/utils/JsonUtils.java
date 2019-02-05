package com.ajtech.mail.batch.utils;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
	public static <T> String convertObjectToJsonString(T object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		objectMapper.setDateFormat(new SimpleDateFormat("dd-mm-yyyy"));
		ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
		return objectWriter.writeValueAsString(object);
	}
}
