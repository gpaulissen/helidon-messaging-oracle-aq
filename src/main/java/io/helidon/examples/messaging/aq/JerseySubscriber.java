package io.helidon.examples.messaging.aq;

import java.util.concurrent.Flow;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.sse.SseEventSink;

import org.glassfish.jersey.media.sse.OutboundEvent;

public class JerseySubscriber<T> implements Flow.Subscriber<T> {

    private static final Logger LOGGER = Logger.getLogger(JerseySubscriber.class.getName());

    private final SseEventSink eventSink;
    private final org.glassfish.jersey.internal.jsr166.Flow.Subscriber<OutboundEvent> jerseySubscriber;
    private Flow.Subscription subscription;

    @SuppressWarnings("unchecked")
    JerseySubscriber(SseEventSink jerseySubscriber) {
        this.jerseySubscriber = (org.glassfish.jersey.internal.jsr166.Flow.Subscriber<OutboundEvent>)jerseySubscriber;
        this.eventSink = jerseySubscriber;
    }

    public static <T> JerseySubscriber<T> create(SseEventSink jerseySubscriber) {
        return new JerseySubscriber<>(jerseySubscriber);
    }

    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        this.subscription = subscription;
        jerseySubscriber.onSubscribe(new org.glassfish.jersey.internal.jsr166.Flow.Subscription() {
            @Override
            public void request(final long l) {
                subscription.request(l);
            }

            @Override
            public void cancel() {
                subscription.cancel();
            }
        });
    }

    @Override
    public void onNext(final T item) {
//        System.out.println("broadcaster to jersey " + item);
        try {
            jerseySubscriber.onNext(new OutboundEvent.Builder().data(item).build());
        } catch (Throwable e) {
            subscription.cancel();
            LOGGER.log(Level.WARNING, "Error from Jersey.", e);
        }
    }

    @Override
    public void onError(final Throwable throwable) {
        try {
            jerseySubscriber.onError(throwable);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Error from Jersey.", e);
        }
    }

    @Override
    public void onComplete() {
        try {
            jerseySubscriber.onComplete();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING, "Error from Jersey.", e);
        }
    }
}