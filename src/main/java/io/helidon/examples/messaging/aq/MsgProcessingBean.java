/*
 * Copyright (c)  2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.helidon.examples.messaging.aq;

import javax.enterprise.context.ApplicationScoped;
import javax.jms.JMSException;

import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.jdbc.JdbcDbClientProviderBuilder;
import io.helidon.messaging.connectors.aq.AqMessage;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Bean for message processing.
 */
@ApplicationScoped
public class MsgProcessingBean {

    @Incoming("fromAqtx")
    @Outgoing("toAqtx")
    //Leave commit by ack to outgoing connector
    @Acknowledgment(Acknowledgment.Strategy.NONE)
    public CompletionStage<AqMessage<String>> aqInSameTx(AqMessage<String> msg) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Logging to TEST_LOG table: " + msg.getPayload());
            UUID uuid = UUID.randomUUID();
            try {
                PreparedStatement statement = msg.getDBConnection()
                        .prepareStatement("INSERT INTO kec.test_log (uuid, message) VALUES (?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, msg.getPayload());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return msg;
        });
    }
}
