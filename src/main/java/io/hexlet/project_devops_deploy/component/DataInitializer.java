package io.hexlet.project_devops_deploy.component;

import io.hexlet.project_devops_deploy.repository.BulletinRepository;
import io.hexlet.project_devops_deploy.util.ModelGenerator;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final int BULLETIN_SEED_COUNT = 10;

    private final BulletinRepository repository;
    private final ModelGenerator modelGenerator;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }

        IntStream.range(0, BULLETIN_SEED_COUNT).mapToObj(i -> modelGenerator.generateBulletin())
                .forEach(repository::save);
    }
}
