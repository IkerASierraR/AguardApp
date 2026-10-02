package com.example.aguardapp.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aguardapp.feature.bienvenida.presentation.BienvenidaScreen
import com.example.aguardapp.feature.deposito.presentation.ConfiguracionScreen
import com.example.aguardapp.feature.deposito.presentation.DepositoScreen
import com.example.aguardapp.feature.deposito.presentation.QueRecortarScreen
import com.example.aguardapp.feature.deposito.presentation.RegistrarLlenadoScreen
import com.example.aguardapp.feature.deposito.presentation.SinAguaScreen
import com.example.aguardapp.feature.deposito.presentation.TipoLlenado

/** Declara todas las pantallas de la app y cómo se pasa de una a otra. */
@Composable
fun AppNavHost(rutaInicial: String, hayConfiguracion: Boolean) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = rutaInicial) {

        composable(Rutas.BIENVENIDA) {
            BienvenidaScreen(onListo = { irDespuesDeLaBienvenida(navController, hayConfiguracion) })
        }

        composable(Rutas.CONFIGURAR_HOGAR) {
            // Si se llegó desde Mi depósito se está editando: se puede volver atrás.
            val editando = navController.previousBackStackEntry != null
            ConBarraDelSistema {
                ConfiguracionScreen(
                    onListo = { irDespuesDeConfigurar(navController, editando) },
                    onVolver = if (editando) ({ navController.popBackStack() }) else null
                )
            }
        }

        composable(Rutas.MI_DEPOSITO) {
            ConBarraDelSistema {
                DepositoScreen(
                    onRegistrarLlenado = { tipo -> navController.navigate(Rutas.registrarLlenado(tipo.ruta)) },
                    onQueRecortar = { deficit -> navController.navigate(Rutas.queRecortar(deficit)) },
                    onSinAgua = { navController.navigate(Rutas.ME_QUEDE_SIN_AGUA) },
                    onEditarHogar = { navController.navigate(Rutas.CONFIGURAR_HOGAR) }
                )
            }
        }

        composable(
            Rutas.REGISTRAR_LLENADO,
            arguments = listOf(navArgument(Rutas.ARG_TIPO) { type = NavType.StringType })
        ) { entrada ->
            val tipo = TipoLlenado.desdeRuta(entrada.arguments?.getString(Rutas.ARG_TIPO))
            ConBarraDelSistema {
                RegistrarLlenadoScreen(
                    tipo = tipo,
                    onListo = { navController.popBackStack() },
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(
            Rutas.QUE_RECORTAR,
            arguments = listOf(navArgument(Rutas.ARG_DEFICIT) { type = NavType.IntType })
        ) { entrada ->
            val deficit = entrada.arguments?.getInt(Rutas.ARG_DEFICIT) ?: 0
            ConBarraDelSistema {
                QueRecortarScreen(deficitLitros = deficit, onVolver = { navController.popBackStack() })
            }
        }

        composable(Rutas.ME_QUEDE_SIN_AGUA) {
            ConBarraDelSistema {
                SinAguaScreen(onVolver = { navController.popBackStack() })
            }
        }
    }
}

// Tras la bienvenida: configurar el hogar, o Mi depósito si ya estaba configurado. La bienvenida no queda atrás.
private fun irDespuesDeLaBienvenida(navController: NavHostController, hayConfiguracion: Boolean) {
    val destino = if (hayConfiguracion) Rutas.MI_DEPOSITO else Rutas.CONFIGURAR_HOGAR
    navController.navigate(destino) { popUpTo(Rutas.BIENVENIDA) { inclusive = true } }
}

// Al guardar: si se estaba editando se vuelve atrás; la primera vez se va a Mi depósito.
private fun irDespuesDeConfigurar(navController: NavHostController, editando: Boolean) {
    if (editando) {
        navController.popBackStack()
    } else {
        navController.navigate(Rutas.MI_DEPOSITO) { popUpTo(Rutas.CONFIGURAR_HOGAR) { inclusive = true } }
    }
}

/** Deja libre el espacio de la barra de navegación del sistema, con el fondo de la app debajo. */
@Composable
private fun ConBarraDelSistema(contenido: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding()) {
        contenido()
    }
}
