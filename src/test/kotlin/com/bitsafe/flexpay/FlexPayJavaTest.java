package com.bitsafe.flexpay;

import com.bitsafe.flexpay.enums.SaleCurrency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class FlexPayJavaTest {

    @Test
    void default_parameters_in_java_work() {
        FlexPayClient client = new FlexPayClient("7", "xxx");
        URL url = client.getPurchaseUrl(BigDecimal.TEN, SaleCurrency.AUD, "Foo");

        assertThat(url).isNotNull();
    }


}
