# Documentación de la Estructura de la Aplicación Vita

Este documento explica cada línea de la estructura del proyecto **Vita**, una aplicación de gamificación de hábitos saludables desarrollada con Jetpack Compose, Material 3, Hilt, Room y Firebase.

---

## 📁 Nivel Raíz del Proyecto

### `settings.gradle.kts`
- **Función**: Configuración del proyecto multi-módulo Gradle
- **Líneas clave**:
  - Define los repositorios donde Gradle buscará dependencias (Google, Maven Central)
  - Establece el nombre del proyecto como "Vita"
  - Incluye el módulo `:app` como submódulo principal

### `build.gradle.kts` (raíz)
- **Función**: Archivo de configuración de build a nivel de proyecto
- **Plugins aplicados**:
  - `android.application`: Plugin de Android para aplicaciones
  - `kotlin.android`: Soporte para Kotlin en Android
  - `kotlin.compose`: Compilador de Jetpack Compose
  - `hilt.android`: Plugin de inyección de dependencias Hilt
  - `ksp`: Kotlin Symbol Processing (usado en lugar de kapt)
  - `google-services`: Plugin de Firebase
  - `firebase.crashlytics`: Plugin de Crashlytics para reportes de errores

---

## 📁 Módulo App (`app/`)

### `app/build.gradle.kts`
- **Función**: Configuración de dependencias y build específicas del módulo de la aplicación
- **Dependencias principales**:
  - **Jetpack Compose**: UI declarativa (`compose.ui`, `compose.material3`)
  - **Navigation 3**: Sistema de navegación (`navigation3.runtime`, `navigation3.ui`)
  - **Firebase**: Autenticación, Firestore, Analytics, Crashlytics
  - **Room**: Base de datos local con KSP (no kapt)
  - **Hilt**: Inyección de dependencias
  - **WorkManager**: Para notificaciones programadas
  - **Retrofit**: Cliente HTTP para APIs externas
  - **Coroutines**: Manejo asíncrono

### `app/google-services.json`
- **Función**: Archivo de configuración de Firebase generado desde Firebase Console
- **Contenido**: Credenciales y configuración del proyecto Firebase (API keys, project ID, etc.)

---

## 📁 Estructura de Código Fuente (`app/src/main/`)

### `AndroidManifest.xml`
- **Función**: Configuración de la aplicación Android
- **Elementos clave**:
  - Declara `MainActivity` como actividad principal (`LAUNCHER`)
  - Define iconos, tema y nombre de la aplicación
  - Configura permisos (si los hay)
  - Establece reglas de backup

---

## 📁 Paquete Principal (`java/com/example/vita/`)

### `MainActivity.kt`
- **Función**: Punto de entrada principal de la aplicación
- **Responsabilidades**:
  - Anotado con `@AndroidEntryPoint` para inyección de Hilt
  - Inicializa el tema con `VitaTheme`
  - Debería inicializar `AppNavigation` para manejar la navegación

### `VitaGameApp.kt` (mencionado en estructura, no existe actualmente)
- **Función propuesta**: Clase `Application` personalizada
- **Uso típico**: Inicialización global de Hilt y otras configuraciones de aplicación

---

## 📁 Módulo Core (`core/`)

Contiene utilidades y clases base compartidas en toda la aplicación.

### `Constants.kt`
- **Función**: Almacena constantes globales reutilizables
- **Ejemplos típicos**: URLs de APIs, claves de configuración, valores por defecto

### `DateTimeUtils.kt`
- **Función**: Utilidades para manejo de fechas y cálculos de rachas (streaks)
- **Casos de uso**: Calcular días consecutivos, formatear fechas, validar períodos

### `Dispatchers.kt`
- **Función**: Proveedor de `CoroutineDispatchers` para inyección con Hilt
- **Propósito**: Centralizar la configuración de dispatchers (Main, IO, Default) para testing y consistencia

