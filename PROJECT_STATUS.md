# Manager's Log

Offline-first Android manager notes and Store Walks app.

## Build

Open **Actions → Build Android APK** and download the `ManagersLog-debug-apk` artifact after a successful build. The workflow uses Java 17, Gradle 8.9, and Android Gradle Plugin 8.7.3. Alternatively run `gradle :app:assembleDebug` locally.

## v1.0 functionality

- Home dashboard with today's notes, open and overdue counts
- Quick Notes and Store Walk notes, with due dates, departments, optional photos, and optional reminders
- Tasks with completion and overdue prioritization
- Store Walk history, grouping, rename, email share, and cascading deletion
- Department management, light/dark theme, archive after 14 days
- Automatic backup after changes once a folder is selected; merge restore

## Known limitations / test checklist

This is an initial implementation, not a release-certified app. Test the Android build, reminders, EOD rollover, photo handling, backup failure recovery, and restore on an actual device before relying on it. Photos are not embedded in emailed summaries. Android may delay alarms for battery optimization. Auto backup needs a folder selection first. There is no server or account.
