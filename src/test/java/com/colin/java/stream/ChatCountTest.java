package com.colin.java.stream;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatCountTest {

    @Test
    public void test() throws InterruptedException {
        String input = "1234567896665554433231";

        input.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, LinkedHashMap::new, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Character, Long>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
