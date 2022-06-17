package com.bitsafe.flexpay

import com.bitsafe.flexpay.builder.PurchaseBuilder
import com.bitsafe.flexpay.builder.SubscriptionBuilder
import com.bitsafe.flexpay.builder.SubscriptionUpgradeBuilder
import java.math.BigDecimal
import java.net.URL
import java.net.URLEncoder.encode
import java.security.MessageDigest
import java.util.*

typealias ParamsMap = Map<String, String>
typealias MutableParamsMap = MutableMap<String, String>

const val FLEXPAY_VERSION = "3.5"

/**
 * FlexPay client library
 *
 * This library allows merchants to use Verotel payment gateway
 * and get paid by their users via Credit card and other payment methods.
 *
 * It can also verify postback signatures
 *
 * Some functionality depends on your brand (Verotel, CardBilling, BitsafePay etc.).
 * Brand can be obtained manually (<code>Brand.CARDBILLING</code>) or derived from your Customer/Merchant ID
 * (<code>Brand.fromMerchantId("9804000000000000")</code>)
 *
 * @param websiteId Your website/shop ID - can be found in Control Center under "SETUP WEBSITES"
 * @param signatureKey Your website/shop FlexPay secret - can be found in Control Center as signatureKey in website's FlexPay options
 * @param brand Brand of your account in Verotel - can be obtained manually (<code>Brand.CARDBILLING</code>) or derived from your Customer/Merchant ID
 * (<code>Brand.fromMerchantId("9804000000000000")</code>)
 */

