package br.com.leonardo.http.request;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpStatusCode;

import java.util.Map;
import java.util.Optional;

public record QueryParameterMap(Map<String, String> map) {

    public Optional<String> getString(String name) {
        return Optional.ofNullable(map.get(name));
    }

    public Optional<Integer> getInteger(String name) {

        final String value = getNotNullableString(name);

        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new HttpException("Query parameter '" + name + "' is not a valid integer", HttpStatusCode.BAD_REQUEST, null);
        }
    }

    public Optional<Long> getLong(String name) {

        final String value = getNotNullableString(name);

        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            throw new HttpException("Query parameter '" + name + "' is not a valid integer", HttpStatusCode.BAD_REQUEST, null);
        }
    }

    public Optional<Boolean> getBoolean(String name) {
        final String value = getNotNullableString(name);

        if ("true".equalsIgnoreCase(value)) {
            return Optional.of(true);
        }
        else if ("false".equalsIgnoreCase(value)) {
            return Optional.of(false);
        }
        else if (value == null){
            return Optional.empty();
        }

        throw new HttpException("Path variable '" + name + "' is not a valid boolean", HttpStatusCode.BAD_REQUEST, null);
    }

    private String getNotNullableString(String name) {
        return getString(name).orElse(null);
    }

    public Boolean exists(String name) {
        return map.containsKey(name);
    }
}
