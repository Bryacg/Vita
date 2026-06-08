# 🔧 SOLUCIÓN: game_result.txt No Se Crea + Bucle "Abriendo Juego..."

## 🎯 PROBLEMA IDENTIFICADO

**Síntomas:**
- ❌ `game_result.txt` no aparece en `/storage/.../files/`
- ❌ Vita se queda en bucle: "Abriendo juego..."
- ✅ APKs están instaladas y funcionan cuando se lanzan manualmente

**Causa probable:** Los scripts de Godot **NO están escribiendo el archivo** en la ruta correcta

---

## 🛠️ SOLUCIÓN PARA GODOT 4.6

### 1. VERIFICAR: ¿Godot está escribiendo el archivo?

En los scripts de **atrapa-2** y **golpeador-de-comida-rápida**, busca la función que maneja el fin del juego.

Debe tener algo así (CORREGIDO):

```gdscript
# game_manager.gd o main.gd
extends Node

# ✅ CORRECTO: Ruta exacta
const RESULT_FILE_PATH = "/storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt"
# Para golpeador: "/storage/emulated/0/Android/data/com.example.velocidad/files/game_result.txt"

func _on_game_over(player_won: bool):
    """Llamado cuando el juego termina"""
    var result = "GANASTE" if player_won else "PERDISTE"
    
    print("🎮 Guardando resultado: ", result)
    print("📁 En ruta: ", RESULT_FILE_PATH)
    
    # ✅ Escribir el archivo
    var file = FileAccess.open(RESULT_FILE_PATH, FileAccess.WRITE)
    
    if file:
        file.store_string(result)
        file.close()
        print("✅ Archivo guardado exitosamente")
    else:
        print("❌ ERROR: No se pudo abrir el archivo")
        print("   Error: ", FileAccess.get_open_error())
    
    # ✅ Esperar un poco para asegurar que se escriba
    await get_tree().create_timer(1.0).timeout
    
    # ✅ Cerrar el juego (IMPORTANTE)
    get_tree().quit()
```

### 2. CHECKLIST PER-APK

#### 📦 **atrapa-2** (com.example.atrapasalud)

**Archivo: export_presets.cfg**

```ini
[preset.0]
name="Android Debug"
platform="Android"
runnable=true
custom_features=""
export_filter="all_resources"
include_filter=""
exclude_filter=""
export_path="./atrapa-2.apk"
script_export_mode=1
script_encryption_key=""

# ✅ Permisos CRÍTICOS
android/permissions=PoolStringArray( "WRITE_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE" )

# ✅ Otras opciones importantes
android/use_custom_build=false
android/gradle_build=false
android/build_version="1.0"
```

**Revisar en el código principal (`main.gd` o equivalente):**

```gdscript
# Verificar que existe esta variable/constante
const GAME_PACKAGE = "com.example.atrapasalud"
const RESULT_FILE_PATH = "/storage/emulated/0/Android/data/" + GAME_PACKAGE + "/files/game_result.txt"
```

#### 📦 **golpeador-de-comida-rápida** (com.example.velocidad)

Lo mismo, pero con:

```gdscript
const GAME_PACKAGE = "com.example.velocidad"
const RESULT_FILE_PATH = "/storage/emulated/0/Android/data/" + GAME_PACKAGE + "/files/game_result.txt"
```

---

## ⚠️ PROBLEMAS COMUNES EN GODOT

### Problema 1: Ruta Relativa Incorrecta

❌ **INCORRECTO:**
```gdscript
var file = FileAccess.open("game_result.txt", FileAccess.WRITE)  # ❌ ¿Dónde se guarda?
var file = FileAccess.open("user://game_result.txt", FileAccess.WRITE)  # ❌ Ruta Godot
var file = FileAccess.open("res://game_result.txt", FileAccess.WRITE)  # ❌ App intterna
```

✅ **CORRECTO:**
```gdscript
var file = FileAccess.open("/storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt", FileAccess.WRITE)
```

### Problema 2: No Esperar a Que Se Escriba

