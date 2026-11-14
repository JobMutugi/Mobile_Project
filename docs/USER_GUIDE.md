# User Guide

## Employee

### Sign in
Use the email address and password assigned to your account. For the first cloud sign-up, select the employee account option, enter your name, email, password, and the company join code from your manager.

### Clock in
1. Open Employee Time Clock.
2. Sign in using your employee account.
3. Review the shift displayed under Your shift.
4. Tap **CLOCK IN** once when you begin work.
5. Check that the screen shows the clock-in time and that you are currently clocked in.

Do not clock in twice for one shift. The app checks whether an open attendance record already exists.

### Clock out
1. Open the app while signed in.
2. Tap **CLOCK OUT** when you finish the shift.
3. The app closes the open record and updates the worked time.
4. Review Attendance history to see your clock-in, clock-out, worked-time duration, and overtime estimate.

### Refresh
Tap **Refresh attendance** if the data has changed on another device or the screen looks out of date.

## Manager

### Open the dashboard
Sign in with the manager account provisioned by the administrator. The manager dashboard shows the company ID and summary counts for employees, attendance today, absence, and lateness.

### Read employee status
- **Present:** an attendance record exists and the employee is clocked in without a late flag.
- **Late:** the latest clock-in is later than the shift start in the employee's profile/record.
- **Completed:** the record has been clocked out and is not late.
- **Absent:** no attendance record exists for that employee's current local date.

The starter status model is intentionally simple; businesses with breaks, multiple daily shifts, remote work, leave, and overnight shifts need additional rules.

### Edit an employee's shift
1. Find the employee card.
2. Tap **Edit shift**.
3. Enter the start time in 24-hour format, for example `08:00`.
4. Enter the end time, for example `17:00`.
5. Tap **Save shift**.

The starter version requires shift end to be later than shift start on the same date. Overnight shifts are not supported. A shift change affects future attendance records; existing records keep the shift times captured when the employee clocked in.

### View reports
1. Scroll to **Attendance reports**.
2. Choose **Today** for the current date.
3. Choose **This month** for records in the current calendar month.
4. Each record lists employee name, date, clock-in, clock-out, worked duration, overtime estimate, and status.
5. Tap **Refresh dashboard** to retrieve current cloud data.

## Important notes

- Demo mode is only for exploring UI behavior. It uses sample data stored locally and should not be used for real attendance.
- The cloud version requires Firebase Authentication, a company record, a manager profile, and published Firestore rules.
- Use the join code only for employees who are allowed to join the company.
- This is a starter project, not a payroll or legal timekeeping authority. Client-side calculations and device dates need server-side validation before production use.
