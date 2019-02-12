package za.co.absa.payment;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.junit4.SpringRunner;
import za.co.absa.payment.bean.Message;
import za.co.absa.payment.bean.MessageStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentAppTest {

    private static final Logger logger = LoggerFactory
            .getLogger(PaymentAppTest.class);
    private static final String TEST_DEST = "paymentMQ";

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSendingMessage() {

        Message message = generateTestMessage();
        jmsTemplate.convertAndSend(TEST_DEST, message,
                new MessagePostProcessor() {
            @Override
            public javax.jms.Message postProcessMessage(javax.jms.Message message)
                    throws JMSException {
                if (message instanceof BytesMessage) {
                    BytesMessage messageBody = (BytesMessage) message;
                    messageBody.reset();
                    Long length = messageBody.getBodyLength();
                    logger.debug("***** MESSAGE LENGTH is {} bytes",
                            length);
                    byte[] byteMyMessage = new byte[length.intValue()];
                    int red = messageBody.readBytes(byteMyMessage);
                    logger.debug(
                            "***** SENDING MESSAGE - n"
                            + "<!-- MSG START -->n{}n<!-- MSG END -->",
                            new String(byteMyMessage));
                }
                return message;
            }

        });
        Message message2 = (Message) jmsTemplate.receiveAndConvert(TEST_DEST);
        assertNotNull("Message MUST return from JMS", message2);
        assertEquals("Message MUST match", message.getMessage(), message2.getMessage());
        assertEquals("Status MUST match", message.getStatus(),
                message2.getStatus());
    }

    private Message generateTestMessage() {
        Message message = new Message();
        message.setMessage("SWIFT MT101");
        message.setStatus(MessageStatus.ACCEPTED);
        logger.debug("Generated Test Message: " + message.toString());
        return message;
    }

}
