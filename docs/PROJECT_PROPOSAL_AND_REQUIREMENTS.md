# Employee Time Clock Android Application
## Project Proposal and Software Requirements Specification

**Project type:** Android mobile application  
**Working title:** Employee Time Clock  
**Prepared for:** Group 13  
**Platform:** Android 8.0 (API 26) and newer  
**Implementation:** Kotlin, Jetpack Compose, Firebase Authentication, Cloud Firestore  
**Version:** 1.0 starter release  

## 1. Abstract

Employee Time Clock is an Android application for small businesses that need a simple way to record employee attendance and manage work hours. Employees use the app to clock in when they arrive and clock out when their shift ends. The application records the timestamps and calculates the time worked. A manager can see employee attendance status, edit shift start and end times, and review daily or current-month records. The starter project includes a local demonstration mode and a Firebase configuration for shared cloud records across devices.

## 2. Problem statement

Some businesses record attendance in paper books, spreadsheets, or informal messages. These methods can make it difficult to see who has arrived, identify late arrivals, calculate working hours consistently, and prepare attendance summaries. A mobile time-clock system can centralize these tasks and reduce manual calculations.

## 3. Main aim

To develop an Android application that helps a business record employee arrival and departure times, calculate working hours and overtime, manage shifts, and review attendance reports.

## 4. Objectives

1. Allow employees to clock in and clock out using an Android phone.
2. Calculate elapsed working time from the recorded timestamps.
3. Compare recorded clock-in time with the employee's scheduled start time to indicate lateness.
4. Estimate overtime when an employee clocks out after the scheduled end time.
5. Allow a manager to view employee attendance status for the current day.
6. Allow a manager to set each employee's shift start and end time.
7. Provide daily and current-month attendance history.
8. Separate manager and employee permissions.
9. Store shared records in Firestore when the Firebase setup is complete.

## 5. Scope

### Included in this starter version

- Employee and manager roles.
- Employee email/password sign-in and employee self-registration after Firebase is configured.
- Manager account provisioning outside the employee registration form.
- Clock-in and clock-out.
- Working-time display.
- Late, present, completed, and absent status labels.
- Manager-configurable same-day shifts.
- Daily and current-month attendance report views.
- Firestore collections and role-based security rules.
- Local demo screens that can be explored before connecting Firebase.

### Not included in version 1

- GPS/location tracking. The app does not request location permission or secretly track staff. A location feature should be added only with a clear business purpose, user notice, permission handling, and a retention policy.
- Push notifications to managers.
- Payroll, tax calculations, or direct salary payments.
- Automatic break deductions, leave requests, biometric checks, or face recognition.
- Overnight shifts whose scheduled end is on the following day.
- CSV/PDF export buttons, multi-branch companies, or a web admin portal.
- An employee invitation approval workflow beyond the company join code.

## 6. Stakeholders and users

| User | Responsibilities |
|---|---|
| Employee | Sign in, clock in, clock out, view personal attendance history. |
| Manager | View employees in the manager's company, review attendance status and reports, and update shift times. |
| Project administrator | Create the Firebase project, provision the first manager account, configure the company record, and maintain access rules. |

## 7. Functional requirements

**FR-01 - Sign in:** Registered users shall be able to sign in with email and password when Firebase is configured.

**FR-02 - Employee registration:** A new user shall be able to register as an employee using their name, email, password, and company join code. The app must not provide a public manager-role selector.

**FR-03 - Clock in:** An employee shall be able to create one open attendance record for their current shift.

**FR-04 - Prevent duplicate open shifts:** The app shall refuse to create a second shift while the employee already has an open record.

**FR-05 - Clock out:** An employee shall be able to close their own open attendance record.

**FR-06 - Worked time:** The app shall display elapsed time between clock-in and clock-out; while a shift is open, it shall display elapsed time to the present moment.

**FR-07 - Late status:** The app shall show a late status when the recorded clock-in local time is later than the employee's scheduled start time.

**FR-08 - Overtime estimate:** The app shall estimate overtime as time after the scheduled end time. This estimate is a convenience feature and is not validated payroll data.

