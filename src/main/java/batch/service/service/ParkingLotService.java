package batch.service.service;

import batch.service.encoder.FeignSimpleEncoderConfig;
import batch.service.model.ParkingLotUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient(name = "parking-lot-service", configuration = FeignSimpleEncoderConfig.class)
@Service
public interface ParkingLotService {

    @RequestMapping(method = PUT, value = "/parking-lot-service/api/parking-lot",
            headers = {"content-type=application/json"})
    void updateParkingLot(ParkingLotUpdateEvent event);

    @Component
    @Slf4j
    class ParkingLotServiceFallback implements ParkingLotService {

        @Override
        public void updateParkingLot(ParkingLotUpdateEvent parkingLot) {
            log.error("Cannot update parking lots. Could not connect to parking-lot-service");
        }

    }

}
