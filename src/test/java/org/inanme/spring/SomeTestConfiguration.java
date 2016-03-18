package org.inanme.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.IntSupplier;

@Configuration
public class SomeTestConfiguration {

    @Bean
    public IntSupplier int10() {
        return new ConstantIntSupplier(Integer.MAX_VALUE);
    }
}
