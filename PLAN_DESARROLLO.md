# 🚀 Plan de Desarrollo - Aplicación Vita

Este documento describe el orden correcto para empezar y desarrollar el proyecto **Vita**, siguiendo Clean Architecture y mejores prácticas.

---

## 📋 Fase 0: Configuración Inicial (PRIORITARIO)

### ✅ Paso 0.1: Application Class para Hilt
**Archivo**: `app/src/main/java/com/example/vita/VitaGameApp.kt`

**¿Por qué primero?** Hilt necesita una clase Application para inicializar el grafo de dependencias.

```kotlin
package com.example.vita

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitaGameApp : Application()
```

**Registrar en AndroidManifest.xml**:
```xml
<application
    android:name=".VitaGameApp"
    ...>
```

---

## 📋 Fase 1: Capa de Datos - Base (DENTRO → FUERA)

### ✅ Paso 1.1: Completar Entidades Room
**Orden sugerido**:
1. `UserEntity.kt` ✅ (ya existe)
2. `ProfileEntity.kt` ✅ (verificar que esté completa)
3. `FoodPreferenceEntity.kt` ✅
4. `ProgressEntity.kt` ✅
5. `MealEntity.kt` ✅
6. `AchievementEntity.kt` ✅
7. `ChallengeEntity.kt` ✅
8. `GameResultEntity.kt` ✅

**Verificar que cada entidad tenga**:
- `@Entity` con `tableName`
- `@PrimaryKey`
- Relaciones FK correctas
- Índices si es necesario

### ✅ Paso 1.2: Crear/Completar DAOs
**Orden sugerido** (mismo que entidades):

1. **UserDao.kt**
   ```kotlin
   @Dao
   interface UserDao {
       @Query("SELECT * FROM users WHERE uid = :uid")
       fun getUserById(uid: String): Flow<UserEntity?>
       
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertUser(user: UserEntity)
       
       @Update
       suspend fun updateUser(user: UserEntity)
   }
   ```

2. **ProfileDao.kt** - CRUD de perfil
3. **FoodPreferenceDao.kt** - CRUD de preferencias
4. **ProgressDao.kt** - CRUD de progreso
5. **MealDao.kt** - CRUD de comidas + queries por fecha
6. **AchievementDao.kt** - CRUD de logros
7. **ChallengeDao.kt** - CRUD de retos + queries de retos activos
8. **GameResultDao.kt** - CRUD de resultados de juegos

### ✅ Paso 1.3: Verificar AppDatabase
**Archivo**: `data/local/db/AppDatabase.kt`

- ✅ Ya existe
- Verificar que todas las entidades estén listadas
- Verificar que todos los DAOs estén declarados

### ✅ Paso 1.4: Crear Mappers
**Orden sugerido**:

1. `UserMapper.kt` - `UserEntity` ↔ `User`
2. `ProfileMapper.kt` - `ProfileEntity` ↔ `Profile`
3. `FoodPreferenceMapper.kt` - `FoodPreferenceEntity` ↔ `FoodPreference`
4. `ProgressMapper.kt` - `ProgressEntity` ↔ `Progress`
5. `MealMapper.kt` - `MealEntity` ↔ `Meal`
6. `AchievementMapper.kt` - `AchievementEntity` ↔ `Achievement`
7. `ChallengeMapper.kt` - `ChallengeEntity` ↔ `Challenger`
8. `GameResultMapper.kt` - `GameResultEntity` ↔ `GameResult`
9. `ChatMessageMapper.kt` - Para conversaciones del chatbot

**Ejemplo de mapper**:
```kotlin
object UserMapper {
    fun toDomain(entity: UserEntity): User = User(
        uid = entity.uid,
        name = entity.name,
        lastname = entity.lastname,
        email = entity.email,
        level = entity.level,
        xp = entity.xp
    )
    
    fun toEntity(domain: User): UserEntity = UserEntity(
        uid = domain.uid,
        name = domain.name,
        lastname = domain.lastname,
        email = domain.email,
        level = domain.level,
        xp = domain.xp
    )
}
```

---

## 📋 Fase 2: Capa de Dominio

### ✅ Paso 2.1: Verificar Modelos de Dominio
**Ubicación**: `domain/model/`

Verificar que todos los modelos estén completos y coincidan con las entidades.

### ✅ Paso 2.2: Crear Interfaces de Repositorios
**Ubicación**: `domain/repository/`

**Orden de creación** (según dependencias):

1. **AuthRepository.kt** (sin dependencias)
   ```kotlin
   interface AuthRepository {
       fun isUserLoggedInFlow: Flow<Boolean>
       suspend fun signIn(email: String, password: String): Result<User>
       suspend fun signInWithGoogle(): Result<User>
       suspend fun register(email: String, password: String, userData: UserData): Result<User>
       suspend fun signOut(): Result<Unit>
       fun getCurrentUserId(): String?
   }
   ```

