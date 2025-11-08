package br.com.leonardo.http.request.map;

import java.util.Map;
import java.util.NoSuchElementException;

public record PathVariableMap(Map<String, String> map) {

    public String getString(String name) {
        final String value = map.get(name);
        if (value == null) {
            throw new NoSuchElementException("Required path variable '" + name + "' is missing.");
        }
        return value;
    }

    public Integer getInteger(String name) throws NumberFormatException {
        final String value = getString(name);
        return Integer.parseInt(value);
    }

    public Long getLong(String name) throws NumberFormatException {
        final String value = getString(name);
        return Long.parseLong(value);
    }

    public Boolean getBoolean(String name) {
        final String value = getString(name);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Path variable '" + name + "' is not a valid boolean. Received: " + value);
    }

    public Boolean exists(String name) {
        return map.containsKey(name);
    }

}
