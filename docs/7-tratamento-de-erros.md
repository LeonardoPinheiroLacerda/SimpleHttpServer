# Tratamento de Erros

O framework fornece um mecanismo robusto e flexível para tratamento de exceções, permitindo que você centralize a lógica de erro e forneça respostas HTTP consistentes (como RFC 7807 Problem Details) para seus clientes.

## Visão Geral

O tratamento de erros é baseado na interface `HttpExceptionHandler` e na anotação `@ExceptionHandler`. Quando uma exceção é lançada durante o processamento de uma requisição (seja no endpoint, middleware ou durante o parsing), o framework busca um manipulador registrado para aquele tipo de exceção e o executa.

### Componentes Principais

*   **`HttpExceptionHandler<T, O>`**: Interface que você deve implementar para criar um manipulador de erro. `T` é o tipo da exceção e `O` é o tipo do corpo da resposta.
*   **`@ExceptionHandler`**: Anotação usada para registrar sua classe como um manipulador de erros. O framework descobre essas classes automaticamente na inicialização.
*   **`ProblemDetails`**: Objeto que encapsula os detalhes do erro, incluindo a requisição original e o `traceId`, facilitando a depuração.

## Criando um Manipulador de Erros

Para criar um manipulador de erros customizado:

1.  Crie uma classe que estenda `HttpExceptionHandler<SuaException, SeuCorpoDeResposta>`.
2.  Anote a classe com `@ExceptionHandler`.
3.  Implemente o método `handle`.

**Exemplo: Manipulador para `ResourceNotFoundException`**

Suponha que você tenha uma exceção de negócio:

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

Você pode criar um manipulador para ela:

```java
import br.com.leonardo.annotation.ExceptionHandler;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.enums.HttpStatusCode;

import java.util.Map;

@ExceptionHandler
public class ResourceNotFoundExceptionHandler extends HttpExceptionHandler<ResourceNotFoundException, Map<String, String>> {

    @Override
    public HttpResponse<Map<String, String>> handle(ProblemDetails problemDetails, ResourceNotFoundException exception) {
        return HttpResponse
                .<Map<String, String>>builder()
                .statusCode(HttpStatusCode.NOT_FOUND)
                .body(Map.of(
                    "error", "Not Found",
                    "message", exception.getMessage(),
                    "traceId", problemDetails.getTraceId()
                ))
                .build();
    }
}
```

### ProblemDetails

O objeto `ProblemDetails` passado para o método `handle` fornece contexto sobre o erro:

*   `getRequest()`: A `HttpRequest` que causou o erro (pode ser parcial se o erro ocorreu durante o parsing).
*   `getTraceId()`: O ID de rastreamento único da requisição, útil para logs e correlação de erros.

## Prioridade e Resolução

O framework resolve o manipulador de exceção mais específico para a exceção lançada.

1.  Procura um manipulador registrado exatamente para a classe da exceção.
2.  Se não encontrar, procura recursivamente por manipuladores das superclasses da exceção.
3.  Se nenhum manipulador for encontrado, o `InternalServerErrorHttpExceptionHandler` padrão é usado, retornando um erro 500 genérico.

> **Nota:** O framework impede que múltiplos manipuladores sejam registrados para o mesmo tipo de exceção, lançando uma `ServerInitializationException` na inicialização se isso ocorrer.

## Manipuladores Padrão

O framework já vem com alguns manipuladores pré-configurados:

*   **`InternalServerErrorHttpExceptionHandler`**: Captura qualquer `Throwable` não tratado e retorna um status 500 (Internal Server Error).
*   **`HttpHttpExceptionHandler`**: Captura `HttpException` (exceção base do framework) e retorna o status code definido na própria exceção.
*   **`HttpMiddlewareExceptionHandler`**: Captura erros ocorridos em middlewares.

Você pode sobrescrever o comportamento padrão registrando um manipulador mais específico, se desejar, mas geralmente os padrões cobrem os casos de infraestrutura.
