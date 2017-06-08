package org.inanme.spring;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SomeTestConfiguration {

    @Bean
    public Supplier<Integer> int10() {
        return Suppliers.ofInstance(Integer.MAX_VALUE);
    }
}
