# 🎮 VitaGame - App de Gamificación para la Salud

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2024%2B-green.svg)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-purple.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **Transforma tus hábitos saludables en una aventura llena de retos, logros y recompensas.**

VitaGame es una aplicación Android que combina seguimiento nutricional, retos diarios/semanales y minijuegos para motivar a los usuarios a mantener un estilo de vida saludable mediante mecánicas de gamificación.

---

## 📑 Tabla de Contenidos
- [🎮 Características Principales](#-características-principales)
- [🏗️ Arquitectura](#️-arquitectura)
- [🚀 Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [🎨 Diseño y UX](#-diseño-y-ux)
- [🛠️ Configuración del Proyecto](#️-configuración-del-proyecto)
- [🎯 Sistema de Gamificación](#-sistema-de-gamificación)
- [📈 Roadmap](#-roadmap)
- [🤝 Contribuir](#-contribuir)

---

## 📱 Características Principales

### 🏠 **Panel de Control (Home)**
- Dashboard personalizado con resumen del usuario
- Registro rápido de comidas con clasificación nutricional automática
- Sistema de XP basado en la calidad de las comidas
- Visualización de retos del día activos
- Acceso directo a minijuegos

### 🎯 **Sistema de Retos**
- **Retos Diarios**: Generados automáticamente cada día con IA (Gemini)
- **Retos Semanales**: Desafíos más complejos que se generan los lunes
- Progreso de retos con incremento paso a paso
- Recompensas de XP al completar retos
- Clasificación por categorías nutricionales

### 🤖 **ChatBot Chef IA**
- Asistente virtual powered by Gemini para recomendaciones de recetas
- Chat en tiempo real integrado con Floating Action Button
- Interfaz de chat con burbujas animadas
- Sugerencias personalizadas basadas en preferencias alimentarias

### 🎮 **Minijuegos (Godot Engine)**
- **"Atrapa Saludable"**: Juego de velocidad para capturar alimentos saludables
- **"Nutri Defensores"**: Juego de defensa contra comida chatarra
- Integración con juegos Godot como apps externas
- Sistema de recompensas de XP por victoria (+170 XP)

### 📊 **Seguimiento de Progreso**
- Gráficos de XP semanal (barras)
- Gráficos de calorías semanales (líneas)
- Indicador de IMC con visualización visual
- Sistema de niveles y experiencia acumulada
- Racha de días consecutivos completando retos
- Contador de logros desbloqueados

### 👤 **Perfil de Usuario**
- Gestión de datos biométricos (peso, altura, edad, género)
- Preferencias alimentarias personalizables
- Recordatorios configurables (agua y caminata)
- Sincronización con Firebase Auth

---

## 🏗️ Arquitectura

```
VitaGame/
├── 📱 app/src/main/java/com/example/vita/
│   ├── 🎯 ui/                          # Capa de Presentación
│   │   ├── screens/                    # Pantallas (Login, Home, Retos, etc.)
│   │   ├── components/                 # Componentes reutilizables
│   │   ├── charts/                     # Gráficos personalizados
│   │   ├── navigation/                 # Sistema de navegación
│   │   └── theme/                      # Temas y colores
│   ├── 🧠 domain/                      # Capa de Dominio
│   │   ├── model/                      # Modelos de dominio
│   │   └── usecase/                    # Casos de uso
│   ├── 💾 data/                        # Capa de Datos
│   │   ├── local/                      # Room Database (DAO, Entities)
│   │   ├── remote/                     # Firebase & APIs
│   │   ├── repository/                 # Implementaciones de repositorios
│   │   └── mapper/                     # Mapeadores de datos
│   ├── 🔧 di/                          # Inyección de dependencias (Hilt)
│   └── ⚙️ work/                        # WorkManager (recordatorios)
└── 🎨 app/src/main/res/                # Recursos Android
```

### **Patrón Arquitectónico: Clean Architecture + MVVM**

| Capa | Tecnología | Descripción |
|------|-----------|-------------|
| **UI** | Jetpack Compose + Material 3 | Interfaces declarativas y reactivas |
| **ViewModel** | Hilt ViewModel + StateFlow | Gestión de estado y lógica de presentación |
| **Domain** | Use Cases + Models | Lógica de negocio pura |
| **Data** | Repository Pattern | Abstracción de fuentes de datos |
| **Local** | Room + KSP | Base de datos SQLite con tipado seguro |
| **Remote** | Firebase + Retrofit | Autenticación, Firestore y APIs |

---

## 🚀 Tecnologías Utilizadas

### **Core Android**
- **Kotlin 2.0.21** - Lenguaje principal
- **Jetpack Compose** - UI toolkit moderno
- **Material Design 3** - Sistema de diseño
- **Navigation Component** - Navegación entre pantallas

### **Inyección de Dependencias**
- **Hilt 2.52** - DI framework
- **KSP** - Procesamiento de anotaciones

### **Persistencia Local**
- **Room 2.6.1** - ORM para SQLite
- **DataStore** - Preferencias tipadas (opcional)

### **Backend & Cloud**
- **Firebase BOM 33.7.0**
  - **Firebase Auth** - Autenticación (Email/Contraseña, Google Sign-In)
  - **Firestore** - Base de datos NoSQL
  - **Analytics** - Análisis de uso
- **Google Play Services Auth** - OAuth 2.0

### **Networking**
- **Retrofit 2.11.0** - Cliente HTTP
- **OkHttp 4.12.0** - Interceptor de logs
- **Kotlinx Serialization** - Serialización JSON

### **IA & ML**
- **Gemini API** (Google AI) - Generación de retos y chatbot
- **Google AI Client** - SDK oficial para Android

### **Gráficos & UI**
- **Vico Charts** - Gráficos estadísticos
- **Canvas API** - Dibujos personalizados

### **Background Processing**
- **WorkManager 2.10.0** - Tareas programadas (recordatorios)

### **Game Engine Integration**
- **Godot Engine** - Motor de juegos integrado
- **Apps externas de Godot** lanzadas desde la app

---

## 🎨 Diseño y UX

### **Paleta de Colores**
- **Primario**: Verde saludable (#4C662B)
- **Secundario**: Dorado/Ámbar (#FFC107)
- **Superficie**: Tema adaptable claro/oscuro
- **Éxito**: Verde (#1D9E75)
- **Advertencia**: Naranja (#F2994A)
- **Error**: Rojo (#C62828)

### **Animaciones**
- Transiciones suaves entre pantallas
- Animaciones de entrada/salida para banners de feedback
- Efectos pulsantes en elementos hero
- Scroll fluido con Material Motion

---

## 📦 Estructura de Datos

### **Entidades Principales (Room)**
| Entidad | Descripción |
|---------|-------------|
| `UserEntity` | Información básica del usuario (Firebase UID, nombre) |
| `ProfileEntity` | Datos biométricos (peso, altura, IMC) |
| `ProgressEntity` | XP, nivel, racha, logros |
| `MealEntity` | Registro de comidas con calorías y nutrición |
| `ChallengeEntity` | Retos generados con su estado |
| `GameEntity` | Resultados de minijuegos |
| `ChatMessageEntity` | Historial de conversaciones con IA |
| `AchievementEntity` | Logros desbloqueados |

---

## 🛠️ Configuración del Proyecto

### **Requisitos Previos**
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- SDK de Android API 35
- Cuenta Firebase activa

### **1. Clonar el Repositorio**
```bash
git clone https://github.com/tu-usuario/vitagame.git
cd vitagame
```

### **2. Configurar Firebase**
1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agrega una app Android con el package name: `com.example.vita`
3. Descarga el archivo `google-services.json` y colócalo en `app/`
4. Habilita Authentication (Email/Password y Google Sign-In)

### **3. Configurar Variables Locales**
Crea el archivo `local.properties` en la raíz del proyecto:
```properties
# Modelo de Gemini para generación de retos
modelName=gemini-pro

# API Keys (obtener desde Google AI Studio)
apiChatb=TU_API_KEY_PARA_CHATBOT
apiRetos=TU_API_KEY_PARA_RETOS
```

> ⚠️ **IMPORTANTE**: Nunca subas `local.properties` o `google-services.json` a tu repositorio público. Estos archivos ya están incluidos en `.gitignore`.

### **4. Compilar y Ejecutar**
```bash
# Sincronizar Gradle
./gradlew sync

# Compilar debug APK
./gradlew assembleDebug

# Instalar en dispositivo conectado
./gradlew installDebug
```

---

## 🎯 Sistema de Gamificación

### **Economía de XP**
| Acción | XP Ganado |
|--------|-----------|
| Registrar comida (Muy Saludable) | +20 XP |
| Registrar comida (Saludable) | +15 XP |
| Registrar comida (Regular) | +8 XP |
| Completar reto diario | +80 XP |
| Completar reto semanal | +400 XP |
| Ganar minijuego Godot | +170 XP |
| Superar límite calórico | +2 XP (penalización) |

### **Sistema de Niveles**
- Nivel calculado dinámicamente basado en XP total acumulado
- Progresión cuadrática: más XP necesario por nivel
- Visualización de nivel actual en el perfil

### **Rachas**
- Incrementa al completar al menos un reto por día
- Se resetea si se pierde un día
- Motivación visual con indicador de fuego 🔥

---

## 🤝 Minijuegos Godot

VitaGame integra minijuegos desarrollados en Godot Engine que se comunican con la app principal:

### **Integración**
- Los juegos son apps Android independientes (`.apk` separados)
- Se lanzan mediante `ActivityResultContracts`
- Resultados escritos en archivo compartido (`game_result.txt`)
- La app principal lee el resultado al regresar

### **Juegos Disponibles**
1. **Atrapa Saludable** - Package: `com.example.atrapasalud`
2. **Nutri Defensores** - Package: `com.example.velocidad`

### **Configuración de Godot**
- Exportar juegos Godot como bibliotecas (`.aar`) en `app/libs/`
- Configurar GodotPlugin para comunicación nativa
- Manejo de archivos en almacenamiento externo

---

## 📊 Gráficos y Visualizaciones

Utilizando **Vico Charts** para representar:

- **Gráfico de Barras**: XP ganada por día de la semana
- **Gráfico de Líneas**: Calorías consumidas semanalmente
- **Indicador de IMC**: Visualización semicircular del índice
- **Barras de Progreso**: Retos completados vs pendientes

---

## 🔒 Seguridad

- **Firebase Authentication**: Tokens JWT seguros
- **Google Sign-In**: OAuth 2.0 con credenciales nativas
- **Room Encryption**: Datos locales protegidos
- **Proguard**: Ofuscación de código en release
- **Variables Ocultas**: API keys en `BuildConfig` via `local.properties`

---

## 🧪 Testing

```bash
# Ejecutar pruebas unitarias
./gradlew test

# Ejecutar pruebas instrumentadas
./gradlew connectedAndroidTest

# Generar reporte de cobertura
./gradlew jacocoTestReport
```

---

## ❓ Preguntas Frecuentes (FAQ)

### **P: ¿Qué necesito para compilar la app?**
**R:** JDK 17+, Android Studio Hedgehog+ y una cuenta Firebase.

### **P: ¿Cómo obtengo las API keys de Gemini?**
**R:** Accede a [Google AI Studio](https://aistudio.google.com/), crea un proyecto y genera tus API keys en `local.properties`.

### **P: ¿Puedo usar la app sin Firebase?**
**R:** No, Firebase es obligatorio para autenticación y sincronización de datos en Firestore.

### **P: ¿Qué versión mínima de Android soporta?**
**R:** API 24 (Android 7.0) o superior.

### **P: ¿Los minijuegos vienen integrados?**
**R:** No, son apps separadas que se instalan aparte. Los `.apk` se lanzan desde VitaGame.

---

## 📈 Roadmap

### **Próximas Funcionalidades**
- [ ] Dashboard de estadísticas avanzadas
- [ ] Integración con Google Fit / Health Connect
- [ ] Sistema de logros con insignias visuales
- [ ] Leaderboard con amigos
- [ ] Modo oscuro completo
- [ ] Widget de home screen
- [ ] Notificaciones push personalizadas
- [ ] Exportar datos a CSV/PDF

### **Mejoras Técnicas**
- [ ] Migrar a Compose Multiplatform
- [ ] Implementar Offline-first con WorkManager
- [ ] Añadir tests de UI con Compose Testing
- [ ] CI/CD con GitHub Actions

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si quieres mejorar VitaGame:

1. **Fork** el repositorio
2. Crea una **rama** (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** tus cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un **Pull Request**

### **Guías de Contribución**
- Sigue la guía de estilo de Kotlin oficial
- Asegúrate de que el código pase `ktlintFormat`
- Añade tests para nuevas funcionalidades
- Actualiza la documentación si es necesario

---

## 📄 Licencia

```
Copyright 2025-2026 VitaGame Team

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🙏 Agradecimientos

- **Google** - Por Firebase, Gemini y Jetpack Compose
- **JetBrains** - Por Kotlin y las herramientas de desarrollo
- **Godot Engine** - Por el motor de juegos de código abierto
- **Patrick Michalik** - Por la librería Vico Charts
- **Comunidad Android** - Por las mejores prácticas y librerías open source

---

## 📬 Contacto

¿Preguntas o sugerencias? ¡Contáctanos!

- **Issues**: [GitHub Issues](https://github.com/tu-usuario/vitagame/issues)
- **Email**: contacto@vitagame.app
- **Discord**: [Únete a nuestro servidor](https://discord.gg/vitagame)

---

<p align="center">
  <strong>💚 Hecho con pasión por la salud y el código abierto</strong>
</p>

<p align="center">
  <a href="#-características-principales">Características</a> •
  <a href="#-arquitectura">Arquitectura</a> •
  <a href="#-tecnologías-utilizadas">Tecnologías</a> •
  <a href="#-configuración-del-proyecto">Configuración</a> •
  <a href="#-roadmap">Roadmap</a>
</p>
