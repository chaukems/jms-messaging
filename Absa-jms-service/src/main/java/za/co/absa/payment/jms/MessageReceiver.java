/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.jms;

import za.co.absa.payment.bean.MessageStatus;
import za.co.absa.payment.bean.Message;
import java.io.StringWriter;
import java.util.List;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import za.co.absa.payment.bean.TargetEngine;
import za.co.absa.payment.service.RoutingRulesService;

@Component
public class MessageReceiver {

    @Autowired
    private Jaxb2Marshaller marshaller;

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private RoutingRulesService routingRules;

    @Value("${jms.destination.settlement.que}")
    private String SETTLEMENT_ADAPTOR_MQ;

    @Value("${jms.destination.settlement.acknak.que}")
    private String SETTLEMENT_ACKNAK_MQ;

    @Value("${jms.destination.payment.que}")
    private String PAYMENT_MQ;

    @JmsListener(destination = "paymentMQ")
    public void receiveMessage(Message message) throws JAXBException {
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);
        marshaller.marshal(message, result);

        System.out.println("Received <\n" + sw.toString() + ">");

        processMessage(message);
    }

    private void processMessage(Message message) {

        String destination = resolveDestination(PAYMENT_MQ);

        if (message.getStatus() != MessageStatus.ACCEPTED || message.getStatus() == MessageStatus.REJECTED) {
            sendResponseMessage(message, destination);
        } else {
            //List rules = getRoutingRules(); // Here you connect to the routing engine using rest.
            sendPaymentToSettlememtEngine(message, 1);
        }
    }

    public void sendPaymentToSettlememtEngine(Message message, long targetEngineId) {
        TargetEngine targetEngine = TargetEngine.getById(targetEngineId);
        String destination = resolveDestination(PAYMENT_MQ);

        if (targetEngine == TargetEngine.NONE) {
            sendPaymentEngineAdaptor(message);

        } else if (targetEngine == TargetEngine.WHATCHAMAKALIT) {
            sendPaymentEngineAdaptorWithAcknak(message);
            sendResponseMessage(message, destination);
        }
    }

    public void sendPaymentEngineAdaptor(Message message) {
        jmsTemplate.convertAndSend(SETTLEMENT_ADAPTOR_MQ, message, new MessagePostProcessor() {
            public javax.jms.Message postProcessMessage(javax.jms.Message message) throws JMSException {
                message.setIntProperty("Message_ID", 1235);
                message.setJMSCorrelationID("123-00002");
                return message;
            }
        });
    }

    public void sendPaymentEngineAdaptorWithAcknak(Message message) {
        jmsTemplate.convertAndSend(SETTLEMENT_ACKNAK_MQ, message, new MessagePostProcessor() {
            public javax.jms.Message postProcessMessage(javax.jms.Message message) throws JMSException {
                message.setIntProperty("Message_ID", 1236);
                message.setJMSCorrelationID("123-00002");
                return message;
            }
        });
    }

    public void sendResponseMessage(Message message, String destination) {

        Message responseMessage = new Message();
        responseMessage.setMessage("SWIFT MT195 Acknowledgement");
        responseMessage.setStatus(message.getStatus());

        jmsTemplate.convertAndSend(destination, responseMessage, new MessagePostProcessor() {
            public javax.jms.Message postProcessMessage(javax.jms.Message message) throws JMSException {
                message.setIntProperty("Message_ID", 1234);
                message.setJMSCorrelationID("123-00001");
                return message;
            }
        });
    }

    private List getRoutingRules() {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "http://localhost:8090/PaymentApp/rulesEngine/rules";
        ResponseEntity<List> response
                = restTemplate.getForEntity(fooResourceUrl, List.class);

        return response.getBody();
    }

    private String resolveDestination(String destinationName) {
        String dname = environment.getProperty("jms.destination." + destinationName,
                destinationName);
        return dname;
    }
}
