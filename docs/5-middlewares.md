# Middlewares

Middlewares são componentes poderosos que interceptam requisições HTTP antes que elas cheguem ao `HttpEndpoint`. Eles permitem executar lógicas transversais, como autenticação, logging, compressão e manipulação de headers, de forma modular e reutilizável.

Para criar um middleware, estenda a classe abstrata `Middleware` e implemente o método `run`. A responsabilidade de invocar o próximo middleware na cadeia recai sobre o desenvolvedor, que deve chamar `super.next(request)`. Se `super.next(request)` não for chamado, a cadeia de execução será interrompida e o endpoint final será executado. Caso não haja um próximo middleware, a chamada a `super.next(request)` não terá efeito.

Middlewares são associados a um endpoint através do parâmetro `middlewares` da anotação `@Endpoint` e são executados na ordem em que são declarados.

### Comunicação entre Middlewares e Endpoints

Uma das funcionalidades mais importantes dos middlewares é a capacidade de compartilhar dados com os próximos middlewares na cadeia e com o `HttpEndpoint` final. Isso é feito através de um mapa de propriedades que é transportado dentro do objeto `HttpRequest`.

Um middleware pode, por exemplo, validar um token de autenticação e, em seguida, adicionar o ID do usuário autenticado a essas propriedades. O endpoint pode então acessar esse ID de forma segura, sem precisar conhecer os detalhes da lógica de autenticação.

O objeto `HttpRequest` fornece métodos específicos para essa comunicação:
-   `addMiddlewareProperty(String key, Object value)`: Adiciona um novo dado ao contexto da requisição.
-   `getMiddlewareProperty(String key, Class<T> clazz)`: Recupera um dado do contexto, fazendo o cast para o tipo esperado.
-   `hasMiddlewareProperty(String key)`: Verifica se um dado existe no contexto.
-   `removeMiddlewareProperty(String key)`: Remove um dado do contexto.
-   `clearMiddlewareProperties()`: Remove todos os dados do contexto.

**Exemplo: Cadeia de Middlewares (Logging e Autenticação)**

Vamos criar uma cadeia com dois middlewares: o primeiro para logging e o segundo para autenticação.

**1. Middleware de Logging**

Este middleware registra a chegada de uma requisição e, em seguida, passa o controle para o próximo componente na cadeia.

```java
public class LoggingMiddleware extends Middleware {
    private static final Logger logger = LoggerFactory.getLogger(LoggingMiddleware.class);

    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        logger.info("Requisição recebida para: {} {}", request.getMethod(), request.getUri());

        // Chama o próximo middleware na cadeia para continuar o processamento
        super.next(request);
    }
}
```

**2. Middleware de Autenticação**

Este middleware simula a validação de um token e adiciona o ID do usuário à requisição. Se a autenticação falhar, ele interrompe a cadeia lançando uma exceção.

```java
public class AuthenticationMiddleware extends Middleware {
    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        // Em um cenário real, você validaria um token (ex: JWT)
        final String token = request.headers().getString("Authorization").orElse(null);

        if (token == null || !token.equals("Bearer valid-token")) {
            // Lança uma exceção para interromper o fluxo e retornar um erro 401
            throw new HttpMiddlewareException(HttpStatusCode.UNAUTHORIZED, "Token inválido ou ausente.");
        }

        // Token válido, extrai o ID do usuário (simulado) e o adiciona à requisição
        String userId = "user-123";
        request.addMiddlewareProperty("authenticatedUserId", userId);

        // Chama o próximo middleware na cadeia para continuar o processamento
        super.next(request);
    }
}
```

**Aplicação em um Endpoint**

O endpoint agora aplica os dois middlewares. Eles serão executados na ordem declarada: primeiro `LoggingMiddleware`, depois `AuthenticationMiddleware`.

```java
@Endpoint(
    url = "/profile", 
    method = HttpMethod.GET, 
    middlewares = {LoggingMiddleware.class, AuthenticationMiddleware.class} // Aplica os middlewares em ordem
)
public class GetProfileEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        // Recupera o ID do usuário adicionado pelo middleware de autenticação
        String userId = request.getMiddlewareProperty("authenticatedUserId", String.class);

        // Lógica para buscar o perfil do usuário
        String userProfile = "Perfil do usuário: " + userId;

        return HttpResponse
                .<String>builder()
                .statusCode(HttpStatusCode.OK)
                .body(userProfile)
                .build();
    }
}
```