2. **ProgresoRepository.kt** (depende de User)
3. **MealRepository.kt** (depende de User)
4. **ChallengeRepository.kt** (depende de User, Progress)
5. **GamesRepository.kt** (depende de User, Progress)
6. **ChatRepository.kt** (depende de User, Profile, FoodPreference)

### ✅ Paso 2.3: Crear Casos de Uso Básicos (Auth primero)
**Ubicación**: `domain/usecase/`

**Orden sugerido**:

#### 2.3.1: Casos de Uso de Autenticación
1. **SignInUseCase.kt**
2. **SignInWithGoogleUseCase.kt**
3. **RegisterUserCase.kt**
4. **SignOutUseCase.kt**

**Ejemplo**:
```kotlin
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signIn(email, password)
    }
}
```

#### 2.3.2: Casos de Uso de Progreso (después de Auth)
5. **AgregarXpUseCase.kt**
6. **ActualizarNivelUseCase.kt**
7. **ActualizarBmiUseCase.kt**
8. **TrackStreakUseCase.kt**
9. **ResetearRachaUseCase.kt**

#### 2.3.3: Otros Casos de Uso
10. **RegistrarComidaUseCase.kt**
11. **ObtenerRetosActivosUseCase.kt**
12. **InsertarRetoUseCase.kt**
13. **ActualizarProgresoRetoUseCase.kt**
14. **ProcessGameResultUseCase.kt**
15. **SendChatMessageUseCase.kt**

---

## 📋 Fase 3: Implementación de Repositorios

### ✅ Paso 3.1: Data Sources Remotos

#### 3.1.1: Firebase Auth DataSource
**Archivo**: `data/remote/firebase/FirebaseAuthDataSource.kt`
- ✅ Ya existe parcialmente
- Completar métodos: `signIn()`, `signInWithGoogle()`, `register()`

#### 3.1.2: Firestore DataSource (Opcional)
**Archivo**: `data/remote/firebase/FirestoreDataSource.kt`
- Para sincronización en la nube (puede hacerse después)

#### 3.1.3: OpenAI DataSource (Después, no crítico ahora)
**Archivos**: `data/remote/openai/`
- Implementar después de que funcione la autenticación

#### 3.1.4: Godot DataSource (Después, no crítico ahora)
**Archivos**: `data/remote/godot/`
- Implementar cuando se integre Godot

### ✅ Paso 3.2: Implementar Repositorios
**Ubicación**: `data/repository/`

**Orden sugerido**:

1. **AuthRepositoryImpl.kt**
   - Implementa `AuthRepository`
   - Usa `FirebaseAuthDataSource`
   - Guarda usuario en Room después de login exitoso
   - Usa `UserMapper` para convertir

2. **ProgresoRepositoryImpl.kt**
   - Implementa `ProgresoRepository`
   - Usa `ProgressDao` + `ProgressMapper`

3. **MealRepositoryImpl.kt**
   - Implementa `MealRepository`
   - Usa `MealDao` + `MealMapper`

4. **ChallengeRepositoryImpl.kt**
   - Implementa `ChallengeRepository`
   - Usa `ChallengeDao` + `ChallengeMapper`

5. **JuegosRepositoryImpl.kt**
   - Implementa `GamesRepository`
   - Usa `GameResultDao` + `GameResultMapper`

6. **ChatRepositoryImpl.kt**
   - Implementa `ChatRepository`
   - Combina datos de Profile y FoodPreference
   - Llama a OpenAI (después)

---

## 📋 Fase 4: Inyección de Dependencias (Hilt)

### ✅ Paso 4.1: Verificar Módulos Existentes
**Ubicación**: `di/`

- ✅ **AppModule.kt** - Ya existe
- ✅ **DatabaseModule.kt** - Ya existe
- ✅ **NetworkModule.kt** - Ya existe (verificar base URL de OpenAI)

### ✅ Paso 4.2: Completar RepositoryModule.kt
**Archivo**: `di/RepositoryModule.kt`

Proveer todas las implementaciones de repositorios:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgresoRepositoryImpl
    ): ProgresoRepository
    
    // ... resto de repositorios
}
```

### ✅ Paso 4.3: Completar UseCaseModule.kt
**Archivo**: `di/UseCaseModule.kt`

Proveer todos los casos de uso:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    fun provideSignInUseCase(
        authRepository: AuthRepository
    ): SignInUseCase = SignInUseCase(authRepository)
    
    // ... resto de use cases
}
```

### ✅ Paso 4.4: WorkManagerModule.kt (Después)
- Configurar después de que funcionen las pantallas principales

---

## 📋 Fase 5: Core Utilities

### ✅ Paso 5.1: Completar Utilidades Core
**Ubicación**: `core/`