**FR-09 - Manager status:** The manager shall see company employees as Absent when no record exists for the current date, Late when the latest clock-in is after the scheduled start, Present while a record is open and not late, and Completed after a record is closed and not late.

**FR-10 - Shift scheduling:** The manager shall be able to set an employee's shift start and end in 24-hour `HH:mm` format. The starter version requires the end time to be later than the start time on the same day.

**FR-11 - Daily report:** The manager shall be able to view today's attendance records.

**FR-12 - Monthly report:** The manager shall be able to view records whose local date falls in the current calendar month.

**FR-13 - Role access:** Employees shall read their own attendance records. Managers shall read records from their company. Employees shall not edit another employee's records or change their own role/company.

**FR-14 - Demo mode:** The app shall offer sample employee and manager screens when Firebase has not yet been configured.

## 8. Non-functional requirements

- **Usability:** The most common action, clock in or clock out, should be visible without navigating through multiple menus.
- **Compatibility:** Minimum Android API level 26 for this starter project.
- **Security:** Firebase Authentication and restrictive Firestore rules must be used for cloud accounts. Do not publish permissive rules such as `allow read, write: if true`.
- **Privacy:** Collect only data needed for attendance. No GPS tracking is included in this build.
- **Reliability:** Errors such as a missing company code or denied database write should be surfaced to the user.
- **Maintainability:** UI, data models, and repository logic are separated into different Kotlin files.
- **Performance:** The starter version reads company attendance records for report filtering on the device. Large deployments should add server-side date filters, pagination, and monitoring.

## 9. Assumptions and business rules

1. A company has one join code represented by the ID of its Firestore company document.
2. A project administrator manually creates the first manager's Firebase Authentication account and Firestore profile.
3. New users who successfully use the company join code register as employees.
4. Shift times use the device's local timezone and `HH:mm` format.
5. One employee should not have more than one open attendance record at a time.
6. The first version does not subtract unpaid breaks.
7. The first version handles same-day shifts only.
8. The device's local date is used for the `dateKey`; a production payroll product should move trusted time calculations and authoritative timestamps into backend code.

## 10. Acceptance criteria

- The project opens and runs in Android Studio with the included demo mode before Firebase is configured.
- In employee demo mode, tapping Clock In produces an open record; tapping Clock Out closes it.
- The UI calculates working duration and overtime estimate from the stored timestamps.
- In manager demo mode, the employee list displays sample attendance statuses and editable shifts.
- When Firebase is configured correctly, an employee can register using a valid join code, then sign in on another device and see their own cloud attendance.
- A manager can see records for users with the same company ID but cannot see records from another company.
- Employee registration cannot assign the manager role through the app.
- Invalid times, incorrect passwords, duplicate clock-ins, and missing/invalid company codes produce a readable error.

## 11. Risks and limitations

- Firebase setup errors are common for first-time users; follow the setup guide in order.
- A leaked company join code can allow unwanted employee registrations. Use a hard-to-guess code and distribute it privately. A production system should use individually issued invitations.
- A user can manipulate device time and potentially misrepresent attendance because this version calculates some information on the client. Do not use it as the sole source for payroll, disciplinary decisions, or legal timekeeping without server-side validation and review.
- Firestore costs and query performance depend on usage. Read limits and indexes should be reviewed before growing the employee count.
- The starter rules are a useful foundation but should receive a security review before public deployment.

## 12. Future improvements

- Push notifications when employees clock in late or fail to arrive.
- Optional location-based attendance with explicit consent and data minimization.
- Individual invitation codes and manager-controlled employee approval.
- Backend validation using Cloud Functions or another trusted server.
- Paid-break settings, overtime rules, leave management, public holidays, and overnight shifts.
- Export of daily/monthly reports to CSV and PDF.
- Company branches, teams, audit logs, and a browser-based manager portal.

## 13. Reference documentation

- Android Studio: https://developer.android.com/studio
- Compose setup: https://developer.android.com/develop/ui/compose/setup
- Firebase Android setup: https://firebase.google.com/docs/android/setup
- Firestore security rules: https://firebase.google.com/docs/firestore/security/get-started
