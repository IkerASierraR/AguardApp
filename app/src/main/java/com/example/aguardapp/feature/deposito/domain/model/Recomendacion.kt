package com.example.aguardapp.feature.deposito.domain.model

/** Lo que el hogar puede dejar de hacer hoy para que el agua alcance, con los litros que ahorra cada cosa. */
enum class Recomendacion(
    val descripcion: String,
    val litrosQueAhorra: Int,
    private val corresponde: (HabitosDelHogar) -> Boolean
) {
    NO_LAVAR_ROPA("No lavar ropa hoy", 80, { it.usaLavadora }),
    NO_REGAR("No regar el jardín hoy", 80, { it.riegaJardin }),
    DUCHAS_CORTAS("Duchas de 5 minutos", 40, { it.duchasPorDia > 0 }),
    CERRAR_EL_CANO("Cerrar el caño al lavar platos", 45, { true }),
    BALDE_EN_EL_BANO("Usar un balde en el inodoro", 30, { true });

    /** Solo se sugiere lo que el hogar hace: no se propone "no regar" a quien no riega. */
    fun correspondeA(habitos: HabitosDelHogar): Boolean = corresponde(habitos)
}
