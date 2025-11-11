package br.com.leonardo.observability;


import br.com.leonardo.http.HttpHeader;
import org.slf4j.MDC;

import java.util.Set;
import java.util.UUID;

public class TraceIdLifeCycleHandler {

    private static final String MDC_NAME = "traceId";
    private static final String HEADER_NAME = "X-Trace-Id";

    private TraceIdLifeCycleHandler() {}

    public static void initializeTraceId() {
        MDC.put(MDC_NAME, UUID.randomUUID().toString());
    }

    public static String getTraceId() {
        return MDC.get(MDC_NAME);
    }

    public static String getHeader() {
        return "%s: %s\r%n"
                .formatted(HEADER_NAME, getTraceId());
    }

    public static void addHeader(Set<HttpHeader> headers) {
        headers.add(new HttpHeader(HEADER_NAME, getTraceId()));
    }
}
