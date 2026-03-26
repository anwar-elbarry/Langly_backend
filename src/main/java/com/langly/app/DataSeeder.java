package com.langly.app;

import com.langly.app.Authority.entity.Role;
import com.langly.app.course.entity.*;
import com.langly.app.course.entity.enums.*;
import com.langly.app.course.repository.*;
import com.langly.app.finance.entity.*;
import com.langly.app.finance.entity.enums.*;
import com.langly.app.finance.repository.*;
import com.langly.app.notification.entity.Notification;
import com.langly.app.notification.entity.enums.NotificationStatus;
import com.langly.app.notification.entity.enums.NotificationType;
import com.langly.app.notification.repository.NotificationRepository;
import com.langly.app.school.entity.School;
import com.langly.app.school.repository.SchoolRepository;
import com.langly.app.student.entity.Certification;
import com.langly.app.student.entity.Student;
import com.langly.app.student.entity.enums.Gender;
import com.langly.app.student.repository.CertificationRepository;
import com.langly.app.student.repository.StudentRepository;
import com.langly.app.user.entity.User;
import com.langly.app.user.enums.UserRole;
import com.langly.app.user.enums.UserStatus;
import com.langly.app.user.repository.RoleRepository;
import com.langly.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final CertificationRepository certificationRepository;
    private final BillingRepository billingRepository;
    private final BillingHistoryRepository billingHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final BillingSettingRepository billingSettingRepository;
    private final DiscountRepository discountRepository;
    private final FeeTemplateRepository feeTemplateRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final PaymentScheduleRepository paymentScheduleRepository;
    private final NotificationRepository notificationRepository;
    private final BankInfoRepository bankInfoRepository;

    @Value("${app.seed-fake-data:false}")
    private boolean seedFakeData;

    @Override
    public void run(String... args) {
        seedRoles();
        seedSuperAdmin();

        if (seedFakeData) {
            log.info(">>> app.seed-fake-data=true — seeding comprehensive fake data...");
            seedAllFakeData();
            log.info(">>> Fake data seeding complete!");
        }
    }

    // ── Role & Super-Admin (always runs, idempotent) ──────────────────

    private void seedRoles() {
        Arrays.stream(UserRole.values()).forEach(userRole -> {
            if (roleRepository.findByName(userRole.name()).isEmpty()) {
                Role role = new Role();
                role.setName(userRole.name());
                roleRepository.save(role);
            }
        });
    }

    private void seedSuperAdmin() {
        String email = "admin@langly.com";
        if (userRepository.findByEmail(email).isPresent()) return;

        Role superAdminRole = roleRepository.findByName(UserRole.SUPER_ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found after seeding"));

        User superAdmin = new User();
        superAdmin.setFirstName("Super");
        superAdmin.setLastName("Admin");
        superAdmin.setEmail(email);
        superAdmin.setPassword(passwordEncoder.encode("Admin@1234"));
        superAdmin.setPhoneNumber("+212600000000");
        superAdmin.setStatus(UserStatus.ACTIVE);
        superAdmin.setRole(superAdminRole);
        userRepository.save(superAdmin);
    }

    // ── Full fake-data pipeline ───────────────────────────────────────

    @Transactional
    private void seedAllFakeData() {
        wipeAllData();

        // Fetch roles
        Role studentRole   = roleRepository.findByName("STUDENT").orElseThrow();
        Role teacherRole   = roleRepository.findByName("TEACHER").orElseThrow();
        Role adminRole     = roleRepository.findByName("SCHOOL_ADMIN").orElseThrow();
        User superAdmin    = userRepository.findByEmail("admin@langly.com").orElseThrow();

        String encodedPwd = passwordEncoder.encode("Password@123");

        // ── Schools ────────────────────────────────────────────────
        School schoolA = createSchool("Langly Academy",
                "https://ui-avatars.com/api/?name=LA&background=4f46e5&color=fff",
                "123 Boulevard Mohammed V, Casablanca 20250", "Casablanca", "Morocco", SchoolStatus.ACTIVE);
        School schoolB = createSchool("Atlas Language Center",
                "https://ui-avatars.com/api/?name=ALC&background=059669&color=fff",
                "45 Avenue Hassan II, Rabat 10000", "Rabat", "Morocco", SchoolStatus.ACTIVE);

        // ── School Admins ──────────────────────────────────────────
        User adminA = createUser("Youssef", "El Amrani", "youssef.admin@langly-academy.com",
                encodedPwd, "+212611000001", UserStatus.ACTIVE, adminRole, schoolA);
        User adminB = createUser("Fatima", "Bennani", "fatima.admin@atlas-lang.com",
                encodedPwd, "+212611000002", UserStatus.ACTIVE, adminRole, schoolB);

        // ── Teachers ───────────────────────────────────────────────
        User teacherA1 = createUser("Ahmed",  "Tazi",    "ahmed.teacher@langly-academy.com",
                encodedPwd, "+212622000001", UserStatus.ACTIVE, teacherRole, schoolA);
        User teacherA2 = createUser("Sara",   "Idrissi", "sara.teacher@langly-academy.com",
                encodedPwd, "+212622000002", UserStatus.SUSPENDED, teacherRole, schoolA);
        User teacherB1 = createUser("Karim",  "Ouazzani","karim.teacher@atlas-lang.com",
                encodedPwd, "+212622000003", UserStatus.ACTIVE, teacherRole, schoolB);
        User teacherB2 = createUser("Nadia",  "Chraibi", "nadia.teacher@atlas-lang.com",
                encodedPwd, "+212622000004", UserStatus.SUSPENDED, teacherRole, schoolB);

        // ── Students (School A: 8, School B: 8) ───────────────────
        String[][] studentsDataA = {
                {"Amine",   "Hajji",    "amine.student@langly-academy.com",   "+212633000001", "ACTIVE"},
                {"Lina",    "Berrada",  "lina.student@langly-academy.com",    "+212633000002", "ACTIVE"},
                {"Omar",    "Kettani",  "omar.student@langly-academy.com",    "+212633000003", "ACTIVE"},
                {"Hiba",    "Fassi",    "hiba.student@langly-academy.com",    "+212633000004", "ACTIVE"},
                {"Mehdi",   "Alaoui",   "mehdi.student@langly-academy.com",   "+212633000005", "ACTIVE"},
                {"Salma",   "Ziani",    "salma.student@langly-academy.com",   "+212633000006", "ACTIVE"},
                {"Yassine", "Bouazza",  "yassine.student@langly-academy.com", "+212633000007", "SUSPENDED"},
                {"Imane",   "Lahlou",   "imane.student@langly-academy.com",   "+212633000008", "ACTIVE"},
        };
        String[][] studentsDataB = {
                {"Rachid",  "Saidi",    "rachid.student@atlas-lang.com",  "+212633000011", "ACTIVE"},
                {"Zineb",   "Benhima",  "zineb.student@atlas-lang.com",   "+212633000012", "ACTIVE"},
                {"Hamza",   "Tahiri",   "hamza.student@atlas-lang.com",   "+212633000013", "ACTIVE"},
                {"Kenza",   "Mouline",  "kenza.student@atlas-lang.com",   "+212633000014", "ACTIVE"},
                {"Adil",    "Benjelloun","adil.student@atlas-lang.com",   "+212633000015", "ACTIVE"},
                {"Meryem",  "Kadiri",   "meryem.student@atlas-lang.com",  "+212633000016", "ACTIVE"},
                {"Reda",    "Filali",   "reda.student@atlas-lang.com",    "+212633000017", "SUSPENDED"},
                {"Houda",   "Lamrani",  "houda.student@atlas-lang.com",   "+212633000018", "ACTIVE"},
        };

        Level[]  levels  = {Level.A0, Level.A1, Level.A2, Level.B1, Level.B2, Level.C1, Level.A1, Level.B1};
        Gender[] genders = {Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE, Gender.MALE, Gender.FEMALE};

        List<Student> studentsA = new ArrayList<>();
        List<Student> studentsB = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            User uA = createUser(studentsDataA[i][0], studentsDataA[i][1], studentsDataA[i][2],
                    encodedPwd, studentsDataA[i][3],
                    "ACTIVE".equals(studentsDataA[i][4]) ? UserStatus.ACTIVE : UserStatus.SUSPENDED,
                    studentRole, schoolA);
            // Some students missing birthDate/CNIE to test "incomplete" endpoints
            LocalDate birthA = (i < 6) ? LocalDate.of(2000 + (i % 5), 1 + i, 10 + i) : null;
            String cnieA = (i < 6) ? "A" + (100000 + i) : null;
            studentsA.add(createStudent(uA, birthA, cnieA, levels[i], genders[i]));
        }
        for (int i = 0; i < 8; i++) {
            User uB = createUser(studentsDataB[i][0], studentsDataB[i][1], studentsDataB[i][2],
                    encodedPwd, studentsDataB[i][3],
                    "ACTIVE".equals(studentsDataB[i][4]) ? UserStatus.ACTIVE : UserStatus.SUSPENDED,
                    studentRole, schoolB);
            LocalDate birthB = (i < 6) ? LocalDate.of(1998 + (i % 6), 3 + i, 5 + i) : null;
            String cnieB = (i < 6) ? "B" + (200000 + i) : null;
            studentsB.add(createStudent(uB, birthB, cnieB, levels[i], genders[i]));
        }

        // ── Courses (4 per school) ─────────────────────────────────
        LocalDate now = LocalDate.now();

        Course cA1 = createCourse("English Basics", "ENG-A1A2-S1", "English", Level.A1, Level.A2,
                now.minusMonths(2), now.plusMonths(4), true, new BigDecimal("1200"), 20, 3, 90, teacherA1);
        Course cA2 = createCourse("French Intermediate", "FRA-B1B2-S1", "French", Level.B1, Level.B2,
                now.minusMonths(1), now.plusMonths(5), true, new BigDecimal("1500"), 15, 2, 120, teacherA1);
        Course cA3 = createCourse("Spanish Beginners", "SPA-A0A1-S1", "Spanish", Level.A0, Level.A1,
                now.plusWeeks(2), now.plusMonths(6), true, new BigDecimal("800"), 25, 2, 60, teacherA2);
        Course cA4 = createCourse("Arabic Advanced", "ARA-B2C1-S1", "Arabic", Level.B2, Level.C1,
                now.minusMonths(8), now.minusMonths(1), false, new BigDecimal("2000"), 10, 4, 90, teacherA2);

        Course cB1 = createCourse("English Conversation", "ENG-B1B2-S2", "English", Level.B1, Level.B2,
                now.minusMonths(3), now.plusMonths(3), true, new BigDecimal("1400"), 18, 3, 90, teacherB1);
        Course cB2 = createCourse("French for Business", "FRA-B2C1-S2", "French", Level.B2, Level.C1,
                now.minusWeeks(3), now.plusMonths(5), true, new BigDecimal("1800"), 12, 2, 120, teacherB1);
        Course cB3 = createCourse("German Basics", "GER-A0A1-S2", "German", Level.A0, Level.A1,
                now.plusMonths(1), now.plusMonths(7), true, new BigDecimal("900"), 20, 2, 60, teacherB2);
        Course cB4 = createCourse("Italian Intermediate", "ITA-A2B1-S2", "Italian", Level.A2, Level.B1,
                now.minusMonths(10), now.minusMonths(2), false, new BigDecimal("1600"), 10, 3, 90, teacherB2);

        // ── Sessions ───────────────────────────────────────────────
        // Active courses: 3 past + 3 upcoming sessions
        // Inactive courses: 4 past sessions
        List<Session> sessA1 = createSessionsForCourse(cA1, 3, 3, Mode.IN_PERSON);
        List<Session> sessA2 = createSessionsForCourse(cA2, 3, 3, Mode.ONLINE);
        List<Session> sessA3 = createSessionsForCourse(cA3, 0, 3, Mode.HYBRID);   // upcoming course, no past
        List<Session> sessA4 = createSessionsForCourse(cA4, 4, 0, Mode.IN_PERSON); // ended course

        List<Session> sessB1 = createSessionsForCourse(cB1, 3, 3, Mode.HYBRID);
        List<Session> sessB2 = createSessionsForCourse(cB2, 3, 3, Mode.ONLINE);
        List<Session> sessB3 = createSessionsForCourse(cB3, 0, 3, Mode.IN_PERSON);
        List<Session> sessB4 = createSessionsForCourse(cB4, 4, 0, Mode.ONLINE);

        // ── Enrollments (School A) ─────────────────────────────────
        // cA1 (English): 5 students IN_PROGRESS, 1 PASSED, 1 FAILED
        Enrollment eA1_0 = createEnrollment(studentsA.get(0), cA1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(2), null, false);
        Enrollment eA1_1 = createEnrollment(studentsA.get(1), cA1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(2), null, false);
        Enrollment eA1_2 = createEnrollment(studentsA.get(2), cA1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(2), null, false);
        Enrollment eA1_3 = createEnrollment(studentsA.get(3), cA1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(2), null, false);
        Enrollment eA1_4 = createEnrollment(studentsA.get(4), cA1, EnrollmentStatus.PASSED, now.minusMonths(2), null, true);
        Enrollment eA1_5 = createEnrollment(studentsA.get(5), cA1, EnrollmentStatus.FAILED, now.minusMonths(2), null, false);

        // cA2 (French): 3 students IN_PROGRESS, 1 WITHDRAWN
        Enrollment eA2_0 = createEnrollment(studentsA.get(0), cA2, EnrollmentStatus.IN_PROGRESS, now.minusMonths(1), null, false);
        Enrollment eA2_1 = createEnrollment(studentsA.get(2), cA2, EnrollmentStatus.IN_PROGRESS, now.minusMonths(1), null, false);
        Enrollment eA2_2 = createEnrollment(studentsA.get(4), cA2, EnrollmentStatus.IN_PROGRESS, now.minusMonths(1), null, false);
        Enrollment eA2_3 = createEnrollment(studentsA.get(6), cA2, EnrollmentStatus.WITHDRAWN, now.minusMonths(1), now.minusWeeks(1), false);

        // cA3 (Spanish): PENDING_APPROVAL + REJECTED (upcoming course)
        Enrollment eA3_0 = createEnrollment(studentsA.get(5), cA3, EnrollmentStatus.PENDING_APPROVAL, now.minusWeeks(1), null, false);
        Enrollment eA3_1 = createEnrollment(studentsA.get(7), cA3, EnrollmentStatus.REJECTED, now.minusWeeks(2), null, false);

        // cA4 (Arabic, ended): 1 PASSED, 1 TRANSFERRED
        Enrollment eA4_0 = createEnrollment(studentsA.get(1), cA4, EnrollmentStatus.PASSED, now.minusMonths(8), null, true);
        Enrollment eA4_1 = createEnrollment(studentsA.get(3), cA4, EnrollmentStatus.TRANSFERRED, now.minusMonths(8), now.minusMonths(3), false);

        // ── Enrollments (School B) ─────────────────────────────────
        Enrollment eB1_0 = createEnrollment(studentsB.get(0), cB1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(3), null, false);
        Enrollment eB1_1 = createEnrollment(studentsB.get(1), cB1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(3), null, false);
        Enrollment eB1_2 = createEnrollment(studentsB.get(2), cB1, EnrollmentStatus.IN_PROGRESS, now.minusMonths(3), null, false);
        Enrollment eB1_3 = createEnrollment(studentsB.get(3), cB1, EnrollmentStatus.PASSED, now.minusMonths(3), null, true);
        Enrollment eB1_4 = createEnrollment(studentsB.get(4), cB1, EnrollmentStatus.FAILED, now.minusMonths(3), null, false);

        Enrollment eB2_0 = createEnrollment(studentsB.get(0), cB2, EnrollmentStatus.IN_PROGRESS, now.minusWeeks(3), null, false);
        Enrollment eB2_1 = createEnrollment(studentsB.get(2), cB2, EnrollmentStatus.IN_PROGRESS, now.minusWeeks(3), null, false);
        Enrollment eB2_2 = createEnrollment(studentsB.get(5), cB2, EnrollmentStatus.WITHDRAWN, now.minusWeeks(3), now.minusWeeks(1), false);

        Enrollment eB3_0 = createEnrollment(studentsB.get(4), cB3, EnrollmentStatus.PENDING_APPROVAL, now.minusDays(5), null, false);
        Enrollment eB3_1 = createEnrollment(studentsB.get(7), cB3, EnrollmentStatus.APPROVED, now.minusDays(3), null, false);

        Enrollment eB4_0 = createEnrollment(studentsB.get(1), cB4, EnrollmentStatus.PASSED, now.minusMonths(10), null, true);
        Enrollment eB4_1 = createEnrollment(studentsB.get(5), cB4, EnrollmentStatus.TRANSFERRED, now.minusMonths(10), now.minusMonths(4), false);

        // ── Attendance (for past sessions of active + ended courses) ──
        List<Student> enrolledA1Active = List.of(studentsA.get(0), studentsA.get(1), studentsA.get(2), studentsA.get(3), studentsA.get(4), studentsA.get(5));
        List<Student> enrolledA2Active = List.of(studentsA.get(0), studentsA.get(2), studentsA.get(4));
        List<Student> enrolledA4Active = List.of(studentsA.get(1), studentsA.get(3));
        List<Student> enrolledB1Active = List.of(studentsB.get(0), studentsB.get(1), studentsB.get(2), studentsB.get(3), studentsB.get(4));
        List<Student> enrolledB2Active = List.of(studentsB.get(0), studentsB.get(2));
        List<Student> enrolledB4Active = List.of(studentsB.get(1), studentsB.get(5));

        seedAttendance(sessA1.subList(0, 3), enrolledA1Active);   // 3 past sessions × 6 students
        seedAttendance(sessA2.subList(0, 3), enrolledA2Active);   // 3 × 3
        seedAttendance(sessA4, enrolledA4Active);                  // 4 × 2
        seedAttendance(sessB1.subList(0, 3), enrolledB1Active);   // 3 × 5
        seedAttendance(sessB2.subList(0, 3), enrolledB2Active);   // 3 × 2
        seedAttendance(sessB4, enrolledB4Active);                  // 4 × 2

        // ── Billings ───────────────────────────────────────────────
        // School A billings
        createBilling(eA1_0, studentsA.get(0), new BigDecimal("1200"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusWeeks(6), now.minusWeeks(6).atStartOfDay());
        createBilling(eA1_1, studentsA.get(1), new BigDecimal("1200"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusWeeks(5), now.minusWeeks(5).atStartOfDay());
        createBilling(eA1_2, studentsA.get(2), new BigDecimal("1200"), PaymentStatus.PENDING, null, now.plusWeeks(2), null);
        createBilling(eA1_3, studentsA.get(3), new BigDecimal("1200"), PaymentStatus.OVERDUE, null, now.minusWeeks(1), null);
        createBilling(eA1_4, studentsA.get(4), new BigDecimal("1200"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusMonths(1), now.minusMonths(1).atStartOfDay());
        createBilling(eA1_5, studentsA.get(5), new BigDecimal("1200"), PaymentStatus.CANCELLED, null, null, null);
        createBilling(eA2_0, studentsA.get(0), new BigDecimal("1500"), PaymentStatus.PENDING, null, now.plusWeeks(3), null);
        createBilling(eA2_1, studentsA.get(2), new BigDecimal("1500"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusWeeks(2), now.minusWeeks(2).atStartOfDay());
        createBilling(eA2_2, studentsA.get(4), new BigDecimal("1500"), PaymentStatus.PENDING_TRANSFER, PaymentMethod.BANK_TRANSFER, now.plusWeeks(1), null);
        createBilling(eA2_3, studentsA.get(6), new BigDecimal("1500"), PaymentStatus.CANCELLED, null, null, null);

        // School B billings
        createBilling(eB1_0, studentsB.get(0), new BigDecimal("1400"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusMonths(2), now.minusMonths(2).atStartOfDay());
        createBilling(eB1_1, studentsB.get(1), new BigDecimal("1400"), PaymentStatus.PENDING, null, now.plusWeeks(2), null);
        createBilling(eB1_2, studentsB.get(2), new BigDecimal("1400"), PaymentStatus.OVERDUE, null, now.minusDays(10), null);
        createBilling(eB1_3, studentsB.get(3), new BigDecimal("1400"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusMonths(1), now.minusMonths(1).atStartOfDay());
        createBilling(eB1_4, studentsB.get(4), new BigDecimal("1400"), PaymentStatus.CANCELLED, null, null, null);
        createBilling(eB2_0, studentsB.get(0), new BigDecimal("1800"), PaymentStatus.PENDING, null, now.plusWeeks(4), null);
        createBilling(eB2_1, studentsB.get(2), new BigDecimal("1800"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusWeeks(1), now.minusWeeks(1).atStartOfDay());
        createBilling(eB2_2, studentsB.get(5), new BigDecimal("1800"), PaymentStatus.CANCELLED, null, null, null);

        // ── Billing History (for PAID billings) ────────────────────
        billingRepository.findAll().stream()
                .filter(b -> b.getStatus() == PaymentStatus.PAID)
                .forEach(b -> {
                    BillingHistory bh1 = new BillingHistory();
                    bh1.setPrice(b.getPrice());
                    bh1.setStatus(PaymentStatus.PENDING);
                    bh1.setPaymentMethod(null);
                    bh1.setPaidAt(b.getPaidAt().minusDays(7));
                    bh1.setBilling(b);
                    billingHistoryRepository.save(bh1);

                    BillingHistory bh2 = new BillingHistory();
                    bh2.setPrice(b.getPrice());
                    bh2.setStatus(PaymentStatus.PAID);
                    bh2.setPaymentMethod(b.getPaymentMethod());
                    bh2.setPaidAt(b.getPaidAt());
                    bh2.setBilling(b);
                    billingHistoryRepository.save(bh2);
                });

        // ── Subscriptions ──────────────────────────────────────────
        Subscription subA = createSubscription(schoolA, new BigDecimal("12000"), "MAD", BillingCycle.YEARLY,
                now.minusMonths(6), now.plusMonths(6), PaymentStatus.PAID, now.plusMonths(6));
        Subscription subB = createSubscription(schoolB, new BigDecimal("1200"), "MAD", BillingCycle.MONTHLY,
                now.minusDays(25), now.plusDays(5), PaymentStatus.PENDING, now.plusDays(5));

        // ── Subscription History ───────────────────────────────────
        createSubHistory(subA, new BigDecimal("12000"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusMonths(6).atStartOfDay());
        createSubHistory(subA, new BigDecimal("12000"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusMonths(18).atStartOfDay());
        createSubHistory(subA, new BigDecimal("10000"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusMonths(30).atStartOfDay());
        createSubHistory(subB, new BigDecimal("1200"), PaymentStatus.PAID, PaymentMethod.CASH, now.minusDays(25).atStartOfDay());
        createSubHistory(subB, new BigDecimal("1200"), PaymentStatus.PAID, PaymentMethod.BANK_TRANSFER, now.minusDays(55).atStartOfDay());

        // ── Billing Settings ───────────────────────────────────────
        createBillingSetting(schoolA, new BigDecimal("20"), 30, InstallmentPlan.FULL, true, true, "MAD");
        createBillingSetting(schoolB, new BigDecimal("0"),  15, InstallmentPlan.THREE_PARTS, false, true, "MAD");

        // ── Fee Templates ──────────────────────────────────────────
        FeeTemplate ftA1 = createFeeTemplate(schoolA, "Registration Fee", new BigDecimal("500"), false, true);
        FeeTemplate ftA2 = createFeeTemplate(schoolA, "Monthly Tuition", new BigDecimal("800"), true, true);
        FeeTemplate ftB1 = createFeeTemplate(schoolB, "Registration Fee", new BigDecimal("400"), false, true);
        FeeTemplate ftB2 = createFeeTemplate(schoolB, "Monthly Tuition", new BigDecimal("700"), true, true);

        // ── Discounts ──────────────────────────────────────────────
        createDiscount(schoolA, "Early Bird", DiscountType.PERCENTAGE, new BigDecimal("10"), true);
        createDiscount(schoolA, "Sibling Discount", DiscountType.FIXED_AMOUNT, new BigDecimal("200"), true);
        createDiscount(schoolB, "Early Bird", DiscountType.PERCENTAGE, new BigDecimal("15"), true);
        createDiscount(schoolB, "Loyalty Discount", DiscountType.FIXED_AMOUNT, new BigDecimal("150"), false);

        // ── Fee Payments ───────────────────────────────────────────
        createFeePayment(schoolA, studentsA.get(0), ftA1, new BigDecimal("500"), now.minusMonths(2), null, true);
        createFeePayment(schoolA, studentsA.get(0), ftA2, new BigDecimal("800"), now.minusMonths(1), "January tuition", true);
        createFeePayment(schoolA, studentsA.get(1), ftA1, new BigDecimal("500"), now.minusWeeks(6), null, true);
        createFeePayment(schoolA, studentsA.get(1), ftA2, new BigDecimal("800"), now.minusWeeks(2), "February tuition", false);
        createFeePayment(schoolB, studentsB.get(0), ftB1, new BigDecimal("400"), now.minusMonths(3), null, true);
        createFeePayment(schoolB, studentsB.get(0), ftB2, new BigDecimal("700"), now.minusMonths(2), "January tuition", true);
        createFeePayment(schoolB, studentsB.get(1), ftB1, new BigDecimal("400"), now.minusWeeks(4), null, false);

        // ── Invoices, Lines & Payment Schedules ────────────────────
        // School A invoices
        Invoice invA1 = createInvoice("INV-2026-001", studentsA.get(0), schoolA, eA1_0, new BigDecimal("1200"), InvoiceStatus.PAID, now.minusMonths(2).atStartOfDay(), now.minusMonths(1));
        createInvoiceLine(invA1, ftA1, "Registration Fee", new BigDecimal("500"));
        createInvoiceLine(invA1, ftA2, "Monthly Tuition - January", new BigDecimal("700"));

        Invoice invA2 = createInvoice("INV-2026-002", studentsA.get(1), schoolA, eA1_1, new BigDecimal("1200"), InvoiceStatus.PAID, now.minusMonths(2).atStartOfDay(), now.minusMonths(1));
        createInvoiceLine(invA2, ftA1, "Registration Fee", new BigDecimal("500"));
        createInvoiceLine(invA2, ftA2, "Monthly Tuition", new BigDecimal("700"));

        Invoice invA3 = createInvoice("INV-2026-003", studentsA.get(2), schoolA, eA1_2, new BigDecimal("1200"), InvoiceStatus.PARTIALLY_PAID, now.minusWeeks(6).atStartOfDay(), now.plusWeeks(2));
        createInvoiceLine(invA3, ftA1, "Registration Fee", new BigDecimal("500"));
        createInvoiceLine(invA3, ftA2, "Monthly Tuition", new BigDecimal("700"));
        createPaymentSchedule(invA3, 1, new BigDecimal("400"), now.minusWeeks(4), InstallmentStatus.PAID, now.minusWeeks(4).atStartOfDay());
        createPaymentSchedule(invA3, 2, new BigDecimal("400"), now.minusWeeks(1), InstallmentStatus.OVERDUE, null);
        createPaymentSchedule(invA3, 3, new BigDecimal("400"), now.plusWeeks(2), InstallmentStatus.PENDING, null);

        Invoice invA4 = createInvoice("INV-2026-004", studentsA.get(3), schoolA, eA1_3, new BigDecimal("1200"), InvoiceStatus.UNPAID, now.minusWeeks(3).atStartOfDay(), now.plusDays(5));
        createInvoiceLine(invA4, null, "Course Fee - English Basics", new BigDecimal("1200"));

        Invoice invA5 = createInvoice("INV-2026-005", studentsA.get(0), schoolA, eA2_0, new BigDecimal("1500"), InvoiceStatus.UNPAID, now.minusWeeks(2).atStartOfDay(), now.plusWeeks(3));
        createInvoiceLine(invA5, null, "Course Fee - French Intermediate", new BigDecimal("1500"));
        createPaymentSchedule(invA5, 1, new BigDecimal("750"), now.plusWeeks(1), InstallmentStatus.PENDING, null);
        createPaymentSchedule(invA5, 2, new BigDecimal("750"), now.plusWeeks(3), InstallmentStatus.PENDING, null);

        // School B invoices
        Invoice invB1 = createInvoice("INV-2026-101", studentsB.get(0), schoolB, eB1_0, new BigDecimal("1400"), InvoiceStatus.PAID, now.minusMonths(3).atStartOfDay(), now.minusMonths(2));
        createInvoiceLine(invB1, ftB1, "Registration Fee", new BigDecimal("400"));
        createInvoiceLine(invB1, ftB2, "Course Fee", new BigDecimal("1000"));

        Invoice invB2 = createInvoice("INV-2026-102", studentsB.get(1), schoolB, eB1_1, new BigDecimal("1400"), InvoiceStatus.UNPAID, now.minusWeeks(2).atStartOfDay(), now.plusWeeks(2));
        createInvoiceLine(invB2, null, "Course Fee - English Conversation", new BigDecimal("1400"));
        createPaymentSchedule(invB2, 1, new BigDecimal("470"), now.plusDays(3), InstallmentStatus.PENDING, null);
        createPaymentSchedule(invB2, 2, new BigDecimal("470"), now.plusWeeks(2), InstallmentStatus.PENDING, null);
        createPaymentSchedule(invB2, 3, new BigDecimal("460"), now.plusWeeks(4), InstallmentStatus.PENDING, null);

        Invoice invB3 = createInvoice("INV-2026-103", studentsB.get(2), schoolB, eB1_2, new BigDecimal("1400"), InvoiceStatus.PARTIALLY_PAID, now.minusMonths(2).atStartOfDay(), now.minusDays(10));
        createInvoiceLine(invB3, ftB1, "Registration Fee", new BigDecimal("400"));
        createInvoiceLine(invB3, null, "Course Fee", new BigDecimal("1000"));
        createPaymentSchedule(invB3, 1, new BigDecimal("700"), now.minusMonths(1), InstallmentStatus.PAID, now.minusMonths(1).atStartOfDay());
        createPaymentSchedule(invB3, 2, new BigDecimal("700"), now.minusDays(10), InstallmentStatus.OVERDUE, null);

        Invoice invB4 = createInvoice("INV-2026-104", studentsB.get(0), schoolB, eB2_0, new BigDecimal("1800"), InvoiceStatus.UNPAID, now.minusWeeks(1).atStartOfDay(), now.plusWeeks(4));
        createInvoiceLine(invB4, null, "Course Fee - French for Business", new BigDecimal("1800"));

        // ── Certifications ─────────────────────────────────────────
        createCertification(studentsA.get(4), cA1, schoolA, Level.A2, "English", now.minusWeeks(2).atStartOfDay());
        createCertification(studentsA.get(1), cA4, schoolA, Level.C1, "Arabic", now.minusMonths(1).atStartOfDay());
        createCertification(studentsB.get(3), cB1, schoolB, Level.B2, "English", now.minusWeeks(1).atStartOfDay());
        createCertification(studentsB.get(1), cB4, schoolB, Level.B1, "Italian", now.minusMonths(2).atStartOfDay());

        // ── Course Materials ───────────────────────────────────────
        for (Course c : List.of(cA1, cA2, cA3, cB1, cB2, cB3)) {
            createCourseMaterial(c, c.getLanguage() + " Grammar Guide", MaterialType.PDF,
                    "/uploads/materials/" + c.getCode() + "-grammar.pdf");
            createCourseMaterial(c, c.getLanguage() + " Lesson Video", MaterialType.VIDEO,
                    "/uploads/materials/" + c.getCode() + "-lesson.mp4");
        }

        // ── Notifications ──────────────────────────────────────────
        createNotification(adminA, "New Enrollment Request", "Salma Ziani has requested enrollment in Spanish Beginners",
                NotificationType.ENROLLMENT_REQUEST, NotificationStatus.UNREAD, eA3_0.getId(), "ENROLLMENT");
        createNotification(adminA, "Payment Received", "Amine Hajji paid 1200 MAD for English Basics",
                NotificationType.PAYMENT_RECEIVED, NotificationStatus.READ, eA1_0.getId(), "BILLING");
        createNotification(adminA, "Payment Overdue", "Hiba Fassi has an overdue payment of 1200 MAD",
                NotificationType.INSTALLMENT_OVERDUE, NotificationStatus.UNREAD, eA1_3.getId(), "BILLING");
        createNotification(studentsA.get(4).getUser(), "Certificate Issued", "Your English A2 certificate is ready",
                NotificationType.CERTIFICATE_ISSUED, NotificationStatus.UNREAD, null, "CERTIFICATION");
        createNotification(studentsA.get(0).getUser(), "Enrollment Approved", "You have been enrolled in English Basics",
                NotificationType.ENROLLMENT_APPROVED, NotificationStatus.READ, eA1_0.getId(), "ENROLLMENT");
        createNotification(studentsA.get(5).getUser(), "Course Completed", "Arabic Advanced course has ended",
                NotificationType.COURSE_COMPLETED, NotificationStatus.UNREAD, cA4.getId(), "COURSE");
        createNotification(studentsA.get(7).getUser(), "Enrollment Rejected", "Your enrollment in Spanish Beginners was rejected",
                NotificationType.ENROLLMENT_REJECTED, NotificationStatus.UNREAD, eA3_1.getId(), "ENROLLMENT");

        createNotification(adminB, "New Enrollment Request", "Adil Benjelloun has requested enrollment in German Basics",
                NotificationType.ENROLLMENT_REQUEST, NotificationStatus.UNREAD, eB3_0.getId(), "ENROLLMENT");
        createNotification(adminB, "Subscription Expiring", "Your monthly subscription expires in 5 days",
                NotificationType.SUBSCRIPTION_EXPIRED, NotificationStatus.UNREAD, subB.getId(), "SUBSCRIPTION");
        createNotification(adminB, "Payment Received", "Rachid Saidi paid 1400 MAD for English Conversation",
                NotificationType.PAYMENT_RECEIVED, NotificationStatus.READ, eB1_0.getId(), "BILLING");
        createNotification(studentsB.get(3).getUser(), "Certificate Issued", "Your English B2 certificate is ready",
                NotificationType.CERTIFICATE_ISSUED, NotificationStatus.UNREAD, null, "CERTIFICATION");
        createNotification(studentsB.get(0).getUser(), "Invoice Created", "A new invoice INV-2026-104 has been created",
                NotificationType.INVOICE_CREATED, NotificationStatus.UNREAD, invB4.getId(), "INVOICE");
        createNotification(studentsB.get(2).getUser(), "Installment Reminder", "Your installment of 700 MAD is due soon",
                NotificationType.INSTALLMENT_REMINDER, NotificationStatus.UNREAD, invB3.getId(), "INVOICE");
        createNotification(superAdmin, "Subscription Transfer", "Atlas Language Center declared a bank transfer for subscription",
                NotificationType.SUBSCRIPTION_TRANSFER_DECLARED, NotificationStatus.UNREAD, subB.getId(), "SUBSCRIPTION");
        createNotification(superAdmin, "Subscription Activated", "Langly Academy subscription has been activated",
                NotificationType.SUBSCRIPTION_ACTIVATED, NotificationStatus.READ, subA.getId(), "SUBSCRIPTION");

        // ── Bank Info ──────────────────────────────────────────────
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBankName("Attijariwafa Bank");
        bankInfo.setAccountHolder("Langly SARL");
        bankInfo.setIban("MA64 0000 0000 0000 0000 0000 000");
        bankInfo.setMotive("Subscription Payment");
        bankInfo.setNote("Please include your school name as reference");
        bankInfoRepository.save(bankInfo);
    }

    // ── Data wipe (respects FK constraints) ───────────────────────

    private void wipeAllData() {
        log.info("  Wiping all existing data...");
        // Order matters: children before parents
        paymentScheduleRepository.deleteAll();
        invoiceLineRepository.deleteAll();
        invoiceRepository.deleteAll();
        feePaymentRepository.deleteAll();
        notificationRepository.deleteAll();
        attendanceRepository.deleteAll();
        courseMaterialRepository.deleteAll();
        billingHistoryRepository.deleteAll();
        billingRepository.deleteAll();
        certificationRepository.deleteAll();
        enrollmentRepository.deleteAll();
        sessionRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        discountRepository.deleteAll();
        feeTemplateRepository.deleteAll();
        billingSettingRepository.deleteAll();
        subscriptionHistoryRepository.deleteAll();
        subscriptionRepository.deleteAll();
        bankInfoRepository.deleteAll();
        // Delete all users except those without a school (super admin)
        userRepository.findAll().stream()
                .filter(u -> u.getSchool() != null)
                .forEach(userRepository::delete);
        schoolRepository.deleteAll();
        log.info("  Data wipe complete.");
    }

    // ── Helper factory methods ────────────────────────────────────

    private School createSchool(String name, String logo, String address, String city, String country, SchoolStatus status) {
        School s = new School();
        s.setName(name);
        s.setLogo(logo);
        s.setAddress(address);
        s.setCity(city);
        s.setCountry(country);
        s.setStatus(status);
        return schoolRepository.save(s);
    }

    private User createUser(String first, String last, String email, String encodedPwd,
                            String phone, UserStatus status, Role role, School school) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        u.setEmail(email);
        u.setPassword(encodedPwd);
        u.setPhoneNumber(phone);
        u.setStatus(status);
        u.setRole(role);
        u.setSchool(school);
        return userRepository.save(u);
    }

    private Student createStudent(User user, LocalDate birth, String cnie, Level level, Gender gender) {
        Student s = new Student();
        s.setUser(user);
        s.setBirthDate(birth);
        s.setCNIE(cnie);
        s.setLevel(level);
        s.setGender(gender);
        return studentRepository.save(s);
    }

    private Course createCourse(String name, String code, String lang, Level req, Level target,
                                LocalDate start, LocalDate end, boolean active, BigDecimal price,
                                int capacity, int sessPerWeek, int minsPerSess, User teacher) {
        Course c = new Course();
        c.setName(name);
        c.setCode(code);
        c.setLanguage(lang);
        c.setRequiredLevel(req);
        c.setTargetLevel(target);
        c.setStartDate(start);
        c.setEndDate(end);
        c.setIsActive(active);
        c.setPrice(price);
        c.setCapacity(capacity);
        c.setSessionPerWeek(sessPerWeek);
        c.setMinutesPerSession(minsPerSess);
        c.setTeacher(teacher);
        return courseRepository.save(c);
    }

    private List<Session> createSessionsForCourse(Course course, int pastCount, int upcomingCount, Mode primaryMode) {
        List<Session> sessions = new ArrayList<>();
        Mode[] modes = Mode.values();

        for (int i = 0; i < pastCount; i++) {
            Session s = new Session();
            s.setTitle(course.getName() + " - Session " + (i + 1));
            s.setDescription("Session " + (i + 1) + " of " + course.getName());
            s.setDurationMinutes(course.getMinutesPerSession());
            s.setScheduledAt(LocalDateTime.now().minusWeeks(pastCount - i).plusHours(9));
            s.setMode(modes[(primaryMode.ordinal() + i) % modes.length]);
            s.setRoom(s.getMode() != Mode.ONLINE ? "Room " + ((i % 3) + 1) : null);
            s.setMeetingLink(s.getMode() != Mode.IN_PERSON ? "https://meet.langly.com/" + course.getCode() + "-" + (i + 1) : null);
            s.setCourse(course);
            sessions.add(sessionRepository.save(s));
        }
        for (int i = 0; i < upcomingCount; i++) {
            Session s = new Session();
            s.setTitle(course.getName() + " - Session " + (pastCount + i + 1));
            s.setDescription("Upcoming session " + (i + 1) + " of " + course.getName());
            s.setDurationMinutes(course.getMinutesPerSession());
            s.setScheduledAt(LocalDateTime.now().plusWeeks(i + 1).plusHours(10));
            s.setMode(modes[(primaryMode.ordinal() + i) % modes.length]);
            s.setRoom(s.getMode() != Mode.ONLINE ? "Room " + ((i % 3) + 1) : null);
            s.setMeetingLink(s.getMode() != Mode.IN_PERSON ? "https://meet.langly.com/" + course.getCode() + "-" + (pastCount + i + 1) : null);
            s.setCourse(course);
            sessions.add(sessionRepository.save(s));
        }
        return sessions;
    }

    private Enrollment createEnrollment(Student student, Course course, EnrollmentStatus status,
                                        LocalDate enrolledAt, LocalDate leftAt, boolean certIssued) {
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setStatus(status);
        e.setEnrolledAt(enrolledAt);
        e.setLeftAt(leftAt);
        e.setCertificateIssued(certIssued);
        return enrollmentRepository.save(e);
    }

    private void seedAttendance(List<Session> pastSessions, List<Student> enrolledStudents) {
        AttendanceStatus[] statuses = AttendanceStatus.values();
        // Weighted distribution: 60% PRESENT, 15% ABSENT, 15% LATE, 10% EXCUSED
        int[] weights = {60, 15, 15, 10};
        int idx = 0;
        for (Session session : pastSessions) {
            for (Student student : enrolledStudents) {
                Attendance a = new Attendance();
                a.setSession(session);
                a.setStudent(student);
                a.setMarkedAt(session.getScheduledAt().plusMinutes(5));
                // Deterministic distribution based on index
                int roll = (idx * 37 + 13) % 100;
                if (roll < weights[0]) a.setStatus(AttendanceStatus.PRESENT);
                else if (roll < weights[0] + weights[1]) a.setStatus(AttendanceStatus.ABSENT);
                else if (roll < weights[0] + weights[1] + weights[2]) a.setStatus(AttendanceStatus.LATE);
                else a.setStatus(AttendanceStatus.EXCUSED);
                attendanceRepository.save(a);
                idx++;
            }
        }
    }

    private void createBilling(Enrollment enrollment, Student student, BigDecimal price, PaymentStatus status,
                               PaymentMethod method, LocalDate nextBillDate, LocalDateTime paidAt) {
        Billing b = new Billing();
        b.setEnrollment(enrollment);
        b.setStudent(student);
        b.setPrice(price);
        b.setStatus(status);
        b.setPaymentMethod(method);
        b.setNextBillDate(nextBillDate);
        b.setPaidAt(paidAt);
        billingRepository.save(b);
    }

    private Subscription createSubscription(School school, BigDecimal amount, String currency, BillingCycle cycle,
                                            LocalDate periodStart, LocalDate periodEnd, PaymentStatus status, LocalDate nextPayment) {
        Subscription s = new Subscription();
        s.setSchool(school);
        s.setAmount(amount);
        s.setCurrency(currency);
        s.setBillingCycle(cycle);
        s.setCurrentPeriodStart(periodStart);
        s.setCurrentPeriodEnd(periodEnd);
        s.setStatus(status);
        s.setNextPaymentDate(nextPayment);
        return subscriptionRepository.save(s);
    }

    private void createSubHistory(Subscription sub, BigDecimal amount, PaymentStatus status,
                                  PaymentMethod method, LocalDateTime paidAt) {
        SubscriptionHistory h = new SubscriptionHistory();
        h.setSubscription(sub);
        h.setAmount(amount);
        h.setStatusAtThatTime(status);
        h.setPaymentMethod(method);
        h.setPaidAt(paidAt);
        subscriptionHistoryRepository.save(h);
    }

    private void createBillingSetting(School school, BigDecimal tva, int dueDays, InstallmentPlan plan,
                                      boolean blockUnpaid, boolean discountEnabled, String currency) {
        BillingSetting bs = new BillingSetting();
        bs.setSchool(school);
        bs.setTvaRate(tva);
        bs.setDueDateDays(dueDays);
        bs.setDefaultInstallmentPlan(plan);
        bs.setBlockOnUnpaid(blockUnpaid);
        bs.setDiscountEnabled(discountEnabled);
        bs.setCurrency(currency);
        billingSettingRepository.save(bs);
    }

    private FeeTemplate createFeeTemplate(School school, String name, BigDecimal amount, boolean recurring, boolean active) {
        FeeTemplate ft = new FeeTemplate();
        ft.setSchool(school);
        ft.setName(name);
        ft.setAmount(amount);
        ft.setIsRecurring(recurring);
        ft.setIsActive(active);
        return feeTemplateRepository.save(ft);
    }

    private void createDiscount(School school, String name, DiscountType type, BigDecimal value, boolean active) {
        Discount d = new Discount();
        d.setSchool(school);
        d.setName(name);
        d.setType(type);
        d.setValue(value);
        d.setIsActive(active);
        discountRepository.save(d);
    }

    private void createFeePayment(School school, Student student, FeeTemplate ft, BigDecimal amount,
                                  LocalDate paidAt, String note, boolean closed) {
        FeePayment fp = new FeePayment();
        fp.setSchool(school);
        fp.setStudent(student);
        fp.setFeeTemplate(ft);
        fp.setAmount(amount);
        fp.setPaidAt(paidAt);
        fp.setNote(note);
        fp.setIsClosed(closed);
        feePaymentRepository.save(fp);
    }

    private Invoice createInvoice(String number, Student student, School school, Enrollment enrollment,
                                  BigDecimal total, InvoiceStatus status, LocalDateTime issuedAt, LocalDate dueDate) {
        Invoice inv = new Invoice();
        inv.setInvoiceNumber(number);
        inv.setStudent(student);
        inv.setSchool(school);
        inv.setEnrollment(enrollment);
        inv.setTotal(total);
        inv.setStatus(status);
        inv.setIssuedAt(issuedAt);
        inv.setDueDate(dueDate);
        return invoiceRepository.save(inv);
    }

    private void createInvoiceLine(Invoice invoice, FeeTemplate ft, String desc, BigDecimal amount) {
        InvoiceLine line = new InvoiceLine();
        line.setInvoice(invoice);
        line.setFeeTemplate(ft);
        line.setDescription(desc);
        line.setAmount(amount);
        invoiceLineRepository.save(line);
    }

    private void createPaymentSchedule(Invoice invoice, int installment, BigDecimal amount,
                                       LocalDate dueDate, InstallmentStatus status, LocalDateTime paidAt) {
        PaymentSchedule ps = new PaymentSchedule();
        ps.setInvoice(invoice);
        ps.setInstallment(installment);
        ps.setAmount(amount);
        ps.setDueDate(dueDate);
        ps.setStatus(status);
        ps.setPaidAt(paidAt);
        paymentScheduleRepository.save(ps);
    }

    private void createCertification(Student student, Course course, School school,
                                     Level level, String language, LocalDateTime issuedAt) {
        Certification cert = new Certification();
        cert.setStudent(student);
        cert.setCourse(course);
        cert.setSchool(school);
        cert.setLevel(level);
        cert.setLanguage(language);
        cert.setIssuedAt(issuedAt);
        cert.setPdfUrl("/uploads/certificates/" + student.getUser().getFirstName().toLowerCase() + "-" + language.toLowerCase() + "-" + level + ".pdf");
        cert.setDigitalSignature("LANGLY-CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        certificationRepository.save(cert);
    }

    private void createCourseMaterial(Course course, String name, MaterialType type, String url) {
        CourseMaterial cm = new CourseMaterial();
        cm.setCourse(course);
        cm.setName(name);
        cm.setType(type);
        cm.setFileUrl(url);
        cm.setUploadedAt(LocalDateTime.now().minusDays(7));
        courseMaterialRepository.save(cm);
    }

    private void createNotification(User recipient, String title, String message,
                                    NotificationType type, NotificationStatus status,
                                    String refId, String refType) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setStatus(status);
        n.setCreatedAt(LocalDateTime.now().minusHours((long)(Math.random() * 168)));
        n.setReferenceId(refId);
        n.setReferenceType(refType);
        notificationRepository.save(n);
    }
}
