# Employee Time Clock — Full Code Explanation (Line by Line)

This document explains the project's source code file by file, line by line, including each function.

---

## File 1: `app/src/main/java/com/example/employeetimeclock/Models.kt`

This file defines the data models used across the app.

### Line 1: `package com.example.employeetimeclock`
Declares the package name for the file so classes belong to this namespace.

### Line 3: `import java.util.Date`
Imports Java's Date class for timestamp fields.

### Lines 5–13: `data class UserProfile(...)`
A data class representing a user account.
- `uid`: unique user id
- `name`: full name
- `email`: login email
- `role`: "employee" or "manager"
- `companyId`: which company the user belongs to
- `shiftStart`: scheduled shift start time (default "09:00")
- `shiftEnd`: scheduled shift end time (default "17:00")

### Lines 15–25: `data class AttendanceRecord(...)`
Represents one clock-in/clock-out record.
- `id`: record id
- `uid`: employee id
- `employeeName`: employee's name
- `companyId`: company id
- `dateKey`: date string (e.g. "2026-10-27")
- `clockIn`: clock-in timestamp (nullable)
- `clockOut`: clock-out timestamp (nullable)
- `shiftStart` / `shiftEnd`: snapshot of the employee's shift at that time

### Lines 27–30: `enum class ReportPeriod`
Two reporting periods: TODAY and THIS_MONTH.

---

## File 2: `app/src/main/java/com/example/employeetimeclock/EmployeeTimeClockApp.kt`

Jetpack Compose UI. Imports (lines 3–47) bring in layout, Material3 components, runtime state, and Java time classes.

### Lines 49–103: `EmployeeTimeClockApp(...)` composable
Root composable. Receives all app state and callbacks. Wraps content in `MaterialTheme`. Shows `LoginScreen` if `profile == null`, otherwise `MainScreen`.

### Lines 105–196: `LoginScreen(...)`
Login/registration screen.
- Lines 115–119: state variables `createAccount`, `name`, `email`, `password`, `companyCode` (rememberSaveable).
- Lines 121–134: centered column with title text.
- Lines 136–151: demo mode block with demo login buttons.
- Lines 152–185: real sign-in/register form with fields and buttons.
- Lines 187–194: message and busy indicator.

### Lines 198–269: `MainScreen(...)`
Main screen after login. Shows header, demo banner, messages, busy indicator, then ManagerDashboard or EmployeeDashboard based on role.

### Lines 271–314: `EmployeeDashboard(...)`
Employee view.
- Line 281: `today` = current date string
- Line 282: filters attendance for today
- Line 283: latest today record
- Lines 284–306: card showing shift, clock in/out button, refresh
- Lines 308–313: attendance history list

### Lines 316–377: `ManagerDashboard(...)`
Manager view.
- Lines 327–334: computes today records, attended ids, absent count, late count, present count
- Lines 336–345: stat cards
- Lines 348–356: employee attendance cards
- Lines 358–376: attendance reports with filter chips

### Lines 379–428: `EmployeeStatusCard(...)`
Per-employee card with status and edit-shift dialog.

### Lines 430–441: `AttendanceRecordCard(...)`
Card showing a single attendance record's details.

### Lines 443–451: `StatCard(...)`
Small stat display card.

### Lines 453–462: `InfoCard(...)`
Generic info card wrapper.

### Lines 464–516: helper functions
- `formatDateTime(date)`: formats Date to "dd MMM yyyy, HH:mm"
- `formatTime(date)`: formats Date to "HH:mm"
- `formatDateKey(dateKey)`: parses date key to display format
- `formatWorkedDuration(start, end)`: computes worked duration
- `formatMinutes(minutes)`: formats minutes as "Xh Ym"
- `isLate(record)`: checks if clock-in later than shift start
- `overtimeMinutes(record)`: minutes worked after shift end
- `statusForRecord(record)`: returns status string
" filePath">
/home/cj/Pictures/Employee_Time_Clock_Project/docs/CODE_EXPLANATION.md
---

## File 3: `app/src/main/java/com/example/employeetimeclock/MainActivity.kt`

