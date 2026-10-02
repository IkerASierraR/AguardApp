package com.example.aguardapp.feature.deposito.presentation.componentes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.LocalTime
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.FuenteTexto

/** Pide una hora con el selector del sistema; la usa "Se acabó antes". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoDeHora(
    titulo: String,
    horaInicial: LocalTime,
    onConfirmar: (LocalTime) -> Unit,
    onCancelar: () -> Unit
) {
    val estado = rememberTimePickerState(horaInicial.hour, horaInicial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo, fontFamily = FuenteTexto) },
        text = { TimePicker(estado) },
        confirmButton = { TextButton({ onConfirmar(LocalTime(estado.hour, estado.minute)) }) { Text("Aceptar", color = AguaMedia) } },
        dismissButton = { TextButton(onCancelar) { Text("Cancelar", color = AguaMedia) } }
    )
}
