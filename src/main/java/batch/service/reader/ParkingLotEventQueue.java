package batch.service.reader;

import batch.service.event.generator.model.ParkingLotEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ParkingLotEventQueue {
    private ConcurrentLinkedQueue<ParkingLotEvent> queue;

    public ParkingLotEventQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    public void offer(ParkingLotEvent event) {
        queue.offer(event);
    }

    public ParkingLotEvent poll() {
        if (queue.size() > 1)
            return queue.poll();
        return null;
    }
}
