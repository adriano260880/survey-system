# ADR-001: Use MongoDB as Primary Database

- **Status:** Accepted
- **Date:** 2026-07-16
- **Authors:** Adriano Lopes

---

# Context

O Survey System é uma aplicação responsável pelo armazenamento e consulta de avaliações de restaurantes.

Os principais requisitos funcionais são:

- Registrar avaliações de restaurantes.
- Consultar avaliações por restaurante.
- Calcular a média das avaliações.
- Disponibilizar consultas rápidas das médias consolidadas.
- Permitir alto volume de escrita.

As características esperadas da aplicação incluem:

- Grande quantidade de inserções.
- Poucas atualizações.
- Consultas predominantemente por restaurante.
- Baixa necessidade de relacionamentos entre entidades.
- Escalabilidade horizontal.

---

# Problem

Qual tecnologia de persistência deve ser utilizada para armazenar as avaliações e os dados consolidados dos restaurantes?

As principais opções consideradas foram:

- PostgreSQL
- MySQL
- MongoDB

---

# Decision

Foi escolhido o **MongoDB** como banco de dados principal da aplicação.

Duas coleções independentes foram definidas:

```
reviews
```

Armazena todas as avaliações recebidas.

```
restaurant_rating
```

Armazena os dados consolidados utilizados pelas consultas.

---

# Motivation

As avaliações possuem natureza documental.

Cada avaliação contém todas as informações necessárias para seu processamento:

```json
{
    "orderId": 100,
    "restaurantId": 1,
    "userId": 20,
    "rating": 5,
    "createdAt": "...",
    "processedAt": null
}
```

Não existe necessidade de relacionamentos complexos ou operações JOIN.

O MongoDB oferece excelente desempenho para esse tipo de carga.

---

# Benefits

## Alta performance de escrita

As avaliações são persistidas através de uma única operação de insert.

Não há necessidade de transações envolvendo múltiplas tabelas.

---

## Modelo orientado a documentos

Cada documento representa uma avaliação completa.

Isso reduz a complexidade do modelo e facilita sua evolução.

---

## Agregações nativas

O MongoDB possui um Aggregation Framework extremamente eficiente.

Ele é utilizado pelo Average Service para calcular:

- quantidade de avaliações
- soma das notas
- média das notas

Exemplo:

```javascript
db.reviews.aggregate([
    {
        $match: {
            processedAt: { $exists: false }
        }
    },
    {
        $group: {
            _id: "$restaurantId",
            totalReviews: { $sum: 1 },
            ratingSum: { $sum: "$rating" },
            average: { $avg: "$rating" }
        }
    }
])
```

---

## Escalabilidade Horizontal

Caso necessário, o MongoDB permite evolução para:

- Replica Sets
- Sharding

Sem mudanças significativas na aplicação.

---

## Flexibilidade

Novos campos podem ser adicionados aos documentos sem migrações complexas.

Exemplo:

```json
{
    "device": "Android",
    "city": "São Paulo",
    "appVersion": "2.5.0"
}
```

---

# Collection Design

## reviews

Armazena todas as avaliações recebidas.

Campos principais:

- orderId
- restaurantId
- userId
- rating
- createdAt
- processedAt

---

## restaurant_rating

Representa o Read Model da aplicação.

Campos:

- restaurantId
- ratingSum
- totalReviews
- average
- lastProcessedDate
- updatedAt

Esta coleção é atualizada pelo Average Service.

---

# Index Strategy

## Idempotência

```java
idx_order_unique
```

```text
orderId ASC
```

Impede avaliações duplicadas para um mesmo pedido.

---

## Consolidação diária

```java
idx_pending_reviews
```

```text
processedAt ASC
createdAt ASC
restaurantId ASC
```

Utilizado pelo processo de agregação.

Permite localizar rapidamente avaliações ainda não processadas.

---

## Consulta paginada

```java
idx_restaurant_recent
```

```text
restaurantId ASC
createdAt DESC
```

Utilizado pelo Query Service para consultas ordenadas por data.

---

# Alternatives Considered

## PostgreSQL

### Vantagens

- Forte consistência.
- Excelente suporte transacional.
- JOINs eficientes.

### Desvantagens

- Modelo relacional não traz benefícios para este domínio.
- Agregações frequentes poderiam exigir tabelas auxiliares.
- Escalabilidade horizontal mais complexa.

---

## MySQL

Apresenta características semelhantes ao PostgreSQL.

Foi descartado pelos mesmos motivos.

---

# Consequences

## Positivas

- Excelente desempenho para escrita.
- Modelo simples.
- Aggregation Framework poderoso.
- Facilidade de evolução.
- Boa integração com Spring Data MongoDB.

---

## Negativas

- Não possui JOINs tradicionais.
- Transações distribuídas possuem maior custo.
- Consistência eventual em alguns cenários.
- Maior responsabilidade da aplicação na modelagem dos dados.

---

# Trade-offs

Optou-se por modelar explicitamente dois modelos de dados:

```
reviews
```

Write Model

```
restaurant_rating
```

Read Model

Essa abordagem elimina agregações em tempo de consulta e reduz significativamente a latência das operações de leitura.

---

# Architecture Decision

Esta decisão está alinhada com a adoção de uma arquitetura baseada em **CQRS**, onde:

- o modelo de escrita é otimizado para inserções rápidas;
- o modelo de leitura é otimizado para consultas frequentes.

Essa separação reduz a carga sobre a coleção de avaliações e simplifica a evolução futura do sistema.

---

# References

- MongoDB Documentation
- Spring Data MongoDB
- MongoDB Aggregation Framework
- CQRS Pattern (Martin Fowler)
- Building Microservices (Sam Newman)