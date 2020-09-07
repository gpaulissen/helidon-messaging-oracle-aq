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

import java.sql.SQLException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.SubmissionPublisher;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.ws.rs.sse.SseEventSink;

import io.helidon.common.reactive.Multi;
import io.helidon.config.Config;
import io.helidon.messaging.connectors.aq.AqMessage;
import io.helidon.messaging.connectors.jms.JmsMessage;

import oracle.jms.AQjmsSession;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

/**
 * Bean for message processing.
 */
@ApplicationScoped
public class MsgProcessingBean {
    private final SubmissionPublisher<String> emitter = new SubmissionPublisher<>();
    private final SubmissionPublisher<String> broadcaster = new SubmissionPublisher<>();
    private final Config config;

    @Inject
    public MsgProcessingBean(Config config) {
        this.config = config;
    }

    /**
     * Create a publisher for the emitter.
     *
     * @return A Publisher from the emitter
     */
    @Outgoing("multiplyVariants")
    public Publisher<String> preparePublisher() {
        // Create new publisher for emitting to by this::process
        return ReactiveStreams
                .fromPublisher(
                        FlowAdapters.toPublisher(
                                Multi.create(emitter)
                                        // Log all signals
                                        .log()
                        )
                )
                .buildRs();
    }

    /**
     * Returns a builder for a processor that maps a string into three variants.
     *
     * @return ProcessorBuilder
     */
    @Incoming("multiplyVariants")
    @Outgoing("toJms")
    public ProcessorBuilder<String, Message<String>> multiply() {
        // Multiply to 3 variants of same message
        return ReactiveStreams.<String>builder()
                .map(Message::of);
    }


    @Incoming("fromJms")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public CompletionStage<?> fromJms(AqMessage<String> msg) throws SQLException {
        System.out.println("Received: " + msg.getPayload());
        // direct commit
        //msg.getDBConnection().commit();
        this.broadcaster.submit(msg.getPayload());
        // ack commits only in non-transacted mode
        return msg.ack();
    }

    /**
     * Consumes events.
     *
     * @param eventSink event sink
     */
    public void addSink(final SseEventSink eventSink) {
        broadcaster.subscribe(JerseySubscriber.create(eventSink));
    }

    /**
     * Emit a message.
     *
     * @param msg message to emit
     */
    public void process(final String msg) {
        emitter.submit(msg);
    }
}
