package br.com.leonardo.annotation;

import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.middleware.Middleware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Endpoint {
    String url();
    HttpMethod method() default HttpMethod.GET;
    Class<? extends Middleware>[] middlewares() default {};
}