1. **Constants.kt** - Agregar constantes necesarias (API keys, URLs, etc.)
2. **Result.kt** - Wrapper Result<T> si no existe
3. **Formatters.kt** - Funciones de formateo (calorías, XP, IMC)
4. **DateTimeUtils.kt** - Utilidades de fechas y rachas
5. **Dispatchers.kt** - Proveedor de CoroutineDispatchers para Hilt
6. **AuthNavGuard.kt** - Guardia de navegación (ya está en navegación)

---

## 📋 Fase 6: UI - Navegación y Componentes Base

### ✅ Paso 6.1: Verificar Routes.kt
**Archivo**: `ui/navigation/Routes.kt`
- ✅ Ya existe
- Verificar que todas las rutas estén definidas

### ✅ Paso 6.2: Completar AppNavigation.kt
**Archivo**: `ui/navigation/AppNavigation.kt`
- ✅ Ya existe parcialmente
- Verificar que funcione correctamente

### ✅ Paso 6.3: Crear Componentes Base
**Ubicación**: `ui/components/`

1. **TopBar.kt** - Barra superior con nombre de la app
2. **BottomBar.kt** - Barra de navegación inferior ✅ (verificar)
3. **CustomButton.kt** - Botón personalizado
4. **LoadingIndicator.kt** - Indicador de carga
5. **AchievementCard.kt** - Card de logro

### ✅ Paso 6.4: Configurar Tema
**Ubicación**: `ui/theme/`

1. **Color.kt** - Paleta de colores Material 3
2. **Type.kt** - Tipografías
3. **Theme.kt** - Composable principal del tema ✅ (verificar)
4. **Shapes.kt** (opcional) - Formas personalizadas

---

## 📋 Fase 7: UI - Pantallas de Autenticación (PRIMERA FUNCIONALIDAD COMPLETA)

### ✅ Paso 7.1: Pantalla de Login
**Archivos**: `ui/screens/Login/`

1. **LoginScreen.kt**
   - Campos: Email, Password
   - Botones: "Iniciar sesión", "Iniciar con Google", "Crear cuenta"
   - Manejo de errores
   - Indicador de carga

2. **LoginViewModel.kt**
   - Usa `SignInUseCase` y `SignInWithGoogleUseCase`
   - Estados: `email`, `password`, `isLoading`, `error`
   - Funciones: `signIn()`, `signInWithGoogle()`

### ✅ Paso 7.2: Pantalla de Crear Cuenta
**Archivos**: `ui/screens/CreateAcount/`

1. **CreateAcountScreen.kt**
   - Campos: Nombre, Apellido, Email, Password, Altura, Peso, Edad, Alergias
   - Validación de formulario
   - Botones: "Crear cuenta", "Iniciar con Google", "Ya tengo cuenta"

2. **CreateAcountViewModel.kt**
   - Usa `RegisterUserCase`
   - Valida datos
   - Crea perfil inicial después del registro

### ✅ Paso 7.3: Conectar MainActivity con Navegación
**Archivo**: `MainActivity.kt`

```kotlin
setContent {
    VitaTheme {
        AppNavigation() // Reemplazar código comentado
    }
}
```

---

## 📋 Fase 8: UI - Pantallas Principales (Después de Auth)

### ✅ Paso 8.1: Pantalla Home
**Archivos**: `ui/screens/Home/`

1. **HomeScreen.kt**
   - Card principal: XP, barra circular de progreso, nivel
   - Cards secundarias: "Retos", "Juegos"
   - FAB: Abre Chatbot

2. **HomeViewModel.kt**
   - Carga progreso del usuario
   - Usa `ProgresoRepository`

### ✅ Paso 8.2: Pantalla Perfil
**Archivos**: `ui/screens/Profile/`

1. **ProfileScreen.kt**
   - Datos del usuario
   - Configuración de notificaciones
   - Botón cerrar sesión

2. **ProfileViewModel.kt**
   - Usa `SignOutUseCase`
   - Carga datos del usuario

### ✅ Paso 8.3: Pantalla Progreso
**Archivos**: `ui/screens/Progress/`

1. **ProgressScreen.kt**
   - Dashboard con gráficos
   - Estadísticas
   - Lista de logros

2. **ProgressViewModel.kt**
   - Carga progreso y logros

### ✅ Paso 8.4: Pantalla Retos
**Archivos**: `ui/screens/Retos/`

1. **RetosScreen.kt**
   - Lista de retos activos
   - Cards de retos

2. **RetosViewModel.kt**
   - Usa `ObtenerRetosActivosUseCase`
   - Maneja inicio/completado de retos

### ✅ Paso 8.5: Pantalla Juegos
**Archivos**: `ui/screens/Game/`

1. **GameLauncherScreen.kt**
   - Cards de juegos disponibles
   - Botón "Iniciar juego"

