package com.bitsafe.flexpay

import com.bitsafe.flexpay.enums.PaymentMethod
import com.bitsafe.flexpay.enums.SaleCurrency
import com.bitsafe.flexpay.enums.SubscriptionType
import com.bitsafe.flexpay.utils.encodeUrlValue
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.net.URL

internal class FlexPayClientTest {
    val protocolVersion = "4";
    val secret = "zpXwe2D77g4P7ysGJcr3rY87TBYs6J"
    val testDescription = "My Dščřčřřěřě&?=blah123"
    val params = mapOf(
        "shopID" to "68849",
        "saleID" to "433456",
        "referenceID" to "5566",
        "priceAmount" to "0.00",
        "referenceID" to "reference1234",
        "priceCurrency" to "USD",
        "custom1" to "My",
        "description" to testDescription,
        "subscriptionType" to "RECURRING",
        "period" to "P1M",
        "name" to "My name",
        "trialAmount" to "0.01",
        "trialPeriod" to "P3D",
        "successURL" to "http://successURL.test",
        "declineURL" to "http://declineURL.test",
        "cancelDiscountPercentage" to "30",
        "blah" to "something",
    )
    val signatureOfFiltered = "1a790e1ebf654b9a38884a5c9a867a7e57134f103a1c9c8d9854da794a839d03"
    val signatureOfAll = "a8c18e900fad7af686c3b6dc9f00f197f9d6ea210566ef0d81fb07555f23504d"
    val oldSha1SignatureOfAll = "3650ddcc9360de60f4fc78604057c9f3246923cb"
    val baseUrl = "https://secure.verotel.com/"
    val commonURLParams = buildString {
        append("successURL=http%3A%2F%2FsuccessURL.test")
        append("&blah=something")
        append("&cancelDiscountPercentage=30")
        append("&custom1=My")
        append("&declineURL=http%3A%2F%2FdeclineURL.test")
        append("&description=")
        append("My+D%C5%A1%C4%8D%C5%99%C4%8D%C5%99%C5%99")
        append("%C4%9B%C5%99%C4%9B%26%3F%3Dblah123")
        append("&name=My+name")
        append("&period=P1M")
        append("&priceAmount=0.00")
        append("&priceCurrency=USD")
        append("&referenceID=reference1234")
        append("&saleID=433456")
        append("&shopID=68849")
        append("&subscriptionType=recurring")
        append("&trialAmount=0.01")
        append("&trialPeriod=P3D")
    }
    val shopId = "60678"
    val client = FlexPayClient(shopId, secret, brand = Brand.VEROTEL)

    @Test
    fun `MCC raises exception if iDEAL is not used`() {
        assertThatCode {
            client.getPurchaseUrl(
                priceAmount = BigDecimal("45.20"),
                priceCurrency = SaleCurrency.EUR,
                description = testDescription,
                paymentMethod = PaymentMethod.CC,
                mcc = "1144"
            )
        }
            .hasMessage("MCC code and subCreditor can only be used with iDEAL payment method")
            .isInstanceOf(WrongParameterCombinationException::class.java)
    }

    @Test
    fun `Sub creditor raises exception if iDEAL is not used`() {
        assertThatCode {
            client.getPurchaseUrl(
                priceAmount = BigDecimal("45.20"),
                priceCurrency = SaleCurrency.EUR,
                description = testDescription,
                paymentMethod = PaymentMethod.CC,
                subCreditor = SubCreditor(name = "Foo", id = "bar", country = "NL")
            )
        }
            .hasMessage("MCC code and subCreditor can only be used with iDEAL payment method")
            .isInstanceOf(WrongParameterCombinationException::class.java)
    }

    @Test
    fun `Sub creditor country must be 2 letter country code`() {
        assertThatCode {
            client.getPurchaseUrl(
                priceAmount = BigDecimal("45.20"),
                priceCurrency = SaleCurrency.EUR,
                description = testDescription,
                paymentMethod = PaymentMethod.IDEAL,
                subCreditor = SubCreditor(name = "Foo", id = "bar", country = "moo")
            )
        }
            .hasMessage("Sub creditor country must be a 2-letter ISO 3166 country code")
            .isInstanceOf(WrongParameterValueException::class.java)
    }

    @Test
    fun `Sub creditor and MCC work with iDEAL`() {
        assertThatCode {
            val url = client.getPurchaseUrl(
                priceAmount = BigDecimal("45.20"),
                priceCurrency = SaleCurrency.EUR,
                description = testDescription,
                paymentMethod = PaymentMethod.IDEAL,
                mcc = "1144",
                subCreditor = SubCreditor(name = "Foo", id = "bar", country = "NL")
            )
            assertThat(url.query).contains("mcc")
            assertThat(url.query).contains("subCreditorName")
            assertThat(url.query).contains("subCreditorId")
            assertThat(url.query).contains("subCreditorCountry")
        }.doesNotThrowAnyException()

        assertThatCode {
            val url = client.purchaseBuilder()
                .withAmount(BigDecimal("45.20"), SaleCurrency.EUR)
                .withDescription(testDescription)
                .withPaymentMethod(PaymentMethod.IDEAL)
                .withMcc("1144")
                .withSubCreditor(SubCreditor(name = "Foo", id = "bar", country = "NL"))
                .build()
            assertThat(url.query).contains("mcc")
            assertThat(url.query).contains("subCreditorName")
            assertThat(url.query).contains("subCreditorId")
            assertThat(url.query).contains("subCreditorCountry")
        }
            .doesNotThrowAnyException()
    }

