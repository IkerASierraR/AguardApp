package com.example.aguardapp.feature.deposito.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalTime
import com.example.aguardapp.R
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.Fondo
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaTenue

// Piezas de diseño que comparten todas las pantallas del depósito. Colores sólidos y una sola fuente.

private val FORMA_BOTON = RoundedCornerShape(16.dp)
private val FORMA_TARJETA = RoundedCornerShape(18.dp)

/** La barra blanca de arriba: botón atrás (si hay a dónde volver), título y subtítulo. */
@Composable
fun BarraSuperior(titulo: String, subtitulo: String, onVolver: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().background(Blanco).statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (onVolver != null) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(Fondo).clickable(role = Role.Button, onClick = onVolver),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_atras), contentDescription = "Volver", modifier = Modifier.size(20.dp), tint = Tinta)
            }
        }
        Column(Modifier.padding(top = 2.dp, start = if (onVolver == null) 4.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, fontFamily = FuenteTexto, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Tinta)
            Text(subtitulo, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaTenue)
        }
    }
}

/** El botón principal: a lo ancho de la pantalla y de un solo color. Deshabilitado se pinta en gris. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = AguaMedia,
    habilitado: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(FORMA_BOTON)
            .background(if (habilitado) color else TintaTenue)
            .clickable(enabled = habilitado, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Blanco)
    }
}

/** El botón secundario: fondo blanco con borde del color del texto. */
@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Coral) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(FORMA_BOTON)
            .background(Blanco)
            .border(1.5.dp, color, FORMA_BOTON)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

/** Tarjeta blanca con esquinas redondeadas, la base de casi todos los bloques. */
@Composable
fun TarjetaBlanca(modifier: Modifier = Modifier, padding: Int = 18, contenido: @Composable () -> Unit) {
    Column(
        modifier.fillMaxWidth().clip(FORMA_TARJETA).background(Blanco).padding(padding.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) { contenido() }
}

/** Un error general de la pantalla, con un botón para cerrarlo. */
@Composable
fun AvisoDeError(mensaje: String, onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Blanco)
            .border(1.5.dp, Coral, RoundedCornerShape(14.dp)).padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mensaje, Modifier.weight(1f), fontFamily = FuenteTexto, fontSize = 13.sp, color = Tinta)
        TextButton(onClick = onCerrar) { Text("Cerrar", fontFamily = FuenteTexto, color = AguaMedia) }
    }
}

/** El mensaje de error que va debajo de un campo; no muestra nada si no hay error. */
@Composable
fun TextoDeError(mensaje: String?) {
    if (mensaje == null) return
    Text(
        mensaje,
        Modifier.padding(start = 4.dp, top = 2.dp),
        fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Coral
    )
}

/** Pide una hora con el selector del sistema. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDeHora(titulo: String, horaInicial: LocalTime, onConfirmar: (LocalTime) -> Unit, onCancelar: () -> Unit) {
    val estado = rememberTimePickerState(horaInicial.hour, horaInicial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, fontFamily = FuenteTexto) },
        text = { TimePicker(estado) },
        confirmButton = { TextButton({ onConfirmar(LocalTime(estado.hour, estado.minute)) }) { Text("Aceptar", color = AguaMedia) } },
        dismissButton = { TextButton(onCancelar) { Text("Cancelar", color = AguaMedia) } }
    )
}
