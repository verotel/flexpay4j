# FlexPay4j

This library allows you, as a Verotel/CardBilling/GayCharge merchant to easily
**perform payments** on the Verotel platform (https://www.verotel.com) in a
Java or Kotlin environment.

## FlexPay

FlexPay is a protocol that facilitates these payments.

**[Full FlexPay documentation can be found in the Control Center](https://controlcenter.verotel.com/flexpay-doc/#verotel-flexpay-documentation)**

This library makes it easier to use this protocol:

## Usage

### Import

Gradle:

```kotlin
implementation("com.verotel:flexpay4j:1.2.0")
```

Maven:

```XML
<dependency>
    <groupId>com.verotel</groupId>
    <artifactId>flexpay4j</artifactId>
    <version>1.2.0</version>
</dependency>
```

### Code

Kotlin:

```kotlin
val flexPayClient = FlexPayClient(
    websiteId = 685478,
    signatureKey = "d6dToIj2d6YJ1PX2D1W9",
    brand = Brand.VEROTEL
)

val purchaseUrl = flexPayClient.getPurchaseUrl(
    priceAmount = "25.99".toBigDecimal(),
    priceCurrency = SaleCurrency.EUR,
    description = "Extra comfy XL pyjamas",
)
//  ---- OR ----
val purchaseUrl = flexPayClient.purchaseBuilder()
    .withAmount("14".toBigDecimal(), SaleCurrency.EUR)
    .withDescription("test description")
    .withPaymentMethod(PaymentMethod.CC)
    .build();
```

Java:

```java
FlexPayClient flexPayClient = new FlexPayClient(
    685478,
    "d6dToIj2d6YJ1PX2D1W9",
    Brand.VEROTEL
);

URL purchaseUrl = flexPayClient.getPurchaseUrl(
    new BigDecimal("25.99"),
    SaleCurrency.EUR,
    "Extra comfy XL pyjamas"
);
//  ---- OR ----
URL purchaseUrl = flexPayClient.purchaseBuilder()
    .withAmount("14".toBigDecimal(), SaleCurrency.EUR)
    .withDescription("test description")
    .withPaymentMethod(PaymentMethod.CC)
    .build();
```


