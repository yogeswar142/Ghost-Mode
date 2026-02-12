# 👻 GhostMode -- WhatsApp Notification Intelligence

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Room](https://img.shields.io/badge/Room-Database-4285F4?style=for-the-badge)](https://developer.android.com/training/data-storage/room)
[![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)]()

**GhostMode** is a privacy-focused Android application that captures
WhatsApp notifications locally and transforms them into a searchable,
pinnable, and analyzable message archive.

Built with modern Android architecture principles, GhostMode
demonstrates advanced handling of NotificationListenerService, Room
database flows, and real-time UI updates.

------------------------------------------------------------------------

## 🚀 Core Features

### 🔔 Smart Notification Capture

-   Uses `NotificationListenerService`
-   Parses MessagingStyle for full message extraction
-   Captures expanded message content (not truncated preview)

------------------------------------------------------------------------

### 💬 Chats Dashboard

-   Real-time Room + Flow powered updates
-   RecyclerView message list
-   Search functionality:
    -   Conversation name
    -   Sender name
    -   Message content
-   Message detail view
-   Smooth lifecycle-safe collection

------------------------------------------------------------------------

### 📌 Pin System

-   Pin/unpin messages
-   Dedicated Pinned tab
-   Persistent storage in Room
-   Instant UI refresh

------------------------------------------------------------------------

### 📊 Analytics Dashboard

Visual insights powered by MPAndroidChart:

-   📈 Messages per Day (Line Chart)
-   📊 Top Conversations (Bar Chart)
-   📌 Total Pinned Count
-   🗓 Messages Today
-   ⏰ Hourly Activity Distribution

All analytics generated locally from stored Room data.

------------------------------------------------------------------------

### 🔐 Password Security

-   App Lock using encrypted preferences
-   PBKDF2 password hashing
-   Change password with old-password validation
-   Secure reset option

------------------------------------------------------------------------

## 🏗 Tech Stack

  Category       Technology
  -------------- ----------------------------
  Language       Kotlin
  Architecture   MVVM
  Database       Room
  Async          Coroutines + Flow
  Charts         MPAndroidChart
  Security       EncryptedSharedPreferences
  UI             Material Design Components

------------------------------------------------------------------------

## 🔒 Privacy & Security

GhostMode: - Does NOT access WhatsApp private database - Does NOT
require SMS permission - Does NOT read contacts - Stores all data
locally - No internet transmission of messages

Only messages shown in Android notifications are captured.

------------------------------------------------------------------------

## ⚙️ Required Permissions

-   Notification Access (Special Permission)
-   POST_NOTIFICATIONS (Android 13+)

No dangerous permissions required.

------------------------------------------------------------------------

## 🛠 How It Works

1.  User enables Notification Access
2.  WhatsApp sends notification
3.  GhostMode intercepts it
4.  Extracts MessagingStyle content
5.  Stores message in Room database
6.  UI updates automatically via Flow observers

------------------------------------------------------------------------

## 🧪 Debugging Guide

To monitor message capture:

``` bash
adb logcat MessageNLS:D AndroidRuntime:E *:S
```

To verify listener connection:

``` bash
adb shell dumpsys notification listeners
```

------------------------------------------------------------------------

## 📂 Project Structure

    app/
     ├── core/
     │    ├── notification/
     │    ├── security/
     │    ├── lock/
     ├── data/
     │    ├── local/
     ├── feature/
     │    ├── home/
     │    ├── lock/
     │    ├── whatsapp/
     │         ├── chats/
     │         ├── pinned/
     │         ├── analytics/

------------------------------------------------------------------------

## 📦 Setup Instructions

1.  Clone the repository:

``` bash
git clone https://github.com/yourusername/ghostmode.git
cd ghostmode
```

2.  Build the project:

``` bash
./gradlew assembleDebug
```

3.  Install on device:

``` bash
./gradlew installDebug
```

4.  Enable Notification Access in device settings.

------------------------------------------------------------------------

## 📈 Future Improvements

-   Export messages to JSON/CSV
-   Backup & Restore
-   Multi-app support (Telegram, Instagram)
-   Advanced analytics filters
-   Dark mode refinements

------------------------------------------------------------------------

## ⚠️ Disclaimer

This project is intended for educational and personal use.

It does not bypass encryption or access private app storage.\
It only processes notification data made available by Android.

------------------------------------------------------------------------


## 👨‍💻 Author

**Yogeswar** *Aspiring Software Engineer & AI Enthusiast*

* [LinkedIn](https://linkedin.com/in/your-profile)
* [Portfolio](https://your-portfolio.com)


------------------------------------------------------------------------