### `Formatters.kt`
- **Función**: Funciones de formateo para diferentes tipos de datos
- **Formateos típicos**:
  - Calorías: "250 kcal"
  - XP (experiencia): "1,250 XP"
  - IMC (Índice de Masa Corporal): "22.5"

### `Result.kt`
- **Función**: Wrapper genérico `sealed class Result<T>` para manejar operaciones exitosas/fallidas
- **Estructura típica**:
  ```kotlin
  sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Throwable) : Result<T>()
  }
  ```

### `AuthNavGuard.kt`
- **Función**: Guardia de navegación que protege rutas autenticadas
- **Comportamiento**: Redirige a Login si el usuario no está autenticado

---

## 📁 Módulo de Datos (`data/`)

Implementa la capa de datos siguiendo el patrón Repository. Se divide en tres subcategorías: local, remoto y repositorios.

---

### 📂 Datos Locales (`data/local/`)

Almacena datos en el dispositivo usando Room Database.

#### `data/local/dao/` - Data Access Objects (DAOs)

Interfaces que definen operaciones de base de datos usando anotaciones de Room.

- **`UserDao.kt`**: Operaciones CRUD para usuarios (insert, update, getById, delete)
- **`ProfileDao.kt`**: Acceso a datos de perfil (altura, peso, edad, IMC)
- **`FoodPreferenceDao.kt`**: Gestión de preferencias alimentarias (gustos, alergias, preferencias saludables)
- **`MealDao.kt`**: Registro de comidas y calorías consumidas
- **`AchievementDao.kt`**: Consultas sobre logros e insignias desbloqueadas
- **`ChallengeDao.kt`**: Operaciones sobre retos (diarios/semanales)
- **`ProgressDao.kt`**: Acceso a datos de progreso (XP, nivel, racha, última actividad)
- **`GameResultDao.kt`**: Almacenamiento de resultados de juegos de Godot (XP ganada, logros desbloqueados)

#### `data/local/db/AppDatabase.kt`
- **Función**: Base de datos Room que actúa como contenedor principal
- **Responsabilidades**:
  - Define todas las entidades que componen la base de datos
  - Proporciona métodos abstractos para obtener instancias de DAOs
  - Configura el número de versión para migraciones
  - Se genera automáticamente por Room usando KSP

#### `data/local/entities/` - Entidades Room

Clases de datos anotadas con `@Entity` que representan tablas en la base de datos SQLite.

- **`UserEntity.kt`**: Tabla de usuarios con campos: `uid` (clave primaria), `nombre`, `apellido`, `email`, `fechaCreacion`
- **`ProfileEntity.kt`**: Perfil de usuario con: `userId` (FK a User), `altura`, `peso`, `edad`, `imc`
- **`FoodPreferenceEntity.kt`**: Preferencias alimentarias: `userId` (FK), `alergias` (lista), `gustos` (lista), `preferenciaSaludable` (boolean)
- **`MealEntity.kt`**: Registro de comidas: `id`, `userId`, `nombre`, `calorias`, `fecha`, `tipoComida` (desayuno/almuerzo/cena)
- **`AchievementEntity.kt`**: Logros: `id`, `userId`, `nombre`, `descripcion`, `icono`, `fechaDesbloqueo`, `tipo` (receta/insignia)
- **`ChallengeEntity.kt`**: Retos: `id`, `userId`, `nombre`, `descripcion`, `tipo` (diario/semanal), `xp`, `fechaInicio`, `fechaFin`, `completado`
- **`ProgressEntity.kt`**: Progreso del usuario: `userId` (FK), `xp`, `nivel`, `racha`, `ultimaActividad`, `totalRetosDiarios`, `totalRetosSemanales`
- **`GameResultEntity.kt`**: Resultados de juegos: `id`, `userId`, `juegoId`, `xpGanada`, `logroId` (opcional), `fecha`

---

### 📂 Mappers (`data/mapper/`)

Clases que convierten entre entidades de Room y modelos de dominio.

