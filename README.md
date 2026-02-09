#  RiderFit - Mobile App

Aplicación móvil Android de autenticación y catálogo de productos desarrollada con **Kotlin**, **Jetpack Compose** y **Firebase**.

##  Descripción

**RiderFit** es una aplicación que permite a los usuarios:
-  Registrarse e iniciar sesión con Firebase Authentication
-  Ver un catálogo público de productos
-  Acceder a su perfil tras autenticarse
-  Gestionar su información de usuario
-  Agregar productos al carrito

La arquitectura sigue **Clean Architecture** + **MVVM** para máxima escalabilidad y mantenibilidad.

---

##  Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| **UI** | Jetpack Compose 1.6.0 |
| **Arquitectura** | Clean Architecture + MVVM |
| **Inyección de Dependencias** | Hilt 2.57.1 |
| **Autenticación** | Firebase Authentication 23.2.1 |
| **Base de Datos (Remota)** | Firebase Firestore 25.1.4 |
| **Base de Datos (Local)** | Room 2.8.4 |
| **Preferencias** | DataStore 1.2.0 |
| **Lenguaje** | Kotlin 2.0 |
| **JVM Target** | Java 11 |
| **API Mínima** | Android 7 (API 24) |
| **API Destino** | Android 16 (API 36) |
| **Testing** | JUnit 4, MockK, Espresso, Truth |
| **Cobertura** | JaCoCo 0.8.10 |

---

##  Requisitos Previos

### Software Requerido
- **Android Studio** (última versión estable)
- **JDK 11** o superior
- **Gradle 8.x**
- **Google Play Services** (para Firebase)