❌ **INCORRECTO:**
```gdscript
file.store_string("GANASTE")
get_tree().quit()  # ❌ Cierra sin guardar
```

✅ **CORRECTO:**
```gdscript
file.store_string("GANASTE")
file.close()  # ✅ Forzar cierre
await get_tree().create_timer(0.5).timeout  # ✅ Esperar
get_tree().quit()
```

### Problema 3: Sin Manejo de Errores

❌ **INCORRECTO:**
```gdscript
var file = FileAccess.open(path, FileAccess.WRITE)
file.store_string("GANASTE")  # ❌ Si file es null → CRASH
```

✅ **CORRECTO:**
```gdscript
var file = FileAccess.open(path, FileAccess.WRITE)
if file:
    file.store_string("GANASTE")
    file.close()
    print("✅ Guardado")
else:
    print("❌ Error: ", FileAccess.get_open_error())
```

### Problema 4: Permisos Faltantes

❌ **Sin permisos:**
```ini
# ❌ Vacío o solo lectura
android/permissions=PoolStringArray()
```

✅ **Con permisos:**
```ini
# ✅ Necesarios para escribir en almacenamiento externo
android/permissions=PoolStringArray( 
    "WRITE_EXTERNAL_STORAGE", 
    "READ_EXTERNAL_STORAGE" 
)
```

---

## 📋 SCRIPT COMPLETO PARA GODOT

Copia este código en tu `main.gd` o el script que llama al fin del juego:

```gdscript
# game_result_manager.gd
extends Node

# ✅ Variables configurables por APK
var game_package: String = "com.example.atrapasalud"  # Cambiar por velocidad si aplica
var result_file_path: String

func _ready():
    # Construir ruta en tiempo de ejecución
    result_file_path = "/storage/emulated/0/Android/data/" + game_package + "/files/game_result.txt"
    print("📁 Ruta de archivo configurada: ", result_file_path)

func save_game_result(won: bool) -> bool:
    """Guarda el resultado del juego en un archivo compartido"""
    
    var result = "GANASTE" if won else "PERDISTE"
    
    print("\n" + "="*60)
    print("🎮 RESULTADO DEL JUEGO: ", result)
    print("📁 Guardando en: ", result_file_path)
    print("="*60)
    
    # Intentar abrir el archivo
    var file = FileAccess.open(result_file_path, FileAccess.WRITE)
    
    # Verificar si se abrió correctamente
    if file == null:
        var error = FileAccess.get_open_error()
        print("❌ ERROR AL ABRIR ARCHIVO!")
        print("   Código de error: ", error)
        print("   Posibles causas:")
        print("   1. Permisos insuficientes (WRITE_EXTERNAL_STORAGE)")
        print("   2. Ruta incorrecta o directorio no existe")
        print("   3. Almacenamiento lleno")
        return false
    
    # Escribir el resultado
    try:
        file.store_string(result)
        file.close()
        print("✅ Archivo guardado exitosamente")
        print("✅ Contenido: ", result)
        return true
    except:
        print("❌ ERROR al escribir en archivo")
        return false

func end_game(won: bool):
    """Finaliza el juego y regresa a Vita"""
    
    # Guardar resultado
    var saved = save_game_result(won)
    
    if saved:
        # Si se guardó, esperar un poco y cerrar
        print("⏳ Esperando a que se escriba el archivo...")
        await get_tree().create_timer(1.0).timeout
        print("🔙 Cerrando juego...")
        get_tree().quit()
    else:
        # Si falló, intentar de todas formas cerrar
        print("⚠️ Cerrando sin poder guardar resultado")
        await get_tree().create_timer(0.5).timeout
        get_tree().quit()

# Ejemplo de uso en tu game manager:
# Si el usuario gana:
#   yield(get_node("GameManager"), "game_ended")
#   end_game(true)
#
# Si el usuario pierde:
#   end_game(false)
```

---

## 📦 RECOMPILACIÓN CORRECTA

Después de hacer cambios en Godot:

### 1. **En Godot Editor:**

