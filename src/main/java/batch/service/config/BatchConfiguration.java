package batch.service.config;

import batch.service.listener.JobCompletionNotificationListener;
import batch.service.model.ParkingLotEvent;
import batch.service.processor.ParkingLotEventProcessor;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
@EnableBatchProcessing
@Log
public class BatchConfiguration {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Autowired
    private ParkingLotService parkingLotService;

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

    @Bean
    public FlatFileItemReader<ParkingLotEvent> reader() {
        FlatFileItemReader<ParkingLotEvent> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<ParkingLotEvent>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "eventType", "parkingLotId", "parkingLotType", "spots" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<ParkingLotEvent>() {{
                setTargetType(ParkingLotEvent.class);
            }});
        }});
        return reader;
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
                .<ParkingLotEvent, ParkingLotEvent> chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
