# 📊 ANÁLISIS DETALLADO: Flujo de Comunicación Android ↔ Godot 4.6

## 🎯 Resumen Ejecutivo

La aplicación VitaGame recibe datos de dos minijuegos Godot mediante un **sistema de archivos compartidos**. El flujo es:

```
┌─────────────────────────────────────────────────────────────────────────┐
│ 1. Usuario toca "Jugar" en GameLaucherScreen                             │
│ 2. GameViewModel emite packageName a través de NavigarAJuego             │
│ 3. GameLaucherScreen lanza el Intent hacia la APK de Godot              │
│ 4. Godot corre el juego y escribe resultado en game_result.txt          │
│ 5. Usuario regresa a VitaGame                                            │
│ 6. GameLaucherScreen lee game_result.txt desde almacenamiento externo   │
│ 7. Procesa el resultado y guarda en BD local                             │
│ 8. Suma XP al usuario si ganó                                            │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📱 FLUJO DETALLADO EN ANDROID

### 1️⃣ INICIO: GameLaucherScreen.kt (Líneas 45-77)

```kotlin
fun GameScreen(viewModel: GameViewModel = hiltViewModel()) {
    val context = LocalContext.current
    
    // ✅ Launcher configurado para recibir resultado de la APK de Godot
    val juegoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // 🔍 AQUÍ: A los ~200ms después de regresar del juego
        val archivo = File(context.getExternalFilesDir(null), "game_result.txt")
        val resultado = if (archivo.exists()) {
            val texto = archivo.readText().trim()    // Lee: "GANASTE" o "PERDISTE"
            archivo.delete()                           // Limpia después de leer
            texto.ifBlank { null }
        } else null
        viewModel.onRegresarDeJuego(resultado)      // Procesa el resultado
    }
    
    // ✅ Escucha los eventos de navegación
    LaunchedEffect(Unit) {
        viewModel.navegarAJuego.collect { packageName ->
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) juegoLauncher.launch(intent)  // Lanza la APK
            else viewModel.onRegresarDeJuego(null)            // Sin APK = sin juego
        }
    }
}
```

**Detalles críticos:**
- 🎮 `context.getExternalFilesDir(null)` = Carpeta privada de la app (e.g., `/storage/emulated/0/Android/data/com.example.vita/files/`)
- 📄 Nombre del archivo: `game_result.txt` (constante en `GodotGameDataSource`)
- ⚠️ El archivo se **ELIMINA** después de leerlo (importante para evitar releer resultados viejos)
- ⏱️ Se asume que Godot escribe el archivo ANTES de cerrar la app

---

### 2️⃣ PROCESAMIENTO: GameViewModel.kt (Líneas 38-86)

```kotlin
@HiltViewModel
class GameViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val procesarResultadoJuegoUseCase: ProcesarResultadoJuegoUseCase,
    private val agregarXpUseCase: AgregarXpUseCase
) : ViewModel() {
    
    private var juegoActual: String = ""  // Recuerda qué juego se abrió
    
    fun solicitarAbrirJuego(packageName: String) {
        viewModelScope.launch {
            juegoActual = packageName                          // "com.example.atrapasalud"
            _uiState.update { it.copy(juegoActivo = true) }   // Mostrar "Abriendo juego..."
            _navegarAJuego.emit(packageName)                  // Notificar a Screen
        }
    }
    
    fun onRegresarDeJuego(resultado: String?) {
        val uid = authRepository.getCurrentUserId() ?: return  // ⚠️ Sin usuario, sin procesamiento
        
        viewModelScope.launch {
            try {
                val resultadoFinal = resultado ?: return@launch  // ⚠️ Si resultado es null, retorna
                
                // ✅ Solo ganancia si resultado == "GANASTE"
                val xpGanada = if (resultadoFinal == "GANASTE") 
                    GameConfig.XP_MINIJUEGO_GODOT  // 170 XP
                else 
                    0
                
                // Determinar nombre del juego
                val nombreJuego = when (juegoActual) {
                    "com.example.atrapasalud" -> "AtrapaSalud"
                    "com.example.velocidad" -> "Velocidad"
                    else -> "Juego"
                }
                
                // ✅ Crear modelo GameResult
                procesarResultadoJuegoUseCase(GameResult(
                    id       = 0,           // Auto-generado por Room
                    userId   = uid,         // Firebase UID
                    name     = nombreJuego,
                    weight   = 10,          // Importancia (fija)
                    xpEarned = xpGanada,    // 170 o 0
                    date     = System.currentTimeMillis()
                ))
                
                // ✅ Agregar XP al usuario (actualiza en tabla 'users')
                if (xpGanada > 0) agregarXpUseCase(uid, xpGanada)
                
                // ✅ Mostrar feedback al usuario
                _uiState.update { it.copy(
                    juegoActivo      = false,
                    mensajeResultado = if (xpGanada > 0) 
                        "+$xpGanada XP ganados!" 
                    else 
                        "Sigue intentando"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    juegoActivo = false,
                    error = e.message
                )}
            }
        }
    }
}
```

**Flujo condicional:**
```
resultado = null       → No procesa nada
resultado = "GANASTE"  → +170 XP + GuardaBD
resultado = "PERDISTE" → +0 XP + GuardaBD (sin reward)
```

---

### 3️⃣ ALMACENAMIENTO: GameRepositoryImpl.kt

```kotlin
@Singleton
class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val userDao: UserDao
) : GameRepository {
    
    override suspend fun saveGameResult(result: GameResult): GameResult {
        // ✅ Insertar en tabla 'game_result' de SQLite
        gameDao.insertResult(result.toEntity())
        return result
    }
    
    override suspend fun addXpToUser(uid: String, xp: Int): Int {
        val user = userDao.getUserById(uid) ?: return 0
        val nuevaXp = user.currentXp + xp
        val nuevoNivel = user.currentLevel
        
        // ✅ Actualizar tabla 'users' con nuevo XP
        userDao.updateUserXpAndLevel(uid, nuevaXp, nuevoNivel)
        
        return nuevaXp
    }
}
```

**Operaciones en BD:**
1. INSERT en `game_result` (tabla de histórico)
2. UPDATE en `users.currentXp` (acumulativo)

---

### 4️⃣ MAPEO DE DATOS: Mapper.kt

```kotlin
// Convertir GameResult (Domain) → GameEntity (Data)
fun GameResult.toEntity() = GameEntity(
    id        = id,
    userId    = userId,
    name      = name,      // "AtrapaSalud" o "Velocidad"
    weight    = weight,
    xpEarned  = xpEarned,  // 170 o 0
    date      = date
)

