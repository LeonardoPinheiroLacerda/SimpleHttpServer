# FastLeaf

[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=coverage)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=bugs)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=LeonardoPinheiroLacerda_FastLeaf&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=LeonardoPinheiroLacerda_FastLeaf)

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.leonardopinheirolacerda/fastleaf)
![Java](https://img.shields.io/badge/Java-21+-blue)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)


O FastLeaf é um framework Java acadêmico, construído com base na especificação [RFC 2616](https://datatracker.ietf.org/doc/html/rfc2616), para simplificar o desenvolvimento de aplicações web, desde o fornecimento de arquivos estáticos até a criação de endpoints RESTful.

Este guia detalha como utilizar o framework em seus projetos.

# Documentação

Para um entendimento completo do framework, consulte as seções abaixo:

1.  [**Arquitetura do Framework**](./docs/1-arquitetura.md): Uma visão geral do design, fluxo de requisições e a responsabilidade de cada componente.
2.  [**Começando**](./docs/2-comecando.md): Instruções de como adicionar a dependência e iniciar o servidor.
3.  [**Configuração**](./docs/3-configuracao.md): Detalhes sobre como customizar o servidor, incluindo portas, logs e outras propriedades.
4.  [**Criando Endpoints**](./docs/4-criando-endpoints.md): Um guia completo sobre como criar rotas, acessar dados da requisição e construir respostas.
5.  [**Middlewares**](./docs/5-middlewares.md): Aprenda a interceptar requisições para executar lógicas transversais como autenticação e logging.
6.  [**Servindo Arquivos Estáticos**](./docs/6-servindo-arquivos-estaticos.md): Como servir HTML, CSS, JavaScript e outros assets, e a regra de precedência sobre os endpoints de API.