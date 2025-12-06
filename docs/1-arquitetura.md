# Arquitetura do Framework

O design do framework é baseado em uma clara separação de responsabilidades, com componentes coesos e desacoplados. O núcleo da lógica de roteamento reside no pacote `router`.

### Visão Geral do Fluxo de Requisição

Uma requisição HTTP passa pelas seguintes camadas principais até gerar uma resposta:

```
[Requisição TCP]
       |
       v
+----------------+   1. Aceita a conexão e a entrega para um `ConnectionIOHandler`.
|     server     |
+----------------+
       |
       v
+----------------+   2. O `ConnectionIOHandler` orquestra o fluxo: lê a requisição,
|       io       |      usa o `parser` para decodificá-la, e aciona o `HttpWriter`
+----------------+      apropriado para gerar e escrever a resposta.
       |
       v
+----------------+   3. Converte o texto bruto da requisição em um objeto `HttpRequestData`.
|     parser     |
+----------------+
       |
       v
+----------------+   4. O `ApiHttpResponseWriter` (de `io`) usa o `router`. O `HttpEndpointResolver`
|     router     |      encontra o endpoint, e o `HttpEndpointWrapperFactory` cria um "wrapper"
+----------------+      com os dados extraídos, pronto para execução.
       |
       v
+----------------+   5. O `wrapper` executa a cadeia de `Middlewares` e, em seguida, o método
|  HttpEndpoint  |      `handle` do endpoint para gerar a `HttpResponse`.
+----------------+
       |
       v
[Resposta HTTP]
```

### Descrição dos Pacotes

A arquitetura da aplicação é organizada na seguinte estrutura de pacotes e classes:

