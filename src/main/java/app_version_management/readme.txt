# 📱 App-Version Management System (LLD)

This repository contains a **Low-Level Design (LLD)** implementation of an in-memory App Version Management System for mobile applications, inspired by platforms like PhonePe.

## 📌 Problem Overview

Smartphone apps often go through several lifecycle events — from **fresh installs** to **incremental updates**. In the absence of app stores like Play Store or App Store, app developers must directly manage installations and updates on user devices.

This system simulates such a scenario, assuming:
- Direct interaction between app developers and mobile devices.
- No app store is involved.
- Apps can be installed or updated via an online installer or backend service.

---

## ✅ Real-World Use Cases

### Install
A user gets a new mobile phone and visits the PhonePe website to install the latest app version directly.

### Update
An already installed app needs to be updated with a new feature (e.g., dark mode). The app backend pushes an update (as a diff patch) to the device.

---

## 🧩 System Components

### App & App Versions
- Each app maintains a list of versions.
- Each version includes metadata such as:
  - Version identifier
  - Minimum supported OS version
  - Platform (Android/iOS)
  - Binary file (byte-stream)

### Device
Each device includes:
- Unique Device ID
- OS type and version

### Rollouts
Rollout types supported:
- **Install**: Fresh install of the app on the device.
- **Update**: Patch-based upgrade using binary diffs between versions.

#### Rollout Strategies
- **Beta rollout**: Targeted to specific devices.
- **Percentage rollout**: Gradual rollout to a percentage of users.

---

## 🛠 Functional Requirements

The system should implement the following core functionalities:

- `uploadNewVersion(...)`
  Store a new version and associated file stream.

- `createUpdatePatch(app, fromVersion, toVersion)`
  Generate a binary diff between two versions.

- `releaseVersion(...)`
  Deploy the version using the desired rollout strategy.

- `isAppVersionSupported(...)`
  Check if a specific version supports a given device.

- `checkForInstall(...)`
  Determine whether a device is compatible with the app.

- `checkForUpdates(...)`
  Check if a newer version is available for a specific device.

- `executeTask(...)`
  Either install or update the app on a device.

---

## ⚙️ Assumed Capabilities (Provided APIs)

You may assume the following methods are available:

- `installApp(fileContent)`
- `updateApp(diffPack)`
- `createDiffPack(sourceFile, targetFile)`
- `uploadFile(fileContent) → fileUrl`
- `getFile(fileUrl) → fileContent`

These help abstract away file handling and installation logic.

---

## 🧪 Implementation Expectations

- **Everything is in-memory** (no persistence layer or database).
- Use **OOP/OOD principles** to design maintainable, readable code.
- Choose **any programming language**.
- Avoid REST APIs, focus on **core logic**.
- Prefer a **TDD mindset**: design should allow for easy testing.

---

## 🧠 Evaluation Criteria

- ✅ Working solution
- 📖 Code readability and structure
- 🧱 Proper OOP design with separation of concerns
- 🧪 Testability (without full test cases)
- 💻 Demonstration of language proficiency

---

## 📂 Getting Started

1. Clone the repo.
2. Build your solution incrementally.
3. Focus on completing core functional requirements first.
4. Add rollout strategies and patching logic.
5. Use helper classes or interfaces to encapsulate device and app behavior.

---

## 💬 Note

This is designed for machine coding rounds. Aim for simplicity, clarity, and correctness.