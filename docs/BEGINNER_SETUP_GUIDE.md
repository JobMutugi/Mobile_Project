# Beginner Setup Guide - From Zero to Running the App

This guide assumes you have never used Android Studio. Follow the sections in order. You do **not** need to configure Firebase to test the demo screens.

# Part A - Install and open Android Studio

## Step 1: Install Android Studio

1. Open the official Android Studio download page: https://developer.android.com/studio
2. Download the current stable Linux version.
3. If the download is a `.tar.gz` archive, extract it to a folder you can find. For example, create `~/Applications` and extract the `android-studio` folder inside it.
4. Open the extracted folder, open `bin`, and run `studio.sh`. You can also start Android Studio from an existing desktop shortcut if it is already installed.
5. On first launch, follow the Setup Wizard. Choose the **Standard** setup unless you have a reason to change it.
6. Accept the Android SDK licenses and allow the wizard to download the SDK tools. This requires internet access.
7. Let the initial setup finish before opening the project.

## Step 2: Extract the project ZIP

1. Download `Employee_Time_Clock_Project.zip` from the conversation.
2. In your file manager, right-click the ZIP and choose **Extract Here** or **Extract to...**.
3. Put the extracted folder in a simple location such as `~/Projects/Employee_Time_Clock_Project`.
4. Open that folder and check that you can see a file named `settings.gradle.kts` and a folder named `app`.

Do not open an individual `.kt` file. Open the entire project folder.

## Step 3: Open the project

1. Start Android Studio.
2. On the Welcome screen click **Open**. If another project is open, use **File > Open**.
3. Browse to the extracted `Employee_Time_Clock_Project` folder - the folder containing `settings.gradle.kts`.
4. Select that folder and click **Open**.
5. If asked whether to trust the project, trust it because it is the source you just downloaded.
6. Wait for **Gradle Sync** to finish. The first sync can take a while because Android Studio must download libraries.
7. If a box says Android SDK Platform 36 or Android SDK Build Tools are missing, click the offered install/download option and accept the license.
8. If you see red errors during the first few seconds, wait for sync and indexing to finish before changing files.

## Step 4: Check the Gradle JDK

This project uses Android Gradle Plugin 8.13 and requires JDK 17 or newer. The easiest first choice is Android Studio's embedded JDK.

1. Open **File > Settings**.
2. Go to **Build, Execution, Deployment > Build Tools > Gradle**.
3. Find **Gradle JDK**.
4. Select the embedded JDK / JBR option if it is available and is version 17 or newer.
5. Click **Apply**, then **OK**. Allow Gradle to sync again.

## Step 5: Create an emulator (virtual Android phone)

You can skip this section if you have an Android phone with USB debugging already enabled.

1. In Android Studio, open **Tools > Device Manager**. Depending on the version, the Device Manager may also be a button on the right side.
2. Click **Create Device** or the plus sign.
3. Choose a common phone such as Pixel, then click **Next**.
4. Choose an available Android system image with API 36, or another installed version at API 26 or newer. Download it if Android Studio asks.
5. Finish the wizard.
6. In Device Manager, press the triangle/Play icon beside the new emulator to start it.
7. Wait until the Android home screen appears before running your app.

If the emulator will not start on Linux, virtualization support may need to be enabled for your computer. You can use a physical Android phone instead.

## Step 6: Or connect a real Android phone

1. On the phone, open **Settings > About phone**.
2. Tap **Build number** seven times to enable Developer options. Menu names can vary by phone.
3. Go back to Settings, open **Developer options**, and turn on **USB debugging**.
4. Connect the phone to the computer with a USB cable that supports data.
5. Unlock the phone. If a prompt asks whether to allow USB debugging for this computer, accept it.
6. In Android Studio's device selector, choose the phone.

## Step 7: Run the app for the first time

1. At the top of Android Studio, make sure the run configuration says **app**.
2. Beside the green triangle, select the emulator or your connected phone.
3. Click the green **Run** triangle.
4. Wait for Android Studio to build the app and install it.
5. The Employee Time Clock login screen should open.
6. Since you have not configured Firebase, you should see **Open employee demo** and **Open manager demo**.
7. Click **Open employee demo**. Try Clock In, Refresh attendance, and Clock Out.
8. Return to the app start screen or sign out, then open **manager demo**. Try editing an employee's shift and switch between Today and This month.

Demo information is local and temporary. It is meant for learning and testing the screen flow. It is not shared with another phone.

# Part B - Connect Firebase for real accounts and shared records

Complete this part only after the demo app runs. Firebase allows manager and employee accounts on different phones to share records. You need a Google account and internet access.

## Step 8: Create a Firebase project

1. Open https://console.firebase.google.com/ in a browser.
2. Sign in with your Google account.
3. Click **Create a project** (or add a project).
4. Use a name such as `Employee Time Clock`. You can disable Google Analytics for this starter app if you do not need it.
5. Finish the wizard and wait for the project dashboard.

## Step 9: Register the Android app in Firebase

1. On the Firebase project overview, click the Android icon to add an Android app.
2. For the Android package name, enter exactly:

   `com.example.employeetimeclock`

3. Give the app a nickname such as `Employee Time Clock Android` if requested.
4. Register the app.
5. Click **Download google-services.json**.
6. Copy the downloaded file into the project's `app` folder so the final path is:

   `Employee_Time_Clock_Project/app/google-services.json`

7. Do not rename it to `google-services (1).json`. The name must be exactly `google-services.json`.
8. Return to Android Studio. If it was already open, wait for Gradle sync. If it does not sync automatically, click **File > Sync Project with Gradle Files**.

Do not upload `google-services.json` to a public source-code repository. Firebase client configuration is not a password, but a public repository can still expose project details; access rules and authentication must protect the data.