// Convertir GameEntity (Data) → GameResult (Domain)
fun GameEntity.toDomain() = GameResult(
    id        = id,
    userId    = userId,
    name      = name,
    weight    = weight,
    xpEarned  = xpEarned,
    date      = date
)
```

---

## 📂 ESTRUCTURA DE ARCHIVOS Y PERMISOS

### Ruta de Almacenamiento

```
/storage/emulated/0/
├── Android/
│   └── data/
│       └── com.example.vita/           ← App privada
│           └── files/
│               └── game_result.txt      ← 🎯 AQUÍ escribe Godot
```

**Ventajas de usar `getExternalFilesDir()`:**
- ✅ No requiere `READ_EXTERNAL_STORAGE` en Android 11+
- ✅ Auto-limpiada al desinstalar app
- ✅ No visible en galería de fotos
- ✅ Tanto Vita como Godot pueden acceder

### Permisos en AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="29" />

<!-- ✅ Permite que Vita encuentre las APKs de Godot -->
<queries>
    <package android:name="com.example.atrapasalud" />
    <package android:name="com.example.velocidad" />
</queries>
```

**Notas:**
- Los permisos de almacenamiento son para `Android < 11`
- En `Android 11+` se usa `getExternalFilesDir()` sin permisos adicionales
- El bloque `<queries>` es obligatorio para `targetSdkVersion >= 30`

