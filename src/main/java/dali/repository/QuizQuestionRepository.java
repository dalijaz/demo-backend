package dali.repository;

import dali.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCertificateIdOrderByIdAsc(Long certificateId);
    List<QuizQuestion> findByCertificateId(Long certificateId); // back-compat
}
