package org.inanme.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class JmsModule {

    public static class ExampleListener implements MessageListener {

        public void onMessage(Message message) {
            if (message instanceof ObjectMessage) {
                try {
                    System.out.println(ObjectMessage.class.cast(message).getObject());
                } catch (JMSException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                throw new IllegalArgumentException("Message must be of type TextMessage");
            }
        }
    }
}
