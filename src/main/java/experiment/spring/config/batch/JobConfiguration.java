package experiment.spring.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
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


//    @Bean
//    public JobParametersIncrementer incrementer() {
//        return new CustomJobParametersIncrementer();
//    }
//
//    @Bean
//    public Job batchJob() {
//        return jobBuilderFactory.get("job")
//            .validator(new JobParameterValidator())
//            .incrementer(incrementer())
//            .start(step1())
//            .next(step2("start"))
//            .build();
//    }
//
//    @Bean
//    @JobScope
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
//            .tasklet((contribution, chunkContext) -> {
//                System.out.println(" ============================");
//                System.out.println(" >> Hello Spring Batch");
//                System.out.println(" ============================");
//                return RepeatStatus.FINISHED;
//            }).build();
//    }
//
//    @Bean
//    @JobScope
//    public Step step2(@Value("#{jobParameters['name']}") String name) {
//        return stepBuilderFactory.get("step2")
//            .tasklet(((contribution, chunkContext) -> {
//                System.out.println(" ============================");
//                System.out.println(" >> hello " + name);
//                System.out.println(" ============================");
//                return RepeatStatus.FINISHED;
//            })).build();
//    }


    @Bean
    public Job batchJob() {
        return this.jobBuilderFactory.get("batchJob")
//                .incrementer(new RunIdIncrementer())
            .incrementer(new CustomJobParametersIncrementer())
            .start(step1())
            .next(step2())
            .next(step3())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step1 has executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step2 has executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet((contribution, chunkContext) -> {
                System.out.println("step3 has executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