- **`UserMapper.kt`**: `UserEntity` ↔ `User` (domain model)
- **`ProfileMapper.kt`**: `ProfileEntity` ↔ `Profile`
- **`FoodPreferenceMapper.kt`**: `FoodPreferenceEntity` ↔ `FoodPreference`
- **`MealMapper.kt`**: `MealEntity` ↔ `Meal`
- **`AchievementMapper.kt`**: `AchievementEntity` ↔ `Achievement`
- **`ChallengeMapper.kt`**: `ChallengeEntity` ↔ `Challenger` (domain model)
- **`ProgressMapper.kt`**: `ProgressEntity` ↔ `Progress`
- **`GameResultMapper.kt`**: `GameResultEntity` ↔ `GameResult`
- **`ChatMessageMapper.kt`**: Convierte mensajes de chat entre formatos de API y dominio

---

### 📂 Datos Remotos (`data/remote/`)

Interfaz con servicios externos (Firebase, OpenAI, Godot).

#### `data/remote/firebase/`

- **`FirebaseAuthDataSource.kt`**: Implementación de operaciones de autenticación Firebase
  - `signInWithEmailAndPassword()`
  - `signInWithGoogle()`
  - `createUserWithEmailAndPassword()`
  - `signOut()`
  - `getCurrentUser()`

- **`FirestoreDataSource.kt`**: Acceso a Firestore Database
  - Guardar/leer datos de usuario en la nube
  - Sincronización opcional con Room local

#### `data/remote/openai/` (o `openia/` como está en el código)

Integración con la API de OpenAI para el chatbot de recomendaciones.

- **`ChatApi.kt`**: Interfaz Retrofit que define endpoints de la API de OpenAI
  - `sendMessage(prompt: String): ChatResponse`
  
- **`ChatRemoteDataSource.kt`**: Implementación del data source que llama a `ChatApi`
  - Maneja autenticación (API key)
  - Procesa respuestas y errores

- **`ChatRequest.kt`**: Modelo de datos para requests a OpenAI
  - Contiene: `messages`, `model`, `temperature`, etc.

- **`ChatResponse.kt`**: Modelo de datos para respuestas de OpenAI
  - Contiene: `choices`, `usage`, etc.

#### `data/remote/godot/`

Comunicación con el motor de juegos Godot (integrado nativamente).

- **`GodotApi.kt`**: Interfaz para comunicación con Godot (probablemente JNI/NDK)
  
- **`GodotRemoteDataSource.kt`**: Data source que interactúa con Godot
  - Envía comandos al juego
  - Recibe resultados (XP, logros)

---

### 📂 Repositorios (`data/repository/`)

Implementaciones concretas de los repositorios definidos en `domain/repository/`. Coordinan fuentes de datos locales y remotas.

- **`AuthRepositoryImpl.kt`**: Implementa `AuthRepository`
  - Usa `FirebaseAuthDataSource` para autenticación
  - Puede guardar datos de usuario en Room después del login

- **`ChallengeRepositoryImpl.kt`**: Implementa `ChallengeRepository`
  - Gestiona retos diarios/semanales
  - Combina datos de Room y lógica de negocio

- **`ChatRepositoryImpl.kt`**: Implementa `ChatRepository`
  - Obtiene perfil/preferencias del usuario desde Room
  - Construye prompts enriquecidos para OpenAI
  - Llama a `ChatRemoteDataSource`
  - Guarda conversaciones en Room

- **`MealRepositoryImpl.kt`**: Implementa `MealRepository`
  - CRUD de comidas en Room
  - Actualiza progreso (calorías, XP)

- **`ProgressRepositoryImpl.kt`**: Implementa `ProgresoRepository`
  - Actualiza XP, nivel, racha
  - Calcula progreso basado en retos y juegos completados

- **`JuegosRepositoryImpl.kt`**: Implementa `GamesRepository`
  - Procesa resultados de juegos de Godot
  - Actualiza XP y logros mediante `GameResultDao`

---

## 📁 Inyección de Dependencias (`di/`)

