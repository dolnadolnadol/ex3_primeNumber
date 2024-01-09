/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package livescore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author cld
 */
public class Main {

    @Resource(mappedName = "jms/SimpleJMSTopic")
    private static Topic topic;

    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //DEFAULT USE PULBLISH/SUBSCRIBE
        Connection connection = null;
        String answer = "";
        Destination dest = null;

        try {
            dest = (Destination) topic;
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(dest);
            TextMessage message = session.createTextMessage();
            while (true) {
                System.out.println(
                    "To end program, type Q or q, " + "then <return>");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (!((answer.equals("q")) || (answer.equals("Q")))) {
                    try {
                        System.out.print("Enter Live Score ");
                        answer = reader.readLine();
                    } catch (IOException e) {
                        System.err.println("I/O exception: " + e.toString());
                    }
                    message.setText(answer);
                    producer.send(message);
                }

                break;
            }
            producer.send(session.createMessage()); // send nothing for tell the recieve side that this is the end of the message
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
