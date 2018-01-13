package batch.service.receiver;

import batch.service.event.generator.model.ParkingLotEvent;
import batch.service.reader.ParkingLotEventQueue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ParkingLotEventReceiver {
    private final ObjectMapper objectMapper;
    private final ParkingLotEventQueue eventQueue;

    public ParkingLotEventReceiver(ObjectMapper objectMapper, ParkingLotEventQueue eventQueue) {
        this.objectMapper = objectMapper;
        this.eventQueue = eventQueue;
    }

    @JmsListener(destination = "DLQ")
    public void processMessage(String eventStr) throws IOException {
        ParkingLotEvent parkingLotEvent = objectMapper.readValue(eventStr, ParkingLotEvent.class);

        eventQueue.offer(parkingLotEvent);
        System.out.println(eventStr);
    }

}