### Cuentas Requeridas
- Cuenta de **Firebase** (crear en [console.firebase.google.com](https://console.firebase.google.com))
- Cuenta de **Google Cloud** (opcional, para configuración avanzada)

### Dispositivo o Emulador
- Dispositivo Android con API 24+
- O emulador Android configurado

---

##  Instalación y Setup

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-organizacion/S02-26-Equipo-72-Mobile-Development.git
cd S02-26-Equipo-72-Mobile-Development
```

### 2. Configurar Firebase

#### Paso 1: Crear Proyecto en Firebase Console
1. Ir a [Firebase Console](https://console.firebase.google.com)
2. Click en "Crear Proyecto"
3. Seguir los pasos del asistente

#### Paso 2: Descargar google-services.json
1. En Firebase Console, ir a **Configuración del Proyecto** → **General**
2. En **Tus apps**, seleccionar la app Android
3. Descargar `google-services.json`
4. Copiar el archivo a `app/` (ya incluido en .gitignore)

#### Paso 3: Habilitar Métodos de Autenticación
1. En Firebase Console, ir a **Autenticación** → **Proveedores de servicios**
2. Habilitar **Email/Contraseña**

#### Paso 4: Crear Colección en Firestore
1. En Firebase Console, ir a **Firestore Database**
2. Crear colección `users` con documentos de estructura:
```json
{
  "id": "user_id",
  "email": "user@example.com",
  "displayName": "User Name",
  "profilePictureUrl": "https://...",
  "createdAt": 1707500000000,
  "updatedAt": 1707500000000
}
```

### 3. Abrir en Android Studio
```bash
# Opción 1: Desde terminal
open -a "Android Studio" .

# Opción 2: Manual
# File → Open → Seleccionar la carpeta del proyecto
```

### 4. Compilar el Proyecto
```bash
./gradlew clean build
```

### 5. Ejecutar la Aplicación
```bash
# En emulador
./gradlew installDebug

# O desde Android Studio: Run → Run 'app'
```

---

##  Estructura del Proyecto

```
app/src/main/java/com/store/riderfit/
│
├── di/                              # Inyección de Dependencias (Hilt)
│   ├── AppModule.kt                # Bindings globales
│   └── UseCaseModule.kt            # Bindings de use cases
│
├── data/                           # Data Layer
│   ├── remote/firebase/
│   │   ├── FirebaseAuthService.kt
│   │   ├── FirebaseUserService.kt
│   │   └── FirestoreService.kt
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── entity/UserEntity.kt
│   │   │   └── dao/UserDao.kt
│   │   └── preferences/
│   │       └── UserPreferences.kt
│   ├── repository/
│   │   ├── AuthRepositoryImpl.kt
│   │   ├── UserRepositoryImpl.kt
│   │   └── ProductRepositoryImpl.kt
│   └── model/
│       ├── UserDto.kt
│       ├── ProductDto.kt
│       └── AuthState.kt
│
├── domain/                         # Domain Layer
│   ├── repository/
│   │   ├── IAuthRepository.kt
│   │   ├── IUserRepository.kt
│   │   └── IProductRepository.kt
│   ├── model/
│   │   ├── User.kt
│   │   ├── Product.kt
│   │   └── AuthResult.kt
│   └── usecase/
│       ├── auth/
│       │   ├── LoginUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── GetCurrentUserUseCase.kt
│       ├── user/
│       │   └── GetUserProfileUseCase.kt
│       └── product/
│           └── GetProductsUseCase.kt
│
├── presentation/                   # Presentation Layer
│   ├── viewmodel/
│   │   ├── AuthViewModel.kt
│   │   ├── HomeViewModel.kt
│   │   └── ProfileViewModel.kt
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── auth/
│   │   │   │   ├── SplashScreen.kt
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   └── RegisterScreen.kt
│   │   │   ├── public/
│   │   │   │   └── HomeScreen.kt
│   │   │   └── protected/
│   │   │       └── ProfileScreen.kt
│   │   ├── components/
│   │   │   ├── auth/
│   │   │   │   ├── EmailField.kt
│   │   │   │   ├── PasswordField.kt
│   │   │   │   └── AuthButton.kt
│   │   │   ├── common/
│   │   │   │   ├── Loading.kt
│   │   │   │   ├── ErrorDialog.kt
│   │   │   │   └── SuccessSnackbar.kt
│   │   │   └── products/
│   │   │       ├── ProductCard.kt
│   │   │       └── ProductGrid.kt
│   │   ├── navigation/
│   │   │   ├── Route.kt
│   │   │   └── NavGraph.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Typography.kt
│   │       ├── Theme.kt
│   │       └── Spacing.kt
│   └── state/
│       ├── UiState.kt
│       ├── AuthUiState.kt
│       └── ProductUiState.kt
│
└── utils/                          # Utilidades
    ├── Constants.kt
    └── Extensions.kt

app/src/test/                       # Unit Tests
app/src/androidTest/                # Integration Tests
```

---

##  Progreso del Proyecto

| Fase | Descripción | Estado | Progreso |
|------|-----------|--------|---------|
| 1 | Configuración Base |  | 100% |
| 2 | Data Layer |  | 100% |
| 3 | Domain Layer |  | 100% |
| 4 | DI & State |  | 100% |
| 5 | UI Components |  | 100% |
| 6 | Screens Auth |  | 100% |
| 7 | Screens Main |  | 100% |
| 8 | Navigation |  | 100% |
| 9 | Tema & Utils |  | 100% |
| 10 | Testing |  | 0% |

**Total: 90% completado** 

---

##  Flujo de Usuarios

```
APP INICIA
    ↓
[SplashScreen]
    ↓
¿Usuario autenticado?
    ├─ No → [LoginScreen] → [RegisterScreen] → Login
    └─ Sí → [HomeScreen] (Público)
                ↓
        ¿Ir a Perfil?
            └─ Sí → [ProfileScreen] (Protegido)
                    ↓
            ¿Cerrar sesión?
                └─ Sí → Volver a [LoginScreen]
```

---

##  Seguridad

### Autenticación
- Firebase Authentication con email/contraseña
- Sesiones persistidas en DataStore
- Tokens gestionados automáticamente por Firebase SDK

### Base de Datos Local
- Encriptación de Room (disponible en Room 2.6+)
- DataStore con encriptación opcional

### Firestore Rules (Recomendado)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Solo usuarios autenticados pueden acceder
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    // Productos públicos (lectura)
    match /products/{document=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

---

##  Testing

### Ejecutar Pruebas Unitarias
```bash
./gradlew test
```

### Ejecutar Pruebas de Integración
```bash
./gradlew connectedAndroidTest
```

### Generar Reporte de Cobertura (JaCoCo)
```bash
./gradlew jacocoTestReport
./gradlew connectedAndroidTest createDebugCoverageReport
```

El reporte se encuentra en:
```
app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

##  Características Implementadas

### Autenticación 
- [x] Login con email/contraseña
- [x] Registro de nuevos usuarios
- [x] Logout seguro
- [x] Sesión persistente
- [x] Validación de credenciales

### Catálogo 
- [] Listado de productos
- [] Búsqueda y filtrado
- [] Ratings de productos
- [] Carrito (UI lista)

### Perfil de Usuario 
- [] Visualización de datos
- [] Edición de perfil
- [] Foto de perfil (URL)
- [] Información de cuenta

### Arquitectura 
- [x] Clean Architecture
- [x] MVVM Pattern
- [x] Dependency Injection
- [x] Repository Pattern
- [x] Use Cases

---

##  Debugueo

### Logs en Logcat
```bash
./gradlew installDebug
# En Android Studio → View → Tool Windows → Logcat
# Filtrar por: "RIDERFIT" o "com.store.riderfit"
```

### Breakpoints en Android Studio
1. Hacer click en el número de línea
2. Ejecutar en debug: `./gradlew installDebug --debug`
3. O desde Android Studio: Run → Debug 'app'

### Verificar Firebase en Emulador
```bash
# Terminal 1: Iniciar Firebase Emulator Suite
firebase emulators:start

# Terminal 2: Ejecutar la app contra emulador
./gradlew installDebug
# En el código usar: FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
```

---

##  Documentación Adicional

- [CONTRIBUTING.md](./CONTRIBUTING.md) - Guía para colaboradores
- [ARCHITECTURE.md](./ARCHITECTURE.md) - Detalles de arquitectura
- [PLAN_AUTHETICACION.md](./PLAN_AUTHETICACION.md) - Plan de implementación original

---

##  Licencia

Este proyecto es de **Equipo 72 - No Country**. Todos los derechos reservados.

---



**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Autores**: Jorge Galleguillos - Ingeniero de software
