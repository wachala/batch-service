package batch.service.config;

import batch.service.event.generator.model.ParkingLotEvent;
import batch.service.listener.JobCompletionNotificationListener;
import batch.service.processor.ParkingLotEventProcessor;
import batch.service.reader.ParkingLotEventQueue;
import batch.service.reader.ParkingLotEventReader;
import batch.service.service.ParkingLotService;
import lombok.extern.java.Log;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
@EnableBatchProcessing
@Log
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public DataSource dataSource;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private ParkingLotEventQueue eventQueue;

    @Autowired
    private JobCompletionNotificationListener jobCompletionNotificationListener;

    @Scheduled(fixedDelay = 10000)
    public void perform() throws Exception {
        log.info("Job Started at :" + LocalDateTime.now());

        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = jobLauncher.run(processParkingLotEventJob(jobCompletionNotificationListener), param);

        log.info("Job finished with status :" + execution.getStatus());
    }

    public ParkingLotEventReader reader() {
        return new ParkingLotEventReader(eventQueue);
    }

    @Bean
    public ParkingLotEventProcessor processor() {
        return new ParkingLotEventProcessor(parkingLotService);
    }

    @Bean
    public JdbcBatchItemWriter<ParkingLotEvent> writer() {
        JdbcBatchItemWriter<ParkingLotEvent> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO events (PARKING_LOT_ID, EVENT_TYPE, SPOTS) VALUES " +
                "(:parkingLotId, :eventType, :spots)");
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public Job processParkingLotEventJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("processParkingLotEventJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step())
                .end()
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<ParkingLotEvent, ParkingLotEvent>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
