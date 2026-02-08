
[![Play Store](https://img.shields.io/badge/Download-Play%20Store-34A853?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.nutrino.audiocutter)
[![Release](https://img.shields.io/github/v/release/Codexyze/Audio_Cutter?style=for-the-badge&color=blue)](https://github.com/Codexyze/Audio_Cutter/releases)
[![License](https://img.shields.io/github/license/Codexyze/Audio_Cutter?style=for-the-badge&color=lightgray)](LICENSE)

A smooth, modern and powerful **audio & video editing app** built with Jetpack Compose and ExoPlayer.  
It lets users **trim audio & video files, extract audio from videos, merge multiple audio tracks**, and save custom clips instantly.  
This project follows **Clean Architecture + MVVM + Hilt**, making it modular, testable, and scalable.

> 📦 **Latest Stable Release:**  
> 👉 [Version 2.0.0](https://github.com/Codexyze/Audio_Cutter/releases)
>
> 🚀 Audio Cutter is now **live on the Play Store**:  
> 👉 https://play.google.com/store/apps/details?id=com.nutrino.audiocutter
>
> 🎶 This project is a **sub-feature** of my main music app **LHYTHM**:  
> 👉 https://github.com/Codexyze/Lhythm

---

<div style="display: flex; flex-wrap: wrap; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/29243dbd-faf9-4434-8251-28c0da479be7" width="24%" />
  <img src="https://github.com/user-attachments/assets/d8b9cb2e-d1da-4723-bb68-bebbeff708f7" width="24%" />
  <img src="https://github.com/user-attachments/assets/9b4a3dd9-c8a4-49bc-91ac-f19a3c5e1e58" width="24%" />
  <img src="https://github.com/user-attachments/assets/0eb1235e-c876-456d-ad90-34176d693e29" width="24%" />
  <img src="https://github.com/user-attachments/assets/ef7f76e7-bf1e-40c5-a0bc-2ea8e89e6bc8" width="24%" />
  <img src="https://github.com/user-attachments/assets/4ef1e7d3-d91c-4a48-a243-5d4f084eb1b4" width="24%" />
  <img src="https://github.com/user-attachments/assets/26c7d1e3-9223-433a-ba56-3be221b8d4d7" width="24%" />




</div>

---

## 🧠 Features

### Audio Trimmer
- 🎧 **Play any audio file**
- ✂️ **Trim audio easily using a slider-based editor**
- 🔊 **Real-time waveform preview & playback**
- 💾 **Save trimmed files with your custom filename**
- 🎵 **Create ringtones, alarms, and notification sounds**

### Video Trimmer
- 🎬 **Trim video files with precision**
- 📹 **Real-time video preview while editing**
- ⏱️ **Slider-based start and end time selection**
- 💾 **Save trimmed videos with custom filename**

### Audio Extractor
- 🎵 **Extract audio from video files**
- 🎧 **Select specific time range to extract**
- 💾 **Save extracted audio as M4A format**
- 📹 **Preview video before extraction**

### Audio Merge
- 🎼 **Merge multiple audio tracks into one**
- 🔢 **Select songs in custom sequence**
- ➕ **Combine 2 or more audio files**
- 💾 **Save merged audio with custom filename**

### General
- ⚡ **Fast performance with clean UI**
- 🔐 **Local processing — your files stay on device**
- 🧪 Built using modern Android stack + clean architecture

---
## 👨‍💻 Developer Take

Hi, I’m **Akshay Sarapure** 👋

This project holds a special place for me. **Audio Cutter** is my **first application published on the Google Play Store**, and it represents an important learning milestone in my Android development journey.

I want to be transparent about the intent behind this project.  
While there are more powerful approaches for media processing (such as using native FFmpeg through JNI), I intentionally chose **ExoPlayer and Media3 Transformer** for this app. The goal was to deeply understand **Android’s native media pipeline**, modern playback APIs, and transformation workflows — not just to ship a feature-heavy product.

Yes, there may be flaws, edge cases, or areas that could be optimized further. I acknowledge that.  
But this project was built with a clear purpose: **to learn, to experiment, and to build something practical that genuinely gets the job done**.

Every feature in this app helped me understand real-world challenges like media handling, performance trade-offs, user experience, and clean architecture. The app may not be perfect — but it is honest work, and it delivers on its core promise.

I believe sharing this context openly reflects the ideology behind the project and the mindset .

Thanks for taking the time to check out the project
---

## 📂 Folder Structure

```
com.nutrino.audiocutter
├── core
│   └── MediaPlayerManager          # ExoPlayer setup & control
│
├── data
│   ├── DataClass                   # Data models
│   ├── RepoImpl                    # Implementation of domain repo
│
│
├── di
│   └── (Hilt Modules)              # Dependency injection setup
│
├── domain
│   ├── Repository                  # Interface for data source
│   ├── StateHandeling             # Sealed classes for UI states
│   └── UseCases                   # Business logic use-cases
│
├── presentation
│   ├── Navigation                  # App navigation (NavHost)
│   ├── Screens                     # All composables (Trimmer, Success, Error)
│   ├── Utils                       # Helpers / formatters / converters
│   ├── ViewModel                   # ViewModels (Trimmer, Player)
│  
```

---

## 🧪 Tech Stack

- 💚 Jetpack Compose
- 🔥 Kotlin
- 🎞️ ExoPlayer
- 🧩 Hilt (DI)
- 🎯 Clean Architecture + MVVM
- 📂 Scoped Storage + File I/O

---

## 🛠 How to Use

### Audio Trimmer
1. Select any audio file from storage
2. File loads and plays via ExoPlayer
3. Adjust trimming using the **interactive slider**
4. Enter your desired output filename
5. Tap **Trim Audio**
6. Trimmed result is saved instantly 🎉

### Video Trimmer
1. Select any video file from storage
2. Video loads with preview
3. Adjust start and end time using the **slider**
4. Enter your desired output filename
5. Tap **Trim Video**
6. Trimmed video is saved 🎉

### Audio Extractor
1. Select any video file from storage
2. Video preview loads
3. Choose the time range to extract audio
4. Enter your desired output filename
5. Tap **Extract Audio**
6. Audio extracted and saved as M4A 🎉

### Audio Merge
1. Select **Audio Merge** from main menu
2. Choose 2 or more songs in desired sequence
3. Songs show numbered order (1, 2, 3...)
4. Enter output filename
5. Tap **Merge Tracks**
6. Merged audio saved instantly 🎉

---

## 📦 Versions

| Version | Status | Link |
|--------|--------|------|
| **1.0.0** | Beta (unstable) | https://github.com/Codexyze/Audio_Cutter/releases/tag/v1.0.0 |
| **1.0.2** | Stable | https://github.com/Codexyze/Audio_Cutter/releases/tag/v1.02 |
| **Play Store** | Live | https://play.google.com/store/apps/details?id=com.nutrino.audiocutter |

---

## ⚙️ Permissions

```xml
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />

<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

---

## 📱 More Apps & Contact

If you like Audio Cutter, you might enjoy my other apps too! Explore more on Google Play:  
👉 https://play.google.com/store/apps/dev?id=9069883027072615264

Have suggestions, feature ideas, or found a bug? I'm always happy to improve the project.  
📩 Email: nutrinonovarage@gmail.com

---

## 📥 Clone the Project

```bash
git clone https://github.com/Codexyze/Audio_Cutter
```

---

## 🧾 License

This project is licensed under the MIT License. See the full license here:  
👉 [LICENSE](LICENSE)

---

## ⭐ Support

If you found this project helpful or interesting:

- ⭐ Star the repository
- 🐛 Open issues or send PRs
- 📲 Download and rate the app on Google Play
- 🔗 Share it with others

Your support keeps the project growing!

---

## 🙏 Thanks

Thanks for checking out Audio Cutter! More improvements, features, and refinements are on the way. Stay tuned — and happy trimming! 
