# SimpleHttpServer

O projeto SimpleHttpServer se trata de um projeto de fins academicos, onde com base na especificação
[rfc-2616](https://datatracker.ietf.org/doc/html/rfc2616) foi desenvolvido um framework para facilitar o
desenvolvimento de aplicações web, seja disponibilizando arquivos estáticos ou endpoints REST.

# Getting started

Antes de tudo é necessário importar a dependencia do framework ao projeto com:

~~~xml
<dependency>
    <groupId>br.com.leonardo</groupId>
    <artifactId>HttpServer</artifactId>
    <version>1.0.0</version>
</dependency>
~~~

E em seu método main chamar o método serve da classe Server da seguinte maneira

~~~java
public class App {
    public static void main(String[] args) {
        Server.serve();
    }
}
~~~

E pronto a partir desse momento sua aplicação esta pronta para desenvolvimento com as configurações minimas.

# Propriedades

Nos resources do projeto você pode criar um arquivo chamado `http-server.properties` que pode contér algumas configurações para definir 
o comportamento da aplicação.

| Nome da propriedade                | Descrição                                                                   | Valor default |
|------------------------------------|-----------------------------------------------------------------------------|---------------|
| http.server.port                   | Porta http que a aplicação irá utilizar para abrir conexões                 | 9000          |
| http.server.log.requests           | Define se a aplicação vai realizar logs com o conteúdo das requisições HTTP | false         |
| http.server.log.responses          | Define se a aplicação vai realizar logs com o conteúdo das respostas HTTP   | false         |
| http.server.static.content.enabled | Define se a aplicação vai servir arquivos de forma estática                 | true          |
| http.server.static.content.path    | Define o caminho dentro de resources que deve contér os arquivos estaticos  | static        |
| log.level                          | Define o nivel do log                                                       | INFO          |
