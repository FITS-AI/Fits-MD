# Fits (Food & Intake Tracking Scanner)

<img src="https://raw.githubusercontent.com/nudriin/Fits/refs/heads/main/fits-dark.png" align="center">

# Mobile Development Setup

This guide will help you set up your mobile development environment for Android using Kotlin and Java.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) installed
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed
- [Android SDK](https://developer.android.com/studio#downloads) installed

## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/FITS-AI/Fits-MD
cd Fits-MD
```

### 2. Open the Project in Android Studio

1. Launch Android Studio.
2. Select **Open an existing Android Studio project**.
3. Navigate to the cloned repository and select it.

### 3. Configure Local Properties

Create a `local.properties` file in the root directory of your project and add the following lines:

```ini
sdk.dir=path_to_your_android_sdk
API_URL="http://your_api_url/"
LLM_API_URL="https://llm_api_url"
GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent/"
GEMINI_API_KEY="your_gemini_api_key"
```

Replace `path_to_your_android_sdk`, `your_maps_api_key`, `your_api_url`, and `your_gemini_api_key` with your actual values.

### 4. Obtain Gemini API Keys

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing project.
3. Navigate to **APIs & Services** > **Credentials**.
4. Click **Create credentials** and select **API key**.
5. Enable the **Generative Language API** in the **Library** section.
6. Copy the generated API key and add it to your `local.properties` file as `GEMINI_API_KEY`.

#### API Keys

Ensure your API keys are kept secure and not checked into version control. Use environment variables or a secure vault for production keys.

### 5. Build the Project

1. Open the `build.gradle.kts` file and ensure all dependencies are correctly set up.
2. Sync the project with Gradle files by clicking **Sync Now** in the notification bar.

### 6. Run the Project

1. Connect an Android device or start an emulator.
2. Click the **Run** button in Android Studio or press `Shift + F10`.