Módulos de Hilt que proveen instancias de clases necesarias en toda la aplicación.

### `AppModule.kt`
- **Función**: Módulo básico que provee `Context` de la aplicación
- **Proveedores**: `provideAppContext()`

### `DatabaseModule.kt`
- **Función**: Configura Room Database y DAOs
- **Proveedores**:
  - `provideDatabase()`: Instancia singleton de `AppDatabase`
  - `provideUserDao()`, `provideProfileDao()`, etc.: Instancias de DAOs

### `NetworkModule.kt`
- **Función**: Configura Retrofit para llamadas HTTP
- **Proveedores**:
  - `provideRetrofit()`: Instancia de Retrofit con base URL y converter (Gson)

### `RepositoryModule.kt`
- **Función**: Proporciona implementaciones de repositorios
- **Proveedores**: Instancias de `*RepositoryImpl` inyectadas con sus dependencias

### `UseCaseModule.kt`
- **Función**: Proporciona casos de uso (use cases) con sus dependencias
- **Proveedores**: Todos los use cases inyectados con sus repositorios

### `WorkMarkerModule.kt`
- **Función**: Configura WorkManager para trabajos en segundo plano
- **Proveedores**: Configuración de trabajadores (Workers) para notificaciones

---

## 📁 Dominio (`domain/`)

Capa de negocio pura, sin dependencias de frameworks. Contiene modelos, interfaces de repositorios y casos de uso.

---

### 📂 Modelos (`domain/model/`)

Clases de datos inmutables que representan entidades de negocio.

- **`User.kt`**: Modelo de usuario (`uid`, `nombre`, `apellido`, `email`)
- **`Profile.kt`**: Perfil del usuario (`altura`, `peso`, `edad`, `imc`)
- **`FoodPreference.kt`**: Preferencias alimentarias (`alergias`, `gustos`, `preferenciaSaludable`)
- **`Meal.kt`**: Comida registrada (`nombre`, `calorias`, `fecha`, `tipoComida`)
- **`Achievement.kt`**: Logro/insignia (`nombre`, `descripcion`, `icono`, `fechaDesbloqueo`, `tipo`)
- **`Challenger.kt`**: Reto (`nombre`, `descripcion`, `tipo`, `xp`, `fechaInicio`, `fechaFin`, `completado`)
- **`Progress.kt`**: Progreso del usuario (`xp`, `nivel`, `racha`, `ultimaActividad`, `totalRetosDiarios`, `totalRetosSemanales`)
- **`GameResult.kt`**: Resultado de juego (`juegoId`, `xpGanada`, `logroId`, `fecha`)
- **`ChatMessage.kt`**: Mensaje de chat (`contenido`, `esUsuario`, `timestamp`)

---

### 📂 Repositorios (`domain/repository/`)

Interfaces que definen contratos para acceso a datos (implementadas en `data/repository/`).

- **`AuthRepository.kt`**: 
  - `signIn(email, password): Result<User>`
  - `signInWithGoogle(): Result<User>`
  - `register(userData): Result<User>`
  - `signOut(): Result<Unit>`

- **`ChallengeRepository.kt`**:
  - `getActiveChallenges(userId): Flow<List<Challenger>>`
  - `insertChallenge(challenge): Result<Unit>`
  - `updateChallengeProgress(challengeId, progress): Result<Unit>`

- **`ChatRepository.kt`**:
  - `sendMessage(userId, message): Flow<ChatMessage>`
  - `getChatHistory(userId): Flow<List<ChatMessage>>`

- **`GamesRepository.kt`**:
  - `saveGameResult(result): Result<Unit>`
  - `getGameResults(userId): Flow<List<GameResult>>`

- **`MealRepository.kt`**:
  - `insertMeal(meal): Result<Unit>`
  - `getMealsByDate(userId, date): Flow<List<Meal>>`

