package com.bitsafe.flexpay

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FlexPayClientKtTest {

    @Test
    fun `prepend works`() {
        assertThat(listOf("foo", "bar").prepend("baz")).isEqualTo(listOf("baz", "foo", "bar"))
    }
}