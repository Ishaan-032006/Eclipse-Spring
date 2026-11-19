package com.kiet.Studentspring.repository;

import com.kiet.Studentspring.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Student> findByEmailIgnoreCase(String email);

    List<Student> findByCourseNameIgnoreCase(String courseName);

    boolean existsByEmailIgnoreCase(String email);
}