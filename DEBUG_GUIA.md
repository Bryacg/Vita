# 🔍 GUÍA DE DEBUG: Cómo Diagnosticar El Problema

## 📱 PASO 1: Ver Logs de Android

### Con Android Studio:

```
1. Abre Android Studio
2. Fondo → Logcat (o View → Tool Windows → Logcat)
3. Conecta tu dispositivo con USB
4. Verás logs en tiempo real mientras usas la app
```

### Con Línea de Comandos (ADB):

```powershell
# En PowerShell:
adb logcat | findstr "GameLauncher\|GameViewModel\|Vita"

# O para ver TODO:
adb logcat

# O guardar en archivo:
adb logcat > C:\logcat_vita.txt
```

---

## 🎯 PASO 2: Reproducir El Problema

**Mientras ves los logs:**

1. Abre VitaGame en tu teléfono
2. Ve a la pantalla **Juegos**
3. Toca "Atrapa Saludable" o "Nutri Defensores"
4. **En los logs verás algo como:**

```
D GameLauncher: 🎮 Intentando lanzar: com.example.atrapasalud
D GameLauncher: ✅ Intent creado, lanzando...
```

Aquí es donde Godot se abre.

5. Juega y **gana** el juego
6. El juego debe cerrarse y volver a Vita

**En los logs verás:**

```
D GameLauncher: 🎮 Intent regresó - Buscando archivo en: /storage/.../game_result.txt
```

**Aquí es donde busca el archivo. Verás:**

- ✅ Si existe: `✅ Archivo encontrado. Contenido: 'GANASTE'`
- ❌ Si NO existe: `⚠️ Archivo NO encontrado`

---

## 🔴 PROBLEMA #1: Archivo NO Encontrado

```
⚠️ Archivo NO encontrado en: /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt
📂 Contenido del directorio:
  (Directorio no existe)
```

**Causa:** Godot NO está escribiendo el archivo

**Solución:**
1. Abre `C:\Users\elcar\Documents\Godot\atrapa-2\`
2. Busca el script que maneja el fin del juego
3. Verifica que tenga:

```gdscript
var file = FileAccess.open("/storage/emulated/0/Android/data/com.example.atrapasalud/files/game_result.txt", FileAccess.WRITE)
if file:
    file.store_string("GANASTE")
    file.close()
```

---

## 🔴 PROBLEMA #2: Archivo Existe Pero Contenido Incorrecto

```
✅ Archivo encontrado. Contenido: 'WIN'
D GameViewModel: ⚠️ Resultado es NULL - no se procesará nada
```

**Causa:** Godot escribe "WIN" en lugar de "GANASTE"

**Solución:**
En los scripts de Godot, cambia:
```gdscript
# ❌ Incorrecto
file.store_string("WIN")

# ✅ Correcto
file.store_string("GANASTE")
```

---

## 🔴 PROBLEMA #3: QUE NO VUELVE DE GODOT (Bucle "Abriendo juego...")

```
D GameLauncher: 🎮 Intentando lanzar: com.example.atrapasalud
D GameLauncher: ✅ Intent creado, lanzando...
[... juega ... ]
[... no vuelve, pantalla congelada en "Abriendo juego..." ...]
```

**Causa:** Godot no está llamando a `get_tree().quit()`

**Solución:**
En Godot, después de guardar el archivo, agrega:
```gdscript
await get_tree().create_timer(1.0).timeout  # Esperar a que se guarde
get_tree().quit()  # ✅ IMPORTANTE: Cerrar Godot
```

---

## 🛠️ TEST RÁPIDO SIN GODOT

Si sospechas que Godot está roto, prueba esto:

### 1. Crear carpeta manualmente:

```powershell
# En PowerShell:
adb shell
mkdir -p /storage/emulated/0/Android/data/com.example.vita/files
chmod 777 /storage/emulated/0/Android/data/com.example.vita/files
exit
```

### 2. Escribir archivo de prueba:

```powershell
# Crear archivo desde PC
echo "GANASTE" | adb shell cmd base64 | adb shell "cat > /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"