*   `br.com.leonardo`
    *   `annotation`: Define as anotações customizadas e o scanner para processá-las.
        *   `scanner`: Contém o scanner de anotações.
            *   **`EndpointScanner`**: Varre o classpath em busca de classes com a anotação `@Endpoint` para registrá-las como rotas.
        *   **`@Endpoint`**: Anotação para marcar uma classe como um endpoint HTTP, definindo sua URL, método e `Middlewares`.
        *   **`@ExceptionHandler`**: Anotação para marcar uma classe como um manipulador de exceções global.
    *   `config`: Gerencia a configuração da aplicação.
        *   **`ApplicationProperties`**: Carrega e fornece acesso a propriedades do arquivo `http-server.properties`.
        *   **`HighlightingCompositeConverter`**: Componente do Logback para adicionar cores ao log do console.
    *   `enums`: Centraliza as enumerações utilizadas no framework.
        *   **`ContentTypeEnum`**: Enum para tipos de conteúdo MIME (ex: `application/json`).
        *   **`HttpHeaderEnum`**: Enum para nomes de cabeçalhos HTTP padrão.
        *   **`HttpMethod`**: Enum para os métodos do HTTP (GET, POST, etc.).
        *   **`HttpStatusCode`**: Enum para os códigos de status HTTP e seus textos.
        *   **`SupportedStaticContentTypes`**: Enum que mapeia extensões de arquivo para tipos de conteúdo MIME para servir arquivos estáticos.
    *   `exception`: Define as exceções customizadas do framework.
        *   **`HttpException`**: Exceção base para erros de processamento HTTP, que carrega um `HttpStatusCode`.
        *   **`HttpMiddlewareException`**: Subclasse de `HttpException` para erros que ocorrem em `Middlewares`.
        *   **`ServerInitializationException`**: Lançada quando ocorre um erro crítico durante a inicialização do servidor.
        *   `handler`: Contém a lógica de tratamento de erros.
            *   `impl`: Implementações padrão de manipuladores de erro.
                *   **`InternalServerErrorHttpExceptionHandler`**: Manipulador padrão para erros 500.
                *   **`HttpHttpExceptionHandler`**: Manipulador para `HttpException`.
                *   **`HttpMiddlewareHttpExceptionHandler`**: Manipulador para `HttpMiddlewareException`.
            *   `model`: Modelos de dados para o tratamento de erros.
                *   **`ProblemDetails`**: Objeto que encapsula os detalhes do erro (RFC 7807).
            *   **`HttpExceptionHandler`**: Interface para criar manipuladores de erro customizados.
            *   **`HttpExceptionHandlerResolver`**: Resolve o manipulador de erro apropriado para uma exceção.
            *   **`StandardHttpExceptionHandlersFactory`**: Cria os manipuladores de erro padrão.
    *   `http`: Contém os modelos de dados que representam os conceitos do protocolo HTTP.
        *   `request`: Contém as classes relacionadas à requisição HTTP.
            *   `map`: Contém records que fornecem uma API segura para acessar dados da requisição.
                *   **`HeaderMap`**: Wrapper para os cabeçalhos da requisição.
                *   **`PathVariableMap`**: Wrapper para as variáveis de caminho da URI.
                *   **`QueryParameterMap`**: Wrapper para os parâmetros da query string.
            *   **`HttpRequest`**: Record que representa a requisição HTTP recebida, com acesso ao corpo, cabeçalhos e outros dados.
        *   `response`: Contém as classes relacionadas à resposta HTTP.
            *   **`HttpResponse`**: Representa a resposta HTTP a ser enviada, incluindo status, cabeçalhos e corpo.
            *   **`HttpResponseBuilder`**: Builder para construir um objeto `HttpResponse` de forma fluente.
        *   **`HttpHeader`**: Record que representa um único cabeçalho HTTP (par nome/valor).
        *   **`RequestLine`**: Record que representa a linha inicial de uma requisição HTTP (método, URI, versão).
    *   `io`: Camada de Entrada/Saída (I/O), responsável pela comunicação de baixo nível com o cliente.
        *   `input`: Lida com a leitura da requisição.
            *   **`HttpRequestReader`**: Lê os dados brutos da requisição a partir do `InputStream` da conexão.
        *   `output`: Lida com a escrita da resposta.
            *   `factory`: Contém a fábrica de `HttpWriter`.
                *   **`HttpWriterFactory`**: Decide qual `HttpWriter` usar (`ApiHttpResponseWriter` ou `StaticHttpResponseWriter`).
            *   `util`: Utilitários para a escrita da resposta.
                *   **`ContentTypeNegotiation`**: Lida com a negociação de `Content-Type` e serialização do corpo da resposta.
            *   **`ApiHttpResponseWriter`**: `HttpWriter` para endpoints de API, que orquestra a busca e execução do endpoint.
            *   **`HttpWriter`**: Interface que define o contrato para classes que escrevem respostas HTTP.
            *   **`StaticHttpResponseWriter`**: `HttpWriter` para servir arquivos estáticos.
        *   **`ConnectionErrorHandler`**: Centraliza o tratamento de exceções, usando o `HttpExceptionHandlerResolver` para encontrar um handler e gerar uma resposta de erro.
        *   **`ConnectionIOHandler`**: Orquestra o ciclo de vida de uma única conexão TCP, desde a leitura até a escrita da resposta.
    *   `observability`: Lida com aspectos de observabilidade, como logging e tracing.
        *   `nodetree`: Utilitário para logar informações em formato de árvore.
            *   **`Node`**: Representa um nó na estrutura de árvore para o log.
            *   **`TreeNodeLogger`**: Percorre uma estrutura de `Node` e a imprime no log.
        *   **`TraceIdLifeCycleHandler`**: Gerencia um `traceId` para cada requisição, integrando-o ao log (MDC) e aos cabeçalhos de resposta.
    *   `parser`: Responsável pela análise (parsing) da requisição HTTP bruta.
        *   `factory`: Contém a fábrica de `HttpRequest`.
            *   `model`: Modelo de dados para o parser.
                *   **`HttpRequestData`**: Record que armazena os dados brutos da requisição após o parsing inicial.
            *   **`HttpRequestFactory`**: Fachada (Façade) que usa os outros parsers para criar um objeto `HttpRequestData`.
        *   **`RequestBodyParser`**: Extrai o corpo (body) da requisição bruta.
        *   **`RequestHeaderParser`**: Extrai os cabeçalhos da requisição bruta.
        *   **`RequestLineParser`**: Extrai a linha inicial (método, URI, versão) da requisição bruta.
    *   `router`: O cérebro do framework, responsável pelo roteamento e preparação para a execução.
        *   `core`: Contém as entidades centrais do roteamento.
            *   `middleware`: Contém a abstração de `Middleware`.
                *   **`Middleware`**: Classe abstrata para middlewares, que podem ser encadeados para executar lógicas antes do endpoint.
            *   **`HttpEndpoint`**: Classe abstrata que os usuários estendem para criar suas rotas (endpoints).
            *   **`HttpEndpointResolver`**: Serviço que encontra o `HttpEndpoint` que corresponde a uma dada requisição.
            *   **`HttpEndpointWrapper`**: Encapsula um `HttpEndpoint` e os dados da requisição, executando middlewares e o método `handle`.
            *   **`HttpEndpointWrapperFactory`**: Classe utilitária que cria um `HttpEndpointWrapper`.
        *   `extractor`: Contém classes que extraem dados da requisição para uso no endpoint.
            *   **`HeaderExtractor`**: Extrai cabeçalhos da requisição.
            *   **`PathVariableExtractor`**: Extrai variáveis do caminho da URI.
            *   **`QueryParameterExtractor`**: Extrai parâmetros da query string.
        *   `matcher`: Contém classes que comparam a URI da requisição com os padrões de rota.
            *   **`EndpointUriMatcher`**: `UriMatcher` composto que agrega múltiplas estratégias de match.
            *   **`PathVariableUriMatcher`**: Estratégia de match que suporta variáveis de caminho (ex: `/users/{id}`).
            *   **`QueryParameterUriMatcher`**: Estratégia de match que ignora a query string.
            *   **`UriMatcher`**: Interface que define o contrato para as classes de comparação de URI.
    *   `server`: Ponto de entrada e gerenciamento do ciclo de vida do servidor.
        *   **`Server`**: Gerencia o `ServerSocket` e o pool de threads, aceitando conexões e despachando-as para o `ConnectionIOHandler`.
        *   **`ServerRunner`**: Contém o método `serve` que o usuário invoca para iniciar toda a aplicação.
