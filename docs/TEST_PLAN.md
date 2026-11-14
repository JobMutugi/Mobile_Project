# Test Plan and Acceptance Checklist

## 1. Test environment

- Android Studio using JDK 17 or newer.
- Android emulator or physical device with Android API 26 or newer.
- First run: demo mode without Firebase configuration.
- Cloud run: Firebase Authentication Email/Password, Cloud Firestore, manager/profile setup, and supplied Firestore rules published.

## 2. Functional test cases

| ID | Test | Steps | Expected result |
|---|---|---|---|
| TC-01 | Launch in demo mode | Run app without `app/google-services.json`. | Login page shows employee demo and manager demo buttons. |
| TC-02 | Employee demo | Select employee demo. | Employee screen shows shift and clock action. |
| TC-03 | Clock in | Tap CLOCK IN as demo employee. | Open record appears; clock-out button is available. |
| TC-04 | Prevent duplicate clock-in | While clocked in, try to begin another shift. | App rejects a duplicate open record. |
| TC-05 | Clock out | Tap CLOCK OUT. | Record closes and appears in history with elapsed time. |
| TC-06 | Manager demo | Sign out and select manager demo. | Employee list, sample statuses, shift editors, and report selector appear. |
| TC-07 | Daily report | Select Today. | Only records with today's date key appear. |
| TC-08 | Monthly report | Select This month. | Records in the current month appear. |
| TC-09 | Change a shift | Edit a demo employee to `08:00` - `17:00`. | Shift updates on the manager card. Invalid/overnight times are rejected. |
| TC-10 | Employee sign-up | Configure Firebase, use valid email/password/company code. | Auth account and employee profile are created. |
| TC-11 | Invalid company code | Register using a non-existent company document ID. | Profile creation is rejected and the app displays a useful error. |
| TC-12 | Manager login | Create manager via Firebase Console and add matching profile. | Manager reaches manager dashboard. |
| TC-13 | Employee cloud attendance | Sign in as employee, clock in and out. | Firestore attendance document has server timestamps and expected fields. |
| TC-14 | Cross-device sync | Sign in to the same Firebase project on a second test device. | The manager can refresh and see the employee record. |
| TC-15 | Role protection | Try employee UI and Firestore writes to change role/company. | The UI has no manager sign-up; Firestore denies forbidden updates. |
| TC-16 | Cross-company privacy | Create a second test company/account and attempt to read the first company's records. | Firestore denies cross-company reads. |
| TC-17 | Error handling | Turn off network and attempt a cloud write. | The app shows an error and does not claim a successful cloud save. |

## 3. Calculation cases

1. Clock-in 08:55, shift start 09:00: not late.
2. Clock-in 09:01, shift start 09:00: late.
3. Clock-out 17:00, shift end 17:00: zero overtime.
4. Clock-out 17:45, shift end 17:00: 45 minutes overtime estimate.
5. Clock-out earlier than clock-in due to malformed data: displayed elapsed time must never be negative.
6. Missing clock-out: record remains open and worked duration is measured up to the current time.
7. No record today: manager sees Absent.

## 4. Security checks

- Test with Firestore Rules simulator before using real staff data.
- Ensure a user cannot create a profile with role `manager` through the employee registration path.
- Ensure an employee can read their own attendance but cannot read another employee's attendance.
- Ensure a manager can only read profiles and attendance under their own company ID.
- Ensure an employee cannot update shift fields or attendance identity fields.
- Ensure an employee cannot overwrite a closed attendance record or clock out another employee.
- Ensure user and attendance deletions are denied from the client.

## 5. Known limits for this test version

The local demo does not persist across app process restarts. The cloud version does not currently paginate reports; a manager query fetches company records and filters them on the device. Client-side lateness/overtime and submitted date keys are not authoritative payroll data. GPS, notifications, breaks, overnight shifts, and exporting are not implemented.
