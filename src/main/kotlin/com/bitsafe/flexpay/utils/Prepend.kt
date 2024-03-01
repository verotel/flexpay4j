package com.bitsafe.flexpay.utils

fun <E> List<E>.prepend(valueToPrepend: E): List<E> {
    return buildList(this.size + 1) {
        add(valueToPrepend)
        addAll(this@prepend)
    }
}