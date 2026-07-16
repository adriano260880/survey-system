# Survey System - Tutorial

Este documento descreve como executar todo o ambiente local do Survey System.

---

# Arquitetura

O projeto é composto pelos seguintes serviços:

| Serviço | Porta | Responsabilidade |
|----------|------|------------------|
| review-service | 8080 | Cadastro das avaliações |
| average-service | 8081 | Consolidação diária das avaliações |
| query-service | 8082 | Consultas e cache Redis |
| MongoDB | 27017 | Banco de dados |
| Redis | 6379 | Cache |

---

# Pré-requisitos

- Java 21
- Maven 3.9+
- Docker
- Docker Compose

---

# Estrutura do projeto

```
survey-system/

docs/
infra/
observability/
services/
scripts/
load-tests/
```

---

# 1. Subindo MongoDB e Redis

Entre na pasta:

```bash
cd infra
```

Execute:

```bash
docker compose up -d
```

Verifique:

```bash
docker ps
```

Resultado esperado:

```
mongo
redis
```

---

# 2. Subindo o Review Service

```bash
cd services/review-service

./mvnw spring-boot:run
```

A aplicação ficará disponível em:

```
http://localhost:8080
```

Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

# 3. Subindo o Average Service

```bash
cd services/average-service

./mvnw spring-boot:run
```

Disponível em:

```
http://localhost:8081
```

Health Check

```bash
curl http://localhost:8081/actuator/health
```

---

# 4. Subindo o Query Service

```bash
cd services/query-service

./mvnw spring-boot:run
```

Disponível em:

```
http://localhost:8082
```

Health Check

```bash
curl http://localhost:8082/actuator/health
```

---

# Fluxo de execução

A ordem recomendada é:

```
Docker

↓

Review Service

↓

Average Service

↓

Query Service
```

---

# Criando avaliações

Cadastrar algumas avaliações.

Exemplo:

```bash
curl --request POST \
--url http://localhost:8080/reviews \
--header "Content-Type: application/json" \
--data '{
    "orderId":101,
    "restaurantId":100,
    "userId":1,
    "rating":5
}'
```

Repita com diferentes pedidos.

Também é possível utilizar:

```
load-tests/k6/review-carga.js
```

para gerar dezenas de avaliações automaticamente.

---

# Executando a agregação

A consolidação diária pode ser executada manualmente.

```bash
curl --request POST \
--url http://localhost:8081/internal/aggregation/run
```

Resultado esperado:

```
Aggregation finished successfully
```

Após esta etapa serão atualizadas as coleções:

```
restaurant_rating

aggregation_control
```

e as reviews serão marcadas com

```
processedAt
```

---

# Consultando média do restaurante

```bash
curl http://localhost:8082/restaurants/100
```

Resposta

```json
{
  "restaurantId":100,
  "average":4.40,
  "totalReviews":5,
  "lastProcessedDate":"2026-07-08"
}
```

Na primeira chamada:

```
MongoDB
↓

Redis
```

Nas próximas chamadas:

```
Redis
```

---

# Consultando avaliações

Primeira página

```bash
curl "http://localhost:8082/restaurants/100/reviews?page=0&size=5"
```

Resposta

```json
{
  "content":[
      ...
  ],
  "number":0,
  "size":5,
  "totalPages":2,
  "totalElements":6
}
```

A consulta retorna as avaliações ordenadas por:

```
createdAt DESC
```

---

# Executando testes de carga

Entrar na pasta

```bash
cd load-tests/k6
```

Executar

```bash
k6 run review-post.js
```

ou

```bash
k6 run review-carga.js
```

Também existem cenários de carga maiores:

```
review-post-500.js

review-post-1000.js
```

---

# Observabilidade

Subir o stack

```bash
cd observability

docker compose up -d
```

Componentes disponíveis:

| Ferramenta | Porta |
|------------|------|
| Prometheus | 9090 |
| Grafana | 3000 |
| Tempo | 3200 |
| Loki | 3100 |

---

# Documentação

## APIs

```
docs/api/
```

```
review-service.yml

average-service.yml

query-service.yml
```

---

## ADRs

```
docs/ADR/
```

- 001-use-mongodb.md

- 002-daily-average.md

---

## Arquitetura

```
docs/architecture/
```

- Context Diagram

- Container Diagram

- Deployment Diagram

- Sequence Diagrams

---

# Fluxo completo

```
             POST /reviews
                    │
                    ▼
            Review Service
                    │
                    ▼
                MongoDB
                    │
                    ▼
      Average Service (Quartz Job)
                    │
                    ▼
        restaurant_rating
                    │
                    ▼
           Query Service
             │        │
             │        ▼
             │      Redis
             ▼
          Cliente
```

---

# Encerrando o ambiente

Parar containers

```bash
cd infra

docker compose down
```

Parar observabilidade

```bash
cd observability

docker compose down
```

---

# Limpeza completa

Remover volumes do Docker

```bash
docker compose down -v
```

Ou utilizar o script do projeto

```bash
./scripts/clean.sh
```