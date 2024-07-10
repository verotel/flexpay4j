# FlexPay4j

This library allows you, as a Verotel merchant to easily
**perform payments** on the Verotel platform (https://www.verotel.com) in a
Java or Kotlin environment.

## FlexPay

FlexPay is a protocol that facilitates these payments.

**[Full FlexPay documentation can be found in the Control Center](https://controlcenter.verotel.com/flexpay-doc/#verotel-flexpay-documentation)**

This library makes it easier to use this protocol:

## Usage

This package is hosted in the [Maven central repository](https://search.maven.org/artifact/com.verotel/flexpay4j)

### Import

Gradle:

```kotlin
implementation("com.verotel:flexpay4j:2.2.0")
```

Maven:

```XML
<dependency>
    <groupId>com.verotel</groupId>
    <artifactId>flexpay4j</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Obtain Brand

#### Manually

```kotlin
val brand = Brand.YOURSAFE_DIRECT
```

#### From your merchant ID

```kotlin
val brand = Brand.fromMerchantId("9804000000000100")
```

### Create purchase link

```kotlin
val flexPayClient = FlexPayClient(
    websiteId = 685478,
    signatureKey = "d6dToIj2d6YJ1PX2D1W9",
    brand = brand
)

val purchaseUrl = flexPayClient.purchaseBuilder()
    .withAmount("14".toBigDecimal(), SaleCurrency.EUR)
    .withDescription("test description")
    .withPaymentMethod(PaymentMethod.CC)
    .build()
```

## Changelog
### Version 2.0.0 (Feb 2024)
 - Parameter `backURL` has been renamed to `successURL`, along with the corresponding methods:
   - Simply change calls of `.withBackURL(...)` to the new `.withSuccessURL(...)`
