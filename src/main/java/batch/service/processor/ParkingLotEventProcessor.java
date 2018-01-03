package batch.service.processor;

import batch.service.model.ParkingLotEvent;
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

        //TODO call update on parking-lot-service
        //parkingLotService

        return parkingLotEvent;
    }

}

