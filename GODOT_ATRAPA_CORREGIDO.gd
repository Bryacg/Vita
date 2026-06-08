extends Control

# Variables del juego
var ganador = false
var xp_ganados = 0

func _ready() -> void:
	# Solicitar permisos de escritura en Android
	_request_permissions()

func _request_permissions():
	"""Solicita permisos específicos uno por uno usando el método singular"""
	if OS.get_name() == "Android":
		var permissions = [
			"android.permission.WRITE_EXTERNAL_STORAGE",
			"android.permission.READ_EXTERNAL_STORAGE"
		]

		for permission in permissions:
			OS.request_permission(permission) # Singular, acepta 1 argumento string

# ─── LLAMADO CUANDO EL USUARIO GANA ───────────────────────────────────────
func _on_juego_ganado():
	"""Se llama cuando el usuario gana el juego"""
	ganador = true
	xp_ganados = 170  # O la cantidad que uses
	print("🎉 ¡VICTORIA! Puntos ganados: ", xp_ganados)

	# Mostrar pantalla de victoria (opcional)
	_mostrar_resultado_victoria()

# ─── LLAMADO CUANDO EL USUARIO PIERDE ────────────────────────────────────
func _on_juego_perdido():
	"""Se llama cuando el usuario pierde el juego"""
	ganador = false
	xp_ganados = 0
	print("💔 Perdiste esta partida")

	# Mostrar pantalla de derrota (opcional)
	_mostrar_resultado_derrota()

func _mostrar_resultado_victoria():
	"""Muestra un mensaje de victoria (aquí instancia tu pantalla_final si existe)"""
	print("Mostrando pantalla de victoria...")
	# Si tienes una pantalla final, instáciala aquí:
	# var pantalla_final = preload("res://Scene/pantallafinal.tscn").instantiate()
	# add_child(pantalla_final)
	# pantalla_final.configurar_pantalla("gano")

func _mostrar_resultado_derrota():
	"""Muestra un mensaje de derrota"""
	print("Mostrando pantalla de derrota...")
	# var pantalla_final = preload("res://Scene/pantallafinal.tscn").instantiate()
	# add_child(pantalla_final)
	# pantalla_final.configurar_pantalla("perdio")

# ─── BOTÓN SALIR DE PARTIDA ───────────────────────────────────────────────
func _on_btn_salirde_partida_pressed() -> void:
	"""Botón para salir y volver a Vita con el resultado"""
	print("Saliendo del juego...")
	_guardar_resultado_y_salir()

# ─── BOTÓN JUGAR DE NUEVO ─────────────────────────────────────────────────
func _on_btn_jugarde_nuevo_pressed() -> void:
	"""Botón para reintentar el juego"""
	print("Reiniciando juego...")
	get_tree().reload_current_scene()

# ─── GUARDAR RESULTADO Y SALIR A VITA ──────────────────────────────────────
func _guardar_resultado_y_salir():
	"""
	Guarda el resultado del juego en el archivo que Vita espera leer
	desde /sdcard/Documents/ (OPCIÓN A)

	📱 Ruta: /sdcard/Documents/game_result.txt
	📝 Contenido: "GANASTE" o "PERDISTE"
	"""
	var resultado_android = "GANASTE" if ganador else "PERDISTE"

	if OS.get_name() == "Android":
		# ✅ OPCIÓN A: Guardar en /sdcard/Documents/
		var path = "/sdcard/Documents/game_result.txt"

		print("\n" + "="*70)
		print("🎮 GUARDANDO RESULTADO DEL JUEGO - ATRAPA SALUDABLE")
		print("="*70)
		print("📁 Ruta: ", path)
		print("📝 Resultado: ", resultado_android)
		print("👤 Ganador: ", ganador)
		print("⭐ XP: ", xp_ganados)
		print("="*70 + "\n")

		# ✅ Escribir el archivo en /sdcard/Documents/
		var file = FileAccess.open(path, FileAccess.WRITE)

		if file:
			file.store_string(resultado_android)
			file.close()
			print("✅ ÉXITO: Archivo guardado en /sdcard/Documents/")
			print("   Ruta: ", path)
			print("   Contenido: ", resultado_android)
		else:
			# ❌ Si falla, mostrar error
			print("❌ ERROR: No se pudo escribir en /sdcard/Documents/game_result.txt")
			var error = FileAccess.get_open_error()
			print("   Error code: ", error)
			print("   Asegúrate de que /sdcard/Documents/ existe")
	else:
		# Si no es Android (para testing en editor)
		print("✓ (Simulado en editor) Resultado: ", resultado_android)

	# ✅ Esperar para asegurar que el archivo se escribió
	print("\n⏳ Esperando 2 segundos para que se escriba el archivo...")
	await get_tree().create_timer(2.0).timeout

	# ✅ Cerrar la aplicación
	print("🔙 Cerrando juego y volviendo a Vita...\n")
	get_tree().quit()

# ─── FUNCIONES AUXILIARES PARA EL JUEGO ──────────────────────────────────
# Conecta estas funciones según tu lógica de juego

func determinar_victoria(puntos_finales: int, meta_puntos: int = 10):
	"""
	Determina si el usuario ganó o perdió basado en los puntos
	Llama a esta función cuando el juego termine
	"""
	if puntos_finales >= meta_puntos:
		_on_juego_ganado()
	else:
		_on_juego_perdido()

# Ejemplo: Si tienes un timer que finaliza el juego
func _on_tiempo_agotado(puntos_actuales: int):
	"""Se llama cuando se agota el tiempo"""
	determinar_victoria(puntos_actuales)

# Ejemplo: Si tienes un sistema de vidas
func _on_vidas_agotadas():
	"""Se llama cuando se agotan las vidas"""
	_on_juego_perdido()

