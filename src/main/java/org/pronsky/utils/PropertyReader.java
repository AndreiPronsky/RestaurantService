package org.pronsky.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Getter
public class PropertyReader {
    private static PropertyReader instance;
    private final String url;
    private final String user;
    private final String password;
    private static final String PATH_TO_PROPS = "/connection-config.properties";

    private PropertyReader() {
        Properties properties = new Properties();
        try (InputStream input = this.getClass().getResourceAsStream(PATH_TO_PROPS)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Unable to read properties file", e);
        }
        url = properties.getProperty("db.url");
        user = properties.getProperty("db.user");
        password = properties.getProperty("db.password");
    }

    public static PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }
}
