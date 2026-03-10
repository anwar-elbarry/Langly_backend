package com.langly.app.student.repository;

import com.langly.app.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByUserId(String userId);

    List<Student> findAllByUserSchoolId(String schoolId);

    /**
     * US-AD-05 : retourne les étudiants dont le profil est incomplet
     * (CNIE, birthDate sur Student ; phoneNumber sur User).
     */
    @Query("""
            SELECT s FROM Student s
            WHERE s.CNIE IS NULL
               OR s.birthDate IS NULL
               OR s.user.phoneNumber IS NULL
            """)
    List<Student> findIncomplete();

    /**
     * Variante filtrée par école.
     */
    @Query("""
            SELECT s FROM Student s
            WHERE s.user.school.id = :schoolId
              AND (s.CNIE IS NULL
               OR s.birthDate IS NULL
               OR s.user.phoneNumber IS NULL)
            """)
    List<Student> findIncompleteBySchoolId(String schoolId);
}
