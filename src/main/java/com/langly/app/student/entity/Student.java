package com.langly.app.student.entity;

import com.langly.app.finance.entity.Billing;
import com.langly.app.user.entity.User;
import com.langly.app.student.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.langly.app.course.entity.enums.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDate birthDate;
    private String CNIE;
    @Enumerated(EnumType.STRING)
    private Level level;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL)
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "student",fetch = FetchType.LAZY)
    private List<Billing> billings = new ArrayList<>();

}
