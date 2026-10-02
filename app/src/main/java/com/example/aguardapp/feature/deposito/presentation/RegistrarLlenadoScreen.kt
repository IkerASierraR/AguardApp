package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.Divisor
import com.example.aguardapp.core.ui.theme.FuenteNumeros
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.core.ui.theme.sombraSuave
import com.example.aguardapp.feature.deposito.domain.model.TipoLlenado
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonSecundario
import com.example.aguardapp.feature.deposito.presentation.componentes.TanqueLleno

private val MARGEN = Modifier.padding(horizontal = 24.dp)

/** Pantalla 04 del Figma: el usuario registra cuánto se llenó su depósito. */
@Composable
fun RegistrarLlenadoScreen(
    vista: DepositoVista?,
    onEvento: (DepositoEvent) -> Unit,
    onVolver: () -> Unit
) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("¿Se llenó el tanque?", "Registra cuánto se llenó tu depósito", onVolver)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaDelTanque(vista?.capacidadLitros, MARGEN)
            BotonPrincipal("Sí, está lleno", { onEvento(DepositoEvent.RegistrarLlenado(TipoLlenado.COMPLETO)) }, MARGEN)
            BotonSecundario("Llenó a la mitad", { onEvento(DepositoEvent.RegistrarLlenado(TipoLlenado.MITAD)) }, MARGEN, alto = 48, color = Tinta, borde = Divisor)
            if (vista != null) ConEsteRegistro(vista, MARGEN)
        }
    }
}

@Composable
private fun TarjetaDelTanque(capacidadLitros: Int?, modifier: Modifier) {
    Column(
        modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TanqueLleno()
        capacidadLitros?.let {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                Text(formatearMiles(it), fontFamily = FuenteNumeros, fontSize = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.14).sp, color = AguaMedia)
                Text("L", Modifier.padding(bottom = 7.dp), fontFamily = FuenteTexto, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TintaTenue)
            }
        }
        Text("capacidad completa de tu tanque", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}

@Composable
private fun ConEsteRegistro(vista: DepositoVista, modifier: Modifier) {
    Column(modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(18.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("CON ESTE REGISTRO", fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp, color = TintaTenue)
        FilaDelRegistro("Depósito", "${formatearMiles(vista.capacidadLitros)} L", AguaMedia)
        FilaDelRegistro("Alcanza hasta", vista.textoAlcanzaHastaSiLlena, Tinta)
    }
}

@Composable
private fun FilaDelRegistro(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}
