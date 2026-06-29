extends Node

@export var lpuntos: Label
@export var ltiempo: Label
@export var ltimeinicio: Label

var puntos_actuales := 0
var tiempo_restante := 30
var juego_iniciado := false
var juego_terminado := false
var target_score := 10

func _ready():
	$Timer.stop()
	$spawmer.puede_spawnear = false
	$spawmer.child_entered_tree.connect(_on_spawmer_child_entered)
	_actualizar_pantalla()
	_empezar_countdown()

func _empezar_countdown():
	for i in range(3, 0, -1):
		ltimeinicio.text = str(i)
		await get_tree().create_timer(1.0).timeout
	ltimeinicio.text = ""
	_empezar_juego()

func _empezar_juego():
	juego_iniciado = true
	$spawmer.puede_spawnear = true
	$Timer.start()

func _on_timer_timeout():
	if juego_terminado:
		return
	tiempo_restante -= 1
	_actualizar_pantalla()
	if tiempo_restante <= 0:
		_terminar_juego(false)

func _on_spawmer_child_entered(child):
	if child.has_signal("objeto_eliminado"):
		child.objeto_eliminado.connect(_on_objeto_eliminado)

func _on_objeto_eliminado(valor):
	if juego_terminado:
		return
	puntos_actuales += valor
	_actualizar_pantalla()
	if puntos_actuales >= target_score:
		_terminar_juego(true)

func _terminar_juego(gano: bool):
	juego_terminado = true
	$spawmer.puede_spawnear = false
	$Timer.stop()

	var resultado_export = "GANASTE" if gano else "PERDISTE"
	var resultado_ui = "gano" if gano else "perdio"

	# Enviamos el resultado por Broadcast (Inmune a bloqueos de sistema)
	var enviado = _enviar_por_broadcast(resultado_export)

	if enviado:
		return

	# FALLBACK: Solo si estás probando en PC
	await get_tree().create_timer(0.5).timeout
	var final_scene = load("res://Scene/pantallafinal.tscn")
	get_tree().change_scene_to_packed(final_scene)
	await get_tree().process_frame
	var final_node = get_tree().current_scene.find_child("Pantallafinal", true, false)
	if final_node and final_node.has_method("configurar_pantalla"):
		final_node.configurar_pantalla(resultado_ui)

func _enviar_por_broadcast(resultado: String) -> bool:
	if OS.get_name() != "Android":
		return false

	var godot_singleton = Engine.get_singleton("Godot")
	if godot_singleton:
		var activity = godot_singleton.get_activity()
		if activity:
			activity.runOnUiThread(func():
				var java_wrapper = Engine.get_singleton("JavaClassWrapper")
				var IntentClass = java_wrapper.wrap("android.content.Intent")

				# Creamos un Intent con una acción única para tu app principal
				var broadcast_intent = IntentClass.new("com.example.vita.GAME_RESULT")
				broadcast_intent.putExtra("game_result", resultado)

				# Enviamos la transmisión y cerramos el juego
				activity.sendBroadcast(broadcast_intent)
				activity.finish()
				print("¡Broadcast enviado con éxito desde Godot!: ", resultado)
			)
			return true
	return false

func _actualizar_pantalla():
	lpuntos.text = str(puntos_actuales)
	ltiempo.text = str(tiempo_restante)