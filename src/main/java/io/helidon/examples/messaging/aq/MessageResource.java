
package io.helidon.examples.messaging.aq;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;

@Path("/example")
public class MessageResource {

    private final MsgProcessingBean msgBean;

    @Inject
    public MessageResource(MsgProcessingBean msgBean) {
        this.msgBean = msgBean;
    }

    @Path("/send/{msg}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getSend(@PathParam("msg") String msg) {
        msgBean.process(msg);
    }

    @GET
    @Path("sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listenToEvents(@Context SseEventSink eventSink) {
        msgBean.addSink(eventSink);
    }


}
