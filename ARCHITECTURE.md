#  Arquitectura de RiderFit

Documento técnico que explica cómo está estructurada la aplicación RiderFit, sus capas, patrones y decisiones arquitectónicas.

##  Tabla de Contenidos

1. [Vision General](#visión-general)
2. [Clean Architecture](#clean-architecture)
3. [Capas](#capas)
4. [Patrones Utilizados](#patrones-utilizados)
5. [Flujos de Datos](#flujos-de-datos)
6. [Casos de Uso Detallados](#casos-de-uso-detallados)
7. [Testing](#testing)
8. [Agregar Nuevas Funcionalidades](#agregar-nuevas-funcionalidades)
9. [Decisiones de Diseño](#decisiones-de-diseño)

---

##  Visión General

RiderFit es una aplicación de **autenticación y catálogo de productos** que implementa **Clean Architecture** y **MVVM** para garantizar:

-  **Mantenibilidad**: Código modular y fácil de mantener
-  **Testabilidad**: Cada componente puede testearse aisladamente
-  **Escalabilidad**: Fácil agregar nuevas funcionalidades
-  **Independencia**: Layers desacopladas, sin dependencias circulares

### Visualización

```
┌─────────────────────────────────────────┐
│    PRESENTATION LAYER (UI + ViewModels) │
│  (Screens, Components, States)          │
└──────────────────┬──────────────────────┘
                   │
                   │ depends on
                   ▼
┌─────────────────────────────────────────┐
│     DOMAIN LAYER (Use Cases + Models)   │
│  (Pure business logic, no Android)      │
└──────────────────┬──────────────────────┘
                   │
                   │ depends on
                   ▼
┌─────────────────────────────────────────┐
│ DATA LAYER (Firebase, Room, DataStore)  │
│  (Remote + Local persistence)           │
└─────────────────────────────────────────┘
```

---

##  Clean Architecture

### Principios

1. **Independencia de Frameworks**: La lógica de negocio no depende de frameworks
2. **Testable**: Los tests no necesitan DB, framework web, etc.
3. **Independencia de UI**: La UI puede cambiar sin afectar la lógica
4. **Independencia de Base de Datos**: Cambiar DB no requiere cambiar lógica
5. **Independencia de Agentes Externos**: La lógica no conoce del mapeo externo

### Arquitectura por Capas

```
┌──────────────────────────────────────────────────────┐
│              PRESENTATION (High Level)               │
│  ┌────────────────────────────────────────────────┐  │
│  │ UI Components (Composables)                    │  │
│  │ - LoginScreen, HomeScreen, ProfileScreen      │  │
│  │ - EmailField, PasswordField, ProductCard       │  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ ViewModels                                     │  │
│  │ - AuthViewModel, HomeViewModel, ProfileVM      │  │
│  │ - Gestiona estado de UI y llamadas a use cases│  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ States                                         │  │
│  │ - UiState, AuthUiState, ProductUiState        │  │
│  │ - Modelos que representan estado de pantalla  │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                        ▲
                        │ depends on
                        ▼
┌──────────────────────────────────────────────────────┐
│                DOMAIN (Business Logic)               │
│  ┌────────────────────────────────────────────────┐  │
│  │ Use Cases (Orchestrate business logic)        │  │
│  │ - LoginUseCase, RegisterUseCase               │  │
│  │ - GetProductsUseCase                          │  │
│  │ - Solo lógica, sin Android, sin BD específica │  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ Repository Interfaces                          │  │
│  │ - IAuthRepository, IUserRepository             │  │
│  │ - Define contratos que data layer implementa  │  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ Domain Models                                  │  │
│  │ - User, Product, AuthResult                   │  │
│  │ - Modelos puros, sin referencias a DTO/Entity│  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
                        ▲
                        │ depends on
                        ▼
┌──────────────────────────────────────────────────────┐
│              DATA (Persistence)                      │
│  ┌────────────────────────────────────────────────┐  │
│  │ Remote Sources (Firebase)                      │  │
│  │ - FirebaseAuthService, FirestoreService       │  │
│  │ - Comunicación con APIs externas              │  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ Local Sources (Room, DataStore)                │  │
│  │ - UserDao, UserPreferences                     │  │
│  │ - Persistencia local segura                   │  │
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ Repository Implementations                    │  │
│  │ - AuthRepositoryImpl, UserRepositoryImpl        │  │
│  │ - Combina Remote + Local, caché, sincronización
│  └────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────┐  │
│  │ Models & Entities                              │  │
│  │ - UserDto, ProductDto, UserEntity              │  │
│  │ - Mapeos a/desde Domain Models                │  │
│  └────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

---

##  Capas en Detalle

### 1️ Data Layer (`data/`)

**Responsabilidades**:
- Obtener datos de Firebase (remoto)
- Almacenar datos en Room (local)
- Implementar repositories que define domain
- Mapear DTOs ↔ Domain Models

**Componentes**:

#### Remote (Firebase)
```kotlin
// FirebaseAuthService.kt
class FirebaseAuthService {
    fun signUp(email: String, password: String): Flow<AuthResult<User>>
    fun login(email: String, password: String): Flow<AuthResult<User>>
    fun logout(): Flow<AuthResult<Unit>>
    fun getCurrentUser(): Flow<User?>
}

// FirestoreService.kt
class FirestoreService {
    fun getProducts(): Flow<List<Product>>
    fun saveProduct(product: Product): Flow<AuthResult<Unit>>
}
```

#### Local (Room + DataStore)
```kotlin
// UserDao.kt
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
}

// UserPreferences.kt (DataStore)
class UserPreferences {
    suspend fun saveUser(user: User)
    fun getUser(): Flow<User?>
    suspend fun clearPreferences()
}
```

#### Repository Ejemplo: AuthRepositoryImpl
```kotlin
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val userPreferences: UserPreferences,
    private val userDao: UserDao
) : IAuthRepository {
    
    override fun login(email: String, password: String): Flow<AuthResult<User>> {
        return firebaseAuthService.login(email, password)
            .onEach { result ->
                // Caché local
                if (result is AuthResult.Success) {
                    userPreferences.saveUser(result.data)
                    userDao.insertUser(result.data.toEntity())
                }
            }
    }
}
```

#### Mapeos (Extensiones)
```kotlin
// En UserDto.kt
fun UserDto.toDomain() = User(
    id = id,
    email = email,
    displayName = displayName,
    profilePictureUrl = profilePictureUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun User.toDto() = UserDto(
    id = id,
    email = email,
    displayName = displayName,
    profilePictureUrl = profilePictureUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// Entre Domain y Entity
fun User.toEntity() = UserEntity(
    id = id,
    email = email,
    displayName = displayName,
    profilePictureUrl = profilePictureUrl,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserEntity.toDomain() = User(...)
```

---

### 2️ Domain Layer (`domain/`)

**Responsabilidades**:
- Contener lógica de negocio pura
- Definir contratos (interfaces) que data implementa
- Orquestar operaciones a través de use cases
- No tener dependencias de Android

**Componentes**:

#### Domain Models
```kotlin
// User.kt
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profilePictureUrl: String = "",
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
    val rating: Double
)

// AuthResult.kt
sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val exception: Exception) : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
}
```

#### Repository Interfaces
```kotlin
// IAuthRepository.kt
interface IAuthRepository {
    fun login(email: String, password: String): Flow<AuthResult<User>>
    fun register(email: String, password: String, displayName: String): Flow<AuthResult<User>>
    fun logout(): Flow<AuthResult<Unit>>
    fun getCurrentUser(): Flow<User?>
}

// IUserRepository.kt
interface IUserRepository {
    fun getUserProfile(userId: String): Flow<AuthResult<User>>
    fun updateUserProfile(user: User): Flow<AuthResult<Unit>>
}

// IProductRepository.kt
interface IProductRepository {
    fun getProducts(): Flow<AuthResult<List<Product>>>
    fun getProductById(productId: String): Flow<AuthResult<Product>>
}
```

#### Use Cases
```kotlin
// Cada use case = una acción específica

class LoginUseCase(
    private val repository: IAuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<AuthResult<User>> {
        return repository.login(email, password)
    }
}

class RegisterUseCase(
    private val repository: IAuthRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Flow<AuthResult<User>> {
        return repository.register(email, password, displayName)
    }
}

class GetProductsUseCase(
    private val repository: IProductRepository
) {
    operator fun invoke(): Flow<AuthResult<List<Product>>> {
        return repository.getProducts()
    }
}
```

**Ventajas de Use Cases**:
-  Cada uno es simple y testeable
-  Reutilizables desde múltiples ViewModels
-  Pueden encapsular validaciones complejas

---

### 3️ Presentation Layer (`presentation/`)

**Responsabilidades**:
- Mostrar UI al usuario
- Gestionar estado de pantalla
- Llamar a use cases en response a user actions
- Navegar entre pantallas

**Componentes**:

#### States (Modelos de UI)
```kotlin
// UiState.kt - Estado genérico reutilizable
sealed class UiState<T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String, val data: T? = null) : UiState<T>()
    class Loading<T> : UiState<T>()
    class Idle<T> : UiState<T>()
}

// AuthUiState.kt - Estado específico de autenticación
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
) {
    val isEmailValid = email.isNotEmpty() && email.contains("@")
    val isPasswordValid = password.length >= 8
    val isFormValid = isEmailValid && isPasswordValid
}
```

#### ViewModels
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            loginUseCase(email, password).collect { result ->
                _uiState.update {
                    when (result) {
                        is AuthResult.Success -> it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            error = null
                        )
                        is AuthResult.Error -> it.copy(
                            isLoading = false,
                            error = result.exception.message
                        )
                        is AuthResult.Loading -> it.copy(isLoading = true)
                    }
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            logoutUseCase().collect { result ->
                if (result is AuthResult.Success) {
                    _uiState.value = AuthUiState()
                }
            }
        }
    }
    
    private fun checkCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _uiState.update {
                    it.copy(isAuthenticated = user != null)
                }
            }
        }
    }
}
```

#### Screens (Composables)
```kotlin
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(
            value = uiState.email,
            onValueChange = { /* update */ },
            errorMessage = if (!uiState.isEmailValid) "Email inválido" else ""
        )
        
        PasswordField(
            value = uiState.password,
            onValueChange = { /* update */ }
        )
        
        AuthButton(
            text = "Iniciar sesión",
            isLoading = uiState.isLoading,
            enabled = uiState.isFormValid,
            onClick = { viewModel.login(uiState.email, uiState.password) }
        )
        
        if (uiState.error != null) {
            ErrorDialog(message = uiState.error!!) {
                // Dismiss
            }
        }
        
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
```

#### Navigation
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
        composable(Route.Splash.route) {
            SplashScreen(navController)
        }
        
        navigation(
            startDestination = Route.Login.route,
            route = Route.AuthGraph.route
        ) {
            composable(Route.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.MainGraph.route) {
                            popUpTo(Route.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Route.Register.route)
                    }
                )
            }
            
            composable(Route.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        navigation(
            startDestination = Route.Home.route,
            route = Route.MainGraph.route
        ) {
            composable(Route.Home.route) {
                HomeScreen(
                    onNavigateToProfile = {
                        navController.navigate(Route.Profile.route)
                    }
                )
            }
            
            composable(Route.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.MainGraph.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
```

---

##  Flujos de Datos

### Flujo de Login

```
User digita email y password
        ↓
[LoginScreen] captura cambios
        ↓
[AuthViewModel.login(email, password)]
        ↓
loginUseCase(email, password)
        ↓
[AuthRepositoryImpl.login]
        ↓
[FirebaseAuthService.login] (remoto)
        ↓
Firebase Authentication verifica credenciales
        ↓
Si éxito: Retorna User
        ↓
authRepositoryImpl guarda en:
  • UserPreferences (DataStore)
  • UserDao (Room)
        ↓
Resultado fluye de vuelta a ViewModel
        ↓
ViewModel actualiza uiState.isAuthenticated = true
        ↓
[LoginScreen] recompone y navega a HomeScreen
```

### Flujo de Obtener Productos

```
[HomeScreen] se abre
        ↓
[HomeViewModel.init] → loadProducts()
        ↓
getProductsUseCase()
        ↓
[ProductRepositoryImpl.getProducts]
        ↓
[FirestoreService.getProducts] (remoto)
        ↓
Firestore retorna lista de productos
        ↓
(Opcional) Guardar en caché local
        ↓
Resultado fluye a ViewModel
        ↓
ViewModel actualiza uiState.products
        ↓
[ProductGrid] recompone y muestra productos
```

---

##  Casos de Uso Detallados

### Use Case: Login

**Archivo**: `domain/usecase/auth/LoginUseCase.kt`

```kotlin
class LoginUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    /**
     * Realiza login con email y contraseña.
     * 
     * Validaciones mínimas:
     * - Email no vacío
     * - Contraseña no vacía
     * 
     * @param email Email del usuario
     * @param password Contraseña
     * @return Flow<AuthResult<User>>
     */
    operator fun invoke(
        email: String,
        password: String
    ): Flow<AuthResult<User>> {
        return if (email.isBlank() || password.isBlank()) {
            flowOf(
                AuthResult.Error(
                    Exception("Email y contraseña son requeridos")
                )
            )
        } else {
            repository.login(email, password)
        }
    }
}
```

### Use Case: Register

**Archivo**: `domain/usecase/auth/RegisterUseCase.kt`

```kotlin
class RegisterUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    /**
     * Registra un nuevo usuario.
     * 
     * Validaciones:
     * - Email válido (contiene @)
     * - Contraseña mínimo 8 caracteres
     * - DisplayName no vacío
     */
    operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Flow<AuthResult<User>> {
        return when {
            email.isBlank() -> flowOf(
                AuthResult.Error(Exception("Email es requerido"))
            )
            !email.contains("@") -> flowOf(
                AuthResult.Error(Exception("Email inválido"))
            )
            password.length < 8 -> flowOf(
                AuthResult.Error(Exception("Contraseña mínimo 8 caracteres"))
            )
            displayName.isBlank() -> flowOf(
                AuthResult.Error(Exception("Nombre es requerido"))
            )
            else -> repository.register(email, password, displayName)
        }
    }
}
```

### Use Case: GetProducts

**Archivo**: `domain/usecase/product/GetProductsUseCase.kt`

```kotlin
class GetProductsUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    /**
     * Obtiene lista de todos los productos.
     * 
     * Si la app está offline, puede retornar datos en caché si existen.
     */
    operator fun invoke(): Flow<AuthResult<List<Product>>> {
        return repository.getProducts()
    }
}
```

---

##  Testing

### Estructura de Tests

```
app/src/
├── test/
│   └── java/com/store/riderfit/
│       ├── domain/
│       │   └── usecase/
│       │       └── auth/
│       │           └── LoginUseCaseTest.kt
│       ├── data/
│       │   └── repository/
│       │       └── AuthRepositoryImplTest.kt
│       └── presentation/
│           └── viewmodel/
│               └── AuthViewModelTest.kt
│
└── androidTest/
    └── java/com/store/riderfit/
        ├── ui/
        │   └── screens/
        │       └── LoginScreenTest.kt
        └── integration/
            └── AuthFlowTest.kt
```

### Ejemplo: Unit Test de Use Case

```kotlin
class LoginUseCaseTest {
    
    private val mockRepository = mockk<IAuthRepository>()
    private lateinit var useCase: LoginUseCase
    
    @Before
    fun setup() {
        useCase = LoginUseCase(mockRepository)
    }
    
    @Test
    fun `should return error when email is blank`() = runTest {
        val result = useCase("", "password").first()
        
        assertTrue(result is AuthResult.Error)
        assertEquals("Email y contraseña son requeridos", result.exception.message)
    }
    
    @Test
    fun `should call repository when inputs are valid`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val user = User(id = "1", email = email, displayName = "Test")
        
        coEvery { mockRepository.login(email, password) } returns flowOf(
            AuthResult.Success(user)
        )
        
        val result = useCase(email, password).first()
        
        assertTrue(result is AuthResult.Success)
        assertEquals(user, (result as AuthResult.Success).data)
    }
}
```

### Ejemplo: ViewModel Test

```kotlin
class AuthViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val mockLoginUseCase = mockk<LoginUseCase>()
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()
    
    private lateinit var viewModel: AuthViewModel
    
    @Before
    fun setup() {
        viewModel = AuthViewModel(
            loginUseCase = mockLoginUseCase,
            registerUseCase = mockk(),
            logoutUseCase = mockk(),
            getCurrentUserUseCase = mockGetCurrentUserUseCase
        )
    }
    
    @Test
    fun `when login succeeds, isAuthenticated should be true`() = runTest {
        val user = User(id = "1", email = "test@example.com", displayName = "Test")
        
        coEvery { mockLoginUseCase(any(), any()) } returns flowOf(
            AuthResult.Success(user)
        )
        
        viewModel.login("test@example.com", "password123")
        
        val uiState = viewModel.uiState.first()
        assertTrue(uiState.isAuthenticated)
        assertFalse(uiState.isLoading)
    }
}
```

### Ejemplo: UI Test

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun shouldDisplayAllFormFields() {
        composeTestRule.setContent {
            RiderFitTheme {
                LoginScreen()
            }
        }
        
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Contraseña").assertExists()
        composeTestRule.onNodeWithText("Iniciar sesión").assertExists()
    }
    
    @Test
    fun shouldNavigateToRegisterScreen_whenRegisterLinkIsClicked() {
        composeTestRule.setContent {
            RiderFitTheme {
                LoginScreen(
                    onNavigateToRegister = { /* Verify called */ }
                )
            }
        }
        
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()
    }
}
```

---

##  Agregar Nuevas Funcionalidades

### Paso 1: Definir la Necesidad

Ejemplo: Agregar "Cambio de Contraseña"

### Paso 2: Domain Layer

```kotlin
// domain/usecase/auth/ChangePasswordUseCase.kt
class ChangePasswordUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(
        oldPassword: String,
        newPassword: String
    ): Flow<AuthResult<Unit>> {
        return repository.changePassword(oldPassword, newPassword)
    }
}

// Actualizar interface: domain/repository/IAuthRepository.kt
interface IAuthRepository {
    // ... métodos existentes
    fun changePassword(oldPassword: String, newPassword: String): Flow<AuthResult<Unit>>
}
```

### Paso 3: Data Layer

```kotlin
// data/remote/firebase/FirebaseAuthService.kt
fun changePassword(oldPassword: String, newPassword: String): Flow<AuthResult<Unit>> {
    return flow {
        try {
            val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("No authenticated user")
            emit(AuthResult.Loading())
            
            // Reautenticar antes de cambiar contraseña
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential).await()
            
            // Cambiar contraseña
            user.updatePassword(newPassword).await()
            
            emit(AuthResult.Success(Unit))
        } catch (e: Exception) {
            emit(AuthResult.Error(e))
        }
    }
}

// data/repository/AuthRepositoryImpl.kt
override fun changePassword(oldPassword: String, newPassword: String): Flow<AuthResult<Unit>> {
    return firebaseAuthService.changePassword(oldPassword, newPassword)
}
```

### Paso 4: Presentation Layer

```kotlin
// Actualizar AuthUiState
data class AuthUiState(
    // ... campos existentes
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)

// Agregar método a AuthViewModel
fun changePassword(oldPassword: String, newPassword: String) {
    viewModelScope.launch {
        changePasswordUseCase(oldPassword, newPassword).collect { result ->
            _uiState.update {
                when (result) {
                    is AuthResult.Success -> it.copy(
                        isLoading = false,
                        error = null
                    )
                    is AuthResult.Error -> it.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                    is AuthResult.Loading -> it.copy(isLoading = true)
                }
            }
        }
    }
}

// Crear ChangePasswordScreen
@Composable
fun ChangePasswordScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccess: () -> Unit
) {
    // Composable code...
}
```

### Paso 5: Inyección de Dependencias

```kotlin
// di/UseCaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideChangePasswordUseCase(
        repository: IAuthRepository
    ): ChangePasswordUseCase {
        return ChangePasswordUseCase(repository)
    }
}
```

### Paso 6: Testing

```kotlin
class ChangePasswordUseCaseTest {
    
    @Test
    fun `should return error when old password is invalid`() = runTest {
        val useCase = ChangePasswordUseCase(mockRepository)
        
        coEvery { 
            mockRepository.changePassword(any(), any()) 
        } returns flowOf(
            AuthResult.Error(Exception("Contraseña anterior incorrecta"))
        )
        
        val result = useCase("wrongpass", "newpass123").first()
        
        assertTrue(result is AuthResult.Error)
    }
}
```

### Paso 7: Integración en Navigation

```kotlin
// Actualizar Route.kt
sealed class Route(val route: String) {
    // ... rutas existentes
    object ChangePassword : Route("change_password")
}

// Actualizar NavGraph.kt
composable(Route.ChangePassword.route) {
    ChangePasswordScreen(
        onSuccess = { navController.popBackStack() }
    )
}
```

---

##  Decisiones de Diseño

### 1. Flow vs LiveData

**Decisión**: Usar `Flow` en lugar de `LiveData`

**Razones**:
-  Coroutine-native (mejor con ViewModel)
-  Más flexible (map, filter, combine)
-  Mejor para tests (runTest, collectLatest)
-  Compatible con suspending functions

### 2. Firebase (sin KTX)

**Decisión**: Firebase 23.2.1+ (sin módulos KTX deprecados)

**Razones**:
-  Firebase deprecó KTX en Julio 2025
-  APIs de Firebase ya son suspendibles
-  Usar `.await()` en lugar de `.addOnSuccessListener`

### 3. Room + DataStore

**Decisión**: Usar ambos

**Razones**:
-  **Room**: Para datos estructurados complejos (usuarios, productos)
-  **DataStore**: Para preferencias simples (tokens, flags)

### 4. Sealed Classes para Estados

**Decisión**: Usar `sealed class` en lugar de `when` con booleanos

```kotlin
//  MALO
data class AuthUiState(
    val isLoading: Boolean,
    val isError: Boolean,
    val isSuccess: Boolean
)

//  BUENO
sealed class AuthState {
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

**Razones**:
-  Exhaustive when (compilador verifica todos los casos)
-  Imposible estados inválidos (loading + error simultáneo)
-  Type-safe (acceso a propiedades del estado)

### 5. Single Responsibility Principle

**Decisión**: Un use case por acción

```kotlin
//  MALO
class AuthUseCase {
    fun login(...) { }
    fun register(...) { }
    fun logout(...) { }
}

//  BUENO
class LoginUseCase { }
class RegisterUseCase { }
class LogoutUseCase { }
```

**Razones**:
-  Más testeable
-  Más reutilizable
-  Cambios en logout no afectan login

---

##  Referencias

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Android Architecture](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Jetpack Compose](https://developer.android.com/compose)

---

**Última actualización**: Febrero 2026  
**Versión**: 1.0  
**Autores**: Jorge Galleguillos - Ingeniero de software
