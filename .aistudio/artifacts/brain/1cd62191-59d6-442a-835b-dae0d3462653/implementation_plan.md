# Función de Síntesis Dinámica con la Voz de Víctor Florencio (XTTS / Oráculo y Templo)

Sistema de síntesis vocal dinámica que permite que cualquier palabra o frase consultada (por ejemplo, *"Niltze, joven guerrero"*) sea pronunciada de forma fluida y natural con la voz clonada de Víctor Florencio a través de un servidor XTTS dedicado o Space de Hugging Face, con almacenamiento en caché local y soporte continuo sin bloqueos.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> **Decisiones confirmadas por el usuario:**
> - **Tecnología de síntesis**: Servidor XTTS dedicado o endpoint de Hugging Face utilizando la muestra de audio de Víctor Florencio (`voz_sacerdote_sample.wav`).
> - **Ámbito de activación**: Exclusivamente en las consultas del Templo y en las respuestas del Oráculo Sagrado.
>
> **Estrategia para evitar caídas y errores 503/505:**
> - Los Spaces gratuitos de Hugging Face se suspenden por inactividad. Implementaremos un gestor de conexión robusto compatible con URLs de Spaces (`hf.space`), tokens de Hugging Face (`hf_...`) y APIs Gradio/XTTS personalizadas, complementado con caché local (para que frases ya sintetizadas suenen al instante sin consumir red) y fallback armónico al motor sacerdotal si el servidor remoto está pausado.

---

## 1. Overview & Core Concept

- **Qué hace**: Permite ingresar o consultar cualquier palabra o frase en el Templo y en el Oráculo para escucharla inmediatamente sintetizada con la voz de Víctor Florencio.
- **Audiencia y contexto**: Estudiantes y exploradores de la Huaxlica que desean escuchar palabras en Náhuatl y consejos del Sacerdote con una entonación mística auténtica y personalizada.
- **Valor principal**: Síntesis de texto a voz flexible (no depende de frases pregrabadas fijas), permitiendo al Sacerdote pronunciar frases dinámicas generadas por la IA o consultadas por el usuario.

---

## 2. User Experience & Visual Design

### Flujo de Usuario:
1. **Consulta en el Templo**:
   - El usuario abre la pestaña del Templo y encuentra la tarjeta sagrada **"Consultar Palabra con el Sacerdote"**.
   - Escribe una palabra o frase (ej. *"Niltze, joven guerrero"* o selecciona una sugerencia rápida).
   - Toca el botón **"Pronunciar con Voz de Víctor"** con icono místico de onda sonora.
2. **Respuesta en el Oráculo Sagrado**:
   - Al conversar con el Sacerdote en el Oráculo, cada consejo generado por la IA cuenta con un botón de reproducción directa que sintetiza la respuesta con la voz de Víctor.
3. **Indicador de Estado en Vivo**:
   - Mientras XTTS genera el audio, se muestra una animación sutil de ondas doradas (*"Invocando voz sagrada..."*).
   - Una vez recibido, el audio se reproduce y se almacena en la caché del dispositivo para que la próxima vez que se consulte esa frase se escuche de inmediato.
4. **Panel de Conexión XTTS / Hugging Face (Accesible y Claro)**:
   - Un botón de configuración discreto en el Templo abre un diálogo donde el usuario puede ingresar la URL de su Space de Hugging Face o servidor dedicado, ingresar su Token de Hugging Face (`hf_...`), y pulsar **"Probar Conexión"** para verificar el estado de su servidor en tiempo real.

---

## 3. Key Product Decisions & Trade-Offs

- **Decisión 1: Caché Local Inteligente de Audios Sintetizados**:
  - *Enfoque*: Cada frase sintetizada por XTTS se almacena como archivo `.wav` en la memoria caché del dispositivo indexada por hash de texto.
  - *Por qué*: Si el usuario consulta la misma palabra o frase varias veces, la respuesta es inmediata (0 ms de espera) y funciona sin consumir internet ni créditos del servidor.
- **Decisión 2: Compatibilidad de Endpoints Hugging Face y XTTS**:
  - *Enfoque*: El cliente HTTP admitirá tanto endpoints directos `/tts_to_audio` como endpoints Gradio `/api/predict` y envío de audio de referencia en Base64 o Multipart.
  - *Por qué*: Facilita conectar cualquier Space público o privado de Hugging Face sin requerir reconfiguraciones complejas.
- **Decisión 3: Tolerancia a Fallos sin Silencio**:
  - *Enfoque*: Si el servidor XTTS está dormido o tarda más de lo esperado, la app avisa discretamente y reproduce la entonación sacerdotal configurada localmente para no dejar al usuario esperando sin sonido.

---

## 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       Interfaz UI                           │
│  [Tarjeta Consulta Templo]   /   [Oráculo Sagrado Mensajes] │
└──────────────────────────────┬──────────────────────────────┘
                               │ playSacerdotePhrase(text)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      MainViewModel                          │
│  - Verifica caché local en disco                            │
│  - Gestiona estado isXttsSynthesizing y errores amigables   │
└──────────────────────────────┬──────────────────────────────┘
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
┌──────────────────────────────┐ ┌───────────────────────────┐
│     XttsAudioClient          │ │    Audio Local Caché      │
│ - Envía payload a HuggingFace│ │ - Guarda archivo .wav     │
│ - Soporta token Bearer hf_.. │ │ - Reproduce con           │
│ - Maneja reintentos y waking │ │   MediaPlayer             │
└──────────────────────────────┘ └───────────────────────────┘
```

### Componentes a Modificar / Crear:
1. **`XttsAudioClient.kt`**:
   - Refactorizar para soportar payloads universales de XTTS-v2 y Spaces de Hugging Face con soporte de cabecera `Authorization: Bearer <hf_token>`.
   - Soporte para consulta rápida con streaming y almacenamiento en caché.
2. **`MainViewModel.kt`**:
   - Crear función `consultarPalabraSacerdote(texto: String)` que conecta directamente la entrada de texto con la síntesis de Víctor.
   - Integrar la reproducción automática o manual en los mensajes del Oráculo.
3. **`MainActivity.kt` (Pestaña del Templo)**:
   - Incorporar la tarjeta interactiva de consulta de palabras y frases con botones de ejemplo y botón de configuración del servidor.
4. **`AiOracleScreen.kt`**:
   - Enlazar cada mensaje del Sacerdote al nuevo flujo dinámico de síntesis con la voz de Víctor.