---

## 🎮 CONFIGURACIÓN DE GODOT

### Lo que Godot DEBE hacer:

```gdscript
# En Godot 4.6, cuando el juego termina:

func _on_game_over(won: bool):
    var result = "GANASTE" if won else "PERDISTE"
    
    # Escribir el resultado en el archivo compartido
    var file = FileAccess.open(
        "/storage/emulated/0/Android/data/com.example.vita/files/game_result.txt",
        FileAccess.WRITE
    )
    
    if file:
        file.store_string(result)
        file.close()
    
    # Cerrar el juego (regresar a Vita)
    get_tree().quit()
```

### Rutas Posibles que Godot Podría Usar:

❌ **Incorrecto:**
```
"/data/data/com.example.vita/files/game_result.txt"
"/sdcard/DCIM/game_result.txt"
"/storage/emulated/0/game_result.txt"
"game_result.txt"  (cwd no confiable)
```

✅ **Correcto:**
```
"/storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"
```

---

## 🛢️ ESQUEMA DE BASE DE DATOS

### Tabla: `game_result`

```sql
CREATE TABLE game_result (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId TEXT NOT NULL,
    name TEXT NOT NULL,         -- "AtrapaSalud" o "Velocidad"
    weight INTEGER,
    xpEarned INTEGER,           -- 170 o 0
    date INTEGER,               -- Timestamp en ms
    FOREIGN KEY(userId) REFERENCES users(idUsuario) ON DELETE CASCADE
);
```

### Tabla: `users`

```sql
CREATE TABLE users (
    idUsuario TEXT PRIMARY KEY,
    userName TEXT,
    currentXp INTEGER,          -- Se suma aquí (+170 XP)
    currentLevel INTEGER,
    -- ... otros campos
);
```

---

## 🔄 FLUJO COMPLETO DE DATOS

```
GODOT APK
    ↓
[Usuario juega]
    ↓
[Si gana] → Escribe "GANASTE" en game_result.txt
    ↓
[Cierra la app]
    ↓
────────────────────────────────────────────────────
VITA APP (Regresa del Intent)
    ↓
GameLaucherScreen detecta que volvió
    ↓
Lee game_result.txt desde /storage/emulated/0/Android/data/com.example.vita/files/
    ↓
Obtiene: string = "GANASTE"
    ↓
Llama: viewModel.onRegresarDeJuego("GANASTE")
    ↓
GameViewModel procesa:
    - Verifica: resultado == "GANASTE" ✅
    - xpGanada = 170
    - Crea GameResult(userId, "AtrapaSalud", xpEarned=170, ...)
    ↓
ProcesarResultadoJuegoUseCase:
    - Inserta en game_result BD
    - También suma XP en tabla users
    ↓
Muestra al usuario: "+170 XP ganados!"
    ↓
BD LOCAL (SQLite) sincroniza con Firebase (si está configurado)
```

---

## ⚠️ PROBLEMAS POTENCIALES IDENTIFICADOS

### 1. **Ruta de Archivo Inconsistente**
- ❌ Si Godot escribe en otra ruta, Vita NO LO ENCUENTRA
- ✅ Solución: Verificar ruta exacta en scripts Godot

### 2. **Resultado == null**
```kotlin
// En GameViewModel.onRegresarDeJuego():
val resultadoFinal = resultado ?: return@launch  // ⚠️ Retorna sin procesar
```
- Si el archivo no existe → `null` → No se procesa nada
- ✅ Solución: Verificar que Godot escribe el archivo ANTES de cerrar

### 3. **Sincronización Temporal**
- Godot debe escribir y GUARDAR el archivo antes de hacer `quit()`
- ⚠️ Si hay delay, Android puede no ver el archivo

