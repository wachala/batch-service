package batch.service.processor;

import batch.service.event.generator.model.ParkingLotEvent;
import batch.service.model.ParkingLotEventType;
import batch.service.model.ParkingLotUpdateEvent;
import batch.service.service.ParkingLotService;
import lombok.extern.java.Log;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log
public class ParkingLotEventProcessor implements ItemProcessor<ParkingLotEvent, ParkingLotEvent> {

    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotEventProcessor(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @Override
    public ParkingLotEvent process(ParkingLotEvent parkingLotEvent) throws Exception {
        log.info("Processing event: " + parkingLotEvent.toString());

        ParkingLotUpdateEvent.ParkingLotUpdateEventBuilder parkingLotUpdateEventBuilder = ParkingLotUpdateEvent.builder()
                .parkingLotId(parkingLotEvent.getParkingLotId())
                .parkingLotType(parkingLotEvent.getParkingLotType());

        if (parkingLotEvent.getEventType() == ParkingLotEventType.FREE)
            parkingLotUpdateEventBuilder.spotsDelta(-parkingLotEvent.getSpots());
        else
            parkingLotUpdateEventBuilder.spotsDelta(parkingLotEvent.getSpots());

        parkingLotService.updateParkingLot(parkingLotUpdateEventBuilder.build());

        return parkingLotEvent;
    }

}

