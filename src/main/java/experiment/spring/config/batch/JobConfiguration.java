package experiment.spring.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2("start"))
            .validator(new JobParameterValidator())
            .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                System.out.println(" ============================");
                System.out.println(" >> Hello Spring Batch");
                System.out.println(" ============================");
                return RepeatStatus.FINISHED;
            }).build();
    }

    @Bean
    @JobScope
    public Step step2(@Value("#{jobParameters['name']}") String name) {
        return stepBuilderFactory.get("step2")
            .tasklet(((contribution, chunkContext) -> {
                System.out.println(" ============================");
                System.out.println(" >> hello " + name);
                System.out.println(" ============================");
                return RepeatStatus.FINISHED;
            })).build();
    }
}
