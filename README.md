# 🎓 Learn2Hire — AI-Based Study & Interview Preparation Assistant

[![Release](https://img.shields.io/badge/Release-v1.0.0-22B85A?style=flat&logo=android&logoColor=white)](https://github.com/abhijithshetty12/Learn2Hire/releases)
![Android](https://img.shields.io/badge/Android-API_23+-3DDC84?style=flat&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth_%26_Firestore-DD2C00?style=flat&logo=firebase&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini-AI-8E75B2?style=flat&logo=googlegemini&logoColor=white)

**Learn2Hire** is an AI-powered Android learning and interview preparation application designed to bring structured study, assessment, adaptive practice, AI tutoring, interview preparation, planning, and progress tracking into one unified mobile experience.

Built using **Kotlin, Jetpack Compose, Firebase Authentication, Cloud Firestore, Firebase AI Logic, and Gemini**, Learn2Hire combines deterministic academic assessment with AI-assisted learning. Fixed quizzes remain the authoritative measure of topic performance, while AI Practice, the AI Tutor, and Interview Coach provide personalized training and feedback.

The application currently includes structured learning content for **Machine Learning, Web Application Development, and Automata Theory & Compiler Design**, with persistent user progress and activity stored securely per Firebase-authenticated account.

---

## 🚀 Key Features

* **📚 Structured Study Modules**: Learn through subjects, topics, lesson content, key concepts, estimated study time, and completion tracking.
* **📝 Fixed Quiz Assessments**: Deterministic topic quizzes provide consistent and authoritative learning-performance scores.
* **🧠 Adaptive AI Practice**: Generates topic-aware practice questions with difficulty adapted to the learner's quiz performance.
* **✨ Local Practice Fallback**: Uses authored topic-specific question banks when Gemini is temporarily unavailable or rate-limited.
* **🤖 AI Tutor**: Ask contextual questions about study topics and receive Gemini-powered explanations grounded in the learning material.
* **💬 Persistent Tutor History**: AI Tutor conversations are stored in Firestore and remain available across sessions.
* **🎤 Interview Preparation**: Practice role- and type-based interview questions with deterministic key-point evaluation.
* **💡 AI Interview Coach**: Gemini provides qualitative feedback, improvement suggestions, and stronger example answers.
* **📅 Study Planner**: Create tasks, track completion, and record study sessions.
* **📊 Progress Analytics**: View study completion, quiz performance, AI Practice activity, interview performance, and topic-level learning signals.
* **🎯 Personalized Study Insights**: Home recommendations adapt to the learner's recorded study and assessment activity.
* **🔐 Firebase Authentication**: Supports secure account-based authentication, including Google Sign-In.
* **☁️ Cloud Persistence**: Study progress, quizzes, AI sessions, planner data, profile information, and chat history persist through Firestore.
* **🌗 Theme Support**: Light, Dark, and System theme modes with a custom Learn2Hire visual identity.

---

## 📚 Learning Content

Learn2Hire currently contains **3 subjects and 10 structured topics**:

| Subject | Topics |
| :--- | :--- |
| **Machine Learning** | Introduction to ML, Classification, Regression, Naïve Bayes |
| **Web Application Development** | Client and Server, Node.js and Express, SQL and NoSQL |
| **Automata Theory & Compiler Design** | Finite Automata, DFA and NFA, Regular Expressions |

Each topic contains lesson material, key concepts, estimated study duration, progress tracking, fixed assessment, AI Practice access, and contextual AI Tutor access.

---

## 📝 Assessment vs AI Practice

Learn2Hire deliberately separates **assessment** from **AI-assisted practice**.

| Component | Purpose | Source | Used for Authoritative Learning Level? |
| :--- | :--- | :--- | :---: |
| **Fixed Quiz** | Consistent topic assessment | Authored question bank | ✅ Yes |
| **AI Practice** | Adaptive practice and improvement | Gemini + local fallback | ❌ No |
| **AI Tutor** | Explanation and study assistance | Gemini + study-material fallback | ❌ No |
| **Interview Coach** | Qualitative interview improvement | Gemini | ❌ No |

This prevents generative AI variability from directly determining the learner's official topic-performance score.

---

## 🧠 Adaptive Learning Levels

Quiz performance is used to classify the learner's topic state:

| Quiz Performance | Learning State | AI Practice Level |
| :--- | :--- | :--- |
| **No assessment** | Not Assessed | Standard |
| **Below 60%** | Needs Practice | Foundation |
| **60–79%** | Developing | Intermediate |
| **80% and above** | Strong | Challenge |

AI Practice remains a secondary improvement signal and is displayed separately from fixed quiz assessment in Progress.

---

## 🤖 AI Workflow

```text
                    Study Topic
                        │
          ┌─────────────┼─────────────┐
          ↓             ↓             ↓
     Fixed Quiz     AI Practice    AI Tutor
          │             │             │
          ↓             ↓             ↓
    Deterministic    Gemini AI      Gemini AI
     Assessment          │             │
          │        ┌─────┴─────┐ ┌─────┴─────┐
          │        ↓           ↓ ↓           ↓
          │     Generated   Local Study   AI Response
          │     Questions   Fallback      / Fallback
          │
          ↓
   Authoritative Score
          │
          └─────────────┐
                        ↓
               Learning Profile
                        │
                        ↓
             Adaptive Recommendations
```

Firebase AI Logic is used to access Gemini without embedding a raw Gemini API key directly in the Android source code.

---

## 🎤 Interview Preparation Workflow

```text
Select Role & Interview Type
            ↓
     Interview Questions
            ↓
       User Response
            ↓
  Deterministic Key-Point Check
            ↓
      Coverage Score
            ↓
       Gemini AI Coach
            ↓
What Went Well / Improvements
      / Improved Answer
            ↓
   Firestore Interview Session
```

The deterministic key-point evaluation remains the measurable interview score, while Gemini provides qualitative coaching.

---

## ☁️ Firebase Data Model

Each authenticated user owns their learning data under their Firebase UID.

```text
users/{uid}
│
├── name
├── email
├── photoUrl
├── course
├── semester
├── authProvider
├── createdAt
│
├── studyProgress/{topicId}
├── quizAttempts/{attemptId}
├── aiPracticeSessions/{sessionId}
├── tutorChats/{chatId}
├── interviewSessions/{sessionId}
├── plannerTasks/{taskId}
└── plannerSessions/{sessionId}
```

Firestore security rules restrict user data access to the authenticated owner of that user document.

---

## 🔄 Application Workflow

```text
Authentication
      ↓
     Home
      │
      ├───────────────┬─────────────────┐
      ↓               ↓                 ↓
    Study          Interview          Planner
      │               Prep               │
      ↓                                  ↓
 Subject → Topic                      Tasks &
      │                            Study Sessions
      ├──────────────┐
      ↓              ↓
 Fixed Quiz      AI Features
      │          ┌────┴────┐
      ↓          ↓         ↓
 Assessment  AI Practice  AI Tutor
      │          │         │
      └──────────┼─────────┘
                 ↓
              Progress
                 │
                 ↓
          Personalized Insight
```

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Platform** | Android |
| **Programming Language** | Kotlin |
| **UI** | Jetpack Compose, Material 3 |
| **Architecture** | ViewModel + Repository + Reactive UI State |
| **Navigation** | Jetpack Navigation Compose |
| **Authentication** | Firebase Authentication |
| **Google Sign-In** | Credential Manager + Google Identity |
| **Database** | Cloud Firestore |
| **Generative AI** | Firebase AI Logic + Gemini |
| **Async Operations** | Kotlin Coroutines |
| **Local Preferences** | SharedPreferences |
| **Build System** | Gradle Kotlin DSL |
| **Minimum Android Version** | API 23 |
| **Target SDK** | API 37 |

---

## 📁 Project Structure

```text
Learn2Hire/
│
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   │
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/learn2hire/app/
│       │   │
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── StudyContent.kt
│       │   │   │   ├── SampleQuizData.kt
│       │   │   │   └── SampleInterviewData.kt
│       │   │   │
│       │   │   ├── model/
│       │   │   └── repository/
│       │   │
│       │   ├── ui/
│       │   │   ├── auth/
│       │   │   ├── components/
│       │   │   ├── home/
│       │   │   ├── interview/
│       │   │   ├── navigation/
│       │   │   ├── planner/
│       │   │   ├── practice/
│       │   │   ├── profile/
│       │   │   ├── progress/
│       │   │   ├── quiz/
│       │   │   ├── study/
│       │   │   ├── theme/
│       │   │   └── tutor/
│       │   │
│       │   └── MainActivity.kt
│       │
│       └── res/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🖼️ Interface Preview

The interface gallery below uses the screenshots included in the repository's `screenshots/` directory.

### Authentication

| **Login** | **Register** |
| :---: | :---: |
| <img src="/screenshots/login.png" alt="Learn2Hire Login" /> | <img src="/screenshots/register.png" alt="Learn2Hire Register" /> |

### Home Dashboard

| **Home** | **Home — Continued** |
| :---: | :---: |
| <img src="/screenshots/home.png" alt="Learn2Hire Home Dashboard" /> | <img src="/screenshots/home1.png" alt="Learn2Hire Home Dashboard Continued" /> |

### Learning Experience

| **Study** | **Subject Detail** |
| :---: | :---: |
| <img src="/screenshots/study.png" alt="Study Screen" /> | <img src="/screenshots/subjectdetail.png" alt="Subject Detail Screen" /> |

| **Fixed Quiz** | **AI Practice** |
| :---: | :---: |
| <img src="/screenshots/quiz.png" alt="Fixed Quiz" /> | <img src="/screenshots/aipractice.png" alt="AI Practice" /> |

### AI Tutor

<p align="center">
  <img src="/screenshots/aitutor.png" alt="Learn2Hire AI Tutor" width="45%" />
</p>

### Interview Preparation

| **Interview Setup** | **Interview Practice** |
| :---: | :---: |
| <img src="/screenshots/interview.png" alt="Interview Preparation" /> | <img src="/screenshots/interviewpractice.png" alt="Interview Practice" /> |

### Progress Analytics

| **Progress Overview** | **Topic Performance** |
| :---: | :---: |
| <img src="/screenshots/progress.png" alt="Progress Overview" /> | <img src="/screenshots/progress1.png" alt="Topic Performance" /> |

### Profile

<p align="center">
  <img src="/screenshots/profile.png" alt="Learn2Hire Profile" width="45%" />
</p>

---


## ⚙️ Prerequisites

Before running the project, install:

* **Android Studio**
* **JDK 11 or compatible JDK configured for the project**
* Android SDK required by the project
* A Firebase project configured for the Android package `com.learn2hire.app`
* Firebase Authentication and Cloud Firestore
* Firebase AI Logic with the Gemini Developer API backend configured

---

## ⚙️ Installation

Clone the repository:

```bash
git clone https://github.com/abhijithshetty12/Learn2Hire.git
```

Navigate into the project:

```bash
cd Learn2Hire
```

Open the project in **Android Studio** and allow Gradle synchronization to complete.

Add your Firebase Android configuration file at:

```text
app/google-services.json
```

Do not add release keystores, keystore passwords, or other private signing credentials to the repository.

---

## ▶️ Build the Application

On Windows PowerShell:

```powershell
.\gradlew assembleDebug
```

The debug APK will be generated under:

```text
app/build/outputs/apk/debug/
```

Run Android lint:

```powershell
.\gradlew lintDebug
```

For a release build, configure your own Android signing key and generate a signed APK or Android App Bundle through Android Studio.

---

## 📱 Release APK

Pre-built releases can be published through the repository's **Releases** section:

[![Download Release](https://img.shields.io/badge/Download-Latest_APK-22B85A?style=for-the-badge&logo=android&logoColor=white)](https://github.com/abhijithshetty12/Learn2Hire/releases/latest)

For security, the release signing keystore is **not** included in the repository.

---

## 🔐 Security

Learn2Hire follows several important security practices:

* Firebase user data is isolated by authenticated user ID through Firestore security rules.
* The Gemini API credential is not hard-coded directly into the Android source.
* AI access is implemented through Firebase AI Logic.
* Android release signing keys and passwords must remain outside version control.
* Local machine configuration such as `local.properties` should not be committed.
* Firebase Authentication is used to identify users before accessing personal learning data.

---

## 🧪 Testing & Quality Assurance

The project has been validated through:

```text
Gradle Clean Build       ✓
Debug APK Compilation    ✓
Android Lint             ✓
Runtime Navigation       ✓
Firebase Persistence     ✓
Authentication           ✓
Study Progress           ✓
Fixed Quiz Assessment    ✓
AI Practice              ✓
AI Tutor & Chat History  ✓
Interview Preparation    ✓
Planner                  ✓
Profile & Themes         ✓
Sign-out / Sign-in Data  ✓
```

---

## 🎯 Project Objective

The main objective of Learn2Hire is to create a unified learning and career-preparation platform that combines **structured academic study with responsible AI assistance**.

Instead of allowing generative AI to determine academic performance directly, Learn2Hire maintains deterministic fixed assessments for measurable learning progress while using AI for practice generation, tutoring, explanations, and interview coaching.

The system demonstrates how traditional application logic, cloud persistence, learning analytics, and generative AI can work together in a practical Android application.

---

## ⚠️ Limitations

* The current structured learning library contains three subjects and ten topics.
* Fixed quiz coverage is limited to the learning content currently bundled with the application.
* Gemini responses depend on network connectivity, service availability, and applicable API quotas.
* AI-generated content can vary between requests and should be treated as learning assistance rather than an authoritative academic source.
* Local fallbacks provide continuity when AI generation is unavailable, but they are limited to the authored content bundled with the application.
* Interview evaluation uses predefined key points for deterministic scoring and does not attempt to fully measure real-world interview performance.
* The current application is designed as an academic/project implementation rather than a production-scale learning management system.

---

## 🔮 Future Scope

* Expand the study library with additional engineering and placement subjects.
* Add richer personalized learning paths based on long-term performance.
* Introduce spaced repetition and revision scheduling.
* Add more extensive question banks and assessment modes.
* Support coding interview practice and programming challenges.
* Add resume analysis and role-specific preparation plans.
* Introduce richer progress visualizations and learning streaks.
* Add instructor/admin capabilities for managing learning content.
* Support notifications for planner tasks and revision reminders.
* Add offline-first caching for selected learning content.
* Expand AI grounding and retrieval over larger course materials.
* Publish through Google Play with production-ready monitoring and release infrastructure.

---

## 👨‍💻 Author

**Abhijith Shetty**  
*AI & Machine Learning Student | Android & AI Developer*

> "Building intelligent applications that turn learning into measurable progress and career preparation."

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat&logo=linkedin&logoColor=white)](https://linkedin.com/in/abhijithshetty12)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat&logo=github&logoColor=white)](https://github.com/abhijithshetty12)

---

## 🌟 Show Your Support

If you find **Learn2Hire** interesting or useful, consider giving the project a ⭐ on **GitHub**.

It helps support the project and encourages further development.
