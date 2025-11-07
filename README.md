# SimpleHttpServer

O SimpleHttpServer é um framework Java acadêmico, construído com base na especificação [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616), para simplificar o desenvolvimento de aplicações web, desde o fornecimento de arquivos estáticos até a criação de endpoints RESTful.

Este guia detalha como utilizar o framework em seus projetos.

# Sumário
1. [Começando](#começando)
2. [Configuração](#configuração)
    - [Propriedades da Aplicação](#propriedades-da-aplicação)
    - [Logs](#logs)
3. [Criando Endpoints](#criando-endpoints)
    - [Estrutura Básica](#estrutura-básica)
    - [Acessando Dados da Requisição](#acessando-dados-da-requisição)
        - [Path Variables](#path-variables)
        - [Query Parameters](#query-parameters)
        - [Corpo da Requisição (Body)](#corpo-da-requisição-body)
        - [Headers](#headers)
    - [Construindo a Resposta (HttpResponse)](#construindo-a-resposta-httpresponse)
4. [Middlewares](#middlewares)
5. [Servindo Arquivos Estáticos](#servindo-arquivos-estáticos)

# Começando

Para iniciar, adicione a dependência do framework ao seu `pom.xml`:

```xml
<dependency>
    <groupId>br.com.leonardo</groupId>
    <artifactId>HttpServer</artifactId>
    <version>1.0.0</version>
</dependency>
```

Em seguida, no método `main` da sua aplicação, chame o método estático `serve` da classe `Server`, passando como argumento a classe que servirá como ponto de partida para a configuração da sua aplicação.

```java
public class App {
    public static void main(String[] args) {
        // Inicia o servidor e bloqueia a thread principal
        Server.serve(App.class);
    }
}
```

A classe passada como argumento (neste caso, `App.class`) é fundamental. O framework a utiliza para determinar o pacote base (`base-package`) a partir do qual o escaneamento de componentes, como os seus `@Endpoint`s, será realizado. Apenas as classes dentro do pacote da classe `App` e seus subpacotes serão registradas.

# Configuração

Você pode customizar o comportamento do servidor criando um arquivo `http-server.properties` na pasta `src/main/resources`.

### Propriedades da Aplicação

| Nome da propriedade                | Descrição                                                                   | Valor Padrão  |
|------------------------------------|-----------------------------------------------------------------------------|---------------|
| `http.server.port`                   | Porta HTTP onde a aplicação irá rodar.                                      | `9000`        |
| `http.server.static.content.enabled` | Habilita ou desabilita o recurso de servir arquivos estáticos.              | `true`        |
| `http.server.static.content.path`    | Define o caminho (dentro de `resources`) que contém os arquivos estáticos.  | `static`      |

### Logs

O framework utiliza SLF4J com Logback para um sistema de logs flexível.

#### Propriedades de Log

As seguintes propriedades podem ser configuradas no arquivo `http-server.properties`:

| Nome da propriedade                 | Descrição                                                                            | Valor Padrão |
|-------------------------------------|--------------------------------------------------------------------------------------|--------------|
| `log.level`                         | Altera o nível de log raiz da aplicação (`ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`). | `INFO`       |
| `log.pattern`                       | Define o padrão de formatação das mensagens de log no `logback.xml`.                 | (Padrão do Logback) |
| `http.server.log.detailed-request`  | Se `true`, o conteúdo detalhado das requisições HTTP será logado.                    | `false`      |
| `http.server.log.detailed-response` | Se `true`, o conteúdo detalhado das respostas HTTP será logado.                      | `false`      |

#### Customização Avançada (logback.xml)
Para customizações avançadas (ex: formatters, appenders), crie um arquivo `logback.xml` em `src/main/resources`. O framework fornece um `PatternLayout` customizado que adiciona cores ao output do console, melhorando a legibilidade.

**Exemplo de `logback.xml`:**
```xml
<configuration>
    <conversionRule conversionWord="clr" converterClass="br.com.leonardo.config.HighlightingCompositeConverter"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%clr(%d{HH:mm:ss.SSS}){faint} %clr(%-5level) %clr([%15.15t]){faint} %clr(%-36.36logger{36}){cyan} - %m%n</pattern>
        </encoder>
    </appender>

    <root level="${log.level:-INFO}">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

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
    // getters e setters
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

> As classes DTO (classes que serão serializadas para JSON na resposta HTTP) devem conter getters e setters, para que dessa forma o framework possa serializar e deserializar os dados.

### Acessando Dados da Requisição

O método `handle` recebe um objeto `HttpRequest` que contém todas as informações da requisição.

#### Path Variables
Para definir path variables, use chaves `{}` na URI do endpoint (ex: `/users/{id}`). O valor pode ser acessado através do mapa `pathVariables` do objeto `request`.

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
Query parameters (ex: `/search?q=my-query`) são acessados através do mapa `queries` do objeto `request`.

**Exemplo:**
```java
@Endpoint(url = "/search", method = HttpMethod.GET)
public class SearchEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String query = request.queries().get("q");
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
Os cabeçalhos da requisição podem ser acessados através do mapa `headers` do objeto `request`. Para recuperar um cabeçalho específico, utilize o nome do cabeçalho como chave no mapa.

**Exemplo:**
```java
@Endpoint(url = "/echo-user-agent", method = HttpMethod.GET)
public class EchoUserAgentEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) {
        final String userAgent = request.headers().get("User-Agent");
        return HttpResponse
            .<String> builder()
            .statusCode(HttpStatusCode.OK)
            .body("User-Agent: " + userAgent)
            .build();
    }
}
```

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

# Middlewares

Middlewares permitem executar lógicas antes de um endpoint (ex: autenticação, logging). Para criar um, estenda a classe `Middleware` e implemente o método `run`. Para passar a requisição adiante, chame `this.handle(request, response)`.

Middlewares são adicionados a um endpoint através do parâmetro `middlewares` da anotação `@Endpoint` e são executados na ordem declarada.

**Exemplo: Middleware de logging.**
```java
public class LogMiddleware extends Middleware {
    @Override
    public <I, O> void run(HttpRequest<I> request, HttpResponse<O> response) {
        System.out.println("Middleware: Requisição recebida com os cabeçalhos: " + request.headers());
        
        // Continua para o próximo middleware ou para o endpoint
        this.handle(request, response); 
        
        System.out.println("Middleware: Resposta enviada.");
    }
}
```

**Aplicação em um endpoint:**
```java
@Endpoint(
    url = "/protected-resource", 
    method = HttpMethod.GET, 
    middlewares = {LogMiddleware.class} // Aplica o middleware
)
public class ProtectedEndpoint extends HttpEndpoint<Void, String> {
    // ...
}
```

# Servindo Arquivos Estáticos

O framework pode servir arquivos estáticos (HTML, CSS, JS, imagens).

1.  Certifique-se de que a propriedade `http.server.static.content.enabled` está como `true` (padrão).
2.  Crie uma pasta dentro de `src/main/resources`. O nome padrão é `static`, mas pode ser alterado pela propriedade `http.server.static.content.path`.
3.  Coloque seus arquivos nesta pasta.

Por exemplo, o arquivo `src/main/resources/static/index.html` estará acessível em `http://localhost:9000/index.html`.