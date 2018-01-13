package batch.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;

@Builder
@JsonDeserialize(builder = ParkingLotUpdateEvent.ParkingLotUpdateEventBuilder.class)
public class ParkingLotUpdateEvent {
    @JsonProperty("parkingLotId")
    private Long parkingLotId;

    @JsonProperty("parkingLotType")
    private ParkingLotType parkingLotType;

    @JsonProperty("spotsDelta")
    private Integer spotsDelta;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ParkingLotUpdateEventBuilder {
    }
}