- **`ProgresoRepository.kt`**:
  - `getProgress(userId): Flow<Progress>`
  - `addXp(userId, xp): Result<Unit>`
  - `updateLevel(userId): Result<Unit>`
  - `trackStreak(userId): Result<Unit>`
  - `resetStreak(userId): Result<Unit>`

---

### 📂 Casos de Uso (`domain/usecase/`)

Clases que encapsulan lógica de negocio específica. Cada use case representa una acción del usuario.

#### `auth/`

- **`SignInUseCase.kt`**: Caso de uso para iniciar sesión con email/password
  - Llama a `AuthRepository.signIn()`
  - Maneja errores y transforma resultados

- **`SignInWithGoogleUseCase.kt`**: Inicio de sesión con Google
  - Usa `AuthRepository.signInWithGoogle()`

- **`RegisterUserCase.kt`**: Registro de nuevo usuario
  - Valida datos de entrada
  - Llama a `AuthRepository.register()`
  - Opcionalmente crea perfil inicial

- **`SignOutUseCase.kt`**: Cerrar sesión
  - Limpia sesión local y remota

#### `chat/`

- **`SendChatMessageUseCase.kt`**: Envía mensaje al chatbot
  - Obtiene perfil y preferencias del usuario desde repositorios
  - Construye prompt enriquecido con contexto del usuario (peso, edad, alergias)
  - Llama a `ChatRepository.sendMessage()`
  - Retorna respuesta del chatbot

#### `comida/`

- **`RegistrarComidaUseCase.kt`**: Registra una comida consumida
  - Valida datos (calorías, nombre)
  - Llama a `MealRepository.insertMeal()`
  - Actualiza progreso (XP, calorías diarias)

#### `juegos/`

- **`ProcessGameResultUseCase.kt`**: Procesa resultado de juego de Godot
  - Recibe resultado del juego (XP, logros)
  - Guarda resultado con `GamesRepository.saveGameResult()`
  - Actualiza progreso del usuario (suma XP, verifica nivel)
  - Desbloquea logros si corresponde

#### `progress/`

- **`AgregarXpUseCase.kt`**: Agrega XP al usuario
  - Suma XP al progreso actual
  - Llama a `ProgresoRepository.addXp()`
  - Puede disparar actualización de nivel

- **`ActualizarNivelUseCase.kt`**: Recalcula y actualiza el nivel del usuario
  - Calcula nivel basado en XP total
  - Usa fórmula: `nivel = sqrt(xp / constante)`
  - Actualiza nivel si cambió

- **`ActualizarBmiUseCase.kt`**: Calcula y actualiza IMC
  - Fórmula: `IMC = peso / (altura^2)`
  - Actualiza `Profile.imc`

- **`TrackStreakUseCase.kt`**: Rastrea racha de días consecutivos
  - Verifica última actividad
  - Si es el mismo día: no hace nada
  - Si es día siguiente: incrementa racha
  - Si pasó más de 1 día: resetea racha a 1

- **`ResetearRachaUseCase.kt`**: Resetea racha a 0
  - Se usa cuando el usuario no cumple actividad por más de 1 día

#### `retos/`

- **`ObtenerRetosActivosUseCase.kt`**: Obtiene retos activos del usuario
  - Filtra retos no completados y dentro de fecha válida
  - Usa `ChallengeRepository.getActiveChallenges()`

- **`InsertarRetoUseCase.kt`**: Crea un nuevo reto
  - Valida datos (nombre, descripción, tipo, XP)
  - Llama a `ChallengeRepository.insertChallenge()`

- **`ActualizarProgresoRetoUseCase.kt`**: Actualiza progreso de un reto
  - Marca reto como completado
  - Suma XP al usuario usando `AgregarXpUseCase`
  - Actualiza contador de retos completados en `Progress`

---

## 📁 Integración con Godot (`godot/`)

### `GodotLauncher.kt`
- **Función**: Activity o Contract que lanza el motor de juegos Godot
- **Responsabilidades**:
  - Inicializa el juego Godot
  - Pasa parámetros necesarios (si los hay)
  - Maneja ciclo de vida del juego

