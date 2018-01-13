package batch.service.event.generator.model;

import batch.service.model.ParkingLotEventType;
import batch.service.model.ParkingLotType;
import lombok.Data;

import java.io.Serializable;

@Data
public class ParkingLotEvent implements Serializable {
    private ParkingLotEventType eventType;
    private Long parkingLotId;
    private ParkingLotType parkingLotType;
    private Integer spots;
}