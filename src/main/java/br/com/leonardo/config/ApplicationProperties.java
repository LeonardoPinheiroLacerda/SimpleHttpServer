package br.com.leonardo.config;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ApplicationProperties {

    private static final String PROPERTIES_FILE = "http-server.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ApplicationProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                log.warn("{} not found in the classpath. Using default values.", PROPERTIES_FILE);
            } else {
                properties.load(input);
                log.info("Properties loaded from {}", PROPERTIES_FILE);
            }
        } catch (Exception e) {
            log.error("Error loading properties from {}", PROPERTIES_FILE, e);
        }
    }

    private static String getString(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }

    private static int getInt(String key, int defaultValue) {
        String value = getString(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("Invalid value for property '{}'. Using default value '{}'.", key, defaultValue);
            }
        }
        return defaultValue;
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, null);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }


    public static int getPort() {
        return getInt("http.server.port", 9000);
    }

    public static boolean shouldLogRequests() {
        return getBoolean("http.server.log.detailed-request", false);
    }

    public static boolean shouldLogResponses() {
        return getBoolean("http.server.log.detailed-response", false);
    }

    public static boolean staticContentEnabled() {
        return getBoolean("http.server.static.content.enabled", true);
    }

    public static String getStaticContentPath() {
        return getString("http.server.static.content.path", "static");
    }
}
