# 🎵 Audio Cutter App

An Android app to trim audio files using Jetpack Compose and ExoPlayer. Clean Architecture + MVVM + Hilt make the structure super scalable and modular.

> 📦 [**Version 1.0.2**](https://github.com/Codexyze/Audio_Cutter/releases/tag/v1.02) is now stable and will be deployed to the Play Store very soon!  
> 🚀 This project is a **sub-feature of my major music app LHYTHM**. You can check that out here:  
> 👉 [https://github.com/Codexyze/Lhythm](https://github.com/Codexyze/Lhythm)

---
<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/user-attachments/assets/367a2e03-8f0a-4c78-9ebf-a512308263c4" width="24%" />
  <img src="https://github.com/user-attachments/assets/a4e71e88-e461-4aa6-bd49-651f95ee69c7" width="24%" />
  <img src="https://github.com/user-attachments/assets/a51ed97a-c049-46db-bb33-63437463c5c7" width="24%" />
  <img src="https://github.com/user-attachments/assets/e862064b-9ec2-400a-8c95-50bfda86a5ba" width="24%" />
</div>

---
## 🧠 Features

* 🎧 Play any audio file
* ✂️ Trim audio between start and end time (HH\:MM\:SS)
* 💾 Save trimmed audio with custom filename
* ✅ Real-time UI state (Loading, Success, Error)
* 🧪 Built using clean architecture & modern Android stack

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

* 💚 [Jetpack Compose](https://developer.android.com/jetpack/compose)  
* 🔥 [Kotlin](https://kotlinlang.org/docs/home.html)  
* 🎞️ [ExoPlayer](https://exoplayer.dev/)  
* 🧩 [Hilt (Dependency Injection)](https://developer.android.com/training/dependency-injection/hilt-android)  
* 🧠 [MVVM + Clean Architecture](https://developer.android.com/jetpack/guide)


---

## 🛠 How to Use

1. Select audio file from storage
2. Audio plays via ExoPlayer
3. Set:

   * ⏱ Start Time → (HH\:MM\:SS)
   * ⏱ End Time → (HH\:MM\:SS)
   * 💾 Output File Name
4. Hit the **Trim Audio** button
5. Trimmed file gets saved in local storage 🎉

---
## Versions
[**1.0.0**](https://github.com/Codexyze/Audio_Cutter/releases/tag/v1.0.0) : Beta Unstable




[**1.0.2-stable**](https://github.com/Codexyze/Audio_Cutter/releases/tag/v1.02) : Soon will be deployed on [PLAYSTORE](https://play.google.com/store/apps/dev?id=9069883027072615264)

---


## ⚙️ Permissions

```xml
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
---
## 📱 More Apps & Contact
🚀 Check out more of my apps on Google Play:
👉 https://play.google.com/store/apps/dev?id=9069883027072615264

📩 Got feedback or want to test early versions?
Email me at nutrinonovarage@gmail.com
---
## 📥 Clone the Project

```bash
git clone https://github.com/Codexyze/Audio_Cutter

```


---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
