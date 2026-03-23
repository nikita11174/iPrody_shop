# iPrody Shop Platform

Репозиторий содержит набор независимых backend-сервисов для базового процесса интернет-магазина: оформление заказа, обработка платежа и управление доставкой.

## Архитектура

- `order-service` - сервис заказов
- `payment-service` - сервис платежей
- `delivery-service` - сервис доставки

Каждый сервис запускается отдельно, имеет собственную H2 in-memory базу данных и предоставляет REST API для CRUD-операций.

Во всех трёх сервисах слои разделены одинаково:

- `web` отвечает за transport-модели и HTTP-контракт;
- `web/mapper` переводит `Request/Response` в прикладные модели и обратно;
- `application` работает с собственными `Command` и `Details`;
- `domain` содержит aggregate root, entity, value object и repository интерфейсы.

Это даёт возможность менять внешний transport независимо от прикладного сценария: текущий REST API может быть дополнен новой версией API, WebSocket-обработчиком или consumer'ом сообщений без протаскивания transport DTO в application service.

Начиная с lesson-3, `order-service` умеет инициировать создание платежа в `payment-service` по REST через OpenFeign.

В корне репозитория также находятся диаграммы предметной области:

- `Разбиение_по_бизнес_возможностям.svg`
- `Разбиение_по_поддоменам_DDD.svg`

## Технологический стек

- Java 17
- Spring Boot 3.3.0
- Spring Web
- OpenFeign
- springdoc OpenAPI / Swagger UI
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Структура репозитория

```text
.
├── order-service/
├── payment-service/
├── delivery-service/
├── postman/
│   └── ddd-microservices.postman_collection.json
├── Разбиение_по_бизнес_возможностям.svg
└── Разбиение_по_поддоменам_DDD.svg
```

## Сервисы

### order-service

Назначение: управление жизненным циклом заказа.

- Порт: `8081`
- Базовый URL: `http://localhost:8081`
- API: `/api/orders`
- H2 Console: `http://localhost:8081/h2-console`

### payment-service

Назначение: создание и сопровождение платежей.

- Порт: `8082`
- Базовый URL: `http://localhost:8082`
- API: `/api/payments`
- H2 Console: `http://localhost:8082/h2-console`

### delivery-service

Назначение: управление доставками и временными окнами доставки.

- Порт: `8083`
- Базовый URL: `http://localhost:8083`
- API: `/api/deliveries`
- H2 Console: `http://localhost:8083/h2-console`

## Требования

- JDK 17
- Maven 3.9+

Проверка окружения:

```powershell
java -version
mvn -version
```

## Запуск

### order-service

```powershell
cd order-service
mvn spring-boot:run
```

### payment-service

```powershell
cd payment-service
mvn spring-boot:run
```

### delivery-service

```powershell
cd delivery-service
mvn spring-boot:run
```

## Тесты

### order-service

```powershell
cd order-service
mvn test
```

### payment-service

```powershell
cd payment-service
mvn test
```

### delivery-service

```powershell
cd delivery-service
mvn test
```

## API

Все сервисы поддерживают одинаковый набор CRUD-операций:

- `POST` создать запись
- `GET /{resource}` получить список
- `GET /{resource}/{id}` получить запись по идентификатору
- `PUT /{resource}/{id}` обновить запись
- `DELETE /{resource}/{id}` удалить запись

Конкретные ресурсы:

- `order-service` -> `/api/orders`
- `payment-service` -> `/api/payments`
- `delivery-service` -> `/api/deliveries`

Для ручной проверки запросов можно использовать коллекцию:

- `postman/ddd-microservices.postman_collection.json`

Дополнительный сценарий lesson-3:

- `order-service` -> `POST /api/orders/{orderId}/payment` создаёт платёж в `payment-service` через OpenFeign.

## Хранение данных

- каждый сервис использует отдельную H2 in-memory базу данных;
- схема создается автоматически при старте приложения;
- данные хранятся только на время жизни процесса.

## Обработка ошибок

Во всех сервисах реализована базовая обработка ошибки `not found` через единый JSON-ответ API.

## Swagger UI

- `order-service` -> `http://localhost:8081/swagger-ui/index.html`
- `payment-service` -> `http://localhost:8082/swagger-ui/index.html`
- `delivery-service` -> `http://localhost:8083/swagger-ui/index.html`