2. **GameViewModel.kt**
   - Lanza Godot (después)

### ✅ Paso 8.6: Pantalla Chatbot
**Archivos**: `ui/screens/ChatBot/`

1. **ChatBotScreen.kt**
   - Lista de mensajes
   - Campo de texto
   - Botón enviar

2. **ChatBotViewModel.kt**
   - Usa `SendChatMessageUseCase`
   - Enriquecimiento de prompts (después)

---

## 📋 Fase 9: Funcionalidades Avanzadas (DESPUÉS)

### ✅ Paso 9.1: Gráficos
**Ubicación**: `ui/charts/`

1. **BmiIndicator.kt**
2. **DailyAchievementsChart.kt**
3. **WeeklyXpCaloriesChart.kt**

### ✅ Paso 9.2: Integración con Godot
**Ubicación**: `godot/`

1. **GodotLauncher.kt**
2. **GodotResultContract.kt**

### ✅ Paso 9.3: Integración con OpenAI
**Archivos**: `data/remote/openai/`

1. Completar `ChatApi.kt`
2. Completar `ChatRemoteDataSource.kt`
3. Actualizar `ChatRepositoryImpl.kt`

### ✅ Paso 9.4: Workers (Notificaciones)
**Ubicación**: `work/`

1. **HydrationReminderWorker.kt**
2. **WalkReminderWorker.kt**
3. **StreakAlertWorker.kt**
4. **AchievementNotificationWorker.kt**

---

## 🎯 Orden Recomendado de Desarrollo (RESUMEN)

### **SEMANA 1: Base y Autenticación**
1. ✅ Fase 0: Application Class
2. ✅ Fase 1: Entidades, DAOs, Mappers (User, Profile principalmente)
3. ✅ Fase 2: Repository interfaces y Use Cases de Auth
4. ✅ Fase 3: AuthRepositoryImpl
5. ✅ Fase 4: Hilt Modules (Auth)
6. ✅ Fase 6: Componentes base y navegación
7. ✅ Fase 7: Pantallas de Login y Register
8. ✅ Conectar MainActivity

### **SEMANA 2: Funcionalidades Principales**
9. ✅ Fase 1: Completar entidades restantes
10. ✅ Fase 2: Use Cases de Progreso y Retos
11. ✅ Fase 3: Repositorios de Progreso, Retos, Meals
12. ✅ Fase 5: Utilidades Core
13. ✅ Fase 8: Pantallas Home, Perfil, Progreso, Retos

### **SEMANA 3: Funcionalidades Avanzadas**
14. ✅ Fase 8: Pantalla Juegos (sin Godot todavía)
15. ✅ Fase 9: Gráficos
16. ✅ Fase 9: Integración OpenAI (Chatbot)
17. ✅ Fase 9: Workers (Notificaciones)

### **SEMANA 4: Integración Godot**
18. ✅ Fase 9: Integración con Godot
19. ✅ Testing
20. ✅ Ajustes finales

---

## ✅ Checklist Inicial (LO QUE DEBES HACER PRIMERO)

- [ ] **Crear `VitaGameApp.kt`** (Application class para Hilt)
- [ ] **Registrar en AndroidManifest.xml**
- [ ] **Verificar que todas las entidades estén completas**
- [ ] **Completar DAOs básicos** (UserDao, ProfileDao, ProgressDao)
- [ ] **Crear Mappers básicos** (UserMapper, ProfileMapper, ProgressMapper)
- [ ] **Completar `AuthRepositoryImpl`**
- [ ] **Completar `RepositoryModule.kt`** (al menos AuthRepository)
- [ ] **Completar `UseCaseModule.kt`** (al menos use cases de Auth)
- [ ] **Conectar `MainActivity` con `AppNavigation`**
- [ ] **Probar login básico**

---

## 📝 Notas Importantes

1. **Siempre de dentro hacia afuera**: Domain → Data → UI
2. **Testear cada capa antes de pasar a la siguiente**
3. **Un feature completo a la vez**: Auth primero, luego Home, luego Retos, etc.
4. **No implementar Godot/OpenAI hasta que lo básico funcione**
5. **Usar `Result<T>` para manejo de errores**
6. **Usar `Flow` para datos reactivos**
7. **Hilt debe estar configurado antes de crear ViewModels**

---

## 🚨 Errores Comunes a Evitar

1. ❌ Crear ViewModels antes de tener los Use Cases
2. ❌ Olvidar registrar Application class en Manifest
3. ❌ No usar Mappers (mezclar Entity con Domain)
4. ❌ Implementar UI antes de Data Layer
5. ❌ Olvidar inyectar dependencias en módulos Hilt
6. ❌ No manejar estados de carga/error en UI

---

¡Buena suerte con el desarrollo! 🚀