```
1. Project → Project Settings → Export
2. Editar preset Android existente
3. Verificar:
   - ✅ Permisos: WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
   - ✅ Package Name: com.example.atrapasalud (o velocidad)
   - ✅ Version Code/Name actualizados
4. Export Project → Selecciona APK
5. Sobrescribir APK anterior
```

### 2. **Instalar en dispositivo:**

```bash
# En PowerShell:
adb uninstall com.example.atrapasalud
adb install C:\Users\elcar\Documents\Godot\atrapa-2\build\android\atrapa-2.apk
```

### 3. **Verificar:**

```bash
# Conectarse a dispositivo
adb shell

# Navegar a la carpeta
cd /storage/emulated/0/Android/data/com.example.atrapasalud/files/

# Listar archivos
ls -la

# Después de jugar, debería ver:
# game_result.txt  <- 🎯 Este archivo
```

---

## 🧪 TEST MANUAL RÁPIDO

Si no quieres recompilar, prueba esto primero:

### 1. **Crear carpeta manualmente:**

```bash
adb shell
mkdir -p /storage/emulated/0/Android/data/com.example.atrapasalud/files
chmod 777 /storage/emulated/0/Android/data/com.example.atrapasalud/files
```

### 2. **Escribir archivo de prueba desde Godot:**

En la consola Godot o en un script de test:

```gdscript
var test_path = "/storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt"
var file = FileAccess.open(test_path, FileAccess.WRITE)
if file:
    file.store_string("GANASTE")
    file.close()
    print("✅ Test exitoso - archivo creado")
else:
    print("❌ Error: ", FileAccess.get_open_error())
```

### 3. **Verificar desde Android:**

```bash
adb shell cat /storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt
# Debería mostrar: GANASTE
```

---

## 🔄 FLUJO CORRECTO (CON LOGS)

Cuando todo está funcionando, verás esto:

```
[Vita] Toca "Atrapa Saludable"
[Vita] Lanza Intent a com.example.atrapasalud
[Godot] Abre juego
[Godot] Usuario juega...
[Godot] Usuario gana!
[Godot] 📁 Guardando en: /storage/.../game_result.txt
[Godot] ✅ Archivo guardado exitosamente
[Godot] ✅ Contenido: GANASTE
[Godot] 🔙 Cerrando juego...
[Vita] Detecta regreso del Intent
[Vita] Lee game_result.txt
[Vita] Encuentra: "GANASTE"
[Vita] Suma +170 XP
[Vita] Muestra: "+170 XP ganados!"
[Vita] Elimina game_result.txt
```

---

## ❓ SI SIGUE SIN FUNCIONAR

Si aún no funciona, necesito que me envíes:

1. **El contenido de `main.gd` o el script que maneja el fin del juego** en Godot
2. **El export_presets.cfg** de cada juego
3. **Logs de Godot** cuando intentas guardar el archivo

Envíame capturas o archivos de esos scripts y lo solucionamos.

---

## ✅ CHECKLIST FINAL

- [ ] ¿Verificaste que la ruta es exactamente: `/storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt`?
- [ ] ¿El script de Godot hace `file.close()` después de escribir?
- [ ] ¿Espera con `await get_tree().create_timer(1.0).timeout` antes de `quit()`?
- [ ] ¿export_presets.cfg tiene `WRITE_EXTERNAL_STORAGE` y `READ_EXTERNAL_STORAGE`?
- [ ] ¿Recompilaste las APKs después de cambiar los scripts?
- [ ] ¿Desinstalaste la vieja APK e instalaste la nueva?
- [ ] ¿La carpeta `/storage/.../files/` existe y es escribible?

---

## 💡 PRÓXIMO PASO

Ve a `C:\Users\elcar\Documents\Godot\atrapa-2\` y busca los archivos de script `.gd`. 

Envíame:
1. El archivo principal (main.gd, game_manager.gd, o cómo se llame)
2. El que maneja el "fin del juego"
3. El export_presets.cfg

O usa `adb logcat` mientras juegas para ver si hay errores de permisos.

