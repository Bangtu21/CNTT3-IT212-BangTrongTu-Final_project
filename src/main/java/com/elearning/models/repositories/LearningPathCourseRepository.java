package com.elearning.models.repositories;

import com.elearning.models.entities.LearningPathCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathCourseRepository extends JpaRepository<LearningPathCourse, Long> {
    List<LearningPathCourse> findByCourseId(Long courseId);
    Optional<LearningPathCourse> findByLearningPathIdAndStepOrder(Long learningPathId, Integer stepOrder);
    boolean existsByLearningPathIdAndStepOrder(Long learningPathId, Integer stepOrder);
    boolean existsByLearningPathIdAndCourseId(Long learningPathId, Long courseId);
}
