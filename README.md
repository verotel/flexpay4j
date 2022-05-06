# FlexPay4J

This library allows you, as a verotel/CardBilling/GayCharge merchant to easily
**perform payments** - both single purchases and subscriptions.

## FlexPay

FlexPay is a protocol that facilitates these payments.

**[Full FlexPay documentation can be found in the Control Center](https://controlcenter.verotel.com/flexpay-doc/#verotel-flexpay-documentation)**

This library makes it easier to use this protocol:

## Usage

```kotlin
implementation("com.verotel.flexpay:flexpay4j")
```

```kotlin
val flexPayClient = FlexPayClient(
    websiteId = 685478,
    signatureKey = "6Twn89s80aKQ3hpU89f1",
    brand = Brand.VEROTEL
)

val purchaseUrl = flexPayClient.getPurchaseUrl(
    priceAmount = "25.99".toBigDecimal(),
    priceCurrency = SaleCurrency.EUR,
    description = "Extra comfy XL pyjamas",
)

// You can pay here
println(purchaseUrl)
```


