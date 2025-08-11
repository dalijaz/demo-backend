package dali;

import dali.model.*;
import dali.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = "dali")
@EntityScan(basePackages = "dali.model")
@EnableJpaRepositories(basePackages = "dali.repository")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    // Only created if you don't already define one in SecurityConfig
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ⛔️ Skip in tests
    @Bean
    @Profile("!test")
    CommandLineRunner seedAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            userRepo.findByEmail("admin@site.com").ifPresentOrElse(user -> {
                if (user.getRole() != Role.ROLE_ADMIN) {
                    user.setRole(Role.ROLE_ADMIN);
                    user.setEnabled(true);
                    userRepo.save(user);
                }
            }, () -> {
                User admin = new User();
                admin.setEmail("admin@site.com");
                admin.setPassword(encoder.encode("ChangeMe123!"));
                admin.setEnabled(true);
                admin.setRole(Role.ROLE_ADMIN);
                userRepo.save(admin);
            });

            userRepo.findAll().forEach(u -> {
                if (!u.getEmail().equalsIgnoreCase("admin@site.com") && u.getRole() == Role.ROLE_ADMIN) {
                    u.setRole(Role.ROLE_USER);
                    userRepo.save(u);
                }
            });
        };
    }

    // ⛔️ Skip in tests
    @Bean
    @Profile("!test")
    CommandLineRunner seedData(CertificateRepository certificateRepo,
                               QuizQuestionRepository quizRepo) {
        return args -> {
            if (certificateRepo.count() == 0) {
                Certificate c1 = new Certificate("Java", "Java Programming Certificate");
                Certificate c2 = new Certificate("Python", "Python Programming Certificate");
                Certificate c3 = new Certificate("DevOps", "DevOps Tools & Practices Certificate");
                certificateRepo.save(c1); certificateRepo.save(c2); certificateRepo.save(c3);
            }
            if (quizRepo.count() == 0) {
                Certificate java = certificateRepo.findAll().stream().filter(c -> "Java".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
                Certificate python = certificateRepo.findAll().stream().filter(c -> "Python".equalsIgnoreCase(c.getName())).findFirst().orElse(null);
                if (java != null) {
                    quizRepo.save(new QuizQuestion("What does JVM stand for?",
                            "Java Verified Module", "Java Virtual Machine", "Java Version Manager", "Just Virtual Machine", 1, java));
                    quizRepo.save(new QuizQuestion("What keyword is used to inherit a class in Java?",
                            "implements", "inherits", "extends", "super", 2, java));
                }
                if (python != null) {
                    quizRepo.save(new QuizQuestion("Who created Python?",
                            "Linus Torvalds", "Guido van Rossum", "James Gosling", "Dennis Ritchie", 1, python));
                    quizRepo.save(new QuizQuestion("What is the built-in package manager for Python?",
                            "npm", "pip", "gradle", "maven", 1, python));
                }
            }
        };
    }
}
