# 📅 HelloSchedule

**HelloSchedule** is a lightweight Android app that gives you **quick, glanceable access to your upcoming calendar events** via a sleek bottom drawer. Inspired by Niagara Launcher's minimalist schedule-drawer design, it's fast, theme-aware, and integrates directly with your system calendar.

---

## ✨ Features

- 🔽 **Bottom Sheet UI**: See events for **Today**, **Tomorrow**, and the **Day After** in a scrollable drawer.
- 🕐 **Smart Time Formatting**: Shows relative times like “in 10m” or “Ongoing • 43m”; otherwise shows precise timings like “10:30am – 11:15am”.
- 📍 **Location Aware**: Displays event locations and opens them in the system calendar.
- ➕ **Quick Add**: Tap a "New Event" button to jump straight into your calendar’s add screen.
- 🧭 **Quick Settings Tile**: Instantly open the drawer from Android’s Quick Settings panel.
- 🌙 **Dark/Light Mode Support**: Adapts to system theme and Material You dynamic color.
- 🎨 **Custom App & Tile Icons**: A polished experience from launcher to tile.

---

## 🚀 How It Works

- A homescreen shortcut or Quick Settings tile launches a translucent activity (`TransparentActivity`) that hosts the bottom sheet.
- The bottom sheet (`ScheduleBottomSheet`) reads from your system calendar and organizes events grouped by day.
- Interactions like tapping events, adding new ones, or opening the full calendar app are smooth and integrated.

---

## 🛠 Tech Stack

- **Kotlin**
- **Android Jetpack**
  - `BottomSheetDialogFragment`
  - `TileService`
  - `Intent` system
- **Material Components**
- **Calendar Provider API**

---

## 📦 Build & Run

1. Clone this repo:
   ```bash
   git clone https://github.com/Siddharth-Nath-06/HelloSchedule.git
   ```
2. Open in **Android Studio**.
3. Grant Calendar permissions (`READ_CALENDAR`).
4. Build & run on a **real device** (emulator may not populate events).
5. Optionally add the Quick Settings tile:
   - Pull down your Quick Settings → Tap **Edit** → Drag the **Schedule** tile into your panel.

---

## 🔐 Permissions

- `READ_CALENDAR` – to fetch your events
- `FOREGROUND_SERVICE` – required for Quick Settings tile service (Android 10+)

---

## 🧠 Inspiration

Built with simplicity in mind and inspired by [Niagara Launcher](https://play.google.com/store/apps/details?id=bitpit.launcher), this app is designed for users who want a **frictionless, beautiful calendar glance** — without opening a full calendar app every time.

---

## 📚 First Time Dev Notes 🚀

This is my **very first Android app**, and it was an incredible learning experience!  
I built it from scratch while learning Android development along the way — from layout design and system calendar integration, to customizing icons and handling Quick Settings Tiles.

If you're just starting out in Android, I hope this project helps you like it helped me.  
All feedback, suggestions, and improvements are welcome!

---

## 👤 Author

**[Siddharth Nath]**  
[GitHub @Siddharth-Nath-06](https://github.com/Siddharth-Nath-06) 
Made with ❤️ and Kotlin.

---

> 🙌 Contributions, issues, and stars are welcome! Help improve HelloSchedule or adapt it to your workflow.

