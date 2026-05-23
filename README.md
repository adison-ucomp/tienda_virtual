# Tienda Compensar (Android)

Aplicacion Android nativa (Kotlin + XML) para gestionar un flujo de tienda con perfiles `admin`, `seller` y `buyer`, integraciones con Firebase, Google Maps y pagos con ePayco.

## Explicacion del codigo

La app esta organizada por capas y por modulos funcionales:

- `app/src/main/java/com/compensar/tienda/model`: modelos de datos (`UserModel`, `ProductModel`, `TradeModel`, etc.).
- `app/src/main/java/com/compensar/tienda/firestore`: acceso a Firestore por entidad (`UserFire`, `ProductFire`, `TradeFire`, etc.).
- `app/src/main/java/com/compensar/tienda/ui`: pantallas Android (`Activity`) agrupadas por rol y por dominio (`admin`, `seller`, `buyer`, `home`, `model`, `profile`, `setting`).
- `app/src/main/java/com/compensar/tienda/ui/common`: utilidades compartidas de sesion, carrito, navegacion, correo y helpers.
- `app/src/main/java/com/compensar/tienda/worker`: tareas en segundo plano con WorkManager (`TradeStatusWorker`).
- `app/src/main/res/layout`: layouts XML de cada pantalla y modales.

Flujo general:

1. `SplashActivity` y `HomeLoginActivity` gestionan entrada/autenticacion.
2. Segun rol, la navegacion dirige a dashboard de `admin`, `seller` o vistas de compra.
3. Cada modulo CRUD usa su `Activity` + clase `*Fire` para leer/escribir en Firestore.
4. Para pagos, `HomeEpaycoActivity` usa `EpaycoConfig` para formar URLs y reglas de validacion (minimo/maximo, modo test, callbacks).

## Variables de entorno

La configuracion se carga desde:

1. Variables del sistema (`System.getenv`).
2. Archivo `.env.local` (si existe).
3. Archivo `.env` (si existe).
4. Valor por defecto definido en `app/build.gradle.kts`.

Referencia base: `.env.example`.

| Variable | Uso en la app | Valor por defecto |
|---|---|---|
| `EPAYCO_PUBLIC_KEY` | Llave publica de ePayco (`BuildConfig.EPAYCO_PUBLIC_KEY`) | `EPAYCO_PUBLIC_KEY_AQUI` |
| `EPAYCO_PRIVATE_KEY` | Llave privada para backend/servicios externos (no se inyecta en `BuildConfig`) | Sin defecto |
| `EPAYCO_TEST_MODE` | Activa/desactiva modo pruebas de ePayco | `true` |
| `EPAYCO_MIN_AMOUNT` | Monto minimo permitido para pagar con ePayco | `1000` |
| `EPAYCO_MAX_AMOUNT` | Monto maximo permitido (vacio = sin limite) | `""` |
| `EPAYCO_SERVICE_URL` | URL base de ePayco para validaciones directas | `https://secure.epayco.co` |
| `EPAYCO_CRON_MINUTES` | Intervalo (minutos) para procesos periodicos de estado | `5` |
| `API_BASE_URL` | URL base del servicio backend para callbacks/lookup | `https://xe.engcode.dev/github/adisonucomp/a3d1a7618ca7/` |
| `ANDROID_MAPS_API_KEY` | API key para Google Maps (`AndroidManifest` placeholder) | `""` |

## Configuracion rapida

1. Copiar `.env.example` a `.env.local`.
2. Completar llaves y URLs reales.
3. Verificar que `google-services.json` corresponda al proyecto Firebase correcto.
4. Ejecutar la app desde Android Studio o con:

```bash
./gradlew assembleDebug
```

## Notas de seguridad

- No compartir ni versionar llaves reales (`.env.local` debe quedar fuera del control de versiones).
- `EPAYCO_PRIVATE_KEY` no debe usarse en cliente movil para operaciones sensibles; mantener logica critica en backend.