### 4. **Permisos del Almacenamiento Externo**
- En Android 13+ (API 33+) se requiere `READ_MEDIA_IMAGES` o equivalente
- Actualmente solo tienes `READ_EXTERNAL_STORAGE` (deprecated)

### 5. **Múltiples Ejecuciones**
- Si usuario abre/cierra juego rápido múltiples veces
- Ya hay lógica para evitar: se **DELETE** el archivo después de leer
- ✅ Pero el `juegoActual` podría estar desfasado

---

## 🧪 CÓMO VERIFICAR QUE FUNCIONA

### Testing Manual:

```kotlin
// Agregar esto en GameViewModel para debug:
fun onRegresarDeJuego(resultado: String?) {
    val uid = authRepository.getCurrentUserId() ?: return
    
    Log.d("GameDebug", "[1] Resultado recibido: $resultado")
    Log.d("GameDebug", "[2] Usuario actual: $uid")
    Log.d("GameDebug", "[3] Juego actual: $juegoActual")
    
    viewModelScope.launch {
        try {
            val resultadoFinal = resultado ?: run {
                Log.e("GameDebug", "[ERROR] Resultado NULL - archivo no encontrado?")
                return@launch
            }
            
            val xpGanada = if (resultadoFinal == "GANASTE") 170 else 0
            Log.d("GameDebug", "[4] XP a sumar: $xpGanada")
            
            // ... resto del código
        } catch (e: Exception) {
            Log.e("GameDebug", "[ERROR] Excepción: ${e.message}", e)
        }
    }
}
```

### Verificar Archivo:

```bash
# En Android Studio - Device File Explorer:
# Navega a: /storage/emulated/0/Android/data/com.example.vita/files/
# Deberías ver game_result.txt después de jugar
```

---

## 📋 LISTA DE VERIFICACIÓN

- [ ] ¿Godot escribe en `/storage/emulated/0/Android/data/com.example.vita/files/game_result.txt`?
- [ ] ¿El contenido es exactamente "GANASTE" o "PERDISTE"?
- [ ] ¿Vita tiene permisos para leer ese archivo?
- [ ] ¿El archivo se elimina correctamente después de leer?
- [ ] ¿El XP se suma en la tabla `users`?
- [ ] ¿El histórico se guarda en tabla `game_result`?
- [ ] ¿Se muestra el feedback del +XP al usuario?

---

## 📊 VALORES CONFIGURABLES

```kotlin
// GameConfig.kt
object GameConfig {
    const val XP_MINIJUEGO_GODOT = 170  // Recompensa por victoria
}

// GameViewModel.kt
val nombreJuego = when (juegoActual) {
    "com.example.atrapasalud" -> "AtrapaSalud"      // Nombre visualización
    "com.example.velocidad" -> "Velocidad"
    else -> "Juego"
}
```

---

## 🔗 ARCHIVOS RELACIONADOS

```
LECTURA:
├── GameLaucherScreen.kt           ← Inicia el Intent + Lee archivo
└── GameViewModel.kt               ← Procesa resultado

ALMACENAMIENTO:
├── GameRepositoryImpl.kt           ← Guarda en BD
├── GameDao.kt                      ← Queries SQL
└── GameEntity.kt                   ← Schema

CONFIGURACIÓN:
├── RepositoryModule.kt            ← Inyección de dependencias
├── GodotGameDataSource.kt         ← Manejo de archivos
└── AndroidManifest.xml            ← Permisos

MODELOS:
├── GameResult.kt                  ← Dominio
└── GameEntity.kt                  ← Persistencia
```

---

## 🎯 PRÓXIMO PASO

Antes de revisar scripts de Godot, necesitamos confirmar:

1. ¿Están las APKs compiladas y instaladas?
2. ¿Cuál es la ruta exacta que usan en Godot?
3. ¿Qué versión de Godot 4.6 es (4.6.0, 4.6.1, etc.)?
4. ¿Hay accessos a FileSystem en permisos de Godot?

