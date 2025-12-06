package br.com.leonardo.io;

import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.observability.TraceIdLifeCycleHandler;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.core.HttpEndpointResolver;
import br.com.leonardo.router.extractor.HeaderExtractor;
import br.com.leonardo.router.extractor.PathVariableExtractor;
import br.com.leonardo.router.extractor.QueryParameterExtractor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Optional;

import static br.com.leonardo.enums.HttpMethod.GET;
import static br.com.leonardo.enums.HttpStatusCode.BAD_REQUEST;
import static br.com.leonardo.enums.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionErrorHandlerTest {

    @Mock
    private HttpWriter httpWriter;
    @Mock
    private HttpEndpointResolver endpointResolver;
    @Mock
    private HttpExceptionHandlerResolver exceptionResolver;
    @Mock
    private HttpEndpoint<?, ?> httpEndpoint;
    @Mock
    private HttpExceptionHandler<Exception, Object> httpExceptionHandler;

    private MockedStatic<HeaderExtractor> headerExtractorMock;
    private MockedStatic<PathVariableExtractor> pathVariableExtractorMock;
    private MockedStatic<QueryParameterExtractor> queryParameterExtractorMock;
    private MockedStatic<TraceIdLifeCycleHandler> traceIdLifeCycleHandlerMock;

    private HttpRequestData requestData;
    private OutputStream outputStream;


    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        RequestLine requestLine = new RequestLine(GET, "/test", "HTTP/1.1");
        requestData = new HttpRequestData(requestLine, Collections.emptySet(), new byte[0]);

        headerExtractorMock = mockStatic(HeaderExtractor.class);
        pathVariableExtractorMock = mockStatic(PathVariableExtractor.class);
        queryParameterExtractorMock = mockStatic(QueryParameterExtractor.class);
        traceIdLifeCycleHandlerMock = mockStatic(TraceIdLifeCycleHandler.class);

        headerExtractorMock.when(() -> HeaderExtractor.extract(any())).thenReturn(new HeaderMap(Collections.emptyMap()));
        pathVariableExtractorMock.when(() -> PathVariableExtractor.extract(any(), any())).thenReturn(new PathVariableMap(Collections.emptyMap()));
        queryParameterExtractorMock.when(() -> QueryParameterExtractor.extract(any())).thenReturn(new QueryParameterMap(Collections.emptyMap()));
        traceIdLifeCycleHandlerMock.when(TraceIdLifeCycleHandler::getTraceId).thenReturn("test-trace-id");
    }

    @AfterEach
    void tearDown() {
        headerExtractorMock.close();
        pathVariableExtractorMock.close();
        queryParameterExtractorMock.close();
        traceIdLifeCycleHandlerMock.close();
    }


    @Test
    void shouldDispatchException_whenHandlerIsFound() throws IOException {
        // Given
        final Exception exception = new RuntimeException("Test Exception");
        final HttpResponse<Object> mockResponse = HttpResponse.builder().statusCode(BAD_REQUEST).body("Error").build();

        when(endpointResolver.get(requestData)).thenReturn(Optional.of(httpEndpoint));
        when(exceptionResolver.get(exception.getClass(), true)).thenReturn(Optional.of(httpExceptionHandler));
        when(httpExceptionHandler.handle(any(ProblemDetails.class), eq(exception))).thenReturn(mockResponse);

        // When
        ConnectionErrorHandler.dispatchException(outputStream, httpWriter, requestData, endpointResolver, exceptionResolver, exception);

        // Then
        verify(exceptionResolver).get(exception.getClass(), true);
        verify(httpExceptionHandler).handle(any(ProblemDetails.class), eq(exception));
        verify(httpWriter).writeResponse(outputStream, mockResponse, requestData.requestLine(), requestData.headers());
    }

    @Test
    void shouldDispatchException_whenHandlerIsNotFound() throws IOException {
        // Given
        final Exception exception = new RuntimeException("Test Exception");
        when(endpointResolver.get(requestData)).thenReturn(Optional.empty());
        when(exceptionResolver.get(exception.getClass(), true)).thenReturn(Optional.empty());

        // When
        ConnectionErrorHandler.dispatchException(outputStream, httpWriter, requestData, endpointResolver, exceptionResolver, exception);

        // Then
        ArgumentCaptor<HttpResponse> responseCaptor = ArgumentCaptor.forClass(HttpResponse.class);
        verify(httpWriter).writeResponse(eq(outputStream), responseCaptor.capture(), eq(requestData.requestLine()), eq(requestData.headers()));
        HttpResponse capturedResponse = responseCaptor.getValue();
        assertThat(capturedResponse.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}
