package com.kiet.Studentspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Computed fields (average, category, stronger subject) JSON me dikhenge,
// par client unhe bhej nahi sakta (allowGetters = true)
@JsonIgnoreProperties(value = {"averageScore", "performanceCategory", "strongerSubject"},
        allowGetters = true)
@Entity
@Table(name = "student")
public class Student {

    @Id                                   // ID user deta hai, auto-generate nahi (duplicate check ke liye)
    private int id;

    @Column(length = 100)
    private String name;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 50)
    private String courseName;

    private Double programmingScore;
    private Double mathematicsScore;

    // List of technical skills -> alag table student_skills me store hogi
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "student_skills", joinColumns = @JoinColumn(name = "student_id"))
    @Column(name = "skill", length = 50)
    private List<String> technicalSkills = new ArrayList<>();

    public Student() {}

    public Student(int id, String name, String email, String courseName,
                   Double programmingScore, Double mathematicsScore, List<String> technicalSkills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        setTechnicalSkills(technicalSkills);
    }

    // ---------- Getters and Setters (Encapsulation) ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(Double programmingScore) { this.programmingScore = programmingScore; }

    public Double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(Double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = (technicalSkills == null) ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // ---------- Object methods (business logic) ----------

    // 1. Average score
    public double getAverageScore() {
        double avg = (programmingScore + mathematicsScore) / 2.0;
        return Math.round(avg * 100.0) / 100.0;
    }

    // 2. Performance category
    public String getPerformanceCategory() {
        double avg = (programmingScore + mathematicsScore) / 2.0;
        if (avg >= 85) return "Excellent";
        else if (avg >= 70) return "Good";
        else if (avg >= 50) return "Satisfactory";
        else return "Needs Improvement";
    }

    // 3. Stronger subject
    public String getStrongerSubject() {
        if (programmingScore > mathematicsScore) return "Programming";
        else if (mathematicsScore > programmingScore) return "Mathematics";
        else return "Both are equal";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id == ((Student) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email + "', course='" + courseName
                + "', programming=" + programmingScore + ", maths=" + mathematicsScore
                + ", skills=" + technicalSkills + "}";
    }
}