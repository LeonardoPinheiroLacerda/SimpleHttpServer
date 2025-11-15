# Arquitetura do Framework

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
