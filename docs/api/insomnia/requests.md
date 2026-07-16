Você pode importar diretamente no **Insomnia** usando o formato **cURL**.

---

# 1. Criar Review

```bash
curl --request POST \
  --url http://localhost:8080/reviews \
  --header 'Content-Type: application/json' \
  --data '{
    "orderId": 101,
    "restaurantId": 100,
    "userId": 1,
    "rating": 5
}'
```

---

# 2. Consultar média do restaurante

```bash
curl --request GET \
  --url http://localhost:8082/restaurants/100
```

Resposta

```json
{
  "restaurantId": 100,
  "average": 4.40,
  "totalReviews": 5,
  "lastProcessedDate": "2026-07-08"
}
```

---

# 3. Consultar avaliações paginadas

Primeira página

```bash
curl --request GET \
  --url "http://localhost:8082/restaurants/100/reviews?page=0&size=5"
```

Segunda página

```bash
curl --request GET \
  --url "http://localhost:8082/restaurants/100/reviews?page=1&size=5"
```

Resposta

```json
{
  "content": [
    {
      "orderId": 101,
      "userId": 1,
      "rating": 5,
      "createdAt": "2026-07-15T16:31:13Z"
    },
    {
      "orderId": 20,
      "userId": 500,
      "rating": 5,
      "createdAt": "2026-07-08T18:00:00Z"
    }
  ],
  "totalElements": 30,
  "totalPages": 6,
  "size": 5,
  "number": 0,
  "first": true,
  "last": false,
  "empty": false
}
```

---

# 4. Executar agregação manual

```bash
curl --request POST \
  --url http://localhost:8081/aggregation/run
```

Resposta

```text
Aggregation finished successfully
```

---

# 5. Health Check - Review Service

```bash
curl --request GET \
  --url http://localhost:8080/actuator/health
```

---

# 6. Health Check - Query Service

```bash
curl --request GET \
  --url http://localhost:8082/actuator/health
```

---

# 7. Health Check - Average Service

```bash
curl --request GET \
  --url http://localhost:8081/actuator/health
```

---

# 8. Métricas Prometheus

Review Service

```bash
curl --request GET \
  --url http://localhost:8080/actuator/prometheus
```

Query Service

```bash
curl --request GET \
  --url http://localhost:8082/actuator/prometheus
```

Average Service

```bash
curl --request GET \
  --url http://localhost:8081/actuator/prometheus
```

---

Esses cURLs cobrem todo o fluxo implementado até agora:

1. **POST** → cria uma avaliação (`review-service`).
2. **POST** → executa a agregação (`average-service`).
3. **GET** → consulta a média consolidada (`query-service` com cache Redis).
4. **GET** → consulta as avaliações paginadas (`query-service`).
5. **GET** → endpoints de observabilidade (`Actuator` e `Prometheus`).

Esse conjunto é suficiente para importar no Insomnia e validar o fluxo completo do sistema end-to-end.
