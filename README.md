# Fits Mobile App: Development Environment Setup

<img src="https://raw.githubusercontent.com/nudriin/Fits/refs/heads/main/fits-dark.png" align="center">

## Project Introduction
This guide walks you through setting up the development environment for the Fits mobile application, which uses Android with Kotlin and Java technologies.

## System Requirements
Before diving in, make sure you have:
- Latest version of [Android Studio](https://developer.android.com/studio)
- [Java Development Kit](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Android SDK](https://developer.android.com/studio#downloads)
- Git version control system

## Installation Steps

### Repository Setup
Start by cloning the project repository:
```bash
git clone https://github.com/FITS-AI/Fits-MD
cd Fits-MD
```

### Project Configuration

#### Project Initialization
1. Open Android Studio
2. Choose **Open Existing Project**
3. Select the cloned Fits-MD directory

#### Environment Configuration
Create a `local.properties` file in the project root with these placeholders:

```ini
# Android SDK location
sdk.dir=/absolute/path/to/android/sdk

# Backend API configurations
API_URL="http://your_backend_endpoint/"
LLM_API_URL="https://your_language_model_api/"
GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent/"
GEMINI_API_KEY="your_generative_ai_key"
```

### API Key Acquisition

#### Gemini API Setup
1. Access [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Navigate to **Credentials** section
4. Generate a new API key
5. Enable Generative Language API
6. Copy and securely store your key

### Dependency Management
1. Open `build.gradle.kts`
2. Verify dependency configurations
3. Trigger Gradle synchronization

### Application Launch
- Connect an Android device or launch an emulator
- Use Android Studio's **Run** button or `Shift + F10`

## Security Recommendations
- Avoid committing sensitive credentials to version control
- Use environment variable management
- Implement key rotation strategies
- Minimize API key permissions

## Potential Troubleshooting
- Cache invalidation
- Rebuild project
- Update development tools
- Check network configurations

## Helpful Resources
- [Android Official Documentation](https://developer.android.com/docs)
- [Kotlin Language Guide](https://kotlinlang.org/docs/home.html)
- [Gradle Build System](https://gradle.org/documentation/)
