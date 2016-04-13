package org.inanme.springbatch;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.jms.Queue;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@ContextConfiguration({"/jms-config.xml"})
public class JmsTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    @Qualifier("jmsTemplateWithLowPriority")
    JmsTemplate jmsTemplateWithLowPriority;


    @Autowired
    @Qualifier("jmsTemplateWithHighPriority")
    JmsTemplate jmsTemplateWithHighPriority;

    @Autowired
    Queue queue1;

    private ExecutorService low = Executors.newCachedThreadPool();

    private ExecutorService high = Executors.newCachedThreadPool();

    @Test
    public void test() throws InterruptedException {
        System.out.println("-------------------------------------");
        String now = LocalDateTime.now().toString();

        IntStream.range(0, 20).forEach(i -> low.submit(() -> jmsTemplateWithLowPriority
            .send(queue1, session -> session.createObjectMessage(String.format("%3d %s %s", i, "Low ", now)))));

        TimeUnit.MILLISECONDS.sleep(1000l);

        IntStream.range(0, 20).forEach(i -> high.submit(() -> jmsTemplateWithHighPriority
            .send(queue1, session -> session.createObjectMessage(String.format("%3d %s %s", i, "High", now)))));
        TimeUnit.MINUTES.sleep(20l);
    }

    @Test
    public void test1() throws InterruptedException {

        TimeUnit.MINUTES.sleep(20l);
    }
}


