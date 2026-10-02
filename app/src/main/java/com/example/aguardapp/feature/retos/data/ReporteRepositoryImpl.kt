package com.example.aguardapp.feature.retos.data

import com.example.aguardapp.feature.retos.data.local.ReporteEntity
import com.example.aguardapp.feature.retos.data.local.RetosDao
import com.example.aguardapp.feature.retos.data.sync.NubeReportesSupabase
import com.example.aguardapp.feature.retos.domain.model.Reporte
import com.example.aguardapp.feature.retos.domain.model.TipoReporte
import com.example.aguardapp.feature.retos.domain.repository.ReporteRepository

class ReporteRepositoryImpl(
    private val dao: RetosDao,
    private val usuarioId: String,
    private val nube: NubeReportesSupabase
) : ReporteRepository {
    override suspend fun guardar(reporte: Reporte) {
        dao.guardarReporte(reporte.aEntidad(usuarioId, reporte.fotoBytes))
        runCatching { nube.guardar(reporte) }
    }

    override suspend fun obtenerPendientes(): List<Reporte> {
        runCatching { nube.obtener() }.getOrDefault(emptyList()).forEach {
            val local = dao.obtenerReportes().firstOrNull { guardado -> guardado.id == it.id }
            dao.guardarReporte(it.aEntidad(usuarioId, local?.fotoBytes))
        }
        return dao.obtenerReportes().map { it.aDominio() }
    }
}

private fun Reporte.aEntidad(usuario: String, fotoLocal: ByteArray?) = ReporteEntity(
    id, usuario, tipo.name, descripcion, latitud, longitud, fotoLocal, pendienteSincronizacion
)

private fun ReporteEntity.aDominio() = Reporte(
    id, TipoReporte.valueOf(tipo), descripcion, latitud, longitud,
    fotoUri = if (fotoBytes != null) "foto-local" else null,
    fotoBytes = fotoBytes,
    pendienteSincronizacion = pendienteSincronizacion
)
