# Fake Data Seed Reference

> Controlled by `app.seed-fake-data` (env: `SEED_FAKE_DATA`, default: `true`).  
> When enabled, **all existing data is wiped and re-seeded on every startup**.  
> Set to `false` in production.

---

## Login Credentials

| Role | Email | Password | School |
|------|-------|----------|--------|
| **Super Admin** | `admin@langly.com` | `Admin@1234` | — |
| School Admin (A) | `youssef.admin@langly-academy.com` | `Password@123` | Langly Academy |
| School Admin (B) | `fatima.admin@atlas-lang.com` | `Password@123` | Atlas Language Center |
| Teacher (A1) | `ahmed.teacher@langly-academy.com` | `Password@123` | Langly Academy |
| Teacher (A2) | `sara.teacher@langly-academy.com` | `Password@123` | Langly Academy *(SUSPENDED)* |
| Teacher (B1) | `karim.teacher@atlas-lang.com` | `Password@123` | Atlas Language Center |
| Teacher (B2) | `nadia.teacher@atlas-lang.com` | `Password@123` | Atlas Language Center *(SUSPENDED)* |

### Student Accounts

**Langly Academy (School A)**

| Name | Email | Status | Level | Gender | Profile Complete |
|------|-------|--------|-------|--------|-----------------|
| Amine Hajji | `amine.student@langly-academy.com` | ACTIVE | A0 | MALE | Yes |
| Lina Berrada | `lina.student@langly-academy.com` | ACTIVE | A1 | FEMALE | Yes |
| Omar Kettani | `omar.student@langly-academy.com` | ACTIVE | A2 | MALE | Yes |
| Hiba Fassi | `hiba.student@langly-academy.com` | ACTIVE | B1 | FEMALE | Yes |
| Mehdi Alaoui | `mehdi.student@langly-academy.com` | ACTIVE | B2 | MALE | Yes |
| Salma Ziani | `salma.student@langly-academy.com` | ACTIVE | C1 | FEMALE | Yes |
| Yassine Bouazza | `yassine.student@langly-academy.com` | **SUSPENDED** | A1 | MALE | **No** (missing birthDate & CNIE) |
| Imane Lahlou | `imane.student@langly-academy.com` | ACTIVE | B1 | FEMALE | **No** (missing birthDate & CNIE) |

**Atlas Language Center (School B)**

| Name | Email | Status | Level | Gender | Profile Complete |
|------|-------|--------|-------|--------|-----------------|
| Rachid Saidi | `rachid.student@atlas-lang.com` | ACTIVE | A0 | MALE | Yes |
| Zineb Benhima | `zineb.student@atlas-lang.com` | ACTIVE | A1 | FEMALE | Yes |
| Hamza Tahiri | `hamza.student@atlas-lang.com` | ACTIVE | A2 | MALE | Yes |
| Kenza Mouline | `kenza.student@atlas-lang.com` | ACTIVE | B1 | FEMALE | Yes |
| Adil Benjelloun | `adil.student@atlas-lang.com` | ACTIVE | B2 | MALE | Yes |
| Meryem Kadiri | `meryem.student@atlas-lang.com` | ACTIVE | C1 | FEMALE | Yes |
| Reda Filali | `reda.student@atlas-lang.com` | **SUSPENDED** | A1 | MALE | **No** |
| Houda Lamrani | `houda.student@atlas-lang.com` | ACTIVE | B1 | FEMALE | **No** |

> All student passwords: `Password@123`

---

## Schools

| School | City | Country | Status |
|--------|------|---------|--------|
| Langly Academy | Casablanca | Morocco | ACTIVE |
| Atlas Language Center | Rabat | Morocco | ACTIVE |

---

## Courses

### Langly Academy

| Code | Name | Language | Level | Dates | Active | Teacher | Price (MAD) | Capacity |
|------|------|----------|-------|-------|--------|---------|-------------|----------|
| ENG-A1A2-S1 | English Basics | English | A1→A2 | -2mo → +4mo | Yes | Ahmed Tazi | 1,200 | 20 |
| FRA-B1B2-S1 | French Intermediate | French | B1→B2 | -1mo → +5mo | Yes | Ahmed Tazi | 1,500 | 15 |
| SPA-A0A1-S1 | Spanish Beginners | Spanish | A0→A1 | +2wk → +6mo | Yes | Sara Idrissi | 800 | 25 |
| ARA-B2C1-S1 | Arabic Advanced | Arabic | B2→C1 | -8mo → -1mo | **No** | Sara Idrissi | 2,000 | 10 |

### Atlas Language Center

| Code | Name | Language | Level | Dates | Active | Teacher | Price (MAD) | Capacity |
|------|------|----------|-------|-------|--------|---------|-------------|----------|
| ENG-B1B2-S2 | English Conversation | English | B1→B2 | -3mo → +3mo | Yes | Karim Ouazzani | 1,400 | 18 |
| FRA-B2C1-S2 | French for Business | French | B2→C1 | -3wk → +5mo | Yes | Karim Ouazzani | 1,800 | 12 |
| GER-A0A1-S2 | German Basics | German | A0→A1 | +1mo → +7mo | Yes | Nadia Chraibi | 900 | 20 |
| ITA-A2B1-S2 | Italian Intermediate | Italian | A2→B1 | -10mo → -2mo | **No** | Nadia Chraibi | 1,600 | 10 |