### `GodotResultContract.kt`
- **Función**: Contract para recibir resultados del juego
- **Comportamiento**: 
  - Define estructura de datos de resultado (XP, logroId)
  - Recibe resultado cuando el juego termina
  - Puede usar Activity Result API o callbacks

---

## 📁 Interfaz de Usuario (`ui/`)

Capa de presentación construida con Jetpack Compose y Material 3.

---

### 📂 Navegación (`ui/navigation/`)

### `Routes.kt`
- **Función**: Define rutas de navegación usando sealed classes con serialización
- **Estructura**:
  ```kotlin
  sealed class Routes : NavKey {
    sealed class Auth : Routes() {
      data object Login : Auth()
      data object CreateAccount : Auth()
    }
    sealed class Main : Routes() {
      data object Home : Main()
      data object Retos : Main()
      data object Game : Main()
      data object Progress : Main()
      data object Perfil : Main()
    }
  }
  ```

### `AppNavigation.kt`
- **Función**: NavHost principal que orquesta la navegación
- **Lógica**:
  - Usa `AuthNavGuard` para verificar autenticación
  - Si está autenticado: muestra `MainNavigation` con BottomBar
  - Si no está autenticado: muestra `AuthNavigation` (Login/CreateAccount)
- **Pantallas incluidas**: Todas las pantallas principales con sus rutas

---

### 📂 Componentes Reutilizables (`ui/components/`)

### `TopBar.kt`
- **Función**: Barra superior que muestra el nombre de la aplicación
- **Uso**: Se muestra en todas las pantallas principales

### `BottomBar.kt`
- **Función**: Barra de navegación inferior con iconos para: Home, Retos, Juegos, Progreso, Perfil
- **Comportamiento**: Navega entre pantallas principales usando Navigation 3

### `CustomButton.kt`
- **Función**: Botón personalizado reutilizable con estilo consistente
- **Props**: Texto, onClick, enabled, estilo

### `AchievementCard.kt`
- **Función**: Card que muestra información de un logro
- **Contenido**: Icono, nombre, descripción, fecha de desbloqueo

### `LoadingIndicator.kt`
- **Función**: Indicador de carga (CircularProgressIndicator) reutilizable
- **Uso**: Muestra estado de carga en pantallas que hacen llamadas asíncronas

---

### 📂 Gráficos (`ui/charts/`)

### `BmiIndicator.kt`
- **Función**: Indicador visual del IMC
- **Características**: Muestra valor numérico y posiblemente una barra/círculo con categoría (bajo peso/normal/sobrepeso/obesidad)

### `DailyAchievementsChart.kt`
- **Función**: Gráfico de cumplimiento de logros diarios
- **Tipo**: Probablemente gráfico de barras o líneas mostrando porcentaje de logros completados por día

### `WeeklyXpCaloriesChart.kt`
- **Función**: Gráfico combinado de XP y calorías semanales
- **Tipo**: Gráfico de líneas con dos series (XP y calorías) en el mismo eje

---

### 📂 Pantallas (`ui/screens/`)

Cada pantalla tiene su `*Screen.kt` (Composable) y `*ViewModel.kt` (ViewModel con lógica).

#### `Login/`

- **`LoginScreen.kt`**: Pantalla de inicio de sesión
  - Campos: Email, Password
  - Botones: "Iniciar sesión", "Iniciar sesión con Google", "Crear cuenta"
  - Navega a Home si login exitoso
  - Navega a CreateAccount si se presiona crear cuenta

- **`LoginViewModel.kt`**: ViewModel para Login
  - Estado: `email`, `password`, `isLoading`, `error`
  - Funciones: `signIn()`, `signInWithGoogle()`
  - Usa `SignInUseCase` y `SignInWithGoogleUseCase`

#### `CreateAcount/`

