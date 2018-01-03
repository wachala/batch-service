package batch.service.config;

import batch.service.listener.JobCompletionNotificationListener;
import batch.service.model.ParkingLotEvent;
import batch.service.processor.ParkingLotEventProcessor;
import batch.service.service.ParkingLotService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Autowired
    private ParkingLotService parkingLotService;

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
    public Job processParkingLotEvent(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("processParkingLotEvent")
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
