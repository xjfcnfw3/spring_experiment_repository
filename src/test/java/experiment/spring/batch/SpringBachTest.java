package experiment.spring.batch;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringBachTest {

    @Autowired
    Job job;
    @Autowired
    JobLauncher jobLauncher;

    @Test
    void runJob()
        throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> param = Map.of("name", new JobParameter("batch"));
        jobLauncher.run(job, new JobParameters(param));
    }

    @Test
    void missParameter() {
        assertThatThrownBy(() -> jobLauncher.run(job, new JobParameters()))
            .isInstanceOf(JobParametersInvalidException.class);
    }
}