class FlexPayClient
@JvmOverloads
constructor(
    private val websiteId: String,
    private val signatureKey: String,
    val brand: Brand = Brand.VEROTEL,
) {
    constructor(
        websiteId: Int,
        signatureKey: String,
        brand: Brand = Brand.VEROTEL,
    ) : this(websiteId.toString(), signatureKey, brand)

    init {
        if (signatureKey.isEmpty()) {
            error("No FlexPay secret given");
        }

        if (websiteId.isEmpty()) {
            error("No shop ID (website ID) given");
        }
    }

    fun purchaseBuilder(): PurchaseBuilder = PurchaseBuilder(this)
    fun subscriptionBuilder(): SubscriptionBuilder = SubscriptionBuilder(this)
    fun subscriptionUpgradeBuilder(): SubscriptionUpgradeBuilder = SubscriptionUpgradeBuilder(this)

    /**
     * Provides URL where a buyer can pay the given amount for a one-off purchase
     *
     * @param priceAmount amount to be processed in nnn.nn format
     * @param priceCurrency priceCurrency 3 char ISO code, must be one of the Sale currencies (USD EUR GBP AUD CAD CHF DKK NOK SEK)
     *          NOTE: only EUR is can be used for DDEU payment method system
     * @param description description of the product. Text is displayed on the order page - max 100 printable characters
     * @param paymentMethod payment method, CC or DDEU (if not set then buyers can choose from available payment methods)
     *          NOTE: DDEU is available only in DE, AT, CH, BE, IT, NL, ES and FR
     *          If oneClickToken is sent, the payment method must be set to CC
     * @param referenceID merchant's reference identifier. It must be unique if provided
     * @param custom1 pass-through variable - max 255 printable characters
     * @param custom2 pass-through variable - max 255 printable characters
     * @param custom3 pass-through variable - max 255 printable characters
     * @param backURL URL for redirect after successful transaction - max 255 characters
     * @param declineURL URL for redirect after declined transaction - max 255 characters
     * @param oneClickToken the one-time oneClickToken from previous purchase
     *          NOTE: oneClickToken is excluded from signature calculations
     * @param email email of the buyer. If not set, it will be collected on the Order Page
     *          NOTE: email is excluded from signature calculations (max 100 chars else it will be ignored)
     * @param version version of the FlexPay call
     */
    @JvmOverloads
    fun getPurchaseUrl(
        priceAmount: BigDecimal,
        priceCurrency: SaleCurrency,
        description: String,
        paymentMethod: PaymentMethod? = null,
        referenceID: String? = null,
        custom1: String? = null,
        custom2: String? = null,
        custom3: String? = null,
        backURL: String? = null,
        declineURL: String? = null,
        oneClickToken: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION
    ): URL {
        val purchaseParams = mutableMapOf(
            "version" to version,
            "priceAmount" to priceAmount.toPlainString(),
            "priceCurrency" to priceCurrency.name,
        )

        purchaseParams.putIfNotNull("description", description)
        purchaseParams.putIfNotNull("oneClickToken", oneClickToken)
        purchaseParams.setCommonParams(
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            backURL = backURL,
            declineURL = declineURL,
            email = email
        )

        return generateUrl(brand.FLEXPAY_PATH, UrlType.PURCHASE, purchaseParams)
    }

    /**
     * Provides URL where a buyer can pay the initial amount and subscribe to payments for given service
     *
     * @param period Duration in ISO8601 format, for example: P30D, minimum is 7 days for recurring and 2 days for on-time
     * @param subscriptionType one-time or recurring
     *          NOTE: DDEU only supports one-time
     * @param trialAmount amount to be processed in nnn.nn format for the initial trial period, minimum is 2 days
     * @param trialPeriod amount to be processed in nnn.nn format for the initial trial period, minimum is 2 days
     * @param description description of the product. Text is displayed on the order page - max 100 printable characters
     * @param priceAmount amount to be processed in nnn.nn format
     * @param priceCurrency priceCurrency 3 char ISO code, must be one of the Sale currencies (USD EUR GBP AUD CAD CHF DKK NOK SEK)
     *          NOTE: only EUR is can be used for DDEU payment method system
     * @param paymentMethod payment method, CC or DDEU (if not set then buyers can choose from available payment methods)
     *          NOTE: DDEU is available only in DE, AT, CH, BE, IT, NL, ES and FR
     *          If oneClickToken is sent, the payment method must be set to CC
     * @param referenceID merchant's reference identifier. It must be unique if provided
     * @param custom1 pass-through variable - max 255 printable characters
     * @param custom2 pass-through variable - max 255 printable characters
     * @param custom3 pass-through variable - max 255 printable characters
     * @param backURL URL for redirect after successful transaction - max 255 characters
     * @param declineURL URL for redirect after declined transaction - max 255 characters
     * @param email email of the buyer. If not set, it will be collected on the Order Page
     *          NOTE: email is excluded from signature calculations (max 100 chars else it will be ignored)
     * @param version version of the FlexPay call
     */
    @JvmOverloads
    fun getSubscriptionUrl(
        period: String,
        subscriptionType: SubscriptionType,
        trialAmount: BigDecimal? = null,
        trialPeriod: String? = null,
        description: String? = null,
        priceAmount: BigDecimal,
        priceCurrency: SaleCurrency,
        paymentMethod: PaymentMethod? = null,
        referenceID: String? = null,
        custom1: String? = null,
        custom2: String? = null,
        custom3: String? = null,
        backURL: String? = null,
        declineURL: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION
    ): URL {
        val subscriptionParams = mutableMapOf(
            "version" to version,
            "priceAmount" to priceAmount.toPlainString(),
            "priceCurrency" to priceCurrency.name,
            "type" to "subscription",
            "subscriptionType" to subscriptionType.name,
            "period" to period,
        )

        subscriptionParams.putIfNotNull("name", description)
        subscriptionParams.putIfNotNull("trialAmount", trialAmount?.toPlainString())
        subscriptionParams.putIfNotNull("trialPeriod", trialPeriod)
        subscriptionParams.setCommonParams(
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            backURL = backURL,
            declineURL = declineURL,
            email = email
        )

        return generateUrl(brand.FLEXPAY_PATH, UrlType.SUBSCRIPTION, subscriptionParams)
    }

    /**
     * Provides URL with machine-readable information about the given sale
     * Obtained via sale ID
     * Data is provided in YAML format
     */
    @JvmOverloads
    fun getStatusUrlBySale(saleID: String, version: String = FLEXPAY_VERSION): URL {
        return generateUrl(
            brand.STATUS_PATH, UrlType.STATUS, mapOf(
                "saleID" to saleID,
                "version" to version
            )
        )
    }

    /**
     * Provides URL with machine-readable information about the given sale
     * Obtained via sale reference ID
     * Data is provided in YAML format
     */
    @JvmOverloads
    fun getStatusUrlByReference(referenceID: String, version: String = FLEXPAY_VERSION): URL {
        return generateUrl(
            brand.STATUS_PATH, UrlType.STATUS, mapOf(
                "referenceID" to referenceID,
                "version" to version
            )
        )
    }

    /**
     * Provides URL where a buyer can pay the initial amount and subscribe to payments for given service
     *
     * @param precedingSaleID Identificator of sale the buyer is upgrading from
     * @param period Duration in ISO8601 format, for example: P30D, minimum is 7 days for recurring and 2 days for on-time
     * @param subscriptionType one-time or recurring
     *          NOTE: DDEU only supports one-time
     * @param trialAmount amount to be processed in nnn.nn format for the initial trial period, minimum is 2 days
     * @param trialPeriod amount to be processed in nnn.nn format for the initial trial period, minimum is 2 days
     * @param description description of the product. Text is displayed on the order page - max 100 printable characters
     * @param priceAmount amount to be processed in nnn.nn format
     * @param priceCurrency priceCurrency 3 char ISO code, must be one of the Sale currencies (USD EUR GBP AUD CAD CHF DKK NOK SEK)
     *          NOTE: only EUR is can be used for DDEU payment method system
     * @param paymentMethod payment method, CC or DDEU (if not set then buyers can choose from available payment methods)
     *          NOTE: DDEU is available only in DE, AT, CH, BE, IT, NL, ES and FR
     *          If oneClickToken is sent, the payment method must be set to CC
     * @param custom1 pass-through variable - max 255 printable characters
     * @param custom2 pass-through variable - max 255 printable characters
     * @param custom3 pass-through variable - max 255 printable characters
     * @param backURL URL for redirect after successful transaction - max 255 characters
     * @param email email of the buyer. If not set, it will be collected on the Order Page
     *          NOTE: email is excluded from signature calculations (max 100 chars else it will be ignored)
     * @param version version of the FlexPay call
     */
    @JvmOverloads
    fun getUpgradeSubscriptionUrl(
        precedingSaleID: String,
        period: String,
        subscriptionType: SubscriptionType,
        trialAmount: BigDecimal? = null,
        trialPeriod: String? = null,
        description: String? = null,
        priceAmount: BigDecimal,
        priceCurrency: SaleCurrency,
        paymentMethod: PaymentMethod? = null,
        custom1: String? = null,
        custom2: String? = null,
        custom3: String? = null,
        backURL: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION
    ): URL {
        val upgradeParams = mutableMapOf(
            "precedingSaleID" to precedingSaleID,
            "version" to version,
            "priceAmount" to priceAmount.toPlainString(),
            "priceCurrency" to priceCurrency.name,
            "type" to "subscription",
            "subscriptionType" to subscriptionType.name,
            "period" to period,
        )

        upgradeParams.putIfNotNull("name", description)
        upgradeParams.putIfNotNull("trialAmount", trialAmount?.toPlainString())
        upgradeParams.putIfNotNull("trialPeriod", trialPeriod)
        upgradeParams.putIfNotNull("paymentMethod", paymentMethod?.name)
        upgradeParams.putIfNotNull("custom1", custom1)
        upgradeParams.putIfNotNull("custom2", custom2)
        upgradeParams.putIfNotNull("custom3", custom3)
        upgradeParams.putIfNotNull("backURL", backURL)
        upgradeParams.putIfNotNull("email", email)

        return generateUrl(brand.FLEXPAY_PATH, UrlType.UPGRADESUBSCRIPTION, upgradeParams)
    }

    /**
     * To allow your subscribers to cancel their subscriptions on your website
     * you can now generate a subscription specific cancel URL.
     *
     * @param saleID Verotel saleID identifier
     */
    fun getCancelSubscriptionUrl(saleID: String): URL = generateUrl(
        brand.CANCEL_PATH, UrlType.CANCEL_SUBSCRIPTION, mapOf("saleID" to saleID)
    )

    /**
     * Validates signature of a FlexPay postback to make sure the data is authentic
     * After every sale or transaction based action a corresponding postback is sent to the registered Postback URL.
     * Postback data are sent as GET request.
     *
     * @param urlParams GET params received in postback.
     */
    fun validateSignature(urlParams: ParamsMap): Boolean {
        val workingParams = urlParams.toMutableMap()
        val inputSignature = workingParams.remove("signature")?.lowercase()
        val generatedSignature = signature(workingParams)
        val generatedOldSignature = signature(workingParams, "sha1")

        // accept both old and new hash signature
        return inputSignature in listOf(generatedSignature, generatedOldSignature)
    }

    /**
     * Generates signature of FlexPay params given (non-FlexPay params are ignored)
     *
     * @param params Simple key-value map of params to be signed
     *
     * @return String FlexPay signature (HEX encoded SHA256 of FlexPay secret and FlexPay params)
     */
    fun getSignature(params: ParamsMap): String {
        return signature(params.onlySignatureParams())
    }

    private fun signature(params: ParamsMap, algorithm: String = "sha256"): String {
        val workingParams = params.toMutableMap().apply { putIfAbsent("shopID", websiteId) }.toSortedMap()
        val dataForDigest = listOf(signatureKey) + workingParams.map { "${it.key}=${it.value}" }
        val toDigest = dataForDigest.joinToString(":").toByteArray()
        val digest = MessageDigest.getInstance(algorithm).digest(toDigest)

        return HexFormat.of().formatHex(digest).lowercase()
    }

    private fun generateUrl(path: String, type: UrlType, params: ParamsMap): URL {
        if (params.isEmpty()) {
            error("no params given")
        }

        val workingParams = params.toMutableMap().apply {
            put("version", FLEXPAY_VERSION)
            putIfAbsent("shopID", websiteId)
            putIf("type", type.nameForUrl, condition = type.isPartOfUrl)
        }
            .filter { it.value.isNotEmpty() }
            .toSortedMap()

        return buildUrl(path, workingParams + mapOf("signature" to getSignature(workingParams)))
    }

    private fun buildUrl(path: String, params: ParamsMap): URL {
        val query = params.map {
            "${it.key}=${it.value.encodeUrlValue()}"
        }.joinToString("&")

        return URL("${brand.BASE_URL}$path?$query")
    }

    private fun ParamsMap.onlySignatureParams(): ParamsMap {
        val allowed = listOf(
            "version", "shopID", "priceAmount", "priceCurrency", "paymentMethod", "description",
            "referenceID", "saleID", "custom1", "custom2", "custom3", "subscriptionType",
            "period", "name", "trialAmount", "trialPeriod", "cancelDiscountPercentage", "type",
            "backURL", "declineURL", "precedingSaleID", "upgradeOption",
        )

        return filterKeys { allowed.contains(it) }
    }
}

