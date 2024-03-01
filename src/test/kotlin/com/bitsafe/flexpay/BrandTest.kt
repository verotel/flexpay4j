package com.bitsafe.flexpay

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class BrandTest {
    @Test
    fun `create from merchant id - Verotel brand`() {
        val brand = Brand.fromMerchantId("9804000000000000")
        assertThat(brand).isInstanceOf(Brand.VEROTEL::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.verotel.com")
    }

    @Test
    fun `create from merchant id - CardBilling brand`() {
        val brand = Brand.fromMerchantId("9762000000000000")
        assertThat(brand).isInstanceOf(Brand.CARDBILLING::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.billing.creditcard")
    }

    @Test
    fun `create from merchant id - BitsafePay brand`() {
        val brand = Brand.fromMerchantId("9653000000000000")
        assertThat(brand).isInstanceOf(Brand.BITSAFEPAY::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.bitsafepay.com")
    }

    @Test
    fun `create from merchant id - Bill brand`() {
        val brand = Brand.fromMerchantId("9511000000004236")
        assertThat(brand).isInstanceOf(Brand.BILL::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.bill.creditcard")
    }

    @Test
    fun `create from merchant id - PaintFests brand`() {
        val brand = Brand.fromMerchantId("9444000000000001")
        assertThat(brand).isInstanceOf(Brand.PAINTFEST::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.paintfestpayments.com")
    }

    @Test
    fun `create from merchant id GayCharge brand`() {
        val brand = Brand.fromMerchantId("9388000000000001")
        assertThat(brand).isInstanceOf(Brand.GAYCHARGE::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.gaycharge.com")
    }

    @Test
    fun `create from merchant id YoursafeDirect brand`() {
        val brand = Brand.fromMerchantId("9001000000000001")
        assertThat(brand).isInstanceOf(Brand.YOURSAFE_DIRECT::class.java)
        assertThat(brand.BASE_URL).isEqualTo("https://secure.yoursafedirect.com")
    }

    @Test
    fun `create from merchant id - unknown brand`() {
        assertThatThrownBy {
            Brand.fromMerchantId("1234000000000000")
        }
            .isInstanceOf(FlexPayException::class.java)
            .hasMessage("Invalid merchant ID")
    }

    @Test
    fun `createFromName - Verotel brand`() {
        val brand = Brand.fromName("Verotel")
        assertThat(brand).isInstanceOf(Brand.VEROTEL::class.java)
    }

    @Test
    fun `createFromName - CardBilling brand`() {
        val brand = Brand.fromName("CardBilling")
        assertThat(brand).isInstanceOf(Brand.CARDBILLING::class.java)
    }

    @Test
    fun `createFromName - BitsafePay brand`() {
        val brand = Brand.fromName("BitsafePay")
        assertThat(brand).isInstanceOf(Brand.BITSAFEPAY::class.java)
    }

    @Test
    fun `createFromName - Bill brand`() {
        val brand = Brand.fromName("Bill")
        assertThat(brand).isInstanceOf(Brand.BILL::class.java)
    }

    @Test
    fun `createFromName - PaintFest brand`() {
        val brand = Brand.fromName("PaintFest")
        assertThat(brand).isInstanceOf(Brand.PAINTFEST::class.java)
    }

    @Test
    fun `createFromName - GayCharge brand`() {
        val brand = Brand.fromName("GayCharge")
        assertThat(brand).isInstanceOf(Brand.GAYCHARGE::class.java)
    }

    @Test
    fun `createFromName - unknown brand`() {
        assertThatThrownBy {
            Brand.fromName("UnknownBrand")
        }
            .isInstanceOf(FlexPayException::class.java)
            .hasMessage("Invalid brand name")
    }
}
