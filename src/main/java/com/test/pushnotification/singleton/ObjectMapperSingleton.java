package com.test.pushnotification.singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {

    // Private static instance of ObjectMapper
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    // Private constructor to prevent instantiation
    private ObjectMapperSingleton() {}

    // Public method to provide access to the instance
    public static ObjectMapper getInstance() {
        return INSTANCE;
    }
}