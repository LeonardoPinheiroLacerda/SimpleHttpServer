# Começando

## Requisitos

Para utilizar o framework, você precisará dos seguintes itens:

*   **Java Development Kit (JDK)**: Versão 21 ou superior.
*   **Maven**: Para gerenciamento de dependências e construção do projeto.

Para iniciar, adicione a dependência do framework ao seu `pom.xml`:

```xml
<dependency>
    <groupId>io.github.leonardopinheirolacerda</groupId>
    <artifactId>fastleaf</artifactId>
    <version>1.0.4</version>
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
