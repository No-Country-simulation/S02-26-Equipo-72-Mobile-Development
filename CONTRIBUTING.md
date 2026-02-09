#  Guía de Contribución - RiderFit

¡Gracias por tu interés en contribuir a RiderFit! Este documento describe cómo colaborar efectivamente en el proyecto siguiendo nuestros estándares.

##  Tabla de Contenidos

1. [Código de Conducta](#código-de-conducta)
2. [Antes de Empezar](#antes-de-empezar)
3. [Flujo de Trabajo](#flujo-de-trabajo)
4. [Estándares de Código](#estándares-de-código)
5. [Convenciones de Naming](#convenciones-de-naming)
6. [Arquitectura y Patrones](#arquitectura-y-patrones)
7. [Commits y PRs](#commits-y-prs)
8. [Testing](#testing)
9. [Documentación](#documentación)
10. [Checklist pre-commit](#checklist-pre-commit)

---

##  Código de Conducta

### Principios
-  **Respeto**: Trata a todos con respeto y profesionalismo
-  **Comunicación clara**: Comunica tus intenciones y cambios claramente
-  **Calidad**: Prioriza la calidad del código sobre la velocidad
-  **Aprendizaje**: Ayuda a otros a aprender y crecer

### Inaceptable
-  Discriminación o acoso de cualquier tipo
-  Cambios sin discusión previa en temas complejos
-  Commits con código sin testear
-  Modificar el trabajo de otros sin autorización

---

##  Antes de Empezar

### 1. Configura tu Entorno Local

```bash
# 1. Clone el repositorio
git clone https://github.com/tu-organizacion/S02-26-Equipo-72-Mobile-Development.git
cd S02-26-Equipo-72-Mobile-Development

# 2. Instala dependencias
./gradlew clean build

# 3. Verifica la compilación
./gradlew check
```

### 2. Entiende la Arquitectura

Antes de hacer cambios, lee:
- [ARCHITECTURE.md](./ARCHITECTURE.md) - Estructura del proyecto

### 3. Sincroniza con Main

```bash
git fetch origin
git checkout main
git pull origin main
```

---

##  Flujo de Trabajo

### Paso 1: Crea un Branch

**Nomenclatura**:
```
<tipo>/<numero-issue>-<descripcion-corta>
```

**Tipos**:
- `feature/` - Nueva funcionalidad
- `bugfix/` - Corrección de bug
- `refactor/` - Refactorización de código
- `docs/` - Cambios en documentación
- `test/` - Agregar o mejorar tests
- `chore/` - Cambios en configuración o dependencias

**Ejemplos**:
```bash
git checkout -b feature/72-login-screen
git checkout -b bugfix/15-password-validation
git checkout -b docs/update-readme
```

### Paso 2: Haz tus Cambios

```bash
# Trabaja en los archivos necesarios
# Prueba localmente: ./gradlew clean build
```

### Paso 3: Commit Frecuentes

```bash
# Haz commits pequeños y relacionados
git add src/main/java/com/store/riderfit/.../File.kt
git commit -m "feat: implement email validation"

git add src/test/java/com/store/riderfit/.../FileTest.kt
git commit -m "test: add tests for email validator"
```

### Paso 4: Push a tu Fork

```bash
git push origin feature/72-login-screen
```

### Paso 5: Abre un Pull Request

En GitHub:
1. Click en "Compare & pull request"
2. Rellena la descripción (ver [Commits y PRs](#commits-y-prs))
3. Espera revisión

### Paso 6: Review y Merge

-  Code review completado
-  Todos los tests pasan
-  Documentación actualizada
-  Merge a `main`

---

##  Estándares de Código

### Formatting

**Usa Android Studio Format:**
```
Code → Format Code → Ctrl+Shift+L (Windows) o ⌘+⇧+L (Mac)
```

### Legibilidad

```kotlin
//  MALO
fun loginUser(e:String,p:String){val r=firebaseService.login(e,p);return r}

//  BUENO
fun loginUser(email: String, password: String): Flow<AuthResult<User>> {
    return firebaseService.login(email, password)
}
```

### Línea Máxima

- **Máximo 120 caracteres** por línea
- Esto facilita reviews en pantallas pequeñas

```kotlin
//  MALO - Línea muy larga
fun updateUserProfileWithPhotoUrlAndDisplayNameAndEmailVerification(email: String, displayName: String, photoUrl: String = ""): Flow<AuthResult<Unit>> = firebaseUserService.saveUserProfile(User(email = email, displayName = displayName, profilePictureUrl = photoUrl))

//  BUENO
fun updateUserProfile(
    email: String,
    displayName: String,
    photoUrl: String = ""
): Flow<AuthResult<Unit>> {
    return firebaseUserService.saveUserProfile(
        User(
            email = email,
            displayName = displayName,
            profilePictureUrl = photoUrl
        )
    )
}
```

### Null Safety

Kotlin ofrece null safety. Úsalo:

```kotlin
//  MALO
val user: User = null  // Error de compilación, bien!

//  BUENO
val user: User? = null  // Nullable
if (user != null) {
    println(user.email)
}

//  BUENO (con elvis)
val email = user?.email ?: "unknown@example.com"
```

### Coroutines y Flow

```kotlin
//  MALO - Blocking
fun getUser(): User {
    return runBlocking { firebaseService.getUser() }
}

//  BUENO - Non-blocking
fun getUser(): Flow<User> {
    return firebaseService.getUser()
}
```

---

## 🏷️ Convenciones de Naming

### Clases

```kotlin
// PascalCase
class LoginViewModel : ViewModel()
class UserRepository : IUserRepository
data class UserDto(val id: String, val email: String)
sealed class AuthResult<T>
```

### Funciones

```kotlin
// camelCase, verbo + sustantivo
fun loginUser(email: String): Flow<AuthResult<User>>
fun validateEmail(email: String): Boolean
fun saveUserPreferences(user: User): Unit
```

### Variables

```kotlin
// camelCase, sustantivo
val currentUser: User? = null
var isLoading: Boolean = false
val MAX_PASSWORD_LENGTH = 50  // Constante = SCREAMING_SNAKE_CASE
```

### Interfaces

```kotlin
// IInterfaceName (prefijo I)
interface IAuthRepository
interface IUserRepository
interface IProductRepository
```

### Constants

```kotlin
// Objeto companion en clases o constants.kt
object Constants {
    const val FIREBASE_USERS_COLLECTION = "users"
    const val FIREBASE_PRODUCTS_COLLECTION = "products"
    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 50
}
```

### Parámetros de Composables

```kotlin
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    // ...
}
```

---

##  Arquitectura y Patrones

### Clean Architecture

El proyecto sigue **3 capas principales**:

```
Data Layer (Remote + Local)
       ↓
Domain Layer (Use Cases)
       ↓
Presentation Layer (UI + ViewModels)
```

### Dónde poner cada cosa

#### Data Layer (`data/`)
- Firebase services
- Room DAOs
- Repositories implementations
- DTOs

#### Domain Layer (`domain/`)
- Interfaces de repositories
- Use cases
- Modelos de dominio (sin referencias a Android)

#### Presentation Layer (`presentation/`)
- ViewModels
- Screens (Composables)
- Components (Composables reutilizables)
- States (UI state holders)

### Repository Pattern

```kotlin
// Domain: Interface
interface IAuthRepository {
    fun login(email: String, password: String): Flow<AuthResult<User>>
}

// Data: Implementación
@Singleton
class AuthRepositoryImpl(
    private val firebaseService: FirebaseAuthService,
    private val userPreferences: UserPreferences
) : IAuthRepository {
    override fun login(email: String, password: String): Flow<AuthResult<User>> {
        return firebaseService.login(email, password)
            .onEach { result ->
                if (result is AuthResult.Success) {
                    userPreferences.saveUser(result.data)
                }
            }
    }
}
```

### Use Cases

Cada use case debe ser **una clase** que ejecuta **una acción específica**:

```kotlin
class LoginUseCase(
    private val repository: IAuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<AuthResult<User>> {
        return repository.login(email, password)
    }
}

// Uso en ViewModel
loginUseCase(email, password).collectLatest { result ->
    _authState.value = when (result) {
        is AuthResult.Success -> AuthState.Authenticated
        is AuthResult.Error -> AuthState.Error(result.exception.message)
        is AuthResult.Loading -> AuthState.Loading
    }
}
```

### ViewModels

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password).collect { result ->
                _authState.value = when (result) {
                    is AuthResult.Success -> AuthState.Authenticated(result.data)
                    is AuthResult.Error -> AuthState.Error(result.exception.message)
                    is AuthResult.Loading -> AuthState.Loading
                }
            }
        }
    }
}
```

### Dependency Injection (Hilt)

```kotlin
// Module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuthService(): IFirebaseAuthService {
        return FirebaseAuthService()
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseService: FirebaseAuthService
    ): IAuthRepository {
        return AuthRepositoryImpl(firebaseService)
    }
}

// Uso
@HiltViewModel
class MyViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel()
```

---

##  Commits y PRs

### Conventional Commits

Usa el formato:
```
<tipo>(<alcance>): <descripción>

<cuerpo>

<footer>
```

**Tipos**:
- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de bug
- `refactor:` - Cambio de código sin funcionalidad nueva
- `test:` - Tests nuevos o modificados
- `docs:` - Documentación
- `style:` - Formateo, no afecta lógica
- `chore:` - Cambios en dependencias, build, etc.
- `perf:` - Mejora de performance

**Ejemplos**:

```bash
git commit -m "feat(auth): implement email validation"
git commit -m "fix(login): handle empty password error"
git commit -m "test(auth): add tests for LoginViewModel"
git commit -m "docs(readme): update setup instructions"
git commit -m "refactor(repositories): simplify error handling"
git commit -m "chore(deps): upgrade Firebase to 25.1.4"
```

### Pull Request Template

```markdown
##  Descripción
Describe brevemente qué cambios hiciste y por qué.

##  Tipo de cambio
- [ ] Nueva funcionalidad
- [ ] Corrección de bug
- [ ] Refactorización
- [ ] Cambio de documentación

##  Cambios realizados
- Cambio 1
- Cambio 2
- Cambio 3

##  Testing
- [ ] Agregué tests unitarios
- [ ] Agregué tests de integración
- [ ] Todos los tests pasan: `./gradlew test`

##  Documentación
- [ ] Actualicé documentación si es necesario
- [ ] Documenté funciones públicas con KDoc

##  Checklist
- [ ] Mi código sigue los estándares del proyecto
- [ ] Ejecuté `./gradlew check` sin errores
- [ ] Revisé mis propios cambios
- [ ] No hay cambios innecesarios

##  Screenshots (si aplica)
[Agregar screenshots si hay cambios en UI]

## Relacionado
Closes #123 o Relates to #456
```

---

##  Testing

### Pruebas Unitarias

Ubicación: `app/src/test/java/com/store/riderfit/`

```kotlin
class LoginViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var viewModel: AuthViewModel
    private val loginUseCase = mockk<LoginUseCase>()
    
    @Before
    fun setup() {
        viewModel = AuthViewModel(loginUseCase)
    }
    
    @Test
    fun `when login is called with valid credentials, state should be authenticated`() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "1", email = email, displayName = "Test")
        
        coEvery { 
            loginUseCase(email, password) 
        } returns flowOf(AuthResult.Success(user))
        
        // Act
        viewModel.login(email, password)
        
        // Assert
        assertEquals(
            AuthState.Authenticated(user),
            viewModel.authState.value
        )
    }
}
```

### Pruebas de Integración

Ubicación: `app/src/androidTest/java/com/store/riderfit/`

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun shouldDisplayLoginForm() {
        composeTestRule.setContent {
            RiderFitTheme {
                LoginScreen()
            }
        }
        
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Contraseña").assertExists()
        composeTestRule.onNodeWithText("Iniciar sesión").assertExists()
    }
}
```

### Ejecutar Tests

```bash
# Unit tests
./gradlew test

# Integration tests (requiere emulador)
./gradlew connectedAndroidTest

# Todos
./gradlew check
```

### Cobertura de Tests

```bash
# Generar reporte
./gradlew jacocoTestReport

# Abrir reporte
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

**Target de cobertura**: Mínimo **70%** en nuevas funcionalidades

---

##  Documentación

### KDoc para Funciones Públicas

```kotlin
/**
 * Realiza el login del usuario con email y contraseña.
 *
 * @param email Email del usuario
 * @param password Contraseña (mínimo 8 caracteres)
 * @return Flow que emite [AuthResult<User>] con el usuario autenticado
 *
 * @throws IllegalArgumentException si email o password están vacíos
 *
 * Ejemplo:
 * ```kotlin
 * loginUseCase("user@example.com", "password123")
 *     .collect { result ->
 *         when (result) {
 *             is AuthResult.Success -> println("Login exitoso")
 *             is AuthResult.Error -> println("Error: ${result.exception}")
 *             is AuthResult.Loading -> println("Cargando...")
 *         }
 *     }
 * ```
 */
fun login(
    email: String,
    password: String
): Flow<AuthResult<User>>
```

### Documentación en Clases

```kotlin
/**
 * ViewModel para la pantalla de autenticación.
 *
 * Maneja el flujo de login, registro y logout de usuarios.
 *
 * @property authState Estado actual de autenticación
 * @property uiState Estado de UI (loading, error, etc.)
 *
 * Estados posibles:
 * - [AuthState.Unauthenticated]: Usuario no autenticado
 * - [AuthState.Authenticated]: Usuario autenticado
 * - [AuthState.Loading]: Cargando...
 * - [AuthState.Error]: Error durante operación
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel()
```

### Documentación en Composables

```kotlin
/**
 * Pantalla de login con formulario de email y contraseña.
 *
 * @param viewModel ViewModel que proporciona la lógica
 * @param onNavigateToRegister Callback cuando el usuario quiere registrarse
 * @param onLoginSuccess Callback cuando el login es exitoso
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
)
```

---

##  Checklist Pre-Commit

Antes de hacer commit, verifica:

### Código
- [ ] `./gradlew clean build` compila sin errores
- [ ] `./gradlew ktlintFormat` aplica formateo
- [ ] No hay warnings del linter
- [ ] Código sigue convenciones de naming
- [ ] Máximo 120 caracteres por línea

### Tests
- [ ] Escribí tests para la nueva funcionalidad
- [ ] `./gradlew test` pasa todos los unit tests
- [ ] Cobertura ≥ 70% para nuevas funcionalidades

### Documentación
- [ ] Documenté funciones públicas con KDoc
- [ ] Actualicé [ARCHITECTURE.md](./ARCHITECTURE.md) si es necesario
- [ ] Actualicé [README.md](./README.md) si hay cambios relevantes

### Commits
- [ ] Commits son pequeños y relacionados
- [ ] Mensajes de commit siguen Conventional Commits
- [ ] No hay archivos de configuración personal (local.properties)

### Git
- [ ] Sincronicé con `main` antes de pushear
- [ ] No hay conflictos pending
- [ ] Branch está actualizado con `origin/main`

---

##  Recursos Útiles

### Documentación Oficial
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/compose)
- [Android Architecture](https://developer.android.com/topic/architecture)
- [Firebase Android](https://firebase.google.com/docs/android/setup)
- [Hilt](https://dagger.dev/hilt/)

### Libros y Artículos
- Clean Architecture by Robert C. Martin
- Kotlin in Action (2nd Edition)
- [Google's Android Architecture Guide](https://developer.android.com/topic/architecture/recommendations)

### Herramientas
- Android Studio
- Kotlin Lint
- JaCoCo (Test Coverage)
- Firebase Emulator Suite

---

##  Ayuda

### Dudas sobre el Proyecto
1. Lee [ARCHITECTURE.md](./ARCHITECTURE.md)
2. Revisa issues existentes: [GitHub Issues]

### Dudas sobre Contribuciones
1. Abre una discusión en GitHub
2. Contacta al lead developer

### Reporte de Bugs
1. Abre un issue con template "Bug Report"
2. Incluye: Descripción, pasos para reproducir, comportamiento esperado
3. Espera confirmación antes de hacer fix

---

##  Flujo Completo de Contribución

```
1. Fork del repositorio
   ↓
2. Crea branch: git checkout -b feature/nombre
   ↓
3. Haz cambios y commits
   → Ejecuta: ./gradlew check
   → Escribe tests
   → Documenta con KDoc
   ↓
4. Push: git push origin feature/nombre
   ↓
5. Abre Pull Request
   → Rellena template
   → Esperá review
   ↓
6. Implementa feedback si es necesario
   ↓
7. Merge a main (lo hace el revisor)
   
```

---



**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Autores**: Jorge Galleguillos - Ingeniero de software
