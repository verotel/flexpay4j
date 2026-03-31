package com.bitsafe.flexpay.utils

import java.net.URLEncoder

fun String.encodeUrlValue() = URLEncoder.encode(this, "UTF-8")