- **`CreateAcountScreen.kt`**: Pantalla de registro
  - Campos: Nombre, Apellido, Email, Password, Altura, Peso, Edad, Alergias
  - Botones: "Crear cuenta", "Iniciar sesión con Google", "Ya tengo cuenta"
  - Valida datos antes de registrar
  - Crea perfil inicial después del registro

- **`CreateAcountViewModel.kt`**: ViewModel para CreateAccount
  - Estado: Todos los campos del formulario, `isLoading`, `error`
  - Funciones: `register()`, `validateForm()`
  - Usa `RegisterUserCase`

#### `Home/`

- **`HomeScreen.kt`**: Pantalla principal después del login
  - **Card principal**: Muestra XP, barra de progreso circular, nivel
  - **Cards secundarias**: Card de "Retos" y "Juegos" que navegan a esas pantallas
  - **FAB (Floating Action Button)**: Abre Chatbot
  - Incluye TopBar y BottomBar

- **`HomeViewModel.kt`**: ViewModel para Home
  - Estado: `progress: Flow<Progress>`, `isLoading`
  - Funciones: `loadProgress()`
  - Usa `ProgresoRepository` o use cases de progreso

#### `Retos/`

- **`RetostScreen.kt`** (probablemente `RetosScreen.kt`): Lista de retos
  - **Cards de retos**: Cada card muestra:
    - Nombre del reto
    - Descripción
    - Tipo (diario/semanal)
    - XP que otorga
    - Tiempo restante (si está activo)
    - Botón "Comenar" / "Continuar"
  - Filtra retos activos vs completados

- **`RetosViewModel.kt`**: ViewModel para Retos
  - Estado: `challenges: Flow<List<Challenger>>`, `isLoading`
  - Funciones: `getActiveChallenges()`, `startChallenge(id)`, `completeChallenge(id)`
  - Usa `ObtenerRetosActivosUseCase`, `ActualizarProgresoRetoUseCase`

#### `Game/`

- **`GameLauncherScreen.kt`** (o `GameLaucherScreen.kt`): Pantalla de selección de juegos
  - **Cards de juegos**: Cada card muestra nombre del juego y botón "Iniciar juego"
  - Al presionar "Iniciar": Lanza Godot usando `GodotLauncher`
  - Recibe resultado usando `GodotResultContract`

- **`GameViewModel.kt`**: ViewModel para Game
  - Estado: Lista de juegos disponibles
  - Funciones: `launchGame(gameId)`, `handleGameResult(result)`
  - Usa `ProcessGameResultUseCase` después de recibir resultado

#### `Progress/`

- **`ProgressScreen.kt`**: Dashboard de progreso
  - **Sección superior**: Nivel, XP total, racha
  - **Gráficos**:
    - `DailyAchievementsChart`: Cumplimiento diario de logros
    - `WeeklyXpCaloriesChart`: XP y calorías semanales
  - **Estadísticas**: Total de retos diarios completados, total de retos semanales completados
  - **Logros**: Lista de logros obtenidos usando `AchievementCard`

- **`ProgressViewModel.kt`**: ViewModel para Progress
  - Estado: `progress: Flow<Progress>`, `achievements: Flow<List<Achievement>>`, datos para gráficos
  - Funciones: `loadProgress()`, `loadAchievements()`, `loadChartData()`
  - Usa repositorios de Progress y Achievement

#### `Profile/`

- **`ProfileScreen.kt`**: Pantalla de perfil del usuario
  - **Datos del usuario**: Nombre, edad, peso, alergias, IMC
  - **Galería de logros**: Grid de logros desbloqueados
  - **Configuración de notificaciones**:
    - Toggle para notificaciones de agua
    - Toggle para notificaciones de caminata
  - **Botón**: "Cerrar sesión" que llama a `SignOutUseCase`

- **`ProfileViewModel.kt`**: ViewModel para Profile
  - Estado: `user: Flow<User>`, `profile: Flow<Profile>`, `achievements: Flow<List<Achievement>>`, configuración de notificaciones
  - Funciones: `loadUserData()`, `updateNotificationSettings()`, `signOut()`
  - Usa repositorios de User, Profile, Achievement y `SignOutUseCase`

