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

/**
 * Denotes a version of the FlexPay payment protocol. (Not the version of this library)
 */
const val FLEXPAY_VERSION = "4"

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
     * @param successURL Formerly known as backURL, successURL is a URL to which a buyer is redirected after successful transaction - max 255 characters
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
        successURL: String? = null,
        declineURL: String? = null,
        oneClickToken: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION
    ): URL {
        val purchaseParams = mutableMapOf(
            FlexPayRequestParameters.version.value to version,
            FlexPayRequestParameters.priceAmount.value to priceAmount.toPlainString(),
            FlexPayRequestParameters.priceCurrency.value to priceCurrency.name,
        )

        purchaseParams.putIfNotNull(FlexPayRequestParameters.description.value, description)
        purchaseParams.putIfNotNull(FlexPayRequestParameters.oneClickToken.value, oneClickToken)
        purchaseParams.setCommonParams(
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            successURL = successURL,
            declineURL = declineURL,
            email = email
        )

        return generateUrl(
            path = brand.FLEXPAY_PATH,
            type = UrlType.PURCHASE,
            params = purchaseParams
        )
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
     * @param successURL URL for redirect after successful transaction - max 255 characters
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
        successURL: String? = null,
        declineURL: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION
    ): URL {
        val subscriptionParams = mutableMapOf(
            FlexPayRequestParameters.version.value to version,
            FlexPayRequestParameters.priceAmount.value to priceAmount.toPlainString(),
            FlexPayRequestParameters.priceCurrency.value to priceCurrency.name,
            FlexPayRequestParameters.type.value to "subscription", // TODO - do not use strings
            FlexPayRequestParameters.subscriptionType.value to subscriptionType.name,
            FlexPayRequestParameters.period.value to period,
        )

        subscriptionParams.putIfNotNull(FlexPayRequestParameters.descriptionForSubscription.value, description)
        subscriptionParams.putIfNotNull(FlexPayRequestParameters.trialAmount.value, trialAmount?.toPlainString())
        subscriptionParams.putIfNotNull(FlexPayRequestParameters.trialPeriod.value, trialPeriod)
        subscriptionParams.setCommonParams(
            paymentMethod = paymentMethod,
            referenceID = referenceID,
            custom1 = custom1,
            custom2 = custom2,
            custom3 = custom3,
            successURL = successURL,
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
                FlexPayRequestParameters.saleID.value to saleID,
                FlexPayRequestParameters.version.value to version
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
            path = brand.STATUS_PATH,
            type = UrlType.STATUS,
            params = mapOf(
                FlexPayRequestParameters.referenceID.value to referenceID,
                FlexPayRequestParameters.version.value to version
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
     * @param successURL URL for redirect after successful transaction - max 255 characters
     * @param email email of the buyer. If not set, it will be collected on the Order Page
     *          NOTE: email is excluded from signature calculations (max 100 chars else it will be ignored)
     * @param version version of the FlexPay call
     * @param declineURL URL for redirect after unsuccessful transaction - max 255 characters
     * @param upgradeOption How to deal with the remaining period from the previous sale
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
        successURL: String? = null,
        email: String? = null,
        version: String = FLEXPAY_VERSION,
        declineURL: String? = null,
        upgradeOption: UpgradeOption? = null
    ): URL {
        val upgradeParams = mutableMapOf(
            FlexPayRequestParameters.precedingSaleID.value to precedingSaleID,
            FlexPayRequestParameters.version.value to version,
            FlexPayRequestParameters.priceAmount.value to priceAmount.toPlainString(),
            FlexPayRequestParameters.priceCurrency.value to priceCurrency.name,
            FlexPayRequestParameters.subscriptionType.value to subscriptionType.name,
            FlexPayRequestParameters.period.value to period,
        )

        upgradeParams.putIfNotNull(FlexPayRequestParameters.descriptionForSubscription.value, description)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.trialAmount.value, trialAmount?.toPlainString())
        upgradeParams.putIfNotNull(FlexPayRequestParameters.trialPeriod.value, trialPeriod)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.paymentMethod.value, paymentMethod?.name)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.custom1.value, custom1)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.custom2.value, custom2)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.custom3.value, custom3)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.successURL.value, successURL)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.email.value, email)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.declineURL.value, declineURL)
        upgradeParams.putIfNotNull(FlexPayRequestParameters.upgradeOption.value, upgradeOption?.name)

        return generateUrl(
            path = brand.FLEXPAY_PATH,
            type = UrlType.UPGRADESUBSCRIPTION,
            params = upgradeParams
        )
    }

    /**
     * To allow your subscribers to cancel their subscriptions on your website
     * you can now generate a subscription specific cancel URL.
     *
     * @param saleID Verotel saleID identifier
     */
    fun getCancelSubscriptionUrl(saleID: String): URL = generateUrl(
        path = brand.CANCEL_PATH,
        type = UrlType.CANCEL_SUBSCRIPTION,
        params = mapOf(FlexPayRequestParameters.saleID.value to saleID)
    )

    /**
     * Validates signature of a FlexPay postback to make sure the data is authentic
     * After every sale or transaction based action a corresponding postback is sent to the registered Postback URL.
     * Postback data are sent as GET request.
     * This method can be used to verify the authenticity of such postback
     *
     * @param urlParams *All* GET parameters received in the postback.
     */
    fun validateSignature(urlParams: ParamsMap): Boolean {
        val inputParams = urlParams.toMutableMap()
        val inputSignature = inputParams.remove(FlexPayRequestParameters.signature.value)?.lowercase()
        val checkSignature = signature(inputParams)
        val checkSignatureSha1 = signature(inputParams, SignatureHashAlgorithm.sha1)

        return inputSignature in listOf(checkSignature, checkSignatureSha1)
    }

    /**
     * Generates signature of FlexPay params within the params given.
     * Params that are not part of the signing process are ignored.
     *
     * @param params Simple key-value map of params, from which only signed params will be used
     *
     * @return String FlexPay signature (HEX encoded SHA256 of FlexPay secret and FlexPay params)
     */
    fun getSignature(params: ParamsMap): String {
        return signature(params.onlySignatureParams())
    }

    /**
     * Generates signature of all params given
     */
    private fun signature(flexPayParams: ParamsMap, algorithm: SignatureHashAlgorithm = SignatureHashAlgorithm.sha256): String {
        val signatureInput = flexPayParams
            .toMutableMap()
            .apply {
                putIfAbsent(FlexPayRequestParameters.shopID.value, websiteId)
            }
            .toSortedMap()
            .map {
                "${it.key}=${it.value}"
            }
            .prepend(signatureKey)
            .joinToString(":")
            .toByteArray()
        val digest = MessageDigest.getInstance(algorithm.name).digest(signatureInput)

        return HexFormat.of().formatHex(digest).lowercase()
    }

    /**
     * TODO: Add origin
     */
    private fun generateUrl(path: String, type: UrlType, params: ParamsMap): URL {
        if (params.isEmpty()) {
            error("No params given")
        }

        val workingParams = params.toMutableMap().apply {
            put(FlexPayRequestParameters.version.value, FLEXPAY_VERSION)
            putIfAbsent(FlexPayRequestParameters.shopID.value, websiteId)
            putIf(FlexPayRequestParameters.type.value, type.nameForUrl, condition = type.isPartOfUrl)
        }
            .filter { it.value.isNotEmpty() }
            .toSortedMap()

        return buildUrl(
            path = path,
            queryParams = workingParams + mapOf(
                FlexPayRequestParameters.signature.value to getSignature(workingParams)
            )
        )
    }

    private fun buildUrl(path: String, queryParams: ParamsMap): URL {
        val query = queryParams.map {
            "${it.key}=${it.value.encodeUrlValue()}"
        }.joinToString("&")

        return URL("${brand.BASE_URL}$path?$query")
    }

    private fun ParamsMap.onlySignatureParams(): ParamsMap {
        val signedKeys = FlexPayRequestParameters.entries.filter { it.isSigned }.map { it.value }

        return this.filterKeys {
            signedKeys.contains(it)
        }
    }
}

