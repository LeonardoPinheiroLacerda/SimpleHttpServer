# Configuração

Você pode customizar o comportamento do servidor criando um arquivo `http-server.properties` na pasta `src/main/resources`.

### Propriedades da Aplicação

| Nome da propriedade                  | Descrição                                                                  | Valor Padrão |
|--------------------------------------|----------------------------------------------------------------------------|--------------|
| `http.server.port`                   | Porta HTTP onde a aplicação irá rodar.                                     | `9000`       |
| `http.server.static.content.enabled` | Habilita ou desabilita o recurso de servir arquivos estáticos.             | `true`       |
| `http.server.static.content.path`    | Define o caminho (dentro de `resources`) que contém os arquivos estáticos. | `static`     |

### Logs

O framework utiliza SLF4J com Logback para um sistema de logs flexível.

#### Propriedades de Log

As seguintes propriedades podem ser configuradas no arquivo `http-server.properties`:

| Nome da propriedade                 | Descrição                                                                            | Valor Padrão        |
|-------------------------------------|--------------------------------------------------------------------------------------|---------------------|
| `log.level`                         | Altera o nível de log raiz da aplicação (`ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`). | `INFO`              |
| `log.pattern`                       | Define o padrão de formatação das mensagens de log no `logback.xml`.                 | (Padrão do Logback) |
| `http.server.log.detailed-request`  | Se `true`, o conteúdo detalhado das requisições HTTP será logado.                    | `false`             |
| `http.server.log.detailed-response` | Se `true`, o conteúdo detalhado das respostas HTTP será logado.                      | `false`             |

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
