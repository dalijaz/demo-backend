package dali;

import dali.model.Certificate;
import dali.repository.CertificateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner run(CertificateRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Certificate("Java", "Java Programming Certificate"));
                repository.save(new Certificate("Python", "Python Programming Certificate"));
                repository.save(new Certificate("DevOps", "DevOps Tools & Practices Certificate"));
            }
        };
    }

   
}
