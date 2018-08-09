package tech.mangosoft.autolinkedin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

//@Slf4j
@Component
public class JacksonUtils {


	private static ObjectMapper mapper = getMapper();

	private static ObjectMapper getMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		return objectMapper;
	}

	public static String getJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
		}
		return null;
	}

	public static void getJson(Object object, File file) throws IOException {
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
	}

	public static String getJson(Class<?> view, Object object) throws JsonProcessingException {
		return mapper.writerWithView(view).writeValueAsString(object);
	}

	public static String getPrettyJson(Object object) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}

	public static String getPrettyJson(Class<?> view, Object object) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().withView(view).writeValueAsString(object);
	}

	public static <T> T fromJson(Class<T> clazz, String json) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
		}
		return null;
	}

	public static <T> T fromJson(Class<T> clazz, File file) throws IOException {
		return mapper.readValue(file, clazz);
	}
}
