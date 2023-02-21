package experiment.spring.config.batch;

import java.util.Objects;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class JobParameterValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String name = Objects.requireNonNull(parameters).getString("name");

        if (!StringUtils.hasText(name)) {
            throw new JobParametersInvalidException("name is missing");
        }
    }
}
