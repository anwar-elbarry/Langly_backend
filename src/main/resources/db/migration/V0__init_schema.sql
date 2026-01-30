CREATE TABLE attendance
(
    id         VARCHAR(255) NOT NULL,
    marked_at  VARCHAR(255),
    status     SMALLINT,
    student_id VARCHAR(255),
    session_id VARCHAR(255),
    CONSTRAINT pk_attendance PRIMARY KEY (id)
);

CREATE TABLE billing_history
(
    id             VARCHAR(255) NOT NULL,
    price          DECIMAL,
    status         SMALLINT,
    payment_method SMALLINT,
    paid_at        TIMESTAMP WITHOUT TIME ZONE,
    billing_id     VARCHAR(255),
    CONSTRAINT pk_billing_history PRIMARY KEY (id)
);

CREATE TABLE billings
(
    id             VARCHAR(255) NOT NULL,
    price          DECIMAL,
    status         SMALLINT,
    next_bill_date date,
    student_id     VARCHAR(255),
    CONSTRAINT pk_billings PRIMARY KEY (id)
);

CREATE TABLE billings_histories
(
    billing_id   VARCHAR(255) NOT NULL,
    histories_id VARCHAR(255) NOT NULL
);

CREATE TABLE certifications
(
    id         VARCHAR(255) NOT NULL,
    level      SMALLINT,
    language   VARCHAR(255),
    student_id VARCHAR(255),
    CONSTRAINT pk_certifications PRIMARY KEY (id)
);

CREATE TABLE course
(
    id                  VARCHAR(255) NOT NULL,
    name                VARCHAR(255),
    code                VARCHAR(255),
    language            VARCHAR(255),
    required_level      SMALLINT,
    target_level        SMALLINT,
    start_date          date,
    end_date            date,
    is_active           BOOLEAN,
    price               DECIMAL,
    capacity            INTEGER,
    session_per_week    INTEGER,
    minutes_per_session INTEGER,
    teacher_id          VARCHAR(255),
    CONSTRAINT pk_course PRIMARY KEY (id)
);

CREATE TABLE enrollment
(
    id                 VARCHAR(255) NOT NULL,
    status             SMALLINT,
    enrolled_at        date,
    left_at            date,
    certificate_issued BOOLEAN,
    course_id          VARCHAR(255),
    student_id         VARCHAR(255),
    CONSTRAINT pk_enrollment PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE schools
(
    id         VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    sub_domain VARCHAR(255),
    logo       VARCHAR(255),
    city       VARCHAR(255),
    country    VARCHAR(255),
    address    VARCHAR(255),
    status     SMALLINT,
    CONSTRAINT pk_schools PRIMARY KEY (id)
);

CREATE TABLE schools_subscriptions
(
    school_id        VARCHAR(255) NOT NULL,
    subscriptions_id VARCHAR(255) NOT NULL
);

CREATE TABLE sessions
(
    id               VARCHAR(255) NOT NULL,
    title            VARCHAR(255),
    description      VARCHAR(255),
    duration_minutes VARCHAR(255),
    schedualed_at    VARCHAR(255),
    mode             SMALLINT,
    course_id        VARCHAR(255),
    CONSTRAINT pk_sessions PRIMARY KEY (id)
);

CREATE TABLE students
(
    id         VARCHAR(255) NOT NULL,
    birth_date date,
    cnie       VARCHAR(255),
    level      SMALLINT,
    user_id    VARCHAR(255),
    CONSTRAINT pk_students PRIMARY KEY (id)
);

CREATE TABLE subscription_history
(
    id                  VARCHAR(255) NOT NULL,
    amount              DECIMAL,
    status_at_that_time SMALLINT,
    payment_method      SMALLINT,
    paid_at             TIMESTAMP WITHOUT TIME ZONE,
    subscription_id     VARCHAR(255),
    CONSTRAINT pk_subscription_history PRIMARY KEY (id)
);

CREATE TABLE subscription_history_histories
(
    subscription_history_id VARCHAR(255) NOT NULL,
    histories_id            VARCHAR(255) NOT NULL
);

CREATE TABLE subscriptions
(
    id                   VARCHAR(255) NOT NULL,
    amount               DECIMAL,
    currency             VARCHAR(255),
    billing_cycle        SMALLINT,
    current_period_start date,
    current_period_end   date,
    status               SMALLINT,
    next_payment_date    date,
    school_id            VARCHAR(255),
    CONSTRAINT pk_subscriptions PRIMARY KEY (id)
);

CREATE TABLE users
(
    id           VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255),
    phone_number VARCHAR(255) NOT NULL,
    profile      VARCHAR(255),
    status       SMALLINT,
    role_id      VARCHAR(255),
    school_id    VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE billings_histories
    ADD CONSTRAINT uc_billings_histories_histories UNIQUE (histories_id);

ALTER TABLE course
    ADD CONSTRAINT uc_course_code UNIQUE (code);

ALTER TABLE schools_subscriptions
    ADD CONSTRAINT uc_schools_subscriptions_subscriptions UNIQUE (subscriptions_id);

ALTER TABLE subscription_history_histories
    ADD CONSTRAINT uc_subscription_history_histories_histories UNIQUE (histories_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phonenumber UNIQUE (phone_number);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_SESSION FOREIGN KEY (session_id) REFERENCES sessions (id);

ALTER TABLE attendance
    ADD CONSTRAINT FK_ATTENDANCE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE billings
    ADD CONSTRAINT FK_BILLINGS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE billing_history
    ADD CONSTRAINT FK_BILLING_HISTORY_ON_BILLING FOREIGN KEY (billing_id) REFERENCES billings (id);

ALTER TABLE certifications
    ADD CONSTRAINT FK_CERTIFICATIONS_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE course
    ADD CONSTRAINT FK_COURSE_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES users (id);

ALTER TABLE enrollment
    ADD CONSTRAINT FK_ENROLLMENT_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE enrollment
    ADD CONSTRAINT FK_ENROLLMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES students (id);

ALTER TABLE sessions
    ADD CONSTRAINT FK_SESSIONS_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE students
    ADD CONSTRAINT FK_STUDENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE subscriptions
    ADD CONSTRAINT FK_SUBSCRIPTIONS_ON_SCHOOL FOREIGN KEY (school_id) REFERENCES schools (id);

ALTER TABLE subscription_history
    ADD CONSTRAINT FK_SUBSCRIPTION_HISTORY_ON_SUBSCRIPTION FOREIGN KEY (subscription_id) REFERENCES subscriptions (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_SCHOOL FOREIGN KEY (school_id) REFERENCES schools (id);

ALTER TABLE billings_histories
    ADD CONSTRAINT fk_bilhis_on_billing FOREIGN KEY (billing_id) REFERENCES billings (id);

ALTER TABLE billings_histories
    ADD CONSTRAINT fk_bilhis_on_billing_history FOREIGN KEY (histories_id) REFERENCES billing_history (id);

ALTER TABLE schools_subscriptions
    ADD CONSTRAINT fk_schsub_on_school FOREIGN KEY (school_id) REFERENCES schools (id);

ALTER TABLE schools_subscriptions
    ADD CONSTRAINT fk_schsub_on_subscription FOREIGN KEY (subscriptions_id) REFERENCES subscriptions (id);

ALTER TABLE subscription_history_histories
    ADD CONSTRAINT fk_subhishis_on_histories FOREIGN KEY (histories_id) REFERENCES subscription_history (id);

ALTER TABLE subscription_history_histories
    ADD CONSTRAINT fk_subhishis_on_subscriptionhistory FOREIGN KEY (subscription_history_id) REFERENCES subscription_history (id);