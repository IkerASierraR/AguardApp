package com.example.aguardapp.core.navigation

/** Todas las rutas de la app. Las que llevan datos tienen una función para armar la ruta completa. */
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val CONFIGURAR_HOGAR = "configurar_hogar"
    const val MI_DEPOSITO = "mi_deposito"
    const val ME_QUEDE_SIN_AGUA = "me_quede_sin_agua"
    const val AVISOS = "avisos"

    // Rutas con argumentos.
    const val ARG_TIPO = "tipo"
    const val ARG_DEFICIT = "deficit"
    const val REGISTRAR_LLENADO = "registrar_llenado/{$ARG_TIPO}"
    const val QUE_RECORTAR = "que_recortar/{$ARG_DEFICIT}"

    /** "registrar_llenado/completo" o "registrar_llenado/parcial". */
    fun registrarLlenado(tipo: String): String = "registrar_llenado/$tipo"

    /** "que_recortar/120": el déficit va en litros. */
    fun queRecortar(deficitLitros: Int): String = "que_recortar/$deficitLitros"
}