---

## Sessions

| Course Type | Past Sessions | Upcoming Sessions | Modes |
|-------------|---------------|-------------------|-------|
| Active ongoing | 3 | 3 | Rotating (IN_PERSON / ONLINE / HYBRID) |
| Upcoming (future start) | 0 | 3 | Rotating |
| Ended (inactive) | 4 | 0 | Rotating |

**Total: ~44 sessions** across all 8 courses.

---

## Enrollments

### School A

| Student | Course | Status | Notes |
|---------|--------|--------|-------|
| Amine Hajji | English Basics | IN_PROGRESS | |
| Lina Berrada | English Basics | IN_PROGRESS | |
| Omar Kettani | English Basics | IN_PROGRESS | |
| Hiba Fassi | English Basics | IN_PROGRESS | |
| Mehdi Alaoui | English Basics | **PASSED** | certificateIssued=true |
| Salma Ziani | English Basics | **FAILED** | |
| Amine Hajji | French Intermediate | IN_PROGRESS | Multi-course student |
| Omar Kettani | French Intermediate | IN_PROGRESS | Multi-course student |
| Mehdi Alaoui | French Intermediate | IN_PROGRESS | Multi-course student |
| Yassine Bouazza | French Intermediate | **WITHDRAWN** | leftAt set |
| Salma Ziani | Spanish Beginners | **PENDING_APPROVAL** | |
| Imane Lahlou | Spanish Beginners | **REJECTED** | |
| Lina Berrada | Arabic Advanced | **PASSED** | certificateIssued=true |
| Hiba Fassi | Arabic Advanced | **TRANSFERRED** | leftAt set |

### School B

| Student | Course | Status | Notes |
|---------|--------|--------|-------|
| Rachid Saidi | English Conversation | IN_PROGRESS | |
| Zineb Benhima | English Conversation | IN_PROGRESS | |
| Hamza Tahiri | English Conversation | IN_PROGRESS | |
| Kenza Mouline | English Conversation | **PASSED** | certificateIssued=true |
| Adil Benjelloun | English Conversation | **FAILED** | |
| Rachid Saidi | French for Business | IN_PROGRESS | Multi-course |
| Hamza Tahiri | French for Business | IN_PROGRESS | Multi-course |
| Meryem Kadiri | French for Business | **WITHDRAWN** | leftAt set |
| Adil Benjelloun | German Basics | **PENDING_APPROVAL** | |
| Houda Lamrani | German Basics | **APPROVED** | |
| Zineb Benhima | Italian Intermediate | **PASSED** | certificateIssued=true |
| Meryem Kadiri | Italian Intermediate | **TRANSFERRED** | leftAt set |

---

## Attendance

Generated for all past sessions with enrolled (IN_PROGRESS/PASSED/FAILED) students.

**Distribution:** ~60% PRESENT, ~15% ABSENT, ~15% LATE, ~10% EXCUSED  
**Total:** ~120 attendance records

---

## Billings

### School A (10 billings)

| Student | Course | Amount | Status | Method |
|---------|--------|--------|--------|--------|
| Amine | English | 1,200 | PAID | CASH |
| Lina | English | 1,200 | PAID | BANK_TRANSFER |
| Omar | English | 1,200 | PENDING | — |
| Hiba | English | 1,200 | **OVERDUE** | — |
| Mehdi | English | 1,200 | PAID | CASH |
| Salma | English | 1,200 | CANCELLED | — |
| Amine | French | 1,500 | PENDING | — |
| Omar | French | 1,500 | PAID | BANK_TRANSFER |
| Mehdi | French | 1,500 | **PENDING_TRANSFER** | BANK_TRANSFER |
| Yassine | French | 1,500 | CANCELLED | — |

### School B (8 billings)

| Student | Course | Amount | Status | Method |
|---------|--------|--------|--------|--------|
| Rachid | English Conv. | 1,400 | PAID | CASH |
| Zineb | English Conv. | 1,400 | PENDING | — |
| Hamza | English Conv. | 1,400 | **OVERDUE** | — |
| Kenza | English Conv. | 1,400 | PAID | BANK_TRANSFER |
| Adil | English Conv. | 1,400 | CANCELLED | — |
| Rachid | French Biz | 1,800 | PENDING | — |
| Hamza | French Biz | 1,800 | PAID | CASH |
| Meryem | French Biz | 1,800 | CANCELLED | — |

**Billing History:** 2 entries per PAID billing (PENDING → PAID progression).

---

## Subscriptions

