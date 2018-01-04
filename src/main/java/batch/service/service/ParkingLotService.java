package batch.service.service;

import batch.service.model.ParkingLot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient(name = "parking-lot-service", fallback = ParkingLotService.ParkingLotServiceFallback.class)
@Service
public interface ParkingLotService {

    //TODO endpoint not implemented yet
    @RequestMapping(method = PUT, value = "/parking-lot-service/api/parking-lot/{id}")
    void updateParkingLot(@PathVariable(name = "id") Long id, ParkingLot parkingLot);

    @Component
    @Slf4j
    class ParkingLotServiceFallback implements ParkingLotService {

        @Override
        public void updateParkingLot(Long id, ParkingLot parkingLot) {
            log.error("Cannot update parking lots. Could not connect to parking-lot-service");
        }
    }
}
