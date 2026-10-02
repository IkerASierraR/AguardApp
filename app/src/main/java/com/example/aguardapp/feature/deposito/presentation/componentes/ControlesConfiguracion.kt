package com.example.aguardapp.feature.deposito.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguardapp.R
import androidx.compose.ui.res.painterResource
import com.example.aguardapp.core.ui.theme.Agua
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.Divisor
import com.example.aguardapp.core.ui.theme.Fondo
import com.example.aguardapp.core.ui.theme.FuenteNumeros
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.Tenue
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.core.ui.theme.sombraSuave
import com.example.aguardapp.feature.deposito.domain.model.TipoReservorio
import kotlin.math.roundToInt
import com.example.aguardapp.feature.deposito.presentation.CAPACIDAD_MAXIMA_LITROS
import com.example.aguardapp.feature.deposito.presentation.CAPACIDAD_MINIMA_LITROS
import com.example.aguardapp.feature.deposito.presentation.PASO_CAPACIDAD_LITROS
import com.example.aguardapp.feature.deposito.presentation.formatearMiles

private val OPCIONES_DE_TIPO: List<Triple<TipoReservorio, String, Int>> = listOf(
    Triple(TipoReservorio.TANQUE_ELEVADO, "Tanque elevado", R.drawable.ic_tanque_elevado),
    Triple(TipoReservorio.CISTERNA, "Cisterna", R.drawable.ic_cisterna),
    Triple(TipoReservorio.BIDONES, "Bidones", R.drawable.ic_bidones)
)

@Composable
fun SeccionConfiguracion(titulo: String, modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo.uppercase(), fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.88.sp, color = TintaTenue)
        contenido()
    }
}

@Composable
fun TarjetaBlanca(modifier: Modifier = Modifier, padding: Int = 18, contenido: @Composable () -> Unit) {
    Column(
        modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(padding.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) { contenido() }
}

@Composable
fun SelectorDeTipo(seleccionado: TipoReservorio, onElegir: (TipoReservorio) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OPCIONES_DE_TIPO.forEach { (tipo, etiqueta, icono) ->
            OpcionDeTipo(etiqueta, icono, activa = tipo == seleccionado, Modifier.weight(1f)) { onElegir(tipo) }
        }
    }
}

@Composable
private fun OpcionDeTipo(etiqueta: String, icono: Int, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val forma = RoundedCornerShape(15.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(if (activa) Tenue else Blanco)
            .border(if (activa) 2.dp else 1.2.dp, if (activa) Agua else Divisor, forma)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(24.dp), tint = if (activa) AguaMedia else TintaTenue)
        Text(
            etiqueta, fontFamily = FuenteTexto, fontSize = 10.5.sp, lineHeight = 13.sp, textAlign = TextAlign.Center,
            fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium, color = if (activa) AguaMedia else TintaSuave
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlDeCapacidad(litros: Int, onCambiar: (Int) -> Unit) {
    TarjetaBlanca {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                Text(formatearMiles(litros), fontFamily = FuenteNumeros, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.56).sp, color = Tinta)
                Text("L", Modifier.padding(bottom = 4.dp), fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
            }
            Text("típico 1 000 – 2 500 L", Modifier.padding(bottom = 4.dp), fontFamily = FuenteTexto, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TintaTenue)
        }
        Slider(
            value = litros.toFloat(),
            onValueChange = { onCambiar((it / PASO_CAPACIDAD_LITROS).roundToInt() * PASO_CAPACIDAD_LITROS) },
            modifier = Modifier.fillMaxWidth().height(20.dp),
            valueRange = CAPACIDAD_MINIMA_LITROS.toFloat()..CAPACIDAD_MAXIMA_LITROS.toFloat(),
            thumb = { Box(Modifier.size(16.5.dp).clip(CircleShape).background(Blanco).border(3.5.dp, Agua, CircleShape)) },
            track = { estado -> PistaDelDeslizador(estado) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PistaDelDeslizador(estado: SliderState) {
    val rango = estado.valueRange
    val avance = ((estado.value - rango.start) / (rango.endInclusive - rango.start)).coerceIn(0f, 1f)
    Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Tenue)) {
        Box(Modifier.fillMaxWidth(avance).fillMaxHeight().background(Brush.horizontalGradient(listOf(AguaMedia, Agua))))
    }
}

@Composable
fun ControlDeHabitantes(valor: Int, onCambiar: (Int) -> Unit) {
    TarjetaBlanca(padding = 16) {
        Row(Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            BotonCuadrado("−", Fondo, TintaSuave, 40) { onCambiar(-1) }
            Text("$valor", fontFamily = FuenteNumeros, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Tinta)
            BotonCuadrado("+", Tenue, AguaMedia, 40) { onCambiar(1) }
            Text("define los L/hab·día", fontFamily = FuenteTexto, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, color = TintaTenue)
        }
    }
}

@Composable
fun ControlCompacto(valor: Int, onCambiar: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        BotonCuadrado("−", Fondo, TintaSuave, 24) { onCambiar(-1) }
        Text("$valor", fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Tinta)
        BotonCuadrado("+", Tenue, AguaMedia, 24) { onCambiar(1) }
    }
}

@Composable
private fun BotonCuadrado(simbolo: String, fondo: Color, color: Color, tamano: Int, onClick: () -> Unit) {
    Box(
        Modifier.size(tamano.dp).clip(RoundedCornerShape((tamano / 3).dp)).background(fondo).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(simbolo, fontFamily = FuenteTexto, fontSize = if (tamano >= 40) 20.sp else 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

/** Una fila de la tarjeta de hábitos: etiqueta a la izquierda y el valor a la derecha, en cifras monoespaciadas. */
@Composable
fun FilaDeHabito(etiqueta: String, modifier: Modifier = Modifier, valor: @Composable () -> Unit) {
    Row(modifier.fillMaxWidth().height(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        valor()
    }
}

/** "Sí" en turquesa y "No" en gris; tocar la fila lo alterna. */
@Composable
fun FilaSiNo(etiqueta: String, activo: Boolean, onAlternar: () -> Unit) {
    FilaDeHabito(etiqueta, Modifier.toggleable(value = activo, role = Role.Switch, onValueChange = { onAlternar() })) {
        Text(
            if (activo) "Sí" else "No", fontFamily = FuenteNumeros, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold, color = if (activo) AguaMedia else TintaTenue
        )
    }
}