    @Test
    fun `getSignature returns correct signature`() {
        assertThat(client.getSignature(params)).isEqualTo(signatureOfFiltered)
    }

    @Test
    fun `validates signature as correct`() {
        val signedParams = mapOf(
            "shopID" to "68849",
            "priceAmount" to "0.00",
            "referenceID" to "reference1234",
            "priceCurrency" to "USD",
            "custom1" to "My",
            "description" to testDescription,
            "subscriptionType" to "RECURRING",
            "period" to "P1M",
            "name" to "My name",
            "trialAmount" to "0.01",
            "trialPeriod" to "P3D",
            "successURL" to "http://successURL.test",
            "declineURL" to "http://declineURL.test",
            "cancelDiscountPercentage" to "30",
            "signature" to "0df722dd32b8a9f68e3ae4e3f226651eba1397d7928862635ba32674dc7bed6a"
        )

        assertThat(client.validateSignature(signedParams)).isTrue
    }

    @Test
    fun `validate signature returns false if incorrect`() {
        val signedParams = params + mapOf("custom2" to "Your", "signature" to signatureOfAll)

        assertThat(client.validateSignature(signedParams)).isFalse
    }

    @Test
    fun `lib version is in sync with the test`() {
        assertThat(FLEXPAY_VERSION).isEqualTo(protocolVersion)
    }

    @Test
    fun `get purchase url returns correct url`() {
        val urlParams = listOf(
            "priceAmount=45.20",
            "priceCurrency=EUR",
            "description=${testDescription.encodeUrlValue()}",
            "paymentMethod=CC",
            "email=${"foo@example.com".encodeUrlValue()}",
            "custom1=custom1",
            "custom2=custom2",
            "custom3=custom3",
            "referenceID=ref1",
            "shopID=$shopId",
            "successURL=${"http://example.com/approve".encodeUrlValue()}",
            "declineURL=${"http://example.com/decline".encodeUrlValue()}",
            "type=purchase",
            "version=$protocolVersion",
        )
            .sorted()
            .toMutableList()
            .apply { this.add("signature=08a33095ae3f4cee4ba25bc50a4076d11c008b84e40423a1cd436c29d2a9225b") }
            .joinToString("&")

        assertThat(
            client.getPurchaseUrl(
                priceAmount = BigDecimal("45.20"),
                priceCurrency = SaleCurrency.EUR,
                description = testDescription,
                paymentMethod = PaymentMethod.CC,
                referenceID = "ref1",
                custom1 = "custom1",
                custom2 = "custom2",
                custom3 = "custom3",
                successURL = "http://example.com/approve",
                declineURL = "http://example.com/decline",
                email = "foo@example.com"
            )
        )
            .isEqualTo(URL("${baseUrl}startorder?$urlParams"))
    }

    @Test
    fun `get status url returns correct url`() {
        val signedParams = mapOf("version" to protocolVersion, "saleID" to "433456")
        val signature = client.getSignature(signedParams)

        assertThat(client.getStatusUrlBySale("433456"))
            .isEqualTo(
                URL(
                    "${baseUrl}salestatus?saleID=433456&shopID=60678&version=$protocolVersion&signature=$signature",
                )
            )
    }

    @Test
    fun `get upgrade subscription url returns correct url`() {
        assertThat(
            client.getUpgradeSubscriptionUrl(
                precedingSaleID = "433456",
                period = "P1M",
                subscriptionType = SubscriptionType.recurring,
                priceAmount = "00.00".toBigDecimal(),
                priceCurrency = SaleCurrency.USD,
                custom1 = "custom1 value",
                description = testDescription,
                trialAmount = "0.01".toBigDecimal(),
                trialPeriod = "P3D",
                successURL = "http://successURL.test",
            )
        )
            .isEqualTo(
                URL(
                    "https://secure.verotel.com/startorder?custom1=custom1+value&" +
                            "name=My+D%C5%A1%C4%8D%C5%99%C4%8D%C5%99%C5%99%C4%9B%C5%99%C4%9B%26%3F%3Dblah123&period=P1M&" +
                            "precedingSaleID=433456&priceAmount=0.00&priceCurrency=USD&shopID=60678&" +
                            "subscriptionType=recurring&successURL=http%3A%2F%2FsuccessURL.test&trialAmount=0.01&" +
                            "trialPeriod=P3D&type=upgradesubscription&" +
                            "version=4&signature=cc4da94d214a4eb64a3f7affa3c39d168a9163c020f9caacca9bfd7498e33abc",
                )
            )
    }

}
