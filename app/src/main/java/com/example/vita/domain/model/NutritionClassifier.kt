package com.example.vita.domain.model

object NutritionClassifier {

    // ─── Diccionarios de palabras clave ───────────────────────────────────────

    private val MUY_SALUDABLE = setOf(
        "manzana", "pera", "naranja", "plátano", "banana", "uva", "fresa", "fresas",
        "kiwi", "melón", "sandía", "piña", "mango", "papaya", "ciruela", "durazno",
        "espinaca", "brócoli", "zanahoria", "lechuga", "tomate", "pepino", "acelga",
        "coliflor", "espárrago", "apio", "remolacha", "aguacate", "avocado",
        "salmón", "atún", "sardina", "trucha", "avena", "quinoa", "linaza",
        "chia", "chía", "almendra", "almendras", "nuez", "nueces",
        "ensalada", "fruta", "verdura", "vegetal", "arándano", "frambuesa",
        "berries", "espinacas", "betabel", "jícama", "nopales"
    )

    private val SALUDABLE = setOf(
        "pollo", "pechuga", "pavo", "huevo", "huevos", "arroz integral",
        "lenteja", "lentejas", "frijol", "frijoles", "garbanzo", "garbanzos",
        "leche", "yogur", "yogurt", "queso cottage", "pan integral",
        "proteína", "filete", "filete de pollo", "habas", "sopa",
        "caldo", "mariscos", "camarones", "tilapia", "bacalao",
        "claras", "edamame", "hummus", "tofu", "tempeh"
    )

    private val REGULAR = setOf(
        "pasta", "pan", "queso", "jamón", "embutido", "cereal",
        "galleta", "galletas", "sandwich", "wrap", "jugo", "zumo",
        "café", "tortilla", "leche entera", "yogur azucarado", "arroz",
        "quesadilla", "tostada", "empanada", "tamale", "tamal"
    )

    private val POCO_SANO = setOf(
        "pizza", "hamburguesa", "burger", "papas fritas", "fries",
        "hotdog", "perro caliente", "donut", "dona", "pastel", "torta dulce",
        "cake", "helado", "chocolate", "caramelo", "dulce", "dulces",
        "refresco", "soda", "coca", "pepsi", "sprite", "chips", "frituras",
        "pollo frito", "nuggets", "churros", "waffles", "bacon", "tocino",
        "salchicha", "chorizo", "empanada frita", "alitas fritas",
        "hot dog", "cheetos", "doritos", "gomitas", "palomitas mantequilla"
    )

    // ─── Clasificador principal ───────────────────────────────────────────────

    fun classify(foodName: String, calories: Int): NutritionResult {

        // Regla 1 — límite calórico por ingesta (siempre tiene prioridad)
        if (calories >= GameConfig.LIMITE_CALORIAS_INGESTA) {
            return NutritionResult(
                healthyScore      = 20,
                category          = "Poco sano",
                nutritionCategory = NutritionCategory.POCO_SANO,
                xpEarned          = GameConfig.XP_COMIDA_EXCESO,
                isOverCalorieLimit = true
            )
        }

        val name = foodName.lowercase().trim()

        // Regla 2 — palabras clave (orden importa: poco sano antes que regular)
        val categoryDetected: NutritionCategory = when {
            MUY_SALUDABLE.any { name.contains(it) } -> NutritionCategory.MUY_SALUDABLE
            POCO_SANO.any     { name.contains(it) } -> NutritionCategory.POCO_SANO
            SALUDABLE.any     { name.contains(it) } -> NutritionCategory.SALUDABLE
            REGULAR.any       { name.contains(it) } -> NutritionCategory.REGULAR
            // Regla 3 — fallback por rangos calóricos
            calories <= 150 -> NutritionCategory.MUY_SALUDABLE
            calories <= 300 -> NutritionCategory.SALUDABLE
            calories <= 500 -> NutritionCategory.REGULAR
            else            -> NutritionCategory.POCO_SANO
        }

        return buildResult(categoryDetected)
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private fun buildResult(category: NutritionCategory) = when (category) {
        NutritionCategory.MUY_SALUDABLE -> NutritionResult(
            healthyScore      = 90,
            category          = "Muy saludable",
            nutritionCategory = category,
            xpEarned          = GameConfig.XP_COMIDA_MUY_SALUDABLE,
            isOverCalorieLimit = false
        )
        NutritionCategory.SALUDABLE -> NutritionResult(
            healthyScore      = 70,
            category          = "Saludable",
            nutritionCategory = category,
            xpEarned          = GameConfig.XP_COMIDA_SALUDABLE,
            isOverCalorieLimit = false
        )
        NutritionCategory.REGULAR -> NutritionResult(
            healthyScore      = 50,
            category          = "Regular",
            nutritionCategory = category,
            xpEarned          = GameConfig.XP_COMIDA_REGULAR,
            isOverCalorieLimit = false
        )
        NutritionCategory.POCO_SANO -> NutritionResult(
            healthyScore      = 20,
            category          = "Poco sano",
            nutritionCategory = category,
            xpEarned          = GameConfig.XP_COMIDA_POCO_SANO,
            isOverCalorieLimit = false
        )
    }
}