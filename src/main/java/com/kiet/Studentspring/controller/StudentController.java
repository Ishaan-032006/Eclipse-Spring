package com.kiet.Studentspring.controller;

import com.kiet.Studentspring.entity.Student;
import com.kiet.Studentspring.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // 1. Add a student
    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addStudent(student));
    }

    // 2. Retrieve all students
    @GetMapping
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    // 3. Search for a student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable int id) {
        return service.getStudentById(id);
    }

    // Search by ID, email or course:  /students/search?email=...  /students/search?course=...
    @GetMapping("/search")
    public List<Student> search(@RequestParam(required = false) Integer id,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String course) {
        return service.search(id, email, course);
    }

    // 4. Update student scores and skills
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable int id, @RequestBody Student student) {
        return service.updateScoresAndSkills(id, student);
    }

    // 5. Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        service.deleteStudent(id);
        return ResponseEntity.ok("Student deleted successfully.");
    }

    // Average, category and stronger subject of each student
    @GetMapping("/performance")
    public List<Map<String, Object>> getPerformanceReport() {
        return service.getPerformanceReport();
    }

    // 6. Display the merit list
    @GetMapping("/merit-list")
    public List<Map<String, Object>> getMeritList() {
        return service.getMeritList();
    }

    // Students who have the same technical skills
    @GetMapping("/same-skills")
    public Map<String, List<String>> getSameSkills() {
        return service.getStudentsWithSameSkills();
    }

    // Weak in programming but maths >= 75
    @GetMapping("/needs-improvement")
    public List<Student> getNeedsImprovement() {
        return service.getNeedsImprovementInProgramming();
    }
}