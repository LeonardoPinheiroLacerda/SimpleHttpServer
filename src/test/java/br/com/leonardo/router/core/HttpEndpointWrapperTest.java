package br.com.leonardo.router.core;

import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.router.core.middleware.Middleware;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class HttpEndpointWrapperTest {

    record Person(String name, Integer age) {}

    private HttpEndpointWrapper<String, String> underTest;

    private HttpEndpointWrapper<Person, Person> personUnderTest;

    @Mock
    private HttpEndpoint<String, String> httpEndpoint;

    @Mock
    private HttpRequest<String> httpRequest;

    @Mock
    private HttpResponse<String> httpResponse;

    @Mock
    private HttpEndpoint<Person, Person> httpPersonEndpoint;

    @Mock
    private HttpRequest<Person> httpPersonRequest;

    @Mock
    private HttpResponse<Person> httpPersonResponse;

    @Mock
    private Middleware middleware1;

    @Mock
    private Middleware middleware2;

    @Test
    void shouldRunTwoMiddlewares() {

        //Given
        underTest = new HttpEndpointWrapper<>(
                httpEndpoint,
                "anyBody".getBytes(),
                httpRequest
        );

        Mockito
                .when(httpEndpoint.getMiddlewares())
                .thenReturn(List.of(middleware1, middleware2));

        httpEndpoint.addMiddleware(middleware1);
        httpEndpoint.addMiddleware(middleware2);

        //When + Then
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> underTest.runMiddlewares());

    }

    @Test
    void shouldRunNoMiddlewaresBecauseIsEmpty() {

        //Given
        underTest = new HttpEndpointWrapper<>(
                httpEndpoint,
                "anyBody".getBytes(),
                httpRequest
        );

        //When + Then
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> underTest.runMiddlewares());

    }

    @Test
    void shouldCreateAnHttpResponseForVoidType() throws Exception {

        //Given
        underTest = new HttpEndpointWrapper<>(
                httpEndpoint,
                "anyBody".getBytes(),
                httpRequest
        );

        Mockito
                .when(httpEndpoint.resolveInputType())
                .thenReturn(Void.class);

        Mockito
                .when(httpEndpoint.handle(Mockito.any(HttpRequest.class)))
                .thenReturn(httpResponse);

        //When
        final HttpResponse<String> response = underTest.createResponse();

        //Then
        Assertions
                .assertThat(response)
                .isNotNull()
                .isEqualTo(this.httpResponse);
    }

    @Test
    void shouldNotCreateAnHttpResponseBecauseException() {

        //Given

        byte[] body = "{\"name\":\"\",\"age\":25}".getBytes();

        personUnderTest = new HttpEndpointWrapper<>(
                httpPersonEndpoint,
                body,
                httpPersonRequest
        );

        Mockito
                .when(httpPersonEndpoint.resolveInputType())
                .thenReturn(Person.class);

        Mockito
                .when(httpPersonEndpoint.handle(Mockito.any(HttpRequest.class)))
                .thenThrow(new IndexOutOfBoundsException());


        //When + Then

        Assertions
                .assertThatThrownBy(personUnderTest::createResponse)
                .isInstanceOf(Exception.class);

    }

    @Test
    void shouldBeEquals() {

        //Given
        final byte[] bytes = "anyBody".getBytes();

        //When
        HttpEndpointWrapper<String, String> underTest2 = new HttpEndpointWrapper<>(httpEndpoint, bytes, httpRequest);
        HttpEndpointWrapper<String, String> underTest3 = new HttpEndpointWrapper<>(httpEndpoint, bytes, httpRequest);

        //Then
        Assertions
                .assertThat(underTest2)
                .isEqualTo(underTest3);

    }

    @Test
    void shouldBeEqualsUsingHashCode() {

        //Given
        final byte[] bytes = "anyBody".getBytes();

        //When
        int underTest2 = new HttpEndpointWrapper<>(httpEndpoint, bytes, httpRequest).hashCode();
        int underTest3 = new HttpEndpointWrapper<>(httpEndpoint, bytes, httpRequest).hashCode();

        //Then
        Assertions
                .assertThat(underTest2)
                .isEqualTo(underTest3);

    }

}