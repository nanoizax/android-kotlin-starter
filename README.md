# Android Kotlin Starter

A production-ready Android starter template built with **Clean Architecture**, **Kotlin**, and the modern Android stack.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material3 |
| Architecture | Clean Architecture (Data / Domain / Presentation) |
| DI | Hilt 2.52 |
| Async | Coroutines + Flow |
| Networking | Retrofit 2.11 + OkHttp 4 + Moshi |
| Local DB | Room 2.6 |
| Preferences | DataStore Preferences |
| State | ViewModel + StateFlow |
| Navigation | Navigation Compose |
| Testing | MockK + Turbine + Coroutines Test |

## Architecture

```
app/
└── src/main/java/com/sonholab/androidstarter/
    ├── core/
    │   ├── data/local/       → AppDatabase (Room)
    │   ├── di/               → Hilt modules (Network, Database, Repository)
    │   ├── domain/           → Result<T> sealed class
    │   └── ui/theme/         → Compose theme, colors, typography
    └── features/
        ├── auth/
        │   ├── data/         → AuthRepositoryImpl, AuthApi, TokenDataStore
        │   ├── domain/       → AuthRepository interface, LoginUseCase, LogoutUseCase
        │   └── presentation/ → LoginViewModel, LoginScreen
        └── users/
            ├── data/         → UsersRepositoryImpl, UsersApi, UserDao, UserEntity
            ├── domain/       → UsersRepository interface, GetUsersUseCase
            └── presentation/ → UsersViewModel, UsersScreen
```

## Getting Started

1. Clone the repository
2. Open in Android Studio Koala or newer
3. Update `BASE_URL` in `app/build.gradle.kts` to point to your API
4. Run on an emulator or physical device (API 24+)

## Build Variants

| Variant | Base URL |
|---------|----------|
| `debug` | `https://dev-api.example.com/v1/` |
| `release` | `https://api.example.com/v1/` |

## Key Patterns

### Result<T>

All use cases return `Result<T>` — a sealed class with `Success`, `Error`, and `Loading` states:

```kotlin
when (val result = loginUseCase(email, password)) {
    is Result.Success -> navigateToHome(result.data)
    is Result.Error   -> showError(result.exception.message)
    is Result.Loading -> showProgress()
}
```

### Local-first (Users feature)

1. Emit cached data from Room immediately
2. Fetch fresh data from Retrofit in background
3. Save to Room → Room emits updated data automatically via Flow

### Auth token storage

Tokens are stored securely in `DataStore<Preferences>` (not SharedPreferences).
The `OkHttpClient` reads the token via an auth interceptor on every request.

## Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## Requirements

- Android Studio Koala (2024.1.1) or newer
- JDK 17
- Android SDK 35
- Minimum device: Android 7.0 (API 24)

## Author

**Leandro Perez** — [contacto@sonholab.com](mailto:contacto@sonholab.com)
[SonhoLab](https://sonholab.com)

## License

```
Copyright 2024 Leandro Perez / SonhoLab

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
