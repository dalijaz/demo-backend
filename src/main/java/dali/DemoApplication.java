package dali;

import dali.model.Certificate;
import dali.model.User;
import dali.repository.CertificateRepository;
import dali.repository.UserRepository;
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
    CommandLineRunner run(CertificateRepository certificateRepo, UserRepository userRepo) {
        return args -> {
            if (certificateRepo.count() == 0) {
                // Attach certificates to the first registered user (must be verified)
                User user = userRepo.findAll().stream()
                        .filter(User::isEnabled)
                        .findFirst()
                        .orElse(null);

                if (user != null) {
                    Certificate c1 = new Certificate("Java", "Java Programming Certificate");
                    c1.setUser(user);

                    Certificate c2 = new Certificate("Python", "Python Programming Certificate");
                    c2.setUser(user);

                    Certificate c3 = new Certificate("DevOps", "DevOps Tools & Practices Certificate");
                    c3.setUser(user);

                    certificateRepo.save(c1);
                    certificateRepo.save(c2);
                    certificateRepo.save(c3);
                } else {
                    System.out.println("⚠ No enabled user found — certificates not added.");
                }
            }
        };
    }
}
