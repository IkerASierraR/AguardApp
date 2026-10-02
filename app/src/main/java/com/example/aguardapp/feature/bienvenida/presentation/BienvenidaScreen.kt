package com.example.aguardapp.feature.bienvenida.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.R
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.AguaProfunda
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.TintaTenue

private val FORMA = RoundedCornerShape(16.dp)

@Composable
fun BienvenidaScreen(
    onListo: () -> Unit,
    viewModel: BienvenidaViewModel = viewModel { BienvenidaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.listo) { if (uiState.listo) onListo() }
    IconosClarosEnBarraDeEstado(claros = true)
    Column(Modifier.fillMaxSize().background(AguaMedia).statusBarsPadding().navigationBarsPadding()) {
        Presentacion(Modifier.weight(1f))
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)) {
            Consentimiento(uiState.consentimiento, viewModel::onAlternarConsentimiento)
            Spacer(Modifier.height(20.dp))
            Box(
                Modifier.fillMaxWidth().height(54.dp).clip(FORMA)
                    .background(if (uiState.consentimiento) Blanco else TintaTenue)
                    .clickable(enabled = uiState.consentimiento, role = Role.Button, onClick = viewModel::onEmpezar),
                contentAlignment = Alignment.Center
            ) { Text("Empezar", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AguaMedia) }
        }
    }
}

@Composable
private fun Presentacion(modifier: Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(40.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(AguaProfunda), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_gota), contentDescription = null, modifier = Modifier.size(24.dp), tint = Blanco)
            }
            Text("AguardApp", fontFamily = FuenteTexto, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
        }
        Spacer(Modifier.height(30.dp))
        Text(
            "Sabe cuánta agua te queda y hasta cuándo te alcanza.",
            fontFamily = FuenteTexto, fontSize = 33.sp, lineHeight = 39.sp, fontWeight = FontWeight.ExtraBold, color = Blanco
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Aunque el servicio venga por horas. Sin leer el medidor.",
            fontFamily = FuenteTexto, fontSize = 14.5.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium, color = Blanco
        )
        Spacer(Modifier.height(30.dp))
        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Beneficio("Marcas el llenado", "Un toque cuando llega el agua")
            Beneficio("Calcula la proyección", "Sabe a qué hora se te acaba")
            Beneficio("Funciona sin internet", "Todo se guarda en tu teléfono")
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Beneficio(titulo: String, detalle: String) {
    Row(
        Modifier.fillMaxWidth().clip(FORMA).background(AguaProfunda).padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(20.dp), tint = Blanco)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Blanco)
            Text(detalle, fontFamily = FuenteTexto, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = Blanco)
        }
    }
}

@Composable
private fun Consentimiento(marcado: Boolean, onAlternar: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Checkbox, onClick = onAlternar),
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Box(
            Modifier.size(23.dp).clip(RoundedCornerShape(7.dp))
                .background(if (marcado) Blanco else AguaProfunda)
                .border(1.5.dp, Blanco, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (marcado) Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(15.dp), tint = AguaMedia)
        }
        Text(
            "Acepto que la app guarde en mi teléfono los datos de mi hogar y mi consumo. Puedo borrarlos cuando quiera.",
            fontFamily = FuenteTexto, fontSize = 11.5.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium, color = Blanco
        )
    }
}
