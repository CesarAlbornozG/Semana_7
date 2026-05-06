package com.example.semana6

//importamos clases para el uso de elementos visuales u obtener permisos
import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*

//esta es la pestaña del menu
class MenuActivity : ComponentActivity() {
    //metodo de inicio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //aca se llama al metodo
        obtenerYGuardarGPS()


        //aqui van los elementos que tienen que ver con la interfaz

        setContent {

            //con esta var hacemos el cambio de pestañas
            var pantalla by remember { mutableStateOf("menu") }

            when (pantalla) {

                // Menu
                "menu" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Si hacemos click en el boton, la pantalla cambia a la pestaña correspondiente
                        Button(onClick = { pantalla = "despacho" }) {
                            Text("Calcular Despacho")
                        }

                        Button(onClick = { pantalla = "temperatura" }) {
                            Text("Revisar Temperatura")
                        }
                    }
                }

                // Calculo Despacho
                "despacho" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        //declaramos variables sin asignar valores
                        val marca = remember { mutableStateOf("") }
                        val modelo = remember { mutableStateOf("") }
                        val cilindrada = remember { mutableStateOf("") }
                        val combustible = remember { mutableStateOf("") }
                        val pasajeros = remember { mutableStateOf("") }
                        val totalCompra = remember { mutableStateOf("") }
                        val distancia = remember { mutableStateOf("") }
                        val resultado = remember { mutableStateOf("") }

                        //textos en gris, referencias para ingresar datos en cada campo
                        OutlinedTextField(
                            marca.value,
                            { marca.value = it },
                            label = { Text("Marca del vehiculo") })
                        OutlinedTextField(
                            modelo.value,
                            { modelo.value = it },
                            label = { Text("Modelo del vehiculo") })
                        OutlinedTextField(
                            cilindrada.value,
                            { cilindrada.value = it },
                            label = { Text("Cilindrada") })
                        OutlinedTextField(
                            combustible.value,
                            { combustible.value = it },
                            label = { Text("Tipo de combustible") })
                        OutlinedTextField(
                            pasajeros.value,
                            { pasajeros.value = it },
                            label = { Text("Capacidad de pasajeros") })
                        OutlinedTextField(
                            totalCompra.value,
                            { totalCompra.value = it },
                            label = { Text("Total compra ($)") })
                        OutlinedTextField(
                            distancia.value,
                            { distancia.value = it },
                            label = { Text("Distancia (km)") })

                        //Se ejecuta el metodo al presionar el boton
                        Button(onClick = {
                            try {
                                val compra = totalCompra.value.toInt()
                                val dist = distancia.value.toInt()

                                val costo = calcularCostoDespacho(compra, dist)
                                val total = compra + costo

                                resultado.value =
                                    "Compra: $$compra\nDespacho: $$costo\nTotal: $$total"

                            } catch (e: Exception) {
                                resultado.value = "Error en datos"
                            }
                        }) {
                            Text("Calcular despacho")
                        }

                        Text(resultado.value)

                        Button(onClick = { pantalla = "menu" }) {
                            Text("Volver")
                        }
                    }
                }

                // Temperatura simulacion
                "temperatura" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Seguimos la misma formula que para la interfaz del Calculo Despacho
                        val temp = remember { mutableStateOf("") }
                        val resultado = remember { mutableStateOf("") }

                        OutlinedTextField(
                            value = temp.value,
                            onValueChange = { temp.value = it },
                            label = { Text("Temperatura (°C)") }
                        )

                        Button(onClick = {
                            try {
                                val temperatura = temp.value.toDouble()

                                if (temperatura > -10) {
                                    resultado.value = "Cadena de frio comprometida"
                                } else {
                                    resultado.value = "Temperatura correcta"
                                }

                                // En caso de que el programa se rompa con algun dato, lanzara el mensaje de error
                            } catch (e: Exception) {
                                resultado.value = "Error"
                            }
                        }) {
                            Text("Evaluar temperatura")
                        }

                        Text(resultado.value)

                        Button(onClick = { pantalla = "menu" }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }

    private fun obtenerYGuardarGPS() {

        //obtener servicio ubicacion
        val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager

        //verificando el permiso de ubicacion
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //solocita permiso de ubicacion
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        //obtiene la ultima ubicacion
        val location =
            locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)

        if (location != null) {

            //obtenemos la latitud y longitud del dispositivo
            val lat = location.latitude
            val lon = location.longitude

            //esto es para obtener el ID del usuario
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

            val database = com.google.firebase.database.FirebaseDatabase
                .getInstance()
                .getReference("usuarios")

            if (uid != null) {

                //se guarda la latitud y longitud en la firebase
                database.child(uid).child("latitud").setValue(lat)
                database.child(uid).child("longitud").setValue(lon)
            }

            //logcat
            Log.d("FIREBASE", "GPS guardado: $lat , $lon")

        } else {
            Log.e("GPS", "Ubicacion NULL (emulador sin ubicacion)")
        }
    }

    //metodo para el calculo del despacho
    //por compras sobre 50 mil pesos, dentro de un radio de 20 km. Para compras entre 25000 a 49999,
    // entonces se cobrara una tarifa de $150 pesos por kilometro recorrido.
    // Y si el total de compra es menor, se cobrará $300 pesos por kilometro recorrido
    private fun calcularCostoDespacho(totalCompra: Int, distanciaKm: Int): Int {

        return if (totalCompra >= 50000 && distanciaKm <= 20) {
            0
        } else if ((totalCompra in 25000..49999) ||
            (totalCompra >= 50000 && distanciaKm > 20)
        ) {
            150 * distanciaKm
        } else {
            300 * distanciaKm
        }
    }

    // Metodo radianes
    private fun convertirARadianes(grados: Double) {
        val radianes = grados * Math.PI / 180

        println("Grados: $grados -> Radianes: $radianes")
        Log.d("RADIANES", "Grados: $grados -> Radianes: $radianes")
    }

    // Metodo haversine
    private fun calcularDistancia(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {

        val R = 6371

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }


}