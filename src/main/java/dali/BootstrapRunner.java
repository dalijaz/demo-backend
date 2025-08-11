package dali;

import dali.model.*;
import dali.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test") // don't run during tests
public class BootstrapRunner {

    @Bean
    CommandLineRunner seed(UserRepository users,
                           CertificateRepository certs,
                           QuizQuestionRepository qq,
                           PasswordEncoder encoder) {
        return args -> {
            // ✅ Ensure a single admin exists
            users.findByEmailIgnoreCase("admin@site.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("admin@site.com");
                u.setPassword(encoder.encode("admin123"));
                u.setEnabled(true);
                u.setRole(Role.ROLE_ADMIN);
                return users.save(u);
            });

            // ✅ Ensure a DevOps certificate exists
            Certificate devops = certs.findAll().stream()
                    .filter(c -> "DevOps".equalsIgnoreCase(c.getName()))
                    .findFirst()
                    .orElseGet(() -> certs.save(new Certificate("DevOps", "DevOps Tools & Practices")));

            // ✅ Seed questions only if none exist for this certificate
            if (qq.findByCertificateIdOrderByIdAsc(devops.getId()).isEmpty()) {
                qq.save(new QuizQuestion(
                        "Which CI/CD tool is widely used?",
                        "CircleCI", "GitLab CI", "Jenkins", "TeamCity",
                        2, // correct = Jenkins (0=A,1=B,2=C,3=D)
                        devops
                ));
                qq.save(new QuizQuestion(
                        "What does IaC stand for?",
                        "Infrastructure as Code", "Internet as Cloud", "Integration at Core", "Infra as Container",
                        0, // correct = Infrastructure as Code
                        devops
                ));
            }
        };
    }
}
