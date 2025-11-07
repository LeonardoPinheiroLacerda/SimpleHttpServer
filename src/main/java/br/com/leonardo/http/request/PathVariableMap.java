package br.com.leonardo.http.request;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpStatusCode;

import java.util.Map;

public record PathVariableMap(Map<String, String> map) {

    public String getString(String name) {
        final String value = map.get(name);
        if (value == null) {
            throw new HttpException("Path variable '" + name + "' not found", HttpStatusCode.BAD_REQUEST, null);
        }
        return value;
    }

    public Integer getInteger(String name) {
        final String value = getString(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new HttpException("Path variable '" + name + "' is not a valid integer", HttpStatusCode.BAD_REQUEST, null);
        }
    }

    public Long getLong(String name) {
        final String value = getString(name);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new HttpException("Path variable '" + name + "' is not a valid long", HttpStatusCode.BAD_REQUEST, null);
        }
    }

    public Boolean getBoolean(String name) {
        final String value = getString(name);
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        else if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new HttpException("Path variable '" + name + "' is not a valid boolean", HttpStatusCode.BAD_REQUEST, null);
    }

    public Boolean exists(String name) {
        return map.containsKey(name);
    }

}
