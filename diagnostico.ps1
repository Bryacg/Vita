#!/usr/bin/env pwsh
# ============================================================================
# Script de Diagnóstico para VitaGame + Godot
# Uso: PowerShell -ExecutionPolicy Bypass -File "diagnostico.ps1"
# ============================================================================

Write-Host "🔍 DIAGNÓSTICO DE VITAGAME + GODOT" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Paso 1: Verificar ADB
Write-Host "`n[1] Verificando ADB..." -ForegroundColor Yellow
$adbPath = adb --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ ADB encontrado" -ForegroundColor Green
    $adbPath
} else {
    Write-Host "❌ ADB no encontrado. Instala Android SDK." -ForegroundColor Red
    exit 1
}

# Paso 2: Verificar dispositivo conectado
Write-Host "`n[2] Buscando dispositivo Android..." -ForegroundColor Yellow
$devices = adb devices | Select-Object -Skip 1 | Where-Object { $_ -match "device$" }
if ($devices) {
    Write-Host "✅ Dispositivo conectado:" -ForegroundColor Green
    $devices | ForEach-Object { Write-Host "   $_" -ForegroundColor Green }
} else {
    Write-Host "❌ No hay dispositivo conectado" -ForegroundColor Red
    Write-Host "   Conecta tu teléfono con USB y activa Depuración USB" -ForegroundColor Yellow
    exit 1
}

# Paso 3: Verificar si Vita está instalada
Write-Host "`n[3] Verificando si Vita está instalada..." -ForegroundColor Yellow
$vitaInstalled = adb shell pm list packages | Select-String "com.example.vita"
if ($vitaInstalled) {
    Write-Host "✅ VitaGame está instalada" -ForegroundColor Green
} else {
    Write-Host "❌ VitaGame no está instalada" -ForegroundColor Red
    exit 1
}

# Paso 4: Verificar directorio de files
Write-Host "`n[4] Verificando directorio /storage/.../files/" -ForegroundColor Yellow
$dirCheck = adb shell "ls -la /storage/emulated/0/Android/data/com.example.vita/files/ 2>&1"
if ($dirCheck -match "No such file") {
    Write-Host "⚠️ Directorio NO existe. Creando..." -ForegroundColor Yellow
    adb shell "mkdir -p /storage/emulated/0/Android/data/com.example.vita/files"
    adb shell "chmod 777 /storage/emulated/0/Android/data/com.example.vita/files"
    Write-Host "✅ Directorio creado" -ForegroundColor Green
} else {
    Write-Host "✅ Directorio existe" -ForegroundColor Green
    Write-Host $dirCheck
}

# Paso 5: Verificar archivos existentes
Write-Host "`n[5] Archivos en el directorio:" -ForegroundColor Yellow
$files = adb shell "ls -la /storage/emulated/0/Android/data/com.example.vita/files/ 2>&1"
Write-Host $files

# Paso 6: Buscar game_result.txt
Write-Host "`n[6] Buscando game_result.txt..." -ForegroundColor Yellow
$resultFile = adb shell "cat /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt 2>&1"
if ($resultFile -match "No such file") {
    Write-Host "❌ game_result.txt NO existe" -ForegroundColor Red
    Write-Host "   → Godot no está escribiendo el archivo" -ForegroundColor Yellow
} else {
    Write-Host "✅ game_result.txt encontrado" -ForegroundColor Green
    Write-Host "   Contenido: '$resultFile'" -ForegroundColor Green
}

# Paso 7: Ver APKs instaladas
Write-Host "`n[7] Verificando APKs de Godot..." -ForegroundColor Yellow
$apk1 = adb shell "pm list packages | grep atrapasalud"
$apk2 = adb shell "pm list packages | grep velocidad"

if ($apk1) {
    Write-Host "✅ APK 1 (AtrapaSalud) está instalada" -ForegroundColor Green
} else {
    Write-Host "❌ APK 1 (AtrapaSalud) NO está instalada" -ForegroundColor Red
}

if ($apk2) {
    Write-Host "✅ APK 2 (Velocidad) está instalada" -ForegroundColor Green
} else {
    Write-Host "❌ APK 2 (Velocidad) NO está instalada" -ForegroundColor Red
}

# Paso 8: Ver logs de Vita
Write-Host "`n[8] Últimos logs de Vita (últimas 50 líneas)..." -ForegroundColor Yellow
Write-Host "================================================" -ForegroundColor Cyan
adb logcat -c  # Limpiar logs previos
Write-Host "🔔 Abre Vita ahora y ve a Juegos..." -ForegroundColor Yellow
Write-Host "⏳ Esperando 5 segundos..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Leer logs
$logs = adb logcat -d | Select-String "GameLauncher|GameViewModel|Godot"
if ($logs) {
    Write-Host "📋 Logs encontrados:" -ForegroundColor Green
    $logs | ForEach-Object { Write-Host "   $_" }
} else {
    Write-Host "❌ No se capturaron logs de Vita" -ForegroundColor Yellow
}

# Paso 9: Test manual - Crear archivo
Write-Host "`n[9] Test: Crear archivo manualmente desde PC..." -ForegroundColor Yellow
adb shell "echo GANASTE > /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"
$testContent = adb shell "cat /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"
if ($testContent -match "GANASTE") {
    Write-Host "✅ Se puede escribir archivos en esa ubicación" -ForegroundColor Green
    Write-Host "   Contenido: '$testContent'" -ForegroundColor Green

    # Limpiar
    adb shell "rm /storage/emulated/0/Android/data/com.example.vita/files/game_result.txt"
    Write-Host "   Archivo de test eliminado" -ForegroundColor Gray
} else {
    Write-Host "❌ No se puede escribir en esa ubicación" -ForegroundColor Red
    Write-Host "   Verifica permisos o ruta" -ForegroundColor Yellow
}

# Resultado final
Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "📊 RESUMEN DE DIAGNÓSTICO" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

if ($resultFile -notmatch "No such file" -and $resultFile -match "GANASTE|PERDISTE") {
    Write-Host "✅ TODO PARECE ESTAR FUNCIONANDO" -ForegroundColor Green
    Write-Host "✅ game_result.txt existe y tiene contenido correcto" -ForegroundColor Green
} else {
    Write-Host "❌ PROBLEMA DETECTADO:" -ForegroundColor Red
    if ($resultFile -match "No such file") {
        Write-Host "   - game_result.txt NO se está creando" -ForegroundColor Red
        Write-Host "   - Revisa los scripts de Godot (atrapa-2 y golpeador)" -ForegroundColor Yellow
        Write-Host "   - Verifica que escriben en la ruta correcta" -ForegroundColor Yellow
        Write-Host "   - Verifica que llaman a get_tree().quit()" -ForegroundColor Yellow
    } else {
        Write-Host "   - Contenido inesperado: '$resultFile'" -ForegroundColor Red
        Write-Host "   - Debería ser: GANASTE o PERDISTE" -ForegroundColor Yellow
    }
}

Write-Host "`n✨ DIAGNÓSTICO COMPLETADO" -ForegroundColor Cyan