enum class UrlType(val isPartOfUrl: Boolean) {
    PURCHASE(isPartOfUrl = true),
    SUBSCRIPTION(isPartOfUrl = true),
    UPGRADESUBSCRIPTION(isPartOfUrl = true),
    STATUS(isPartOfUrl = false),
    CANCEL_SUBSCRIPTION(isPartOfUrl = false);

    val nameForUrl = name.lowercase()
}

enum class SaleCurrency {
    USD, EUR, GBP, AUD, CAD, CHF, DKK, NOK, SEK
}

enum class PaymentMethod {
    CC, DDEU
}

enum class SubscriptionType {
    `one-time`, recurring
}

fun error(message: String): Nothing = throw FlexPayException(message)

fun MutableParamsMap.putIf(key: String, value: String, condition: Boolean) {
    if (condition) {
        put(key, value)
    }
}

fun MutableParamsMap.putIfNotNull(key: String, value: String?) {
    if (value != null) {
        put(key, value)
    }
}

fun String.encodeUrlValue() = encode(this, Charsets.UTF_8)

class FlexPayException(message: String) : Throwable(message)

private fun MutableParamsMap.setCommonParams(
    paymentMethod: PaymentMethod?,
    referenceID: String?,
    custom1: String?,
    custom2: String?,
    custom3: String?,
    backURL: String?,
    declineURL: String?,
    email: String?,
) {
    putIfNotNull("paymentMethod", paymentMethod?.name)
    putIfNotNull("referenceID", referenceID)
    putIfNotNull("custom1", custom1)
    putIfNotNull("custom2", custom2)
    putIfNotNull("custom3", custom3)
    putIfNotNull("backURL", backURL)
    putIfNotNull("declineURL", declineURL)
    putIfNotNull("email", email)
}