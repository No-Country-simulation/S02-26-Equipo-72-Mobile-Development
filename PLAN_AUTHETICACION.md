# Plan de Acción: Sistema de Autenticación con Firebase

## Visión General

Implementación completa de un sistema de autenticación (Login/Register) con Firebase Authentication, incluyendo una pantalla pública (Catálogo de productos) y una pantalla protegida (Perfil de usuario) que requiere autenticación.

**Stack**: Jetpack Compose + Firebase Authentication + MVVM + Clean Architecture

---

## Tabla de Contenidos

1. [Estructura de Archivos](#estructura-de-archivos)
2. [Modelos de Datos](#modelos-de-datos)
3. [Capas de la Arquitectura](#capas-de-la-arquitectura)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Componentes Reutilizables](#componentes-reutilizables)
6. [Navigation Graph](#navigation-graph)
7. [Plan de Implementación Paso a Paso](#plan-de-implementación-paso-a-paso)

---

## Estructura de Archivos

```
app/src/main/java/com/store/riderfit/
│
├── di/
│   ├── AppModule.kt                    # Bindings globales
│   ├── AuthModule.kt                   # Bindings de autenticación
│   └── DatabaseModule.kt               # Bindings de BD local
│
├── data/
│   ├── remote/
│   │   ├── firebase/
│   │   │   ├── FirebaseAuthService.kt  # Servicio de Auth
│   │   │   ├── FirebaseUserService.kt  # Servicio de usuarios
│   │   │   └── FirestoreService.kt     # Servicio de Firestore
│   │   └── ApiService.kt               # (Futuro: APIs REST)
│   │
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt          # Room Database
│   │   │   └── dao/
│   │   │       └── UserDao.kt
│   │   └── preferences/
│   │       └── UserPreferences.kt      # DataStore para tokens
│   │
│   ├── repository/
│   │   ├── AuthRepository.kt           # Interface
│   │   ├── AuthRepositoryImpl.kt        # Implementación
│   │   ├── UserRepository.kt           # Interface
│   │   ├── UserRepositoryImpl.kt        # Implementación
│   │   ├── ProductRepository.kt        # Interface
│   │   └── ProductRepositoryImpl.kt     # Implementación
│   │
│   └── model/
│       ├── AuthState.kt                # Estados de autenticación
│       ├── UserDto.kt                  # Data Transfer Object
│       └── ProductDto.kt               # DTO de productos
│
├── domain/
│   ├── model/
│   │   ├── User.kt                     # Modelo de dominio
│   │   ├── Product.kt                  # Modelo de producto
│   │   └── AuthResult.kt               # Resultado de auth
│   │
│   ├── repository/
│   │   ├── IAuthRepository.kt          # Interfaz auth
│   │   ├── IUserRepository.kt          # Interfaz usuarios
│   │   └── IProductRepository.kt       # Interfaz productos
│   │
│   └── usecase/
│       ├── auth/
│       │   ├── LoginUseCase.kt
│       │   ├── RegisterUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── GetCurrentUserUseCase.kt
│       ├── user/
│       │   ├── UpdateProfileUseCase.kt
│       │   └── GetUserProfileUseCase.kt
│       └── product/
│           └── GetProductsUseCase.kt
│
├── presentation/
│   ├── viewmodel/
│   │   ├── AuthViewModel.kt            # ViewModel de autenticación
│   │   ├── HomeViewModel.kt            # ViewModel de catálogo
│   │   └── ProfileViewModel.kt         # ViewModel de perfil
│   │
│   ├── ui/
│   │   ├── screens/
│   │   │   ├── auth/
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   ├── RegisterScreen.kt
│   │   │   │   └── SplashScreen.kt
│   │   │   ├── public/
│   │   │   │   └── HomeScreen.kt       # Catálogo público
│   │   │   └── protected/
│   │   │       └── ProfileScreen.kt    # Perfil (requiere login)
│   │   │
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
│   │   │
│   │   ├── navigation/
│   │   │   ├── NavGraph.kt             # Grafo de navegación
│   │   │   ├── Route.kt                # Rutas
│   │   │   └── NavigationState.kt      # Estado de navegación
│   │   │
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Typography.kt
│   │       ├── Theme.kt
│   │       └── Spacing.kt
│   │
│   └── state/
│       ├── UiState.kt                  # Estados genéricos UI
│       ├── AuthUiState.kt              # Estado UI autenticación
│       └── ProductUiState.kt           # Estado UI productos
│
└── utils/
    ├── Constants.kt                    # Constantes
    ├── Extensions.kt                   # Extensiones
    └── validators/
        ├── EmailValidator.kt
        └── PasswordValidator.kt
```

---

## Modelos de Datos

### Domain Models (Lógica pura)

```kotlin
// User.kt
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAt: Long,
    val updatedAt: Long
)

// Product.kt
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val inStock: Boolean,
    val rating: Double
)

// AuthResult.kt
sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val message: String) : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
}
```

### Data Models (DTOs para Firebase)

```kotlin
// UserDto.kt
@Serializable
data class UserDto(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

// Mapeos
fun UserDto.toDomain() = User(...)
fun User.toDto() = UserDto(...)
```

### Auth States (Estados de UI)

```kotlin
// AuthState.kt
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object SignedOut : AuthState()
}
```

---

## Capas de la Arquitectura

### 1. Data Layer

#### Remote (Firebase)
- **FirebaseAuthService**: Signup, login, logout, reset password
- **FirebaseUserService**: CRUD de usuarios
- **FirestoreService**: Operaciones con Firestore

#### Local (Room + DataStore)
- **UserDao**: Persistencia local de usuario
- **UserPreferences**: Almacenar token/sesión

#### Repository
- Combina remote + local
- Implementa interfaces de domain
- Maneja caché y sincronización

### 2. Domain Layer

#### Use Cases
- **LoginUseCase**: Valida credenciales + llama a repo
- **RegisterUseCase**: Valida datos + crea usuario
- **LogoutUseCase**: Limpia sesión local y remota
- **GetCurrentUserUseCase**: Obtiene usuario actual
- **GetProductsUseCase**: Obtiene catálogo

### 3. Presentation Layer

#### ViewModels
- **AuthViewModel**: Maneja login, register, estado auth
- **HomeViewModel**: Obtiene productos, filtrado
- **ProfileViewModel**: Datos del usuario, edición

#### Screens
- **SplashScreen**: Verifica sesión existente
- **LoginScreen**: Formulario de login
- **RegisterScreen**: Formulario de registro
- **HomeScreen**: Catálogo público
- **ProfileScreen**: Perfil (protegido)

#### Components
- Inputs reutilizables
- Diálogos de error
- Indicadores de carga

---

## Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────────┐
│                    APP LAUNCH (SplashScreen)                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                  ┌──────────────────────┐
                  │ ¿Usuario autenticado?│
                  │ (SharedPrefs/Token)  │
                  └──────────────────────┘
                       ▼             ▼
                    SÍ              NO
                     │               │
        ┌────────────┘               └──────────────┐
        ▼                                           ▼
   HomeScreen              ┌──────────────────────────────┐
   (Catálogo público)      │  LoginScreen / RegisterScreen│
        │                  │  (Selecciona acción)         │
        │                  └──────────────────────────────┘
        │                        ▼
        │                  ┌─────────────────────┐
        │                  │ Envía a Firebase    │
        │                  │ createUserWithEmail │
        │                  └─────────────────────┘
        │                        ▼
        │                  ┌─────────────────────┐
        │                  │ ¿Éxito?             │
        │                  └─────────────────────┘
        │                   ▼             ▼
        │                SÍ              NO
        │                 │               │
        │    ┌────────────┘               └──────────────┐
        │    │                                           ▼
        │    ▼                                    ErrorDialog
        │ Guardar datos                          (Mostrar error)
        │ (SharedPrefs/Room)                              │
        │    │                                           │
        │    └──────────────────────────┬────────────────┘
        │                               ▼
        │                         Vuelve a LoginScreen
        │                               │
        └───────────────┬───────────────┘
                        ▼
                  HomeScreen
              (Usuario autenticado)
                        │
         ┌──────────────┴──────────────┐
         ▼                             ▼
    Ver Catálogo              Ir a Perfil
    (Public)                  (Protected)
         │                             │
         └──────────────┬──────────────┘
                        ▼
                  LogOut → LoginScreen
```

---

## Componentes Reutilizables

### Campos de Entrada
```kotlin
// EmailField.kt
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
)

// PasswordField.kt
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = true,
    isError: Boolean = false
)
```

### Componentes Comunes
```kotlin
// Loading.kt - Indicador de carga
// ErrorDialog.kt - Diálogo de error
// SuccessSnackbar.kt - Snackbar de éxito
// AppButton.kt - Botón estándar
```

### Componentes de Producto
```kotlin
// ProductCard.kt - Card individual de producto
// ProductGrid.kt - Grid de productos
```

---

## Navigation Graph

```kotlin
// Route.kt
sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object Profile : Route("profile")
    
    object AuthGraph : Route("auth_graph")
    object MainGraph : Route("main_graph")
}

// NavGraph.kt
@Composable
fun RiderFitNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) { SplashScreen(navController) }
        
        navigation(
            route = Route.AuthGraph.route,
            startDestination = Route.Login.route
        ) {
            composable(Route.Login.route) { LoginScreen(navController) }
            composable(Route.Register.route) { RegisterScreen(navController) }
        }
        
        navigation(
            route = Route.MainGraph.route,
            startDestination = Route.Home.route
        ) {
            composable(Route.Home.route) { HomeScreen(navController) }
            composable(Route.Profile.route) { ProfileScreen(navController) }
        }
    }
}
```

---

## Plan de Implementación Paso a Paso

### FASE 1: Configuración Base (Pasos 1-3)

#### Paso 1: Configurar Firebase
- [x] Crear proyecto en Firebase Console
- [x] Descargar `google-services.json`
- [x] Agregar dependencias de Firebase en `build.gradle.kts`
- [x] Inicializar Firebase en la app

**Archivos a crear/modificar**:
- [x] `build.gradle.kts` (dependencias) - ✅ Firebase Analytics y Auth añadidos
- [x] `google-services.json` (configuración) - ✅ Descargado y configurado
- [x] `RiderFitApp.kt` (Application class) - ✅ Firebase inicializado
- [x] `AndroidManifest.xml` - ✅ Aplicación registrada
- [x] AGP actualizado a 8.9.1
- [x] compileSdk y targetSdk actualizado a 36

#### Paso 2: Crear Modelos de Dominio
- [x] `domain/model/User.kt` - Modelo de usuario
- [x] `domain/model/Product.kt` - Modelo de producto
- [x] `domain/model/AuthResult.kt` - Resultado genérico

**Archivos a crear**:
- [x] `domain/model/User.kt` - ✅ Creado
- [x] `domain/model/Product.kt` - ✅ Creado
- [x] `domain/model/AuthResult.kt` - ✅ Creado

#### Paso 3: Crear DTOs y Mapeos
- [ ] `data/model/UserDto.kt` - DTO + mapeos a/desde domain
- [ ] `data/model/ProductDto.kt` - DTO + mapeos
- [ ] `data/model/AuthState.kt` - Estados de autenticación

**Archivos a crear**:
- `data/model/UserDto.kt`
- `data/model/ProductDto.kt`
- `data/model/AuthState.kt`

---

### FASE 2: Data Layer - Firebase (Pasos 4-6)

#### Paso 4: Servicios de Firebase
- [ ] `data/remote/firebase/FirebaseAuthService.kt`
  - `signUp(email, password): Flow<AuthResult<User>>`
  - `login(email, password): Flow<AuthResult<User>>`
  - `logout(): Flow<AuthResult<Unit>>`
  - `getCurrentUser(): Flow<User?>`
  
- [ ] `data/remote/firebase/FirebaseUserService.kt`
  - `saveUserProfile(user: User): Flow<AuthResult<Unit>>`
  - `getUserProfile(userId: String): Flow<AuthResult<User>>`
  
- [ ] `data/remote/firebase/FirestoreService.kt`
  - `getProducts(): Flow<AuthResult<List<Product>>>`

**Archivos a crear**:
- `data/remote/firebase/FirebaseAuthService.kt`
- `data/remote/firebase/FirebaseUserService.kt`
- `data/remote/firebase/FirestoreService.kt`

#### Paso 5: Persistencia Local (Room)
- [ ] `data/local/database/AppDatabase.kt` - Configuración Room
- [ ] `data/local/database/dao/UserDao.kt` - Operaciones de usuario
- [ ] `data/local/preferences/UserPreferences.kt` - DataStore para tokens

**Archivos a crear**:
- `data/local/database/AppDatabase.kt`
- `data/local/database/dao/UserDao.kt`
- `data/local/preferences/UserPreferences.kt`

#### Paso 6: Repositorios (Implementación)
- [ ] `data/repository/AuthRepositoryImpl.kt`
  - Combina Firebase + Local storage
  - Caché de usuario actual
  
- [ ] `data/repository/UserRepositoryImpl.kt`
  - Maneja perfil de usuario
  
- [ ] `data/repository/ProductRepositoryImpl.kt`
  - Obtiene y cachea productos

**Archivos a crear**:
- `data/repository/AuthRepositoryImpl.kt`
- `data/repository/UserRepositoryImpl.kt`
- `data/repository/ProductRepositoryImpl.kt`

---

### FASE 3: Domain Layer - Use Cases (Pasos 7-8)

#### Paso 7: Interfaces de Repositorio
- [ ] `domain/repository/IAuthRepository.kt`
- [ ] `domain/repository/IUserRepository.kt`
- [ ] `domain/repository/IProductRepository.kt`

**Archivos a crear**:
- `domain/repository/IAuthRepository.kt`
- `domain/repository/IUserRepository.kt`
- `domain/repository/IProductRepository.kt`

#### Paso 8: Use Cases
- [ ] `domain/usecase/auth/LoginUseCase.kt`
  - Input: email, password
  - Output: Flow<AuthResult<User>>
  
- [ ] `domain/usecase/auth/RegisterUseCase.kt`
  - Input: email, password, displayName
  - Output: Flow<AuthResult<User>>
  
- [ ] `domain/usecase/auth/LogoutUseCase.kt`
  - Output: Flow<AuthResult<Unit>>
  
- [ ] `domain/usecase/auth/GetCurrentUserUseCase.kt`
  - Output: Flow<User?>
  
- [ ] `domain/usecase/user/GetUserProfileUseCase.kt`
  
- [ ] `domain/usecase/product/GetProductsUseCase.kt`

**Archivos a crear**:
- `domain/usecase/auth/LoginUseCase.kt`
- `domain/usecase/auth/RegisterUseCase.kt`
- `domain/usecase/auth/LogoutUseCase.kt`
- `domain/usecase/auth/GetCurrentUserUseCase.kt`
- `domain/usecase/user/GetUserProfileUseCase.kt`
- `domain/usecase/product/GetProductsUseCase.kt`

---

### FASE 4: Inyección de Dependencias (Paso 9)

#### Paso 9: Módulos Hilt
- [ ] `di/AppModule.kt` - Bindings globales
- [ ] `di/AuthModule.kt` - Bindings de autenticación
- [ ] `di/DatabaseModule.kt` - Bindings de BD

**Archivos a crear/modificar**:
- `di/AppModule.kt`
- `di/AuthModule.kt`
- `di/DatabaseModule.kt`

---

### FASE 5: Presentation Layer - ViewModels (Pasos 10-11)

#### Paso 10: Estados de UI
- [ ] `presentation/state/UiState.kt` - Estado genérico
- [ ] `presentation/state/AuthUiState.kt` - Estado de auth
- [ ] `presentation/state/ProductUiState.kt` - Estado de productos

**Archivos a crear**:
- `presentation/state/UiState.kt`
- `presentation/state/AuthUiState.kt`
- `presentation/state/ProductUiState.kt`

#### Paso 11: ViewModels
- [ ] `presentation/viewmodel/AuthViewModel.kt`
  - State: email, password, authState, error
  - Methods: login(), register(), logout()
  
- [ ] `presentation/viewmodel/HomeViewModel.kt`
  - State: productos, estado carga
  - Methods: cargarProductos()
  
- [ ] `presentation/viewmodel/ProfileViewModel.kt`
  - State: datos usuario, estado edición
  - Methods: cargarPerfil(), actualizarPerfil()

**Archivos a crear**:
- `presentation/viewmodel/AuthViewModel.kt`
- `presentation/viewmodel/HomeViewModel.kt`
- `presentation/viewmodel/ProfileViewModel.kt`

---

### FASE 6: Presentation Layer - UI (Pasos 12-16)

#### Paso 12: Componentes Reutilizables - Auth
- [ ] `presentation/ui/components/auth/EmailField.kt`
- [ ] `presentation/ui/components/auth/PasswordField.kt`
- [ ] `presentation/ui/components/auth/AuthButton.kt`

**Archivos a crear**:
- `presentation/ui/components/auth/EmailField.kt`
- `presentation/ui/components/auth/PasswordField.kt`
- `presentation/ui/components/auth/AuthButton.kt`

#### Paso 13: Componentes Reutilizables - Comunes
- [ ] `presentation/ui/components/common/Loading.kt`
- [ ] `presentation/ui/components/common/ErrorDialog.kt`
- [ ] `presentation/ui/components/common/SuccessSnackbar.kt`

**Archivos a crear**:
- `presentation/ui/components/common/Loading.kt`
- `presentation/ui/components/common/ErrorDialog.kt`
- `presentation/ui/components/common/SuccessSnackbar.kt`

#### Paso 14: Componentes de Producto
- [ ] `presentation/ui/components/products/ProductCard.kt`
- [ ] `presentation/ui/components/products/ProductGrid.kt`

**Archivos a crear**:
- `presentation/ui/components/products/ProductCard.kt`
- `presentation/ui/components/products/ProductGrid.kt`

#### Paso 15: Screens de Autenticación
- [ ] `presentation/ui/screens/auth/SplashScreen.kt`
  - Verifica sesión existente
  - Navega a Home o Login
  
- [ ] `presentation/ui/screens/auth/LoginScreen.kt`
  - Formulario email/password
  - Enlace a RegisterScreen
  - Maneja errores
  
- [ ] `presentation/ui/screens/auth/RegisterScreen.kt`
  - Formulario completo (email, password, nombre)
  - Validaciones
  - Enlace a LoginScreen

**Archivos a crear**:
- `presentation/ui/screens/auth/SplashScreen.kt`
- `presentation/ui/screens/auth/LoginScreen.kt`
- `presentation/ui/screens/auth/RegisterScreen.kt`

#### Paso 16: Screens Protegidas y Públicas
- [ ] `presentation/ui/screens/public/HomeScreen.kt`
  - Catálogo visible para todos
  - Botón para ir a Perfil (requiere login)
  - Si no autenticado: botón Login
  
- [ ] `presentation/ui/screens/protected/ProfileScreen.kt`
  - Datos del usuario (requiere autenticación)
  - Botón Logout
  - Edición de perfil

**Archivos a crear**:
- `presentation/ui/screens/public/HomeScreen.kt`
- `presentation/ui/screens/protected/ProfileScreen.kt`

---

### FASE 7: Navegación (Paso 17)

#### Paso 17: Navigation Graph
- [ ] `presentation/ui/navigation/Route.kt` - Definición de rutas
- [ ] `presentation/ui/navigation/NavGraph.kt` - Grafo de navegación

**Archivos a crear**:
- `presentation/ui/navigation/Route.kt`
- `presentation/ui/navigation/NavGraph.kt`

---

### FASE 8: Integración Final (Pasos 18-19)

#### Paso 18: Tema y Estilos
- [ ] Actualizar `presentation/ui/theme/Color.kt` - Colores RiderFit
- [ ] Actualizar `presentation/ui/theme/Typography.kt` - Tipografía
- [ ] Actualizar `presentation/ui/theme/Theme.kt` - Tema principal
- [ ] Crear `presentation/ui/theme/Spacing.kt` - Espaciados

**Archivos a crear/modificar**:
- `presentation/ui/theme/Color.kt`
- `presentation/ui/theme/Typography.kt`
- `presentation/ui/theme/Theme.kt`
- `presentation/ui/theme/Spacing.kt`

#### Paso 19: Utilidades
- [ ] Actualizar `utils/Constants.kt` - Rutas Firebase, BD, etc.
- [ ] Actualizar `utils/Extensions.kt` - Funciones helper
- [ ] `utils/validators/EmailValidator.kt`
- [ ] `utils/validators/PasswordValidator.kt`

**Archivos a crear/modificar**:
- `utils/Constants.kt`
- `utils/Extensions.kt`
- `utils/validators/EmailValidator.kt`
- `utils/validators/PasswordValidator.kt`

---

### FASE 9: Testing (Paso 20)

#### Paso 20: Pruebas Unitarias e Instrumentadas
- [ ] Tests para Use Cases
- [ ] Tests para ViewModels
- [ ] Tests Espresso para Screens

**Archivos a crear**:
- `src/test/java/com/store/riderfit/...`
- `src/androidTest/java/com/store/riderfit/...`

---

## Resumen de Archivos a Crear

### Total: 39 archivos nuevos

**Domain** (6):
- User.kt, Product.kt, AuthResult.kt
- IAuthRepository.kt, IUserRepository.kt, IProductRepository.kt

**Data** (12):
- UserDto.kt, ProductDto.kt, AuthState.kt
- FirebaseAuthService.kt, FirebaseUserService.kt, FirestoreService.kt
- AppDatabase.kt, UserDao.kt, UserPreferences.kt
- AuthRepositoryImpl.kt, UserRepositoryImpl.kt, ProductRepositoryImpl.kt

**Presentation** (15):
- UiState.kt, AuthUiState.kt, ProductUiState.kt
- AuthViewModel.kt, HomeViewModel.kt, ProfileViewModel.kt
- EmailField.kt, PasswordField.kt, AuthButton.kt
- Loading.kt, ErrorDialog.kt, SuccessSnackbar.kt
- ProductCard.kt, ProductGrid.kt

**Screens** (5):
- SplashScreen.kt, LoginScreen.kt, RegisterScreen.kt
- HomeScreen.kt, ProfileScreen.kt

**Navigation** (2):
- Route.kt, NavGraph.kt

**Utils** (4):
- AppModule.kt, AuthModule.kt, DatabaseModule.kt (di/)
- EmailValidator.kt, PasswordValidator.kt

**Modificar**: 
- build.gradle.kts, MainActivity.kt, Color.kt, Typography.kt, Theme.kt, Constants.kt, Extensions.kt

---

## Dependencias Necesarias

```kotlin
// build.gradle.kts

// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Hilt
implementation("com.google.dagger:hilt-android:2.50")
kapt("com.google.dagger:hilt-compiler:2.50")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Room
implementation("androidx.room:room-runtime:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.10.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")

// Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.10.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.01.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.6")

// Image Loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## Reglas de Firestore

```javascript
// Versión de desarrollo (NO usar en producción)
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Usuarios: acceso solo al propio documento
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Productos: lectura pública, escritura solo admin
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth.token.admin == true;
    }
    
    // Órdenes: lectura/escritura solo del propietario
    match /orders/{orderId} {
      allow read, write: if request.auth.uid == resource.data.userId;
    }
  }
}
```

---

## Notas Importantes

1. **Validación en cliente y servidor**: Las validaciones en el ViewModel son para UX, pero Firebase tiene sus propias reglas.

2. **Manejo de errores**: Usar `AuthResult<T>` para manejar success/error/loading de forma elegante.

3. **State Management**: Usar `StateFlow` en ViewModels para reactividad.

4. **Seguridad**: Nunca guardar contraseñas en local, Firebase maneja el token automáticamente.

5. **Testing**: Cada capa debe tener tests unitarios independientes.

6. **CI/CD**: Considera usar GitHub Actions para automatizar tests y builds.

---

## Próximos Pasos

Ejecutar **FASE 1** primero para establecer la base, luego avanzar secuencialmente.

¿Comenzamos con el **Paso 1: Configurar Firebase**?
