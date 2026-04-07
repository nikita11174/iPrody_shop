# iPrody Shop Platform

Репозиторий содержит набор независимых backend-сервисов для базового процесса интернет-магазина: оформление заказа, обработка платежа и управление доставкой.

## Архитектура

- `order-service` — сервис заказов (порт 8081)
- `payment-service` — сервис платежей (порт 8082)
- `delivery-service` — сервис доставки (порт 8083)

Каждый сервис запускается отдельно, имеет собственную H2 in-memory базу данных и предоставляет REST API.

Во всех трёх сервисах слои разделены одинаково:

- `web` — REST-контроллеры, `*Request`/`*Response` DTO, WebMapper
- `application` — ApplicationService, `*Command` (вход), `*Details` (выход)
- `domain` — Aggregate Root, Entity, Value Object, Repository-интерфейсы
- `integration` — Feign-клиенты и адаптеры (только `order-service`)

### Что реализовано

| Урок | Тема | Что добавлено |
|------|------|--------------|
| 1 | Декомпозиция | Диаграммы bounded contexts в корне репозитория |
| 2 | DDD | Три независимых CRUD-сервиса со слоёной архитектурой |
| 3 | REST + OpenFeign | `order-service → payment-service` через Feign; OpenAPI/Swagger UI |
| 4 | Idempotency Key | `IdempotencyFilter` + таблица `idempotency_keys`; заголовок `X-Idempotency-Key` |
| 5 | Circuit Breaker & Retry | Resilience4j: `@Retry` + `@CircuitBreaker` в `PaymentClientAdapter` |
| 6 | Rate Limiter & Bulkhead | Resilience4j: `@RateLimiter` + `@Bulkhead` (клиент); Bucket4j per-IP (сервер) |
| 7 | Integration Tests | WireMock: `PaymentClientIntegrationTest` тестирует HTTP-контракт |
| 8 | RabbitMQ | Async messaging: `order-service ↔ payment-service` через Direct Exchange |
| 9 | Kafka + Docker | `order-service → delivery-service` через Kafka (KRaft); Dockerfile + docker-compose для всех сервисов |

В корне репозитория находятся диаграммы предметной области:

- `Разбиение_по_бизнес_возможностям.svg`
- `Разбиение_по_поддоменам_DDD.svg`

## Технологический стек

- Java 17
- Spring Boot 3.3.0
- Spring Cloud 2023.0.2
- OpenFeign
- springdoc OpenAPI / Swagger UI 2.5.0
- Spring Data JPA
- H2 Database
- Resilience4j 2.1.0
- Bucket4j 8.7.0
- Spring AMQP / RabbitMQ
- Spring Kafka
- Lombok
- Maven 3.9+
- Docker / docker-compose

## Структура репозитория

```text
.
├── order-service/
├── payment-service/
├── delivery-service/
├── postman/
│   └── ddd-microservices.postman_collection.json
├── docker-compose.yml
├── Разбиение_по_бизнес_возможностям.svg
└── Разбиение_по_поддоменам_DDD.svg
```

## Сервисы

### order-service

Назначение: управление жизненным циклом заказа.

- Порт: `8081`
- API: `/api/orders`
- Оплата (sync): `POST /api/orders/{orderId}/payment`
- Оплата (async via RabbitMQ): `POST /api/orders/{orderId}/payment/async` — 202 Accepted
- После оплаты публикует событие `order.paid` в Kafka → delivery-service создаёт доставку
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- H2 Console: `http://localhost:8081/h2-console`

### payment-service

Назначение: создание и сопровождение платежей.

- Порт: `8082`
- API: `/api/payments` — требует заголовок `X-Idempotency-Key`
- Swagger UI: `http://localhost:8082/swagger-ui/index.html`
- H2 Console: `http://localhost:8082/h2-console`

### delivery-service

Назначение: управление доставками и временными окнами доставки.

- Порт: `8083`
- API: `/api/deliveries`
- Swagger UI: `http://localhost:8083/swagger-ui/index.html`
- H2 Console: `http://localhost:8083/h2-console`

## Требования

- JDK 17
- Maven 3.9+
- Docker + Docker Compose

## Запуск

### Docker (рекомендуется)

```bash
docker compose up -d --build
```

Поднимает все сервисы + RabbitMQ + Kafka (KRaft) + Kafka UI (http://localhost:8090).

### Локально (без Docker)

Требуются запущенные RabbitMQ (порт 5672) и Kafka (порт 9093).

```powershell
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd delivery-service && mvn spring-boot:run
```

## Тесты

```powershell
cd order-service && mvn test
cd payment-service && mvn test
```

## API

Все сервисы поддерживают CRUD-операции:

- `POST /{resource}` — создать
- `GET /{resource}` — список
- `GET /{resource}/{id}` — по идентификатору
- `PUT /{resource}/{id}` — обновить
- `DELETE /{resource}/{id}` — удалить

Для ручной проверки: `postman/ddd-microservices.postman_collection.json`

> **Важно:** `POST /api/payments` требует заголовок `X-Idempotency-Key` (UUID).

## Хранение данных

- каждый сервис использует отдельную H2 in-memory базу данных;
- схема создаётся автоматически при старте;
- данные хранятся только на время жизни процесса.

## Обработка ошибок

| Код | Причина |
|-----|---------|
| 400 | Невалидный запрос / отсутствует `X-Idempotency-Key` |
| 404 | Ресурс не найден |
| 409 | Дублирующий запрос (повторный `X-Idempotency-Key`) |
| 429 | Превышен rate limit (Bucket4j) |
| 502 | Ошибка upstream payment-service |
| 503 | Bulkhead или Circuit Breaker открыт |