fun <E> List<E>.prepend(valueToPrepend: E): List<E> {
    return buildList(this.size + 1) {
        add(valueToPrepend)
        addAll(this@prepend)
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
    CC, DDEU, IDEAL
}

enum class SubscriptionType {
    `one-time`, recurring
}

/**
 * How to deal with the remaining period from a previous sale
 */
enum class UpgradeOption {
    /**
     * Remaining period is lost
     */
    lost,

    /**
     * Remaining period is added to the new sale
     */
    extend
}

enum class SignatureHashAlgorithm {
    sha256, sha1
}

enum class FlexPayRequestParameters(val isSigned: Boolean, val flexPayName: String? = null) {
    version(isSigned = true),
    shopID(isSigned = true),
    priceAmount(isSigned = true),
    priceCurrency(isSigned = true),
    paymentMethod(isSigned = true),
    description(isSigned = true),
    referenceID(isSigned = true),
    saleID(isSigned = true),
    custom1(isSigned = true),
    custom2(isSigned = true),
    custom3(isSigned = true),
    subscriptionType(isSigned = true),
    period(isSigned = true),
    descriptionForSubscription(isSigned = true, flexPayName = "name"),
    trialAmount(isSigned = true),
    trialPeriod(isSigned = true),
    cancelDiscountPercentage(isSigned = true),
    type(isSigned = true),
    successURL(isSigned = true),
    declineURL(isSigned = true),
    precedingSaleID(isSigned = true),
    upgradeOption(isSigned = true),
    signature(isSigned = false),
    email(isSigned = false),
    oneClickToken(isSigned = false);

    val value = flexPayName ?: name
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
    successURL: String?,
    declineURL: String?,
    email: String?,
) {
    putIfNotNull(FlexPayRequestParameters.paymentMethod.value, paymentMethod?.name)
    putIfNotNull(FlexPayRequestParameters.referenceID.value, referenceID)
    putIfNotNull(FlexPayRequestParameters.custom1.value, custom1)
    putIfNotNull(FlexPayRequestParameters.custom2.value, custom2)
    putIfNotNull(FlexPayRequestParameters.custom3.value, custom3)
    putIfNotNull(FlexPayRequestParameters.successURL.value, successURL)
    putIfNotNull(FlexPayRequestParameters.declineURL.value, declineURL)
    putIfNotNull(FlexPayRequestParameters.email.value, email)
}