package batch.service.reader;

import batch.service.event.generator.model.ParkingLotEvent;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotEventReader implements ItemReader<ParkingLotEvent> {

    private final ParkingLotEventQueue eventQueue;

    public ParkingLotEventReader(ParkingLotEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public ParkingLotEvent read() throws Exception {
        return eventQueue.poll();
    }
}
