# FastLeaf

O FastLeaf é um framework Java acadêmico, construído com base na especificação [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616), para simplificar o desenvolvimento de aplicações web, desde o fornecimento de arquivos estáticos até a criação de endpoints RESTful.

Este guia detalha como utilizar o framework em seus projetos.

# Sumário
1. [Arquitetura do Framework](#arquitetura-do-framework)
2. [Começando](#começando)
3. [Configuração](#configuração)
    - [Propriedades da Aplicação](#propriedades-da-aplicação)
    - [Logs](#logs)
4. [Criando Endpoints](#criando-endpoints)
    - [Estrutura Básica](#estrutura-básica)
    - [Acessando Dados da Requisição](#acessando-dados-da-requisição)
        - [Path Variables](#path-variables)
        - [Query Parameters](#query-parameters)
        - [Corpo da Requisição (Body)](#corpo-da-requisição-body)
        - [Headers](#headers)
    - [Construindo a Resposta (HttpResponse)](#construindo-a-resposta-httpresponse)
5. [Middlewares](#middlewares)
6. [Servindo Arquivos Estáticos](#servindo-arquivos-estáticos)

## Arquitetura do Framework

O design do framework é baseado em uma clara separação de responsabilidades, com componentes coesos e desacoplados. O núcleo da lógica de roteamento reside no pacote `router`.

### Visão Geral do Fluxo de Requisição

Uma requisição HTTP passa pelas seguintes camadas principais até gerar uma resposta:

```
[Requisição TCP]
       |
       v
+----------------+   1. Aceita a conexão e a entrega para um handler.
|     server     |
+----------------+
       |
       v
+----------------+   2. Lê a requisição como texto bruto. O `ApiHttpResponseWriter` orquestra
|       io       |      a busca e execução do endpoint, e escreve a resposta final.
+----------------+
       |
       v
+----------------+   3. Converte o texto bruto em um objeto de dados estruturado (`HttpRequestData`).
|     parser     |
+----------------+
       |
       v
+----------------+   4. O "cérebro": `HttpEndpointResolver` encontra o endpoint. `HttpEndpointWrapperFactory`
|     router     |      cria um "wrapper" com os dados extraídos, pronto para execução.
+----------------+
       |
       v
+----------------+   5. Sua classe de negócio, que contém a lógica da aplicação.
|  HttpEndpoint  |
+----------------+
       |
       v
[Resposta HTTP]
```

### Descrição dos Pacotes

A seguir, a função de cada pacote principal e suas classes mais importantes.

*   #### `br.com.leonardo.server`
    *   **Função:** Ponto de entrada e gerenciamento do ciclo de vida do servidor.
    *   **`ServerRunner`**: Classe principal que o usuário invoca. É responsável por instanciar o `HttpEndpointResolver` e iniciar o `Server`.
    *   **`Server`**: Gerencia o `ServerSocket` e o pool de threads. Aceita as conexões TCP e despacha cada uma para um `ConnectionIOHandler`, injetando as dependências necessárias.

*   #### `br.com.leonardo.io`
    *   **Função:** Camada de Entrada/Saída (I/O), responsável pela comunicação de baixo nível com o cliente.
    *   **`ConnectionIOHandler`**: Gerencia o ciclo de vida de uma única conexão. Orquestra a leitura da requisição e aciona o `HttpWriter` apropriado para gerar e escrever a resposta.
    *   **`ApiHttpResponseWriter`**: Implementação de `HttpWriter` para endpoints dinâmicos. Ele orquestra a busca e a preparação do endpoint:
        1.  Usa o `HttpEndpointResolver` (injetado) para encontrar o `HttpEndpoint` correto.
        2.  Invoca o método estático `HttpEndpointWrapperFactory.create()` para obter um `HttpEndpointWrapper`.
        3.  Executa o `wrapper` para gerar a `HttpResponse`.

*   #### `br.com.leonardo.parser`
    *   **Função:** Análise (parsing) da requisição HTTP bruta.
    *   **`HttpRequestFactory`**: Atua como uma fachada (Façade) que usa parsers específicos (`RequestLineParser`, `RequestHeaderParser`) para converter a string da requisição em um objeto `HttpRequestData`.

*   #### `br.com.leonardo.router`
    *   **Função:** O cérebro do framework, responsável pelo roteamento e preparação para a execução.
    *   **`router.core`**: Contém as entidades centrais do roteamento.
        *   **`HttpEndpointResolver`**: Um serviço, injetado onde necessário, cuja única função é encontrar o `HttpEndpoint` que corresponde a uma requisição.
        *   **`HttpEndpoint`**: A classe abstrata que os usuários do framework estendem para criar suas rotas.
        *   **`HttpEndpointWrapper`**: Um objeto que encapsula um `HttpEndpoint` e os dados específicos da requisição (path variables, body, etc.), representando uma "rota resolvida, pronta para ser executada".
        *   **`HttpEndpointWrapperFactory`**: Uma classe utilitária com um método de fábrica estático. Sua função é receber um `HttpEndpoint` e os dados da requisição para criar e retornar um `HttpEndpointWrapper` configurado, usando os `Extractors` no processo.
    *   **`router.matcher`**:
        *   **`UriMatcher` (Interface)**: Define um contrato para classes que comparam uma URI de requisição com um padrão de rota.
        *   **`EndpointUriMatcher` (Composite)**: Agrega múltiplos `UriMatcher`s para testar uma URI contra várias estratégias de correspondência.
    *   **`router.extractor`**:
        *   **`PathVariablesExtractor`, `QueryParametersExtractor`, etc.**: Classes utilitárias focadas em extrair informações específicas de uma requisição.

*   #### `br.com.leonardo.http`
    *   **Função:** Contém os modelos de dados imutáveis (records, enums) que representam os conceitos do protocolo HTTP.
    *   **`HttpRequest`, `HttpResponse`, `HttpMethod`, `HttpStatusCode`**: Representam as estruturas fundamentais do HTTP, servindo como a "linguagem" comum usada em todo o framework.

*   #### `br.com.leonardo.annotation`
    *   **Função:** Definição de anotações e lógica de escaneamento.
    *   **`@Endpoint`**: Anotação usada para marcar uma classe como um endpoint HTTP.
    *   **`EndpointScanner`**: Classe que, na inicialização, varre o classpath em busca de classes anotadas com `@Endpoint` e as registra no `HttpEndpointResolver`.

---

# Começando

Para iniciar, adicione a dependência do framework ao seu `pom.xml`:

```xml
<dependency>
    <groupId>io.github.leonardopinheirolacerda</groupId>
    <artifactId>fastleaf</artifactId>
    <version>1.0.0</version>
</dependency>
```

Em seguida, no método `main` da sua aplicação, chame o método estático `serve` da classe `Server`, passando como argumento a classe que servirá como ponto de partida para a configuração da sua aplicação.

```java
public class App {
    public static void main(String[] args) {
        // Inicia o servidor, escaneia os endpoints e bloqueia a thread principal
        ServerRunner.serve(App.class);
    }
}
```

A classe passada como argumento (`App.class`) é usada para determinar o pacote base a partir do qual o escaneamento de `@Endpoint`s será realizado.


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

Middlewares permitem executar lógicas antes de um endpoint (ex: autenticação, logging). Para criar um, estenda a classe `Middleware` e implemente o método `run`. O framework se encarrega de chamar o próximo middleware na cadeia ou o endpoint final automaticamente após a execução do método `run`.

Middlewares são adicionados a um endpoint através do parâmetro `middlewares` da anotação `@Endpoint` e são executados na ordem declarada.

**Exemplo: Middleware de logging.**
```java
public class LogMiddleware extends Middleware {
    @Override
    public void run(HttpRequest<?> request) {
        System.out.println("Middleware: Requisição recebida com os cabeçalhos: " + request.headers());
        // A lógica para continuar para o próximo middleware ou endpoint é tratada automaticamente pelo framework.
    }
}
```

**Aplicação em um endpoint:**
```java
@Endpoint(
    url = "/users", 
    method = HttpMethod.GET, 
    middlewares = {LogMiddleware.class} // Aplica o middleware
)
public class GetUsersEndpoint extends HttpEndpoint<Void, String> {
    // ...
}
```

# Servindo Arquivos Estáticos

O framework pode servir diversos tipos de arquivos estáticos, como HTML, CSS, JS, imagens (PNG, JPG, GIF), vídeos (MP4, WebM), fontes (TTF, OTF, WOFF) e outros.

1.  Certifique-se de que a propriedade `http.server.static.content.enabled` está como `true` (padrão).
2.  Crie uma pasta dentro de `src/main/resources`. O nome padrão é `static`, mas pode ser alterado pela propriedade `http.server.static.content.path`.
3.  Coloque seus arquivos nesta pasta.

Por exemplo, o arquivo `src/main/resources/static/index.html` estará acessível em `http://localhost:9000/index.html`.
