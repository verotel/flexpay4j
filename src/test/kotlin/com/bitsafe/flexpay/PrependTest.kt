package com.bitsafe.flexpay

import com.bitsafe.flexpay.utils.prepend
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PrependTest {

    @Test
    fun `prepend works`() {
        assertThat(listOf("foo", "bar").prepend("baz")).isEqualTo(listOf("baz", "foo", "bar"))
    }
}