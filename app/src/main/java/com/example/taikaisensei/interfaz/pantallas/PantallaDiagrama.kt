package com.example.taikaisensei.interfaz.pantallas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taikaisensei.datos.Competidor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaDiagrama(
    // Lista inicial de competidores
    competidoresIniciales: List<Competidor>,
    // Controlador para navegar entre pantallas
    navController: NavController,
    // Nombre del torneo actual
    nombreTorneo: String,
    // Categoría del torneo actual
    categoriaTorneo: String
) {
    // Índice para saber qué ronda se está mostrando
    var rondaActualIndex by rememberSaveable { mutableStateOf(0) }

    // Estado para guardar el campeón cuando se determine
    var campeon by remember { mutableStateOf<Competidor?>(null) }

    val scrollState = rememberScrollState()

    // Guarda cuántas veces se ha abierto el marcador en cada ronda
    var aperturasMarcadorPorRonda by rememberSaveable {
        mutableStateOf<Map<Int, Int>>(emptyMap())
    }

    // Guarda los ganadores seleccionados por ronda y combate para que no se pierdan al volver del marcador
    var ganadoresSeleccionados by rememberSaveable {
        mutableStateOf<Map<String, String>>(emptyMap())
    }

    // Reconstruye las rondas a partir de los competidores iniciales y los ganadores seleccionados
    val rondas = remember(competidoresIniciales, ganadoresSeleccionados, rondaActualIndex) {
        generarRondasHastaIndice(
            competidoresIniciales = competidoresIniciales,
            ganadoresSeleccionados = ganadoresSeleccionados,
            rondaActualIndex = rondaActualIndex
        )
    }

    // Evita fallos si por cualquier motivo el índice apunta a una ronda que no existe
    val indiceRondaSeguro = rondaActualIndex.coerceIn(0, rondas.lastIndex)

    // Ronda actualmente visible
    val rondaActual = rondas[indiceRondaSeguro]

    // Cada vez que cambia la ronda actual, hace scroll al inicio
    LaunchedEffect(indiceRondaSeguro) {
        scrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título con número de ronda
        Text(
            "Ronda ${indiceRondaSeguro + 1}",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contenedor scrollable para los enfrentamientos de la ronda actual
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            rondaActual.forEachIndexed { index, enfrentamiento ->

                // Clave única para identificar cada combate dentro de cada ronda
                val claveGanador = "ronda_${indiceRondaSeguro}_combate_$index"

                // Ganador guardado para este combate, si existe
                val ganadorSeleccionado = ganadoresSeleccionados[claveGanador]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    // Indicador para que el usuario seleccione ganador
                    Text(
                        "Selecciona al ganador:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Botón para seleccionar competidor 1 como ganador
                        Button(
                            onClick = {
                                enfrentamiento.ganador = "competidor1"

                                ganadoresSeleccionados = ganadoresSeleccionados.toMutableMap().apply {
                                    this[claveGanador] = "competidor1"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ganadorSeleccionado == "competidor1")
                                    Color(0xFFB71C1C) else Color(0xFFEF9A9A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("${enfrentamiento.competidor1.nombre} (${enfrentamiento.competidor1.club})")
                        }

                        // Botón para seleccionar competidor 2 como ganador
                        Button(
                            onClick = {
                                enfrentamiento.ganador = "competidor2"

                                ganadoresSeleccionados = ganadoresSeleccionados.toMutableMap().apply {
                                    this[claveGanador] = "competidor2"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ganadorSeleccionado == "competidor2")
                                    Color(0xFF0D47A1) else Color(0xFF90CAF9),
                                contentColor = Color.White
                            )
                        ) {
                            Text("${enfrentamiento.competidor2.nombre} (${enfrentamiento.competidor2.club})")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para avanzar a la siguiente ronda, solo activo si todos los enfrentamientos tienen ganador
        if (campeon == null) {
            Button(
                onClick = {
                    // Recopila los ganadores seleccionados en la ronda actual
                    val ganadores = rondaActual.mapIndexedNotNull { index, enf ->
                        val claveGanador = "ronda_${indiceRondaSeguro}_combate_$index"

                        when (ganadoresSeleccionados[claveGanador]) {
                            "competidor1" -> enf.competidor1
                            "competidor2" -> enf.competidor2
                            else -> null
                        }
                    }

                    if (ganadores.size == 1) {
                        // Si solo queda un ganador, se declara campeón y se guarda torneo en Firestore
                        campeon = ganadores.first()

                        val ultimoEnfrentamiento = rondaActual.last()

                        val subcampeon = if (campeon!!.nombre == ultimoEnfrentamiento.competidor1.nombre)
                            ultimoEnfrentamiento.competidor2
                        else
                            ultimoEnfrentamiento.competidor1

                        // Referencia a Firestore y usuario autenticado
                        val db = FirebaseFirestore.getInstance()
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                        // Datos que se guardarán del torneo
                        val torneoData = hashMapOf(
                            "usuarioId" to userId,
                            "nombreTorneo" to nombreTorneo,
                            "categoria" to categoriaTorneo,
                            "campeon" to mapOf(
                                "nombre" to campeon!!.nombre,
                                "club" to campeon!!.club
                            ),
                            "subcampeon" to mapOf(
                                "nombre" to subcampeon.nombre,
                                "club" to subcampeon.club
                            ),
                            "timestamp" to com.google.firebase.Timestamp.now()
                        )

                        // Guardado en Firestore con logging para éxito o fallo
                        db.collection("torneos")
                            .add(torneoData)
                            .addOnSuccessListener {
                                Log.d("FIREBASE", "Torneo guardado correctamente.")
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                                Log.e("FIREBASE", "Error al guardar torneo", e)
                            }
                    } else {
                        // Si quedan varios ganadores, avanza a la siguiente ronda
                        rondaActualIndex = indiceRondaSeguro + 1
                    }
                },
                enabled = rondaActual.isNotEmpty() && rondaActual.indices.all { index ->
                    ganadoresSeleccionados.containsKey("ronda_${indiceRondaSeguro}_combate_$index")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Avanzar Ronda")
            }
        }

        // Número de veces que se ha abierto el marcador en la ronda actual
        val aperturasMarcadorRondaActual = aperturasMarcadorPorRonda[indiceRondaSeguro] ?: 0

        // Número máximo de veces que se puede abrir el marcador en esta ronda
        val maxAperturasMarcadorRondaActual = rondaActual.size

        // Separación entre "Avanzar Ronda" y "Abrir marcador Kumite"
        if (campeon == null) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón para abrir el marcador de Kumite desde el diagrama
        if (campeon == null && aperturasMarcadorRondaActual < maxAperturasMarcadorRondaActual) {
            Button(
                onClick = {
                    val aperturasActuales = aperturasMarcadorPorRonda[indiceRondaSeguro] ?: 0

                    aperturasMarcadorPorRonda = aperturasMarcadorPorRonda.toMutableMap().apply {
                        this[indiceRondaSeguro] = aperturasActuales + 1
                    }

                    navController.navigate("pantalla_marcador_diagrama")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Abrir marcador Kumite (${aperturasMarcadorRondaActual + 1}/$maxAperturasMarcadorRondaActual)")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Si hay campeón, se muestra en pantalla junto con el subcampeón
        campeon?.let { campeonNoNulo ->
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "🏆 Campeón: ${campeonNoNulo.nombre} (${campeonNoNulo.club})",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            val ultimoEnfrentamiento = rondaActual.last()

            val subcampeon = if (campeonNoNulo.nombre == ultimoEnfrentamiento.competidor1.nombre)
                ultimoEnfrentamiento.competidor2
            else
                ultimoEnfrentamiento.competidor1

            Text(
                "🥈 Subcampeón: ${subcampeon.nombre} (${subcampeon.club})",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón para volver a la pantalla de creación de plantilla de competidores
        Button(
            onClick = { navController.navigate("pantalla_competidores") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0D47A1), Color(0xFFB71C1C))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Crear nueva plantilla")
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// Clase para representar un enfrentamiento entre dos competidores
class Enfrentamiento(
    val competidor1: Competidor,
    val competidor2: Competidor,
    ganadorInicial: String? = null,
) {
    // Estado mutable para guardar el ganador seleccionado
    var ganador by mutableStateOf(ganadorInicial)
}

// Genera la primera ronda a partir de la lista inicial de competidores
fun generarPrimeraRonda(competidores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()

    // Empareja competidores de dos en dos de forma secuencial
    for (i in competidores.indices step 2) {
        val c1 = competidores.getOrNull(i)
        val c2 = competidores.getOrNull(i + 1)

        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }

    return emparejamientos
}

// Genera una ronda a partir de la lista de ganadores de la ronda anterior
fun generarRonda(ganadores: List<Competidor>): List<Enfrentamiento> {
    val emparejamientos = mutableListOf<Enfrentamiento>()

    // Misma lógica de emparejamiento para la nueva ronda
    for (i in ganadores.indices step 2) {
        val c1 = ganadores.getOrNull(i)
        val c2 = ganadores.getOrNull(i + 1)

        if (c1 != null && c2 != null) {
            emparejamientos.add(Enfrentamiento(c1, c2))
        }
    }

    return emparejamientos
}

// Reconstruye las rondas necesarias hasta llegar a la ronda actual
fun generarRondasHastaIndice(
    competidoresIniciales: List<Competidor>,
    ganadoresSeleccionados: Map<String, String>,
    rondaActualIndex: Int
): List<List<Enfrentamiento>> {

    // Lista donde se guardan las rondas generadas
    val rondasGeneradas = mutableListOf<List<Enfrentamiento>>()

    // Al principio se usan los competidores introducidos por el usuario
    var competidoresRonda = competidoresIniciales

    // Genera las rondas desde la primera hasta la ronda actual
    for (indiceRonda in 0..rondaActualIndex) {

        // Si es la primera ronda, se genera con los competidores iniciales.
        // Si no, se genera con los ganadores de la ronda anterior.
        val ronda = if (indiceRonda == 0) {
            generarPrimeraRonda(competidoresRonda)
        } else {
            generarRonda(competidoresRonda)
        }

        // Se añade la ronda generada a la lista
        rondasGeneradas.add(ronda)

        // Si ya se ha llegado a la ronda actual, se detiene el proceso
        if (indiceRonda == rondaActualIndex) {
            break
        }

        // Se obtienen los ganadores seleccionados de esta ronda
        val ganadores = ronda.mapIndexedNotNull { index, enfrentamiento ->
            val claveGanador = "ronda_${indiceRonda}_combate_$index"

            when (ganadoresSeleccionados[claveGanador]) {
                "competidor1" -> enfrentamiento.competidor1
                "competidor2" -> enfrentamiento.competidor2
                else -> null
            }
        }

        // Si todavía no hay ganadores, no se puede generar la siguiente ronda
        if (ganadores.isEmpty()) {
            break
        }

        // Los ganadores pasan a ser los competidores de la siguiente ronda
        competidoresRonda = ganadores
    }

    // Devuelve las rondas generadas, o una ronda vacía si no se generó ninguna
    return if (rondasGeneradas.isNotEmpty()) {
        rondasGeneradas
    } else {
        listOf(emptyList())
    }
}