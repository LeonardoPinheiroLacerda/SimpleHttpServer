package br.com.leonardo.http.request.map;

import java.util.Map;
import java.util.Optional;

public record QueryParameterMap(Map<String, String> map) {

    public Optional<String> getString(String name) {
        return Optional.ofNullable(map.get(name));
    }

    public Optional<Integer> getInteger(String name) throws NumberFormatException {
        return getString(name).map(Integer::parseInt);
    }

    public Optional<Long> getLong(String name) throws NumberFormatException {
        return getString(name).map(Long::parseLong);
    }

    public Optional<Boolean> getBoolean(String name) throws IllegalArgumentException {
        return getString(name).map(value -> {
            if ("true".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            throw new IllegalArgumentException("Query parameter '" + name + "' is not a valid boolean. Received: " + value);
        });
    }

    public Boolean exists(String name) {
        return map.containsKey(name);
    }
}
