package org.inanme.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.IntSupplier;

@Configuration
public class SomeConfiguration {

    @Bean
    public IntSupplier int10() {
        return new ConstantIntSupplier(10);
    }

    @Bean
    public IntSupplier int11() {
        return new ConstantIntSupplier(11);
    }

    @Bean
    @Autowired
    public SomeService someService(IntSupplier int10) {
        return new SomeService(int10);
    }
}
