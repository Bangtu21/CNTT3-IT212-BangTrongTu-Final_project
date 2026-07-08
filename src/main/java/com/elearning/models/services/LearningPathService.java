package com.elearning.models.services;

import com.elearning.exceptions.BusinessException;
import com.elearning.exceptions.PrerequisiteNotCompletedException;
import com.elearning.models.dto.AddCourseToPathRequest;
import com.elearning.models.entities.Course;
import com.elearning.models.entities.LearningPath;
import com.elearning.models.entities.LearningPathCourse;
import com.elearning.models.entities.User;
import com.elearning.models.entities.UserCourseProgress;
import com.elearning.models.repositories.CourseRepository;
import com.elearning.models.repositories.LearningPathCourseRepository;
import com.elearning.models.repositories.LearningPathRepository;
import com.elearning.models.repositories.UserRepository;
import com.elearning.models.repositories.UserCourseProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final LearningPathCourseRepository learningPathCourseRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void addCourseToLearningPath(Long learningPathId, AddCourseToPathRequest request) {
        LearningPath learningPath = learningPathRepository.findById(learningPathId)
                .orElseThrow(() -> new BusinessException(404, "Learning Path not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(404, "Course not found"));

        if (learningPathCourseRepository.existsByLearningPathIdAndStepOrder(learningPathId, request.getStepOrder())) {
            throw new BusinessException(400, "Step order already exists in this learning path");
        }

        if (learningPathCourseRepository.existsByLearningPathIdAndCourseId(learningPathId, request.getCourseId())) {
            throw new BusinessException(400, "Course already belongs to this learning path");
        }

        LearningPathCourse learningPathCourse = new LearningPathCourse();
        learningPathCourse.setLearningPath(learningPath);
        learningPathCourse.setCourse(course);
        learningPathCourse.setStepOrder(request.getStepOrder());

        learningPathCourseRepository.save(learningPathCourse);
    }

    @Transactional
    public void completeCourse(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(404, "User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(404, "Course not found"));

        Optional<UserCourseProgress> progressOpt = userCourseProgressRepository.findByUserIdAndCourseId(user.getId(), courseId);

        UserCourseProgress progress;
        if (progressOpt.isPresent()) {
            progress = progressOpt.get();
        } else {
            progress = new UserCourseProgress();
            progress.setUser(user);
            progress.setCourse(course);
        }

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        userCourseProgressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public void accessCourse(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(404, "User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(404, "Course not found"));

        List<LearningPathCourse> lpcList = learningPathCourseRepository.findByCourseId(courseId);
        if (lpcList.isEmpty()) {
            // If the course does not belong to any learning path, allow access
            return;
        }

        boolean canAccess = false;
        String prerequisiteErrorMessage = "You must complete the prerequisite courses first.";

        for (LearningPathCourse lpc : lpcList) {
            if (lpc.getStepOrder() == 1) {
                // If it is Step 1 in this learning path, allow access
                canAccess = true;
                break;
            } else {
                int prevStep = lpc.getStepOrder() - 1;
                Optional<LearningPathCourse> prevLpcOpt = learningPathCourseRepository
                        .findByLearningPathIdAndStepOrder(lpc.getLearningPath().getId(), prevStep);

                if (prevLpcOpt.isPresent()) {
                    Course prevCourse = prevLpcOpt.get().getCourse();
                    Optional<UserCourseProgress> progressOpt = userCourseProgressRepository
                            .findByUserIdAndCourseId(user.getId(), prevCourse.getId());

                    if (progressOpt.isPresent() && progressOpt.get().isCompleted()) {
                        canAccess = true;
                        break;
                    } else {
                        prerequisiteErrorMessage = "You must complete the course '" + prevCourse.getTitle()
                                + "' in learning path '" + lpc.getLearningPath().getName()
                                + "' before accessing this course.";
                    }
                } else {
                    // Prerequisite step doesn't exist in DB, treat as accessible
                    canAccess = true;
                    break;
                }
            }
        }

        if (!canAccess) {
            throw new PrerequisiteNotCompletedException(prerequisiteErrorMessage);
        }
    }
}
