# FlexPay4j

This library allows you, as a Verotel/CardBilling/GayCharge merchant to easily
**perform payments** on the Verotel platform (https://www.verotel.com) in a
Java or Kotlin environment.

## FlexPay

FlexPay is a protocol that facilitates these payments.

**[Full FlexPay documentation can be found in the Control Center](https://controlcenter.verotel.com/flexpay-doc/#verotel-flexpay-documentation)**

This library makes it easier to use this protocol:

## Usage

```kotlin
implementation("com.verotel:flexpay4j:1.0.1")
```

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

// You can pay here:
println(purchaseUrl)
```