Android entry point activity. Holds app state in Compose `mutableStateOf` and wires UI callbacks to repository calls.

- Lines 13–24: state variables (demoMode, currentProfile, employees, attendance, openAttendance, reportPeriod, busy, message, messageIsError).
- Lines 26–68: `onCreate` — initializes Firebase (demo mode if null), creates repository, sets Compose content, restores signed-in profile.
- Lines 70–77: `enterDemo` — enters demo role.
- Lines 79–87: `signIn` — signs in user.
- Lines 89–96: `registerEmployee` — registers employee.
- Lines 98–105: `clockIn` — clocks in.
- Lines 107–115: `clockOut` — clocks out using openAttendance.
- Lines 117–124: `saveShift` — updates shift.
- Lines 126–131: `refresh` — refreshes data.
- Lines 133–138: `refreshData` — loads employees, attendance, open attendance.
- Lines 140–149: `signOut` — clears state.
- Lines 151–164: `runAction` — wraps actions with busy flag and error handling.
- Lines 166–169: `showMessage` — sets message text.

---

## File 4: `app/src/main/java/com/example/employeetimeclock/AttendanceRepository.kt`

Data access layer for demo and Firebase modes.

- Lines 17–19: constructor and Firebase instances (null in demo mode).
- Line 21: `enterDemo` — returns demo profile by role.
- Lines 23–32: `restoreSignedInProfile` — restores profile from Firebase auth.
- Lines 34–52: `signIn` — validates, signs in via Firebase, reads profile.
- Lines 54–100: `registerEmployee` — creates account, writes Firestore profile, rolls back on failure.
- Lines 102–104: `signOut`.
- Lines 106–117: `loadEmployees` — loads employees for manager's company.
- Lines 119–133: `loadAttendance` — loads attendance for viewer.
- Lines 135–139: `findOpenAttendance` — finds latest open record.
- Lines 141–183: `clockIn` — creates attendance record.
- Lines 185–206: `clockOut` — closes record with timestamp.
- Lines 208–233: `updateShift` — validates and updates shift times.
- Lines 235–274: private helpers `readProfile`, `profileFromDocument`, `recordFromDocument`, `recordFromMap`.
- Lines 277–343: `DemoStore` object with demo users and records and `profileForRole`.

---

## File 5: `build.gradle.kts` (project-level)

Root Gradle file declaring plugin versions (apply false). Lines 1-6: plugins for Android app, Kotlin android, Kotlin compose, Google services.

---

## File 6: `app/build.gradle.kts`

App module build config.
- Lines 1-6: plugins; Google services plugin applied only if google-services.json exists (lines 8-10).
- Lines 12-32: android block — namespace, compileSdk 37, applicationId, minSdk 26, targetSdk 36, versionCode 1, versionName 1.0, compose build feature, Java 17.
- Lines 34-38: Kotlin jvmTarget JVM_17.
- Lines 40-57: dependencies — Compose BOM, activity-compose, lifecycle, Firebase BOM/auth/firestore, coroutines.

---

## File 7: `settings.gradle.kts`

- Lines 1-7: pluginManagement repositories.
- Lines 8-10: foojay toolchain resolver plugin.
- Lines 12-18: dependencyResolutionManagement repositories.
- Lines 20-21: rootProject.name and include app module.

---

## File 8: `app/src/main/AndroidManifest.xml`

- Line 3: INTERNET permission.
- Lines 5-18: application config — backup, label, RTL, theme, MainActivity as launcher.

---

## File 9: `app/src/main/res/values/strings.xml`

Defines app_name string "Employee Time Clock".

---

## File 10: `app/src/main/res/values/styles.xml`

Defines Theme.EmployeeTimeClock style with font, accent color, status/nav bar colors.

---

## File 11: `firestore.rules`

Firestore security rules.
- Lines 4-29: helper functions signedIn, userPath, hasProfile, myProfile, activeCompany, managerOf.
- Lines 31-39: companies collection rules.
- Lines 41-77: users collection rules (create/read/update/delete).
- Lines 79-108: attendance collection rules.