| School | Cycle | Amount | Status | Period |
|--------|-------|--------|--------|--------|
| Langly Academy | YEARLY | 12,000 MAD | PAID | -6mo → +6mo |
| Atlas Language Center | MONTHLY | 1,200 MAD | **PENDING** | -25d → +5d (expiring soon) |

**Subscription History:** 3 entries for School A, 2 for School B.

---

## Finance

### Billing Settings

| School | TVA | Due Days | Plan | Block on Unpaid | Discounts |
|--------|-----|----------|------|-----------------|-----------|
| Langly Academy | 20% | 30 | FULL | Yes | Enabled |
| Atlas Language Center | 0% | 15 | THREE_PARTS | No | Enabled |

### Fee Templates (per school)

| Name | Amount | Recurring | Active |
|------|--------|-----------|--------|
| Registration Fee | 400–500 | No | Yes |
| Monthly Tuition | 700–800 | Yes | Yes |

### Discounts

| School | Name | Type | Value | Active |
|--------|------|------|-------|--------|
| School A | Early Bird | PERCENTAGE | 10% | Yes |
| School A | Sibling Discount | FIXED_AMOUNT | 200 MAD | Yes |
| School B | Early Bird | PERCENTAGE | 15% | Yes |
| School B | Loyalty Discount | FIXED_AMOUNT | 150 MAD | **No** |

### Invoices (9 total)

| Number | Student | School | Total | Status | Installments |
|--------|---------|--------|-------|--------|--------------|
| INV-2026-001 | Amine | A | 1,200 | PAID | — |
| INV-2026-002 | Lina | A | 1,200 | PAID | — |
| INV-2026-003 | Omar | A | 1,200 | **PARTIALLY_PAID** | 3 (1 PAID, 1 OVERDUE, 1 PENDING) |
| INV-2026-004 | Hiba | A | 1,200 | UNPAID | — |
| INV-2026-005 | Amine | A | 1,500 | UNPAID | 2 (both PENDING) |
| INV-2026-101 | Rachid | B | 1,400 | PAID | — |
| INV-2026-102 | Zineb | B | 1,400 | UNPAID | 3 (all PENDING) |
| INV-2026-103 | Hamza | B | 1,400 | **PARTIALLY_PAID** | 2 (1 PAID, 1 OVERDUE) |
| INV-2026-104 | Rachid | B | 1,800 | UNPAID | — |

### Fee Payments (7 total)

Mix of closed and open payments across both schools.

---

## Certifications (4 total)

| Student | Language | Level | School |
|---------|----------|-------|--------|
| Mehdi Alaoui | English | A2 | Langly Academy |
| Lina Berrada | Arabic | C1 | Langly Academy |
| Kenza Mouline | English | B2 | Atlas Language Center |
| Zineb Benhima | Italian | B1 | Atlas Language Center |

---

## Course Materials (12 total)

Each active course has:
- 1 PDF (Grammar Guide)
- 1 VIDEO (Lesson Video)

---

## Notifications (15 total)

Covers all notification types:

| Type | Count |
|------|-------|
| ENROLLMENT_REQUEST | 2 |
| ENROLLMENT_APPROVED | 1 |
| ENROLLMENT_REJECTED | 1 |
| PAYMENT_RECEIVED | 2 |
| INSTALLMENT_OVERDUE | 1 |
| INSTALLMENT_REMINDER | 1 |
| INVOICE_CREATED | 1 |
| CERTIFICATE_ISSUED | 2 |
| COURSE_COMPLETED | 1 |
| SUBSCRIPTION_EXPIRED | 1 |
| SUBSCRIPTION_TRANSFER_DECLARED | 1 |
| SUBSCRIPTION_ACTIVATED | 1 |

Mix of READ and UNREAD statuses, distributed across admins, students, and super admin.

---

## Bank Info

| Field | Value |
|-------|-------|
| Bank | Attijariwafa Bank |
| Account Holder | Langly SARL |
| IBAN | MA64 0000 0000 0000 0000 0000 000 |
| Motive | Subscription Payment |

---

## Edge Cases Covered

- **Incomplete student profiles** — 4 students missing birthDate/CNIE
- **Suspended users** — 2 teachers + 2 students
- **Overdue billings** — 2 (one per school)
- **Pending transfer** — 1 billing awaiting bank transfer confirmation
- **Cancelled billings** — 4 (for withdrawn/failed enrollments)
- **Partially paid invoices** — 2 with mixed installment statuses (PAID + OVERDUE + PENDING)
- **Upcoming courses** — 2 courses with future start dates, no past sessions
- **Ended courses** — 2 inactive courses with only past sessions
- **Multi-course students** — Several students enrolled in 2+ courses
- **All enrollment statuses** — IN_PROGRESS, PASSED, FAILED, WITHDRAWN, TRANSFERRED, PENDING_APPROVAL, APPROVED, REJECTED
- **All attendance statuses** — PRESENT, ABSENT, LATE, EXCUSED
- **Expiring subscription** — School B subscription expires in 5 days
- **Inactive discount** — 1 discount marked as inactive
