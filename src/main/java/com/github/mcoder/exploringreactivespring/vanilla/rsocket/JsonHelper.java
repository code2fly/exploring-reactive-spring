package com.github.mcoder.exploringreactivespring.vanilla.rsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonHelper {

    private final ObjectMapper objectMapper;

    // this annotation will swallow checked exception and throw RuntimeException
    @SneakyThrows
    public <T> T readString(String data, Class<T> clzz) {
        return objectMapper.readValue(data, clzz);
    }


    @SneakyThrows
    public <T> String writeAsString(T data) {
        return objectMapper.writeValueAsString(data);
    }

}
