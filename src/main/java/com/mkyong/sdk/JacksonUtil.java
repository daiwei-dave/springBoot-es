package com.mkyong.sdk;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhangliewei
 * @date 2018/5/4 10:25
 *
 */
public class JacksonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    public JacksonUtil() {
    }

    public static String toJson(Object value) throws JsonCastException {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException var3) {
            throw new JsonCastException("Json cast failed!", var3);
        }
    }

    public static <T> T toObject(String json, JavaType javaType) throws JsonCastException {
        try {
            return mapper.readValue(json, javaType);
        } catch (JsonProcessingException var4) {
            throw new JsonCastException("Json cast failed!", var4);
        } catch (IOException var5) {
            throw new JsonCastException("Json cast failed!", var5);
        }
    }

    public static <T> T toObject(String json, Class<T> valueType) throws JsonCastException {
        try {
            return mapper.readValue(json, valueType);
        } catch (Exception var4) {
            throw new JsonCastException("Json cast failed!", var4);
        }
    }

    public static <T> T toObject(String json, TypeReference<?> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception var4) {
            throw new JsonCastException("Json cast failed!", var4);
        }
    }

    public static <T> List<T> toObjectList(String json, Class<T> clazz) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, new Class[]{clazz});
            List<T> list = (List) mapper.readValue(json, javaType);
            return list;
        } catch (Exception var6) {
            try {
                return (List) mapper.readValue(json, new TypeReference<List<T>>() {
                });
            } catch (IOException var5) {
                throw new JsonCastException("Json cast failed!", var6);
            }
        }
    }

    public static <T> List<T> jsonToList(String json, Class<T> valueType) {
        ArrayList list = null;

        try {
            JsonParser parser = mapper.getJsonFactory().createJsonParser(json);
            JsonNode nodes = (JsonNode) parser.readValueAsTree();
            list = new ArrayList(nodes.size());
            Iterator i$ = nodes.iterator();

            while (i$.hasNext()) {
                JsonNode node = (JsonNode) i$.next();
                list.add(mapper.readValue(node.textValue(), valueType));
            }

            return list;
        } catch (Exception var8) {
            throw new JsonCastException("Json cast failed!", var8);
        }
    }

    public static <T> Map<String, T> jsonToMap(String json) {
        Map userData = null;

        try {
            userData = (Map) mapper.readValue(json, Map.class);
            return userData;
        } catch (Exception var4) {
            throw new JsonCastException("Json cast failed!", var4);
        }
    }

    public static JavaType getGenericJavaType(Class<?> parametrized, Class<?>... clazz) {
        JavaType inner = null;
        for (int i = clazz.length - 1; i > 0; i--) {
            if (i == clazz.length - 1) {
                inner = mapper.getTypeFactory().constructParametrizedType(clazz[i - 1], clazz[i - 1], clazz[i]);
            } else {
                inner = mapper.getTypeFactory().constructParametrizedType(clazz[i - 1], clazz[i - 1], inner);
            }
        }
        if (clazz.length == 1) {
            return mapper.getTypeFactory().constructParametricType(parametrized, clazz[0]);
        } else {
            return mapper.getTypeFactory().constructParametricType(parametrized, inner);
        }
    }

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
    }

}
