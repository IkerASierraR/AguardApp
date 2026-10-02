package com.example.aguardapp.feature.sector.domain.usecase

import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.model.Sector

class ResolverSector {

    fun resolver(ubicacion: Coordenada, sectores: List<Sector>): Sector? =
        sectores.minByOrNull { it.centro.distanciaKmHasta(ubicacion) }
}
