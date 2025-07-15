# 🎵 Audio Cutter App

An Android app to trim audio files using Jetpack Compose and ExoPlayer. Clean Architecture + MVVM + Hilt make the structure super scalable and modular.

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



## ⚙️ Permissions

```xml
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
---

## 📥 Clone the Project

```bash
git clone https://github.com/Codexyze/Audio_Cutter

```


---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
