package br.com.leonardo.observability;

import br.com.leonardo.http.HttpHeader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class TraceIdLifeCycleHandlerTest {


    @Test
    void shouldInitializeTraceId() {

        //When
        TraceIdLifeCycleHandler.initializeTraceId();

        //Then
        Assertions
                .assertThat(TraceIdLifeCycleHandler.getTraceId())
                .isNotNull();
    }

    @Test
    void shouldGetHttpHeader() {

        //Given
        TraceIdLifeCycleHandler.initializeTraceId();

        //When
        final String header = TraceIdLifeCycleHandler.getHeader();

        //Then
        Assertions
                .assertThat(header)
                .isNotNull();

    }

    @Test
    void shouldAddTraceIdToHeaders() {

        //Given
        TraceIdLifeCycleHandler.initializeTraceId();

        Set<HttpHeader> header = new HashSet<>();

        //When
        TraceIdLifeCycleHandler.addHeader(header);

        //Then
        Assertions
                .assertThat(header)
                .isNotNull()
                .hasSize(1);
    }

}