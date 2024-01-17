/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package primenumberproducer;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author cld
 */
public class TextListener implements MessageListener {

    private MessageProducer replyConsumer;
    private Session session;

    public TextListener(Session session) {

        this.session = session;
        try {
            replyConsumer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;
        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                if (msg.getText() == null) {
                    System.out.println("Message is null.");
                }else{
                    
                    System.out.println("Reading message: "+ msg.getText());
                }
            } else {
                System.err.println("Message is not a TextMessage");
            }
            if (msg.getText() != null) {
                String[] split = msg.getText().split(",");
                int count = 0;
                int firstNum = Integer.parseInt(split[0]);
                int SecondNum = Integer.parseInt(split[1]);
                boolean check = false;
                for (int j = firstNum; j <= SecondNum; j++) {
                    if(j<=1){
                        continue;
                    }
                    for (int i = 2; i <= Math.sqrt(j); i++) {
                        if (j % i == 0) {
                            check = true;
                            break;
                        }
                    }
                    if (!check) {
                        count++;
                    } else {
                        check = false;
                    }
                }

                TextMessage response = session.createTextMessage("sending message The number of the primes between " + firstNum + " and " + SecondNum + " is " + Integer.toString(count));
                System.out.println(response.getText());
                replyConsumer.send(message.getJMSReplyTo(), response);
            }

        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }

    }
}
