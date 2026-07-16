# Performance Benchmark

Este documento registra os resultados dos testes de performance do projeto **Survey System**.

O objetivo é acompanhar a evolução da arquitetura e medir o impacto de cada melhoria implementada.

---

# Ambiente

## Hardware

| Item                | Valor                 |
| ------------------- | --------------------- |
| Máquina             | Desenvolvimento Local |
| Sistema Operacional | Linux Mint            |
| CPU                 | Preencher             |
| Memória             | Preencher             |

---

## Software

| Componente  | Versão    |
| ----------- | --------- |
| Java        | 21        |
| Spring Boot | 3.5.x     |
| MongoDB     | 8.x       |
| Docker      | Preencher |
| k6          | Preencher |

---

# Arquitetura

Versão atual:

* Review Service
* Spring Boot
* MongoDB
* Persistência síncrona
* Índice único por OrderId
* Sem Redis
* Sem Kafka
* Sem Kubernetes
* Sem Cache

---

# Cenário do teste

Endpoint:

```
POST /reviews
```

Payload:

```json
{
  "orderId": "<valor único>",
  "restaurantId": 100,
  "userId": 1,
  "rating": 5
}
```

---

# Baseline

## Configuração

| Item          | Valor       |
| ------------- | ----------- |
| Virtual Users | 50          |
| Duração       | 30 segundos |

## Resultado

| Métrica    |       Valor |
| ---------- | ----------: |
| Requests   |      56.043 |
| Throughput | 1.866 req/s |
| Erros      |          0% |
| Média      |       26 ms |
| Mediana    |       11 ms |
| P90        |       59 ms |
| P95        |       67 ms |
| Máximo     |      512 ms |

---

# Histórico

| Versão | Arquitetura | Throughput |   P95 |
| ------ | ----------- | ---------: | ----: |
| V1     | MongoDB     | 1866 req/s | 67 ms |

---

# Próximos testes

* [X] 500 usuários
* [X] 1000 usuários
* [ ] Ramp-Up
* [ ] Spike Test
* [ ] Stress Test
* [ ] Teste com Redis
* [ ] Teste com Kafka
* [ ] Teste com Kubernetes
* [ ] Teste com KEDA

## Teste de Performance - 500 Virtual Users

**Data:** 04/07/2026

### Configuração

| Item          | Valor         |
| ------------- | ------------- |
| Ferramenta    | k6            |
| Virtual Users | 500           |
| Duração       | 30 segundos   |
| Endpoint      | POST /reviews |

---

### Resultado

| Métrica    |       Valor |
| ---------- | ----------: |
| Requests   |      55.905 |
| Throughput | 1.849 req/s |
| Erros      |          0% |
| Média      |   268,74 ms |
| Mediana    |   197,48 ms |
| P90        |   432,59 ms |
| P95        |   477,15 ms |
| Máximo     |   701,83 ms |

---

### Comparativo

| Métrica        |      50 VUs |     500 VUs | Variação |
| -------------- | ----------: | ----------: | -------: |
| Throughput     | 1.866 req/s | 1.849 req/s |    -0,9% |
| Latência Média |       26 ms |      268 ms |    +932% |
| Mediana        |       11 ms |      197 ms |   +1690% |
| P95            |       67 ms |      477 ms |    +612% |
| Erros          |          0% |          0% |    Igual |

---

### Análise

O aumento de 50 para 500 usuários virtuais não provocou aumento significativo no throughput da aplicação. O serviço manteve aproximadamente 1.850 requisições por segundo.

Em contrapartida, a latência aumentou significativamente, indicando que a aplicação passou a acumular filas internas de processamento enquanto mantinha sua capacidade máxima de atendimento.

Esse comportamento caracteriza um sistema que atingiu seu limite de processamento antes de alcançar seu limite de concorrência.

---

### Conclusões

* O serviço permaneceu estável durante todo o teste.
* Nenhuma requisição retornou erro.
* O throughput permaneceu praticamente constante.
* O aumento da concorrência impactou apenas o tempo de resposta.
* O gargalo atual provavelmente está na capacidade de processamento da aplicação, do MongoDB ou da máquina de desenvolvimento.

---

### Próximos passos

* Executar o benchmark com 1000 Virtual Users.
* Monitorar CPU e memória da JVM.
* Monitorar CPU e I/O do MongoDB.
* Identificar o componente responsável pelo gargalo.


## Teste de Performance - 1000 Virtual Users

**Data:** 04/07/2026

### Configuração

| Item          | Valor         |
| ------------- | ------------- |
| Ferramenta    | k6            |
| Virtual Users | 1000          |
| Duração       | 30 segundos   |
| Endpoint      | POST /reviews |

---

### Resultado

| Métrica    |       Valor |
| ---------- | ----------: |
| Requests   |      58.405 |
| Throughput | 1.920 req/s |
| Erros      |          0% |
| Média      |   514,59 ms |
| Mediana    |   475,83 ms |
| P90        |   690,47 ms |
| P95        |   725,59 ms |
| Máximo     |      1,05 s |

---

### Comparativo Geral

| Métrica        |      50 VUs |     500 VUs |    1000 VUs |
| -------------- | ----------: | ----------: | ----------: |
| Throughput     | 1.866 req/s | 1.849 req/s | 1.920 req/s |
| Latência Média |       26 ms |      268 ms |      515 ms |
| Mediana        |       11 ms |      197 ms |      476 ms |
| P95            |       67 ms |      477 ms |      726 ms |
| Erros          |          0% |          0% |          0% |

---

### Análise

Mesmo com 1000 usuários virtuais simultâneos, o throughput permaneceu próximo de 1.900 requisições por segundo, indicando que a aplicação atingiu sua capacidade máxima de processamento.

O aumento da concorrência provocou crescimento da latência, mas não reduziu a estabilidade do serviço. Durante todo o teste nenhuma requisição falhou.

Esse comportamento demonstra que o sistema absorve grandes volumes de conexões concorrentes, porém passa a formar filas internas quando sua capacidade máxima de processamento é atingida.

---

### Conclusões

* A aplicação manteve estabilidade durante todo o benchmark.
* Não houve perda de throughput com o aumento da concorrência.
* Não foram observados erros HTTP.
* A latência aumentou de forma proporcional ao crescimento do número de usuários simultâneos.
* O limite atual do ambiente está próximo de 1.900 requisições por segundo.

---

### Próximas medições

Na próxima etapa serão coletadas métricas de infraestrutura para identificar o gargalo do sistema.

Métricas a serem monitoradas:

* CPU da JVM
* Heap utilizada
* Garbage Collection
* Threads ativas
* Pool de conexões
* CPU do MongoDB
* Uso de memória do MongoDB
* Operações de escrita por segundo
* I/O em disco

Essas informações permitirão determinar se o limite atual é imposto pela aplicação, pelo banco de dados ou pela máquina de desenvolvimento.
