package dali.repository;

import dali.model.QuizSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    // list a user's attempts (descending)
    List<QuizSubmission> findByUserEmailIgnoreCaseOrderBySubmittedAtDesc(String email);

    // admin: paginate by certificate
    Page<QuizSubmission> findByCertificateIdOrderBySubmittedAtDesc(Long certificateId, Pageable pageable);

    // detail view with answers + question fetched to avoid N+1
    @EntityGraph(attributePaths = {"user", "certificate", "answers", "answers.question"})
    @Query("select s from QuizSubmission s where s.id = :id")
    Optional<QuizSubmission> findWithAnswersById(@Param("id") Long id);
}
