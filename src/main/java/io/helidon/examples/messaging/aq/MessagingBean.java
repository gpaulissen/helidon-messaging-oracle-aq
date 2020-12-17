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

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import io.helidon.common.reactive.Multi;
import io.helidon.messaging.connectors.aq.AqMessage;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

/**
 * Singleton bean for message processing.
 */
@ApplicationScoped
public class MessagingBean {

    @Outgoing("to-aq")
    public Publisher<String> toAq() {
        return FlowAdapters.toPublisher(
                Multi.interval(2, TimeUnit.SECONDS, Executors.newSingleThreadScheduledExecutor())
                        .map(i -> "Message " + i)
        );
    }


    @Incoming("from-aq")
    public CompletionStage<?> fromAq(AqMessage<String> msg) {
        System.out.println("Received: " + msg.getPayload());
        return msg.ack();
    }

}
