package org.inanme.spring;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SomeConfiguration {

    @Bean
    public Supplier<Integer> int10() {
        return Suppliers.ofInstance(10);
    }

    @Bean
    public Supplier<Integer> int11() {
        return Suppliers.ofInstance(11);
    }

    @Bean
    @Autowired
    public SomeService someService(Supplier<Integer> int10) {
        return new SomeService(int10);
    }
}
