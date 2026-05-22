# APM Foundation Quiz

A modern, fully offline Android app that tests your knowledge of the
**Association for Project Management (APM) Foundation** level course.

Every question, hint, answer and explanation is drawn **strictly** from the
APM Foundation precourse learning material — nothing else.

---

## Install it on your Android phone

You install this app from an **APK file** — the standard Android installer
file. You do not need any technical knowledge.

### Step by step

1. On your phone, open this link:
   **https://github.com/chaeljc/apmlearningapp/releases/latest**
2. Under **Assets**, tap **`APM-Foundation-Quiz.apk`** to download it.
3. Open the downloaded file — tap the download notification, or find it in
   your **Downloads** folder and tap it.
4. The first time, Android asks permission to install apps from this source.
   Tap **Settings**, turn the switch **on**, then tap **back**.
5. Tap **Install**, then **Open**. Done.

That's it — the app now lives on your home screen and works completely
offline, with no account and no internet needed.

> **Where does the APK come from?** Every time the code changes, GitHub
> automatically builds the app and publishes a fresh `APM-Foundation-Quiz.apk`
> on the [Releases page](https://github.com/chaeljc/apmlearningapp/releases).
> To update later, just install the newest one over the top.

---

## What the app does

- **Three quiz lengths** — Short (10), Medium (30) and Large (100) questions.
- **Practice by topic** — drill any one of the 14 syllabus topics on its own.
- **One question at a time**, each with five options **A–E**.
- **Give me a hint** — a useful nudge that doesn't give the answer away.
- **Tell me, I don't know** — reveals the answer with a full explanation.
- **Right answers are confirmed** with a short expansion on the topic.
- **Every answer is justified** with the exact wording quoted from the
  course material.
- **Live score and percentage**, shown at the end or whenever you quit.
- **Shuffled and balanced** — questions are randomised every session, and the
  correct answer is spread evenly across A–E (and audited on the results
  screen).

The question bank holds **335 questions** across the whole syllabus, so even a
100-question quiz feels fresh each time.

---

## For developers

Native Android app — **Kotlin**, **Jetpack Compose** and **Material 3**.

| Path | Purpose |
|------|---------|
| `app/src/main/assets/questions.json` | The 335-question bank |
| `app/src/main/java/.../data/` | Models, bank loader, quiz engine |
| `app/src/main/java/.../ui/` | Compose screens and theme |
| `.github/workflows/build-apk.yml` | Builds and publishes the APK |

### Build locally

```bash
./gradlew assembleRelease
```

The signed APK is written to `app/build/outputs/apk/release/`.
Requires JDK 17 and the Android SDK.

### Answer-letter balancing

The course material mandates that correct answers are randomised but balanced
across A–E. `QuizEngine` keeps a per-letter counter, always places the next
correct answer in a lowest-count letter (random tie-break), never uses **B**
for the first question, and never lets the gap between the most- and
least-used letter exceed 1.
