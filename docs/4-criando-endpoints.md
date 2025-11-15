# Criando Endpoints

Endpoints são o coração da sua API. Eles são classes que manipulam as requisições recebidas.

## Recomendações para organização dos endpoints

Crie pacotes em árvore para cada dominio da sua aplicação, como `com.mycompany.endpoints.users`, `com.mycompany.endpoints.products` e assim por diante.

Cada endpoint é representado por uma classe separada, que deve ser criado no pacote correspondente. Assim promovendo uma melhor organização dos seus recursos.

### Estrutura Básica

1.  Crie uma classe que estenda `HttpEndpoint<I, O>`, onde `I` é o tipo do corpo da requisição (Request Body) e `O` é o tipo do corpo da resposta (Response Body). Se não houver corpo, utilize o tipo `Void`.
2.  Anote a classe com `@Endpoint`, especificando a `uri` e o `method`.

O corpo da requisição e da resposta será automaticamente (de)serializado de/para JSON.

**Exemplo: Endpoint GET que retorna uma lista de usuários.**
```java
// DTO para a resposta
public class UserDTO {
    private String name;

    public UserDTO() {
    }

    public UserDTO(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}

// Endpoint
@Endpoint(url = "/users", method = HttpMethod.GET)
public class GetUsersEndpoint extends HttpEndpoint<Void, List<UserDTO>> {

    @Override
    public HttpResponse<List<UserDTO>> handle(HttpRequest<Void> request) {
        // Lógica para buscar usuários
        List<UserDTO> users = List.of(new UserDTO("John Doe"));
        return HttpResponse
                .<List<UserDTO>> builder()
                .statusCode(HttpStatusCode.OK)
                .body(users)
                .build();
    }
}
```

> As classes DTO (classes que serão serializadas para JSON na resposta HTTP) devem conter getters, setters e um construtor sem argumentos, para que dessa forma o framework possa serializar e deserializar os dados.

### Acessando Dados da Requisição

O método `handle` recebe um objeto `HttpRequest` que contém todas as informações da requisição.

#### Path Variables
Para definir path variables, use chaves `{}` na URI do endpoint (ex: `/users/{id}`). Os valores são acessíveis através do objeto `PathVariableMap` retornado por `request.pathVariables()`.

O `PathVariableMap` oferece métodos convenientes para acessar e converter os valores das variáveis de caminho para diferentes tipos:

-   `getString(String name)`: Retorna o valor da variável como `String`. Lança `HttpException` se a variável não for encontrada.
-   `getInteger(String name)`: Retorna o valor da variável como `Integer`. Lança `HttpException` se a variável não for encontrada ou não for um número inteiro válido.
-   `getLong(String name)`: Retorna o valor da variável como `Long`. Lança `HttpException` se a variável não for encontrada ou não for um número longo válido.
-   `getBoolean(String name)`: Retorna o valor da variável como `Boolean`. Lança `HttpException` se a variável não for encontrada ou não for "true" ou "false" (case-insensitive).
-   `exists(String name)`: Verifica se uma variável de caminho com o nome especificado existe.

**Exemplo:**
```java
@Endpoint(url = "/users/{id}", method = HttpMethod.GET)
public class GetUserByIdEndpoint extends HttpEndpoint<Void, UserDTO> {
    @Override
    public HttpResponse<UserDTO> handle(HttpRequest<Void> request) {
        final String userId = request.pathVariables().getString("id");
        // Lógica para buscar o usuário
        UserDTO user = new UserDTO(userId);
        return HttpResponse
                .<UserDTO> builder()
                .statusCode(HttpStatusCode.OK)
                .body(user)
                .build();
    }
}
```

#### Query Parameters
Query parameters (ex: `/search?q=my-query`) são acessados através do método `request.queryParameters()`, que retorna um objeto `QueryParameterMap`.

Como query parameters são opcionais por natureza em uma requisição HTTP, os métodos de `QueryParameterMap` retornam um `Optional<T>`. Isso permite um tratamento elegante para casos onde o parâmetro pode ou não estar presente, evitando `NullPointerException`s e tornando o código mais robusto e legível.

O `QueryParameterMap` oferece os seguintes métodos:

-   `getString(String name)`: Retorna um `Optional<String>` contendo o valor do parâmetro.
-   `getInteger(String name)`: Tenta converter o parâmetro para `Integer` e retorna um `Optional<Integer>`. Lança `HttpException` se o valor não for um número inteiro válido.
-   `getLong(String name)`: Tenta converter o parâmetro para `Long` e retorna um `Optional<Long>`. Lança `HttpException` se o valor não for um número longo válido.
-   `getBoolean(String name)`: Tenta converter o parâmetro para `Boolean` e retorna um `Optional<Boolean>`. Lança `HttpException` se o valor não for "true" ou "false".
-   `exists(String name)`: Verifica se um query parameter com o nome especificado existe na requisição.

