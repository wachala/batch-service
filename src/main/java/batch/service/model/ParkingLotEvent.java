package batch.service.model;

import lombok.Data;

@Data
public class ParkingLotEvent {
    private ParkingLotEventType eventType;
    private Long parkingLotId;
    private ParkingLotType parkingLotType;
    private Integer spots;
}
