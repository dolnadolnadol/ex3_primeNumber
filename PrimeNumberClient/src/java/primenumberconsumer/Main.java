/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package primenumberconsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
/**
 *
 * @author cld
 */
public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MessageProducer sentProducer = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        TextMessage message = null;
        TextListener listener = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            
            listener = new TextListener();
            
            Queue tempDest = session.createTemporaryQueue();
            consumer = session.createConsumer(tempDest);
            sentProducer = session.createProducer(queue);
            
            message = session.createTextMessage();
            consumer.setMessageListener(listener);
            connection.start();
            
            String ch;
            Scanner inp = new Scanner(System.in);
            while(true) {
                System.out.println("Enter two numbers. Use ',' to seperate each number. To end the program press enter");
                ch = inp.nextLine();
                if (ch.equals("")) {
                    break;
                }else{
                    message.setText(ch);
                    message.setJMSReplyTo(tempDest);
                    System.out.println("Sending message : "+ message.getText());
                    sentProducer.send(message);
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
