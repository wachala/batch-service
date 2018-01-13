package batch.service.encoder;

import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;


public class FeignSimpleEncoderConfig {

    @Bean
    public Encoder encoder() {
        return new FeignSpringFormEncoder();
    }

} 
