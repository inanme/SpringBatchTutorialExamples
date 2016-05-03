package org.inanme.spring;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@ContextConfiguration({"/app-config.xml", "/mock-client.xml"})
public class HelloWorldXML {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private SomeService someService;

    @Autowired
    private WebClient webClient;

    @Test
    public void testOverride() {
        assertThat(someService.callService(), is(1));
    }

    @Test
    public void testFactories() {
        WebClient local = new WebClient("val1", "val2");
        assertThat(webClient.toString(), is(local.toString()));
    }
}
