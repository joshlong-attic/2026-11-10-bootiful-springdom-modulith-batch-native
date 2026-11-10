package com.example.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

}


@Configuration
class MyConfig {

    @Bean
    Job job(JobRepository r,
            JobConfiguration configuration) {
        return new JobBuilder("jr", r)
                .flow(configuration.one(null, null, null))
                .next(configuration.two(null, null, null))
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }

}

@Configuration
class JobConfiguration {

    static final String STEP_ONE = "one";
    static final String STEP_TWO = "two";

    // Step
    // Chunk (10) (Reader | Processor | Writer )
    // Tasklet

    @Bean
    FlatFileItemReader<Customer> customerFlatFileItemReader(
            @Value("classpath:/people.csv") Resource csv
    ) {
        return new FlatFileItemReaderBuilder<Customer>()
                .resource(csv)
                .linesToSkip(1)
                .targetType(Customer.class)
                .name("customerFlatFileItemReader")
                .delimited(s -> s
                        .names("id,first,last,email".split(","))
                        .delimiter(",")

                )
                .build();
    }

    @Bean
    JdbcBatchItemWriter<Customer> jdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("""
                        insert into customer (id, first, last, email) values (:id, :first, :last, :email)
                        """)
                .assertUpdates(true)
                .build();
    }

    @Bean
    @Qualifier(STEP_TWO)
    Step two(JobRepository repository,
             FlatFileItemReader<Customer> reader,
             JdbcBatchItemWriter<Customer> writer
    ) {
        return new StepBuilder("two", repository)
                .<Customer, Customer>chunk(10)
                .reader(reader)
                .writer(writer)
                /*.writer(new ItemWriter<Customer>() {
                    @Override
                    public void write(Chunk<? extends Customer> chunk) throws Exception {
                        IO.println("========================================");
                        chunk.forEach(IO::println);
                        IO.println("========================================");
                    }
                })*/
                .build();
    }

    @Bean
    @Qualifier(STEP_ONE)
    Step one(PlatformTransactionManager platformTransactionManager,
             JdbcClient jdbcClient,
             JobRepository repository) {
        return new StepBuilder("one", repository)
                .tasklet((contribution, _) -> {
                    jdbcClient.sql("delete from customer").update();
                    IO.println("starting here");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    // @Bean
    Job peopleCsvEtlJob(JobRepository repository, @Qualifier(STEP_ONE) Step one,
                        @Qualifier(STEP_TWO) Step two) {
        return new JobBuilder("peopleCsvEtlJob", repository)
                .flow(one)
                .next(two)
                .end()
                .incrementer(new RunIdIncrementer())
                .build();
    }


}

record Customer(int id, String first, String last, String email) {
}

