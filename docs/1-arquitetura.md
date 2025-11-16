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

A seguir, a função de cada pacote principal e suas classes mais importantes.

*   #### `br.com.leonardo.server`
    *   **Função:** Ponto de entrada e gerenciamento do ciclo de vida do servidor.
    *   **`ServerRunner`**: Classe principal que o usuário invoca. É responsável por escanear os endpoints e iniciar o `Server`.
    *   **`Server`**: Gerencia o `ServerSocket` e o pool de threads (`VirtualThreadPerTaskExecutor`). Aceita as conexões TCP e despacha cada uma para um `ConnectionIOHandler`.

*   #### `br.com.leonardo.io`
    *   **Função:** Camada de Entrada/Saída (I/O), responsável pela comunicação de baixo nível com o cliente.
    *   **`ConnectionIOHandler`**: Gerencia o ciclo de vida de uma única conexão. Usa `HttpWriterFactory` para obter o `HttpWriter` correto e orquestra o fluxo de geração e escrita da resposta. Em caso de erro, utiliza `ConnectionErrorHandler`.
    *   **`output.factory.HttpWriterFactory`**: Classe de fábrica que decide qual implementação de `HttpWriter` (`ApiHttpResponseWriter` ou `StaticHttpResponseWriter`) deve ser usada com base na URI da requisição.
    *   **`ApiHttpResponseWriter`**: Implementação de `HttpWriter` para endpoints de API. Orquestra a busca e preparação do endpoint.
    *   **`StaticHttpResponseWriter`**: Implementação de `HttpWriter` para servir arquivos estáticos.
    *   **`output.util.ContentTypeNegotiation`**: Classe utilitária responsável pela lógica de negociação de `Content-Type` e serialização do corpo da resposta.
    *   **`ConnectionErrorHandler`**: Centraliza o tratamento de exceções durante o processamento da requisição, garantindo que uma resposta de erro formatada seja enviada ao cliente.

*   #### `br.com.leonardo.parser`
    *   **Função:** Análise (parsing) da requisição HTTP bruta.
    *   **`HttpRequestFactory`**: Atua como uma fachada (Façade) que usa parsers específicos (`RequestLineParser`, `RequestHeaderParser`, `RequestBodyParser`) para converter a string da requisição em um objeto `HttpRequestData`.

*   #### `br.com.leonardo.router`
    *   **Função:** O cérebro do framework, responsável pelo roteamento e preparação para a execução.
    *   **`router.core`**: Contém as entidades centrais do roteamento.
        *   **`HttpEndpointResolver`**: Serviço que encontra o `HttpEndpoint` que corresponde a uma dada requisição.
        *   **`HttpEndpoint`**: A classe abstrata que os usuários estendem para criar suas rotas. Contém a lógica de negócio no método `handle` e pode ter uma cadeia de `Middlewares`.
        *   **`HttpEndpointWrapper`**: Encapsula um `HttpEndpoint` e os dados da requisição. É responsável por executar os middlewares e, em seguida, o método `handle` do endpoint.
        *   **`HttpEndpointWrapperFactory`**: Classe utilitária que cria um `HttpEndpointWrapper` configurado, usando os `Extractors` no processo.
        *   **`middleware.Middleware`**: Classe abstrata para a criação de middlewares, que podem ser encadeados para executar lógica (ex: autenticação, logging) antes do `HttpEndpoint`.
    *   **`router.matcher`**:
        *   **`UriMatcher` (Interface)**: Define um contrato para classes que comparam uma URI de requisição com um padrão de rota.
        *   **`EndpointUriMatcher` (Composite)**: Agrega múltiplos `UriMatcher`s para testar uma URI contra várias estratégias.
    *   **`router.extractor`**:
        *   **`PathVariableExtractor`, `QueryParameterExtractor`, `HeaderExtractor`**: Classes utilitárias focadas em extrair informações específicas de uma requisição.

*   #### `br.com.leonardo.http`
    *   **Função:** Contém os modelos de dados que representam os conceitos do protocolo HTTP.
    *   **`HttpRequest`, `HttpResponse`**: Representam as estruturas fundamentais do HTTP, servindo como a "linguagem" comum usada em todo o framework.
    *   **`request.map`**: Contém records como `HeaderMap`, `PathVariableMap` e `QueryParameterMap`, que fornecem uma API segura e conveniente para acessar dados extraídos da requisição.

*   #### `br.com.leonardo.annotation`
    *   **Função:** Definição de anotações e lógica de escaneamento.
    *   **`@Endpoint`**: Anotação usada para marcar uma classe como um endpoint HTTP, definindo sua URL, método e middlewares.
    *   **`EndpointScanner`**: Classe que, na inicialização, varre o classpath em busca de classes anotadas com `@Endpoint` e as registra no `HttpEndpointResolver`.

*   #### `br.com.leonardo.config`
    *   **Função:** Gerencia a configuração da aplicação.
    *   **`ApplicationProperties`**: Carrega e fornece acesso a propriedades definidas no arquivo `http-server.properties`, permitindo configurar porta, logging, etc.

*   #### `br.com.leonardo.observability`
    *   **Função:** Lida com aspectos de observabilidade, como logging e tracing.
    *   **`TraceIdLifeCycleHandler`**: Gerencia um ID de rastreamento (`traceId`) para cada requisição, adicionando-o ao MDC do SLF4J e ao cabeçalho de resposta `X-Trace-Id`.
    *   **`nodetree`**: Utilitário para logar informações em formato de árvore, usado pelo `EndpointScanner`.

*   #### `br.com.leonardo.enums`
    *   **Função:** Centraliza as enumerações utilizadas no framework.
    *   **`HttpMethod`, `HttpStatusCode`, `ContentTypeEnum`**: Enums que representam os conceitos padrão do protocolo HTTP, garantindo consistência e prevenindo erros.

*   #### `br.com.leonardo.exception`
    *   **Função:** Define as exceções customizadas do framework.
    *   **`HttpException`**: Exceção base para erros relacionados ao processamento HTTP, que carrega um `HttpStatusCode` e informações para a resposta de erro.
    *   **`ServerInitializationException`**: Lançada quando ocorre um erro crítico durante a inicialização do servidor.