**Exemplo:**
```java
@Endpoint(url = "/search", method = HttpMethod.GET)
public class SearchEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String query = request
                .queryParameters()
                .getString("q")
                .orElse("default");

        return HttpResponse
                .<String> builder()
                .statusCode(HttpStatusCode.OK)
                .body("Você buscou por: " + query)
                .build();
    }
}

```

#### Corpo da Requisição (Body)
Para endpoints que recebem um corpo (ex: POST, PUT), defina o tipo genérico `I` na sua classe. O framework desserializará o JSON para um objeto desse tipo, que pode ser acessado via `request.body()`.

**Exemplo:**
```java
// DTO para o corpo da requisição
public class CreateUserDTO {
    private String name;

    public CreateUserDTO() {
    }

    public CreateUserDTO(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}

@Endpoint(url = "/users", method = HttpMethod.POST)
public class CreateUserEndpoint extends HttpEndpoint<CreateUserDTO, Void> {
    @Override
    public HttpResponse<Void> handle(HttpRequest<CreateUserDTO> request) {
        CreateUserDTO userToCreate = request.body();
        System.out.println("Criando usuário: " + userToCreate.getName());
        return HttpResponse
            .<Void> builder()
            .statusCode(HttpStatusCode.CREATED)
            .build();
    }
}
```

#### Headers
Os cabeçalhos da requisição podem ser acessados através do método `request.headers()`, que retorna um objeto `HeaderMap`.

Como os cabeçalhos são opcionais em uma requisição HTTP, os métodos de `HeaderMap` retornam um `Optional<T>`. Isso permite um tratamento elegante para casos onde o cabeçalho pode ou não estar presente, evitando `NullPointerException`s e tornando o código mais robusto e legível.

O `HeaderMap` oferece os seguintes métodos:

-   `getString(String name)`: Retorna um `Optional<String>` contendo o valor do cabeçalho.
-   `getInteger(String name)`: Tenta converter o cabeçalho para `Integer` e retorna um `Optional<Integer>`. Lança `HttpException` se o valor não for um número inteiro válido.
-   `getLong(String name)`: Tenta converter o cabeçalho para `Long` e retorna um `Optional<Long>`. Lança `HttpException` se o valor não for um número longo válido.
-   `getBoolean(String name)`: Tenta converter o cabeçalho para `Boolean` e retorna um `Optional<Boolean>`. Lança `HttpException` se o valor não for "true" ou "false".
-   `exists(String name)`: Verifica se um cabeçalho com o nome especificado existe na requisição.

**Exemplo:**
```java
@Endpoint(url = "/echo-user-agent", method = HttpMethod.GET)
public class EchoUserAgentEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String userAgent = request
                .headers()
                .getString("User-Agent")
                .orElse("Unknown");
        return HttpResponse
                .<String> builder()
                .statusCode(HttpStatusCode.OK)
                .body("User-Agent: " + userAgent)
                .build();
    }
}
```

#### Propriedades do Middleware
Além dos dados padrão de uma requisição HTTP, o objeto `HttpRequest` pode carregar dados customizados adicionados por `Middlewares`. Isso permite uma comunicação segura e desacoplada entre a lógica de infraestrutura (como autenticação) e a lógica de negócio do endpoint.

Os dados são acessados através de métodos específicos no objeto `request`:

-   `getMiddlewareProperty(String key, Class<T> clazz)`: Recupera uma propriedade adicionada por um middleware, convertendo-a para o tipo `clazz`.
-   `hasMiddlewareProperty(String key)`: Verifica se uma propriedade existe.

Para um exemplo detalhado de como adicionar e consumir essas propriedades, consulte a seção [Middlewares](./5-middlewares.md).

### Construindo a Resposta (HttpResponse)

O objeto `HttpResponse` é o que seu endpoint deve retornar. Ele contém o status, cabeçalhos e o corpo da resposta. A construção de um `HttpResponse` é feita exclusivamente através do `HttpResponse.builder()`.

O builder permite configurar cada parte da resposta de forma fluente:
- `HttpResponse.builder()`: Inicia a construção de uma resposta.
- `.statusCode(HttpStatusCode)`: Define o código de status HTTP da resposta.
- `.header(String, Object)`: Adiciona um cabeçalho à resposta. Pode ser chamado múltiplas vezes para adicionar vários cabeçalhos.
- `.body(Object)`: Define o corpo da resposta. O objeto será serializado para JSON automaticamente.
- `.build()`: Finaliza a construção e retorna o objeto `HttpResponse`. Este método é genérico e infere o tipo do corpo da resposta (`O`) a partir do tipo definido na assinatura do método `handle` do seu `HttpEndpoint`.

**Exemplo Completo: Retornando um arquivo para download com cabeçalhos customizados.**
```java
@Endpoint(url = "/download-report", method = HttpMethod.GET)
public class DownloadReportEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        String reportContent = "id,name\n1,John Doe";

        return HttpResponse
            .<String> builder()
            .statusCode(HttpStatusCode.OK)
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=\"report.csv\"")
            .body(reportContent)
            .build();
    }
}
```
