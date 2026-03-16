# Домашнее задание — декомпозиция интернет-магазина

## Что подготовлено

В корень репозитория можно положить следующие файлы:

1. `Разбиение_по_бизнес_возможностям.svg`
2. `Разбиение_по_поддоменам_DDD.svg`
3. `README_домашнее_задание.md` — этот файл

---

## 1) Декомпозиция по бизнес-возможностям

Интернет-магазин разделён на устойчивые бизнес-возможности:

- Каталог и контент
- Поиск и discovery
- Цены и промо
- Клиенты и лояльность
- Корзина и checkout
- Заказы
- Оплата
- Склад и остатки
- Доставка и фулфилмент
- Коммуникации и поддержка

### Почему такое разделение корректно

Это именно бизнес-возможности, а не технические модули.  
Каждый блок отвечает на отдельный бизнес-вопрос:

- как показать товар;
- как найти товар;
- как рассчитать цену;
- как собрать корзину;
- как оформить заказ;
- как принять оплату;
- как зарезервировать товар;
- как доставить;
- как сопровождать клиента после покупки.

Такое разбиение удобно как первый уровень архитектурной декомпозиции.

---

## 2) Декомпозиция по поддоменам (DDD)

Выделены bounded contexts:

### Core subdomains
- **Catalog Context**
- **Cart Context**
- **Order Context**

### Supporting subdomains
- **Pricing Context**
- **Customer Context**
- **Inventory Context**
- **Fulfillment Context**

### Generic subdomains
- **Payment Context**
- **Notification Context**

---

## 3) Контексты и их модели

### Catalog Context
**Назначение:** управление ассортиментом и структурой каталога.  
**Aggregate:** `Product`  
**Entity:** `Product`, `Category`  
**Value Object:** `SKU`, `Attributes`, `MediaSet`

### Pricing Context
**Назначение:** расчёт актуальной цены и применение скидок.  
**Aggregate:** `PricePolicy`  
**Entity:** `Price`, `Promotion`, `Coupon`  
**Value Object:** `Money`, `DiscountRule`, `ValidityPeriod`

### Customer Context
**Назначение:** хранение клиента и его контактных данных.  
**Aggregate:** `Customer`  
**Entity:** `Customer`  
**Value Object:** `CustomerId`, `Address`, `ContactInfo`

### Cart Context
**Назначение:** временное хранение выбранных товаров до оформления заказа.  
**Aggregate:** `Cart`  
**Entity:** `Cart`, `CartItem`  
**Value Object:** `Quantity`, `SelectedOffer`

### Order Context
**Назначение:** управление жизненным циклом заказа.  
**Aggregate:** `Order`  
**Entity:** `Order`, `OrderLine`  
**Value Object:** `OrderStatus`, `DeliverySnapshot`, `BillingSnapshot`

### Payment Context
**Назначение:** проведение и отслеживание платежа.  
**Aggregate:** `Payment`  
**Entity:** `Payment`, `Transaction`, `Refund`  
**Value Object:** `PaymentMethod`, `PaymentStatus`, `Amount`

### Inventory Context
**Назначение:** остатки и резервирование товаров.  
**Aggregate:** `StockItem`  
**Entity:** `StockItem`, `Reservation`, `Warehouse`  
**Value Object:** `StockQuantity`, `ReservationWindow`, `SKU`

### Fulfillment Context
**Назначение:** комплектация, отгрузка и трекинг.  
**Aggregate:** `Shipment`  
**Entity:** `Shipment`, `Parcel`, `TrackingEvent`  
**Value Object:** `TrackingNumber`, `ShippingAddress`, `Carrier`

### Notification Context
**Назначение:** отправка сервисных уведомлений.  
**Aggregate:** `NotificationBatch`  
**Entity:** `Notification`, `Template`  
**Value Object:** `Channel`, `Recipient`, `MessagePayload`

---

## 4) Взаимодействие между контекстами

Основные связи:

- `Catalog Context -> Pricing Context`  
  Каталог получает ценовую информацию.

- `Catalog Context -> Cart Context`  
  Корзина использует данные о товаре.

- `Pricing Context -> Cart Context`  
  Корзина получает актуальные цены, скидки и промо.

- `Customer Context -> Cart / Order Context`  
  Данные клиента и адреса участвуют в checkout.

- `Cart Context -> Order Context`  
  При checkout корзина создаёт заказ.

- `Order Context -> Inventory Context`  
  Заказ резервирует товар или снимает резерв.

- `Order Context -> Payment Context`  
  Для заказа создаётся платёж.

- `Payment Context -> Order Context`  
  После подтверждения оплаты заказ переводится в следующий статус.

- `Order Context -> Fulfillment Context`  
  После успешного оформления создаётся отгрузка.

- `Order Context / Fulfillment Context -> Notification Context`  
  Клиенту отправляются уведомления о статусе заказа и доставки.

---

## 5) Что создать в GitHub

### Рекомендуемая структура корня репозитория

```text
/
├── Разбиение_по_бизнес_возможностям.svg
├── Разбиение_по_поддоменам_DDD.svg
└── README_домашнее_задание.md
```

---

## 6) Что сделать в GitHub пошагово

1. Создать пустой публичный или приватный репозиторий.
2. Загрузить в корень оба `.svg` файла и этот `README`.
3. Сделать отдельную ветку, например: `feature/internet-store-decomposition`.
4. Закоммитить файлы.
5. Открыть Pull Request / Merge Request в `main`.
6. Ссылку на PR приложить как результат домашнего задания.

---

## 7) Пример названия PR

`Домашнее задание: декомпозиция интернет-магазина`

## 8) Пример описания PR

Добавлены две схемы декомпозиции интернет-магазина:

- разбиение по бизнес-возможностям;
- разбиение по поддоменам DDD с выделением bounded contexts, entity, value object и aggregate.

Также добавлен README с кратким описанием модели и взаимодействий между контекстами.
