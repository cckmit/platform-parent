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
        //反序列化的时候如果多了其他属性,不抛出异常
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //将前端传入的字符串转为 LocalDateTime
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(MY_DATE_TIME));
        //将后端 LocalDateTime 转换为 MY_DATE_TIME 格式返回前端
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(MY_DATE_TIME));
        //将前端传入的字符串转为 LocalDate
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(MY_DATE));
        //将后端 LocalDate 转换为 MY_DATE 格式返回前端
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(MY_DATE));
        //会将mybatisplus 分页返回的total也转成字符串导致flutter问题
        //javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        //自定义序列化：long长度超过18位则认为是雪花ID，转为string，否则为数字
        javaTimeModule.addSerializer(Long.class, new LongToStringSerializer());
        OBJECT_MAPPER.registerModule(javaTimeModule);
        //关闭日期序列化为时间戳的功能
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //空值不序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置Date日期格式,不影响LocalDateTime和LocalDate
        OBJECT_MAPPER.setDateFormat(DATE_FORMAT_TIME);
    }

}
