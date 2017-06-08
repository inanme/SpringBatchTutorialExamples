package org.inanme.spring;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@ContextConfiguration(
        classes = {SomeConfiguration.class, SomeTestConfiguration.class},
        loader = AnnotationConfigContextLoader.class)
public class HelloWorldAnno {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private SomeService someService;

    @Test
    public void test() {
        assertThat(someService.callService(), is(10));
    }
}