#### `ChatBot/`

- **`ChatBotScreen.kt`**: Pantalla de chatbot para recomendaciones
  - **Lista de mensajes**: Muestra historial de conversación (mensajes de usuario y respuestas del bot)
  - **Campo de texto**: Para escribir preguntas
  - **Botón enviar**: Envía mensaje
  - El chatbot tiene en cuenta: peso, edad, alergias del usuario (usado en el prompt)

- **`ChatBotViewModel.kt`**: ViewModel para ChatBot
  - Estado: `messages: Flow<List<ChatMessage>>`, `isLoading`, `currentMessage`
  - Funciones: `sendMessage(text)`, `loadChatHistory()`
  - Usa `SendChatMessageUseCase` que enriquece el prompt con perfil/preferencias
  - Guarda conversaciones en Room para persistencia

---

### 📂 Tema (`ui/theme/`)

### `ColorScheme.kt` (o `Color.kt`)
- **Función**: Define paleta de colores de la aplicación
- **Contenido**: Colores Material 3 (primary, secondary, tertiary, error, etc.)

### `Typography.kt` (o `Type.kt`)
- **Función**: Define estilos de texto
- **Contenido**: Tipografías para headings, body, labels, etc.

### `Theme.kt`
- **Función**: Composable principal que aplica tema Material 3
- **Contenido**: Combina `ColorScheme`, `Typography` y `Shapes` en un `MaterialTheme`

### `Shapes.kt`
- **Función**: Define formas de componentes (esquinas redondeadas)
- **Contenido**: `small`, `medium`, `large` para diferentes tamaños de componentes

---

## 📁 Workers (`work/`)

Trabajadores de WorkManager que ejecutan tareas en segundo plano.

### `HydrationReminderWorker.kt`
- **Función**: Notificación programada para recordar tomar agua
- **Trigger**: Probablemente cada 2-3 horas durante el día
- **Acción**: Muestra notificación local con recordatorio

### `WalkReminderWorker.kt` (o `WalkReinderWorker.kt`)
- **Función**: Notificación para recordar caminar
- **Trigger**: Horarios específicos (ej: mañana y tarde)
- **Acción**: Muestra notificación de recordatorio de actividad física

### `StreakAlertWorker.kt`
- **Función**: Alerta si el usuario está en riesgo de perder su racha
- **Trigger**: Fin del día si no completó actividad
- **Acción**: Notificación motivacional para mantener la racha

### `AchievementNotificationWorker.kt`
- **Función**: Notifica cuando se desbloquea un logro o se completa un reto
- **Trigger**: Evento disparado cuando se completa un logro/reto
- **Acción**: Notificación celebratoria con detalles del logro

---

## 🔄 Flujo de Datos General

1. **UI** (Screen) → Llama a funciones del **ViewModel**
2. **ViewModel** → Ejecuta **Use Cases**
3. **Use Case** → Llama a métodos del **Repository** (interfaz)
4. **RepositoryImpl** → Decide si usa **DataSource Local** (Room) o **DataSource Remoto** (Firebase/API)
5. **Mapper** → Convierte entre **Entity** (Room) y **Model** (Domain)
6. **Result** → Flujo de datos de vuelta (Flow/Result) → **ViewModel** → **UI State**

---

## 📝 Notas Finales

- **Arquitectura**: Clean Architecture con MVVM
- **Dependencias**: Inyectadas con Hilt en módulos `di/`
- **Navegación**: Navigation 3 con rutas serializables
- **Persistencia**: Room con KSP (no kapt)
- **UI**: Jetpack Compose + Material 3
- **Autenticación**: Firebase Auth + Google Sign-In
- **Chatbot**: Gemini 
- **Juegos**: Motor Godot integrado nativamente

Esta estructura permite escalabilidad, testabilidad y mantenibilidad siguiendo principios SOLID y mejores prácticas de Android.

