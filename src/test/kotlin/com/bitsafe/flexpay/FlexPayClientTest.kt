package com.bitsafe.flexpay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.net.URL

internal class FlexPayClientTest {
    val protocolVersion = "3.5";
    val secret = "zpXwe2D77g4P7ysGJcr3rY87TBYs6J";
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
        "backURL" to "http://backURL.test",
        "declineURL" to "http://declineURL.test",
        "cancelDiscountPercentage" to "30",
        "blah" to "something",
    );
    val signatureOfFiltered = "c32809a80e3a97d4be5c05b8e241d32141b169c9d7d74294ce50ba313d6817b3"
    val signatureOfAll = "a8c18e900fad7af686c3b6dc9f00f197f9d6ea210566ef0d81fb07555f23504d"
    val oldSha1SignatureOfAll = "3650ddcc9360de60f4fc78604057c9f3246923cb"
    val baseUrl = "https://secure.verotel.com/"
    val commonURLParams = buildString {
        append("backURL=http%3A%2F%2FbackURL.test")
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
        append("&subscriptionType=RECURRING")
        append("&trialAmount=0.01")
        append("&trialPeriod=P3D")
    }
    val shopId = "60678"
    val client = FlexPayClient(shopId, secret, brand = Brand.VEROTEL)

    @Test
    fun `getSignature returns correct signature`() {
        assertThat(client.getSignature(params)).isEqualTo(signatureOfFiltered)
    }

    @Test
    fun `validates signature as correct`() {
        val signedParams = params + mapOf("signature" to signatureOfAll.uppercase())

        assertThat(client.validateSignature(signedParams)).isTrue
    }

    @Test
    fun `validates signature as correct for old sha1 signature`() {
        val signedParams = params + mapOf("signature" to oldSha1SignatureOfAll.uppercase())

        assertThat(client.validateSignature(signedParams)).isTrue
    }

    @Test
    fun `validate signature returns false if incorrect`() {
        val signedParams = params + mapOf("custom2" to "Your", "signature" to signatureOfAll)

        assertThat(client.validateSignature(signedParams)).isFalse
    }

    @Test
    fun `lib version is in sync with the test`() {
        assertThat(FlexPayClient.PROTOCOL_VERSION).isEqualTo(protocolVersion)
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
            "backURL=${"http://example.com/approve".encodeUrlValue()}",
            "declineURL=${"http://example.com/decline".encodeUrlValue()}",
            "type=purchase",
            "version=$protocolVersion",
        )
            .sorted()
            .toMutableList()
            .apply { this.add("signature=e72fb1b6e51024a21c5dfdfd95792a99d774ddaf3036a6ecd2d7def186fb8006") }
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
                backURL = "http://example.com/approve",
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
        val signedParams = params + mapOf("type" to "upgradesubscription", "version" to protocolVersion)
        val signature = client.getSignature(signedParams)

        assertThat(client.getUpgradeSubscriptionUrl(params))
            .isEqualTo(
                URL(
                    "${baseUrl}startorder?$commonURLParams&type=upgradesubscription&version=$protocolVersion&signature=$signature",
                )
            )
    }

}
