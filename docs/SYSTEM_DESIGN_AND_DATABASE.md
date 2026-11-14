# System Design, Database Design, and Security Notes

## 1. Architecture

The starter application uses a small layered structure:

1. **Compose UI** (`EmployeeTimeClockApp.kt`) displays login, employee clock, manager dashboard, shift editor, and reports.
2. **Activity/state coordinator** (`MainActivity.kt`) handles actions, busy/error messages, and refreshes UI data.
3. **Repository** (`AttendanceRepository.kt`) contains sign-in, employee registration, attendance operations, shift updates, and demo mode.
4. **Models** (`Models.kt`) define `UserProfile`, `AttendanceRecord`, and the report period.
5. **Cloud services** Firebase Authentication stores login accounts. Cloud Firestore stores company, user, and attendance documents.
6. **Rules** (`firestore.rules`) authorize each client read or write.

When `app/google-services.json` is absent, the app deliberately uses demo mode. Demo data is held in memory and resets when the app process is restarted. When the config file is present and valid, the Google Services plugin is applied and the Firebase-backed path is used.

## 2. Roles

### Employee
- Sign in or register using a company join code.
- Clock in once for an open shift.
- Clock out their own open shift.
- View their own attendance history.
- Cannot self-assign the manager role or change their company ID.

### Manager
- Has a profile created manually by the project administrator.
- Views employee profiles in the same company.
- Views the company's attendance records.
- Sets employee shift times.
- Cannot change attendance timestamps in the starter UI.

## 3. Firestore data model

### `companies/{companyId}`
Example document ID: `ACME-Q7K2-4N9P`

| Field | Type | Meaning |
|---|---|---|
| `name` | string | Company display name. |
| `active` | boolean | Must be `true` for employee sign-up. |
| `managerUid` | string | UID of the first manager. |

The company document ID is also the company join code. Choose an unpredictable value and share it privately.

### `users/{uid}`
| Field | Type | Meaning |
|---|---|---|
| `uid` | string | Firebase Authentication UID; same as document ID. |
| `name` | string | Employee or manager display name. |
| `email` | string | Login email. |
| `role` | string | `employee` or `manager`. |
| `companyId` | string | Company document ID. |
| `shiftStart` | string | Start time in 24-hour `HH:mm` format. |
| `shiftEnd` | string | End time in 24-hour `HH:mm` format. |
| `createdAt` | timestamp | Account profile creation time. |
| `updatedAt` | timestamp | Optional profile/shift update timestamp. |

For an employee profile, the app writes `role = employee`, `shiftStart = 09:00`, and `shiftEnd = 17:00` during registration. A manager profile is entered manually by an administrator. Manager accounts must not be created through the employee sign-up screen.

### `attendance/{recordId}`
| Field | Type | Meaning |
|---|---|---|
| `uid` | string | Employee who owns the record. |
| `employeeName` | string | Name snapshot used for historical display. |
| `companyId` | string | Company scope. |
| `dateKey` | string | Local date in `YYYY-MM-DD` format. |
| `clockIn` | timestamp | Server timestamp created at clock-in. |
| `clockOut` | timestamp or null | Null while shift is open; server timestamp at clock-out. |
| `shiftStart` | string | Copy of the shift start at clock-in. |
| `shiftEnd` | string | Copy of the shift end at clock-in. |
| `createdAt` | timestamp | Server timestamp. |
| `updatedAt` | timestamp | Server timestamp on creation and clock-out. |

The shift values are copied into each attendance record so a manager changing tomorrow's shift does not rewrite the shift that applied to an earlier record.

## 4. Main workflows

### Employee clock-in
1. The employee signs in.
2. The app reads the employee profile and shift settings.
3. The repository checks for an open record.
4. If no open record exists, it creates an attendance record with a server timestamp for `clockIn` and null `clockOut`.
5. The UI refreshes and displays the open shift.

### Employee clock-out
1. The app finds the employee's open record.
2. It updates only `clockOut` and `updatedAt` using server timestamp values.
3. Firestore rules restrict the operation to the owner of a still-open record.
4. The UI refreshes and displays worked duration and overtime estimate.

### Manager changes a shift
1. The manager opens an employee card and selects Edit shift.
2. The start and end must use `HH:mm`, and end must be later than start on the same day.
3. The repository updates only the shift fields.
4. Firestore rules allow the update only to a manager of the employee's company.

### Manager report
1. The manager loads employees and company attendance records.
2. The UI shows current-day statuses.
3. The report selector filters for today or the current month.
4. Each row shows clock-in, clock-out, elapsed time, and overtime after the scheduled end.

## 5. Security rules overview

The supplied `firestore.rules` file is intended to be published in Firebase Console > Firestore Database > Rules after the initial company and manager profile are created.

- Employees can create their own profile only with role `employee` and only if the company record exists and is active.
- Employees cannot change their role or company ID after registration.
- Managers can update shift start/end for employees in the same company.
- Employees can read their own records. Managers can read records whose company ID matches the manager profile.
- Employees can create an attendance record only for their own UID and can close only their own still-open record.
- Attendance record identity fields cannot be changed when an employee clocks out.
- Deleting users, companies, or attendance records from the mobile client is denied.

Never replace the included rules with a broad rule that allows all signed-in users to read/write the entire database.

## 6. Calculation definitions

- **Worked duration:** clock-out minus clock-in. For an open record, current time minus clock-in is shown.
- **Late:** local clock-in time is later than the recorded shift start time.
- **Overtime estimate:** max(clock-out time minus scheduled end time, 0). While the shift is still open, the screen can show overtime after the scheduled end has passed.
- **Absent:** no attendance record exists for the employee's current local date.
- **Completed:** the attendance record is closed and the clock-in was not late.

This simple model does not subtract unpaid breaks and does not support overnight shifts.

## 7. Known engineering limitations

- The UI downloads all attendance documents for the company and filters daily/monthly reports on the device. Implement server-side date constraints, paging, and suitable composite indexes for a larger company.
- The device's local date is submitted as `dateKey`. A production system should validate dates and payroll calculations in trusted backend code and maintain a reliable timezone policy.
- Firestore rules can restrict who performs a write, but they do not make client-side overtime calculations authoritative.
- Employee join codes are shared secrets; implement individual invitations and manager approval before public release.
- The app does not implement GPS tracking, push notifications, export, leave, payroll, or break deductions in this version.

## 8. Configuration versions

The project uses Kotlin, Jetpack Compose, Android Gradle Plugin, and Firebase. Dependency versions are pinned in the Gradle files so the initial project is reproducible. Android Studio may suggest updates; do not upgrade many dependencies at once if you are still learning. First get the supplied project running as-is.
