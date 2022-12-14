package com.cloud.platform.common.utils;

import com.cloud.platform.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: ZhouShuai
 * @Date: 2021-06-27 17:04
 */
@Slf4j
public class JsonUtil {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MY_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MY_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JsonUtil() {
    }

    private static String writeValue(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String)object;
        } else {
            try {
                return OBJECT_MAPPER.writeValueAsString(object);
            } catch (JsonProcessingException var2) {
                log.error("writeJsonValue error, ", var2);
                return null;
            }
        }
    }

    private static <T> T readValue(String json, Class<T> t) {
        if (json == null) {
            return null;
        } else {
            try {
                return OBJECT_MAPPER.readValue(json, t);
            } catch (Exception var3) {
                log.error("readJsonValue error, ", var3);
                return null;
            }
        }
    }

    private static <T> T readValue(String json, TypeReference<T> t) {
        if (json == null) {
            return null;
        } else {
            try {
                return OBJECT_MAPPER.readValue(json, t);
            } catch (Exception var3) {
                log.error("readJsonValue error, ", var3);
                return null;
            }
        }
    }

    public static String toString(Object object) {
        return writeValue(object);
    }

    public static <T> T toBean(String json, Class<T> t) {
        return readValue(json, t);
    }

    public static <T> List<T> toList(String json) {
        return (List)readValue(json, new TypeReference<List<T>>() {
        });
    }

    public static <K, V> Map<K, V> toMap(String json) {
        return (Map)readValue(json, new TypeReference<Map<K, V>>() {
        });
    }

    public static <T> T toBean(String json, TypeReference<T> t) {
        return readValue(json, t);
    }

    static {
        //?????????????????????????????????????????????,???????????????
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //????????????????????????????????? LocalDateTime
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(MY_DATE_TIME));
        //????????? LocalDateTime ????????? MY_DATE_TIME ??????????????????
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(MY_DATE_TIME));
        //????????????????????????????????? LocalDate
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(MY_DATE));
        //????????? LocalDate ????????? MY_DATE ??????????????????
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(MY_DATE));
        //??????mybatisplus ???????????????total????????????????????????flutter??????
        //javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        //?????????????????????long????????????18?????????????????????ID?????????string??????????????????
        javaTimeModule.addSerializer(Long.class, new LongToStringSerializer());
        OBJECT_MAPPER.registerModule(javaTimeModule);
        //??????????????????????????????????????????
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //??????????????????
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //??????Date????????????,?????????LocalDateTime???LocalDate
        OBJECT_MAPPER.setDateFormat(DATE_FORMAT_TIME);
    }

}