## Step 10: Enable email and password login

1. In Firebase Console, select **Build > Authentication**.
2. Click **Get started** if shown.
3. Open **Sign-in method** (the wording may vary slightly).
4. Choose **Email/Password**.
5. Enable the Email/Password option and save.

## Step 11: Create the Firestore database

1. In Firebase Console, open **Build > Firestore Database**.
2. Click **Create database**.
3. Choose **Production mode** if prompted. We will apply the supplied restrictive rules after creating the first records.
4. Choose a location close to your expected users. The region cannot always be changed later, so choose carefully.
5. Finish database creation.

## Step 12: Create the first manager login

The app intentionally does not let a person select Manager during sign-up. This prevents every new employee from claiming manager permissions.

1. In Firebase Console, open **Authentication > Users**.
2. Click **Add user**.
3. Enter the manager's real email and a strong temporary password.
4. Create the user.
5. Copy the user's **UID** exactly. Keep the Firebase Console open; you will use the UID next.

## Step 13: Create the company document

1. Open **Firestore Database > Data**.
2. Click **Start collection**.
3. Enter the collection ID `companies`.
4. For the first document ID, enter a hard-to-guess company join code, for example `ACME-Q7K2-4N9P`. Your employees will type this exact value in the app, so share it privately.
5. Add the following fields (field names are case-sensitive):

   | Field | Type | Example value |
   |---|---|---|
   | `name` | string | `Acme Services` |
   | `active` | boolean | `true` |
   | `managerUid` | string | paste the manager UID from Step 12 |

6. Save the document. Remember the document ID: it is also the company join code.

## Step 14: Create the manager profile document

1. In Firestore Data, create a collection named `users` if it does not exist.
2. Use the manager's Firebase Authentication UID as the document ID exactly.
3. Add these fields:

   | Field | Type | Example value |
   |---|---|---|
   | `uid` | string | same manager UID |
   | `name` | string | manager's display name |
   | `email` | string | same email used in Authentication |
   | `role` | string | `manager` |
   | `companyId` | string | exact `companies` document ID from Step 13 |
   | `shiftStart` | string | `09:00` |
   | `shiftEnd` | string | `17:00` |
   | `createdAt` | timestamp | current timestamp, if the console requires a value |

4. Save the document.

Check carefully: the user document ID and `uid` field must match the Authentication UID. `companyId` must match the company document ID exactly. The role must be lowercase `manager`.

## Step 15: Publish Firestore security rules

1. In the project folder, open `firestore.rules` in a text editor.
2. In Firebase Console, open **Firestore Database > Rules**.
3. Select all text currently in the rules editor.
4. Copy all content from the project's `firestore.rules` file and paste it into the console editor.
5. Click **Publish**.
6. Do not add a blanket permission such as `allow read, write: if true`.

Create the company and first manager profile in the console before publishing the rules. Console administration is how this starter project bootstraps the first manager; the mobile app is not permitted to create manager profiles.

## Step 16: Run the cloud version

1. Return to Android Studio.
2. Confirm `app/google-services.json` exists in the correct folder.
3. Choose **File > Sync Project with Gradle Files**.
4. Run the app again.
5. The login screen should now show email/password sign-in and employee account creation, not the two demo buttons.
6. Sign in using the manager email and password you created in Step 12.
7. You should see the manager dashboard. Initially the employee list may be empty.
8. Sign out and create an employee account using an employee's name, email, password, and the company join code from Step 13.
9. Sign in as the employee and test Clock In and Clock Out.
10. Sign in as the manager and refresh the dashboard. The employee record should appear.
11. For a genuine multi-device check, install/run the same configured app on a second phone and sign in with the employee account.

## Step 17: Build an APK for testing

1. In Android Studio, open **Build > Build Bundle(s) / APK(s) > Build APK(s)**. The menu wording may vary slightly by Android Studio release.
2. Wait for the build to finish. Android Studio normally shows a notification with a **Locate** link.
3. Click Locate to find the APK. The usual debug APK location is `app/build/outputs/apk/debug/app-debug.apk`.
4. Install this APK only on test devices you control. This debug APK is not the final release build for Google Play.

# Troubleshooting

### The app shows demo buttons even though I added Firebase
- Confirm the file is named exactly `google-services.json`.
- Confirm it is inside the `app` folder, not the project root.
- Confirm the Firebase Android app package name is exactly `com.example.employeetimeclock`.
- Run Gradle sync, then build and run again.

### Sign-in works in Firebase but the app says the profile is missing
- In Firestore, open `users` and check there is a document whose document ID is the same UID shown in Authentication.
- Check that the profile contains `role`, `companyId`, `name`, `email`, `shiftStart`, and `shiftEnd` fields.
- Check the manager role is exactly `manager` and employee role is exactly `employee`.

### Employees cannot register
- Confirm Email/Password is enabled under Authentication sign-in methods.
- Confirm the company document exists and has boolean field `active = true`.
- Ensure the company join code entered into the app matches the company document ID exactly.
- Ensure the Firestore rules from this project are published.

### Manager dashboard is empty
- Confirm the manager's Firestore profile uses the same `companyId` as the company document.
- Confirm the employees used that same company join code during registration.
- Tap Refresh dashboard.

### Gradle sync fails
- Confirm internet access is working.
- Check the Gradle JDK setting described in Step 4.
- Install the Android SDK Platform requested by the sync error.
- Read the first meaningful error in the Build/Sync window instead of changing random Gradle versions.

### The demo clock shows odd sample times
- Demo entries are illustrative sample records and can show times that do not match your actual workday. They are not real attendance. The Firebase version uses server timestamps for clock-in/out.
