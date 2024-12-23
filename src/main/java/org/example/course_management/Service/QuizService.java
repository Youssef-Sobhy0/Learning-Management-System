package org.example.course_management.Service;

import org.example.course_management.Model.Quiz;
import org.example.course_management.Model.Course;
import org.example.course_management.Model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private CourseService courseService;

    public int searchForQuizInCourse(Course course, Quiz Quiz) {
        for (int i = 0; i < course.getAllQuizzes().size(); i++) {
            if (course.getAllQuizzes().get(i).getTitle().equals(Quiz.getTitle())) {
                return i;
            }
        }
        return -1;  // Return -1 if no match is found
    }

    // Create an Quiz
    public Quiz createQuiz(Long courseId, Quiz Quiz) {
        Course course = courseService.GetCourse(courseId);
        if (course != null) {
            int index = searchForQuizInCourse(course, Quiz);
            if (index != -1) {
                return null;
            }
            Quiz.setId(course.QuizCounter.incrementAndGet());
            course.addQuiz(Quiz);
            courseService.UpdateCourse(course);
            return Quiz;
        }
        return null;
    }

    public Quiz getQuiz(Long courseId, Long QuizId) {
        Course course = courseService.GetCourse(courseId);
        if (course != null) {
            for (Quiz Quiz : course.getAllQuizzes()) {
                if (Quiz.getId().equals(QuizId)) {
                    return Quiz;
                }
            }
        }
        return null;
    }

    // List all Quizs for a course
    public ArrayList<Quiz> getQuizsForCourse(Long courseId) {
        Course course = courseService.GetCourse(courseId);
        if (course != null) {
            return course.getAllQuizzes();
        }
        return new ArrayList<>();
    }
    // submit quiz
    public boolean submitQuiz(Long QuizId, Long Courseid, Student student) {
        Course course = courseService.GetCourse(Courseid);
        if (SearchForStudentInCourse(course.getId(), student.getId()) == -1) {
            return false;
        }

        for (Quiz quiz : course.getAllQuizzes()) {
            if (quiz.getId().equals(QuizId)) {
                if (!quiz.isSubmitted() || !quiz.getSubmittedStudents().contains(student)) {
                    quiz.setSubmitted(true);
                    quiz.addSubmittedStudent(student);
                    return true;
                }
            }
        }
        return false;
    }
    public List<Student> getQuizSubmitters( Long CourseID,  Long QuizID) {
        Course course = courseService.GetCourse(CourseID);
        if (course != null) {
            for (Quiz quiz : course.getAllQuizzes()) {
                if (quiz.getId().equals(QuizID)) {
                    return quiz.getSubmittedStudents();
                }
            }
        }
        return new ArrayList<>();
    }

    public int SearchForStudentInCourse(Long CourseID , Long StudentID) {
        Course course = courseService.GetCourse(CourseID);
        if (course != null) {
            for (int i = 0 ; i < course.GetAllStudents().size() ; i ++) {
                if (course.GetAllStudents().get(i).getId().equals(StudentID)) return i ;
            }
        }
        return -1;
    }
    public boolean gradeQuiz(Long QuizId, Long CourseId) {
        Course course = courseService.GetCourse(CourseId);

        if (course != null) {
            for (Quiz quiz : course.getAllQuizzes()) {
                if (quiz.getId().equals(QuizId) && !quiz.isGraded()) {
                    for (Student student : quiz.getSubmittedStudents()) {
                        provideAutomaticFeedback(student, quiz);
                    }
                    quiz.setGraded(true);
                    return true;
                }
            }
        }
        return false;
    }

    public String getQuizFeedback(Long QuizId, Long Courseid) {
        Course course = courseService.GetCourse(Courseid);
        for (Quiz Quiz : course.getAllQuizzes()) {
            if (Quiz.getId().equals(QuizId) && Quiz.isGraded()) {
                return Quiz.getFeedback();
            }
        }
        return "No feedback available.";
    }
    private void provideAutomaticFeedback(Student student, Quiz quiz) {
        String feedback = "Good job, " + student.getName() + "! Your quiz has been graded.";
        quiz.setFeedback(feedback);
        System.out.println("Feedback for " + student.getName() + ": " + feedback);
    }


}