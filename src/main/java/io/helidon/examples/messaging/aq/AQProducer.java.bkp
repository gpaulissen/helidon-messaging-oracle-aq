package io.helidon.examples.messaging.aq;

import oracle.jms.AQjmsQueueConnectionFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

public class AQProducer {
    public static void main(String[] args) throws JMSException, SQLException {
        String hostname = "192.168.0.123";
        String port = "1521";
        String sid = "KECSID";
        String username = "sys as SYSDBA";
        String password = "kec";
        String queue = "KECQUEUE";

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

        java.sql.Connection conn = DriverManager.getConnection(url, props);
        QueueConnection qconn = AQjmsQueueConnectionFactory.createQueueConnection(conn);
        QueueSession qsess = qconn.createQueueSession(true, 0);
        Queue q = qsess.createQueue(queue);

        TextMessage msg = qsess.createTextMessage("TEST JAVA");
        QueueSender qsend = qsess.createSender(q);
        qsend.send(msg);

        qsess.commit();

    }
}
