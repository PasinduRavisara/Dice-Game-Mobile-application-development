# 🎲 Roll Master – Dice Game

![GitHub repo size](https://img.shields.io/github/repo-size/PasinduRavisara/Roll-Master_DiceGame?color=blue&style=flat-square)
![GitHub last commit](https://img.shields.io/github/last-commit/PasinduRavisara/Roll-Master_DiceGame?color=green&style=flat-square)
![GitHub top language](https://img.shields.io/github/languages/top/PasinduRavisara/Roll-Master_DiceGame?logo=kotlin)
![Android](https://img.shields.io/badge/Android-Compose-green?logo=android)

> A fun and interactive Android dice game built using **Kotlin** and **Jetpack Compose**, where you challenge a computer opponent to reach the target score first. 🎯

---

## ✨ Features

- 🎲 **Two-player mode** – Human vs. Computer
- 🔄 **5-dice gameplay** with up to 2 optional rerolls per turn  
- 🤖 **Computer opponent AI** with random strategy 
- 🏆 **Custom target score** (default: 101)
- 📊 **Real-time scoring** with instant updates  
- ⚖️ **Tie-breaker mode** – re-rolls until someone wins  
- 📱 **Responsive UI** with full state retention on rotation  
- 🎨 Clean, simple, and user-friendly interface 

---

## ⚙️ Game Rules Summary

1. Both players roll 5 dice simultaneously each turn.  
2. A turn allows **0–2 rerolls**, selecting which dice to keep each time.  
3. A turn ends automatically after **3 total rolls** or when the player clicks **Score**.  
4. The first to reach or exceed the target score (default 101) **wins**.  
5. If both reach the target with equal turns and score, **tie-breaker rolls** are played until one wins (no rerolls in tie-breaker).  

---

## 🛠️ Tech Stack

- 💻 **Language:** Kotlin  
- 🎨 **UI Framework:** Jetpack Compose  
- 🔄 **State Handling:** `rememberSaveable` for rotation support  
- ❌ **No third-party libraries**, `ViewModel`, or `LiveData` used  

---
## 🎮 Gameplay Demo

https://github.com/user-attachments/assets/b3be593a-6fc5-4548-9081-10184417d279


---
## 📦 Getting Started

Follow these steps to run the project locally:

1. Clone this repository:
   ```bash
   git clone https://github.com/PasinduRavisara/Roll-Master_DiceGame.git
2. Open the project in Android Studio (Hedgehog or newer).

3. Build and run on an Android device or emulator (API 24+).


