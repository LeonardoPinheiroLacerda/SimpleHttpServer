# Servindo Arquivos Estáticos: Do Básico ao Avançado

O FastLeaf se destaca por servir arquivos estáticos de forma simples e eficiente, um recurso essencial para aplicações web modernas que precisam entregar assets de frontend como HTML, CSS, JavaScript e imagens.

### Como Funciona

1.  **Habilite o Recurso:** Certifique-se de que a propriedade `http.server.static.content.enabled` está como `true` (que é o valor padrão).
2.  **Crie a Pasta de Conteúdo:** Crie uma pasta dentro de `src/main/resources`. O nome padrão é `static`. Você pode customizar esse caminho alterando a propriedade `http.server.static.content.path` no seu `http-server.properties`.
3.  **Adicione seus Arquivos:** Coloque seus arquivos diretamente nesta pasta. O framework mapeia a URI da requisição diretamente para a estrutura de arquivos e pastas que você criou.

O servidor define automaticamente o cabeçalho `Content-Type` da resposta com base na extensão do arquivo (ex: `.css` se torna `text/css`), garantindo que o navegador interprete o conteúdo corretamente.

### Exemplo Prático: Criando uma Mini-Página Web

Vamos servir uma página HTML que carrega sua própria folha de estilos e uma imagem.

**1. Estrutura de Arquivos**

Primeiro, organize seus arquivos em `src/main/resources/static` da seguinte forma:

```
src/main/resources/
└── static/
    ├── index.html
    ├── css/
    │   └── style.css
    └── img/
        └── logo.png 
```

**2. Conteúdo do HTML (`index.html`)**

Este arquivo linka para o CSS e a imagem usando caminhos relativos à raiz do servidor.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Página Estática</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <h1>Bem-vindo ao FastLeaf!</h1>
    <img src="/img/logo.png" alt="Logo">
</body>
</html>
```

**3. Conteúdo do CSS (`css/style.css`)**

Um estilo simples para confirmar que o arquivo foi carregado.

```css
body {
    background-color: #f0f0f0;
    font-family: sans-serif;
    text-align: center;
}
```

**4. Imagem**

Coloque qualquer arquivo de imagem chamado `logo.png` dentro da pasta `src/main/resources/static/img/`.

**5. Acessando no Navegador**

Após iniciar seu servidor, acesse `http://localhost:9000/index.html`. O navegador irá carregar o HTML, que por sua vez fará requisições para `/css/style.css` e `/img/logo.png`, e o FastLeaf servirá todos os três arquivos.

### Regra de Precedência: Arquivos Estáticos vs. Endpoints de API

É fundamental entender como o FastLeaf decide o que servir quando uma URI pode corresponder tanto a um arquivo estático quanto a um endpoint de API.

**A regra é simples: arquivos estáticos têm prioridade sobre os endpoints de API.**

Quando uma requisição chega, o servidor executa a seguinte lógica:
1.  Primeiro, ele verifica se um arquivo correspondente à URI da requisição existe na pasta de conteúdo estático (ex: `resources/static`).
2.  **Se o arquivo existir**, ele é servido imediatamente. O sistema de roteamento que busca por `@Endpoint`s nem chega a ser consultado.
3.  **Se nenhum arquivo for encontrado**, o servidor então prossegue para o `HttpEndpointResolver` para encontrar um endpoint de API que corresponda à URI e ao método HTTP.

**Exemplo de Conflito Direto:**

O conflito real acontece quando a URI de uma requisição corresponde **exatamente** a um arquivo estático e a um endpoint.

Imagine que você tem:
*   Um arquivo estático: `src/main/resources/static/profile` (um arquivo chamado `profile`, sem extensão)
*   Um endpoint de API: `@Endpoint(url = "/profile", method = HttpMethod.GET)`

Nesse caso, uma requisição `GET` para `/profile` fará com que o servidor encontre e sirva o arquivo estático `profile`. O endpoint da API, apesar de corresponder à mesma URL, **nunca será alcançado**, pois a verificação de arquivos estáticos tem prioridade.

Uma requisição para `/profile.html` e um endpoint para `/profile` não conflitam, pois as URIs são diferentes.

> **Recomendação:** Para evitar conflitos e manter uma arquitetura clara, agrupe seus endpoints de API sob um prefixo comum (ex: `/api/v1/*`). Isso cria uma separação nítida entre as rotas de API e as URLs usadas para servir o frontend da sua aplicação.