# O más simple:
adb shell "echo GANASTE > /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"
```

### 3. Abrir Vita y verifica:

```
D GameLauncher: ✅ Archivo encontrado. Contenido: 'GANASTE'
D GameViewModel: [5] XP a sumar: 170
✅ +170 XP ganados!
```

Si esto funciona, el problema es **100% en Godot**.

---

## 📋 CHECKLIST DE DEBUG

Copia esto y responde cada punto:

```
PROBLEMA: ¿Se queda en bucle "Abriendo juego..."?
┌─ SI: Godot no cierra. Verifica get_tree().quit()
└─ NO: Continúa...

PROBLEMA: ¿Archivo NO se crea?
┌─ SI: Godot no escribe. Verifica FileAccess.open()
│      Y permisos en export_presets.cfg
└─ NO: Archivo sí existe, continúa...

PROBLEMA: ¿Archivo existe pero contenido incorrecto?
┌─ SI: Godot escribe algo diferente a "GANASTE"
│      Busca qué se guarda en logs de Godot
└─ NO: ¡Debería estar funcionando!

PROBLEMA: ¿Muestra "+170 XP ganados!"?
└─ SI: ✅ TODO ESTÁ FUNCIONANDO
└─ NO: Verifica logs de GameViewModel [1-8]
```

---

## 🎮 LOGS DE GODOT

Godot también tiene logs. Para verlos:

### Si ejecutas desde Godot Editor:

```
Muestra Output directamente en la parte inferior
```

### Si ejecutas APK en teléfono:

```bash
# Ver logs de Godot desde Android
adb logcat | findstr "godot"

# O simplemente conectar teléfono a Godot Editor:
# Device → Remote Deploy... (si está habilitado)
```

---

## 📄 ARCHIVO que DEBE EXISTIR después de jugar

```
/storage/emulated/0/Android/data/com.example.vita/files/game_result.txt
```

Verificar con:

```bash
adb shell
ls -la /storage/emulated/0/Android/data/com.example.vita/files/
cat /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt
```

**Salida esperada:**
```
-rw-rw-rw-  1 u0_a...  u0_a...    7 Jun 8 12:16 game_result.txt

GANASTE
```

---

## 🚀 DESPUÉS DE ENCONTRAR EL PROBLEMA

Una vez sepas qué falla exactamente:

1. **Si es Godot:** Comparte screenshot de error en logs de Godot
2. **Si es Android:** Comparte screensh de logs Logcat
3. **Si es Permisos:** Actualiza export_presets.cfg en Godot

---

## 📸 EJEMPLO DE LOGS CORRECTOS

```
D GameLauncher: 🎮 Intentando lanzar: com.example.atrapasalud
D GameLauncher: ✅ Intent creado, lanzando...

[... usuario juega en Godot ...]

D GameLauncher: 🎮 Intent regresó - Buscando archivo en: /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt
D GameLauncher: ✅ Archivo encontrado. Contenido: 'GANASTE'
D GameLauncher: 🗑️ Archivo eliminado

D GameViewModel: [1] Procesando resultado: 'GANASTE'
D GameViewModel: [2] Usuario: xyz123abc
D GameViewModel: [3] Juego actual: com.example.atrapasalud
D GameViewModel: [4] Resultado final: 'GANASTE'
D GameViewModel: [5] XP a sumar: 170
D GameViewModel: [6] Nombre del juego: AtrapaSalud
D GameViewModel: [7] Resultado guardado en BD
D GameViewModel: [8] XP sumado al usuario
D GameViewModel: ✅ Proceso completado exitosamente

UI muestra: "+170 XP ganados!"
```

---

## ¿QUÉ FAZER AHORA?

1. Ejecuta VitaGame con los logs abiertos
2. Ve a Juegos y juega uno
3. Copia los logs exactos que ves
4. Comparte los logs conmigo en el siguiente mensaje

Así podré decirte exactamente qué está fallando y cómo arreglarlo.

