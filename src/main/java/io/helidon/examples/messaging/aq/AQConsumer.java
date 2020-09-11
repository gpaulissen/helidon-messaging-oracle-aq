package io.helidon.examples.messaging.aq;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TextMessage;

import oracle.jms.AQjmsQueueConnectionFactory;

public class AQConsumer {
    public static void main(String args[]) throws SQLException, JMSException {
        String hostname = "192.168.0.123";
        String port = "1521";
        String sid = "KECSID";
        String username = "sys as SYSDBA";
        String password = "kec";
        String queue = "KEC_OUT_QUEUE";

        String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)" +
                "(Host=" + hostname + ")" +
                "(Port=" + port + ")" +
                ")" +
                "(CONNECT_DATA=(SID=" + sid + "))" +
                ")";

        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);

        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

        java.sql.Connection c = DriverManager.getConnection(url, props);

        QueueConnection conn = AQjmsQueueConnectionFactory.createQueueConnection(c);

        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue q = session.createQueue(queue);
        MessageConsumer consumer = session.createConsumer(q);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                TextMessage received = (TextMessage) consumer.receiveNoWait();
                if(received == null){
                    return;
                }
                System.out.println("Received a message! " + received.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        conn.start();


    }
}
