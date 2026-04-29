package com.example.semana6

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


//esta es la pestaña del menu
class MenuActivity : ComponentActivity() {
//metodo de inicio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    //aca se llama al metodo
        obtenerYGuardarGPS()

        setContent {
            androidx.compose.material3.Text("Bienvenido al menu")
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
        val location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)

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
}