package com.example.aguardapp.core.navigation

import androidx.annotation.DrawableRes
import com.example.aguardapp.R
enum class Destino(
    val etiqueta: String,
    @param:DrawableRes val icono: Int,
    val responsable: String
) {
    RESERVA("Reserva", R.drawable.ic_reserva, "Cristhian"),
    SECTOR("Sector", R.drawable.ic_sector, "Dayan"),
    AHORRO("Ahorro", R.drawable.ic_ahorro, "Jimmy"),
    RECIBO("Recibo", R.drawable.ic_recibo, "Iker")
}
