package org.inanme.springbatch;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.jms.Queue;
import java.util.concurrent.TimeUnit;

@ContextConfiguration({"/jms-config.xml"})
public class JmsTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    Queue queue1;

    @Test
    public void test() throws InterruptedException {
        jmsTemplate.send(queue1, session -> session.createObjectMessage(1));
        jmsTemplate.send(queue1, session -> session.createObjectMessage(2));
        jmsTemplate.send(queue1, session -> session.createObjectMessage(3));
        TimeUnit.MINUTES.sleep(20l);
    }
}
