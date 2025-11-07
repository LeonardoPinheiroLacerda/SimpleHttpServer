package br.com.leonardo.util;


import br.com.leonardo.http.HttpHeader;
import org.slf4j.MDC;

import java.util.Set;
import java.util.UUID;

public class TraceIdUtil {

    private final static String MDC_NAME = "traceId";
    private final static String HEADER_NAME = "X-Trace-Id";

    private TraceIdUtil() {}

    public static void initializeTraceId() {
        MDC.put(MDC_NAME, UUID.randomUUID().toString());
    }

    public static String getTraceId() {
        return MDC.get(MDC_NAME);
    }

    public static String getHeader() {
        return "%s: %s\r\n"
                .formatted(HEADER_NAME, getTraceId());
    }

    public static void addHeader(Set<HttpHeader> headers) {
        headers.add(new HttpHeader(HEADER_NAME, getTraceId()));
    }
}
