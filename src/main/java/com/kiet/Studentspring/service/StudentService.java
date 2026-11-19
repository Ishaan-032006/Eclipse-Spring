package com.kiet.Studentspring.service;

import com.kiet.Studentspring.entity.Student;
import com.kiet.Studentspring.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // ---------- Validation (Requirement 8: scores 0 to 100) ----------
    private void validateScores(Double programming, Double maths) {
        if (programming == null || maths == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Programming and mathematics scores are required.");
        if (programming < 0 || programming > 100 || maths < 0 || maths > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Scores must be between 0 and 100.");
    }

    // ---------- Add student (Requirement 8: prevent duplicate IDs) ----------
    public Student addStudent(Student s) {
        if (s.getId() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID must be positive.");
        if (s.getName() == null || s.getName().isBlank() || s.getEmail() == null || s.getEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and email are required.");

        validateScores(s.getProgrammingScore(), s.getMathematicsScore());

        if (repo.existsById(s.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Student ID already exists: " + s.getId());
        if (repo.existsByEmailIgnoreCase(s.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already exists: " + s.getEmail());

        return repo.save(s);
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public Student getStudentById(int id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));
    }

    // ---------- Requirement 4: search by ID, email or course ----------
    public List<Student> search(Integer id, String email, String course) {
        if (id != null) {
            return repo.findById(id).map(List::of).orElse(Collections.emptyList());
        }
        if (email != null && !email.isBlank()) {
            return repo.findByEmailIgnoreCase(email).map(List::of).orElse(Collections.emptyList());
        }
        if (course != null && !course.isBlank()) {
            return repo.findByCourseNameIgnoreCase(course);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Provide at least one of: id, email, course.");
    }

    // ---------- Update scores and skills ----------
    public Student updateScoresAndSkills(int id, Student updated) {
        Student s = getStudentById(id);
        validateScores(updated.getProgrammingScore(), updated.getMathematicsScore());
        s.setProgrammingScore(updated.getProgrammingScore());
        s.setMathematicsScore(updated.getMathematicsScore());
        if (updated.getTechnicalSkills() != null && !updated.getTechnicalSkills().isEmpty()) {
            s.setTechnicalSkills(updated.getTechnicalSkills());
        }
        return repo.save(s);
    }

    public void deleteStudent(int id) {
        Student s = getStudentById(id);      // 404 agar nahi mila
        repo.delete(s);
    }

    // ---------- Requirements 1, 2, 3: average, category, stronger subject for each student ----------
    public List<Map<String, Object>> getPerformanceReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        for (Student s : repo.findAll()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", s.getId());
            row.put("name", s.getName());
            row.put("programmingScore", s.getProgrammingScore());
            row.put("mathematicsScore", s.getMathematicsScore());
            row.put("averageScore", s.getAverageScore());
            row.put("performanceCategory", s.getPerformanceCategory());
            row.put("strongerSubject", s.getStrongerSubject());
            report.add(row);
        }
        return report;
    }

    // ---------- Requirement 5: merit list (average desc) ----------
    public List<Map<String, Object>> getMeritList() {
        List<Student> sorted = repo.findAll().stream()
                .sorted(Comparator.comparingDouble(Student::getAverageScore).reversed()
                        .thenComparing(Student::getName))
                .collect(Collectors.toList());

        List<Map<String, Object>> merit = new ArrayList<>();
        int rank = 1;
        for (Student s : sorted) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("rank", rank++);
            row.put("id", s.getId());
            row.put("name", s.getName());
            row.put("averageScore", s.getAverageScore());
            row.put("performanceCategory", s.getPerformanceCategory());
            merit.add(row);
        }
        return merit;
    }

    // ---------- Requirement 6: students having the same technical skill ----------
    // Map: skill -> students' names. Sirf wahi skills jo 2 ya zyada students ke paas hain
    public Map<String, List<String>> getStudentsWithSameSkills() {
        Map<String, List<String>> skillMap = new TreeMap<>();

        for (Student s : repo.findAll()) {
            Set<String> uniqueSkills = new HashSet<>();          // ek student ki duplicate skill hatane ke liye
            for (String skill : s.getTechnicalSkills()) {
                uniqueSkills.add(skill.trim().toLowerCase());
            }
            for (String skill : uniqueSkills) {
                skillMap.computeIfAbsent(skill, k -> new ArrayList<>()).add(s.getName());
            }
        }
        skillMap.entrySet().removeIf(e -> e.getValue().size() < 2);
        return skillMap;
    }

    // ---------- Requirement 7: programming weak (< 50) but maths >= 75 ----------
    public List<Student> getNeedsImprovementInProgramming() {
        return repo.findAll().stream()
                .filter(s -> s.getProgrammingScore() < 50 && s.getMathematicsScore() >= 75)
                .collect(Collectors.toList());
    }
}