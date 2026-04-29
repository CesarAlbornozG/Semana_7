package com.example.semana6

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.semana6.ui.theme.Semana6Theme
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue


//clase principal que se conecta con Firebase
class MainActivity : ComponentActivity() {

    //metodo inicio
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Read from the database, para leer datos en tiempo real
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // Para cambios en tiempo real en Firebase.

                //Para obtener el valor en Firebase
                val value = snapshot.getValue<String>()
                //para mostrar valor en logcat
                Log.d(TAG, "Value is: " + value)
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

                setContent {
                    Semana6Theme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                            ) {

                                var email = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
                                var pass = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
                                //outlined textfield campo de texto para el correo
                                androidx.compose.material3.OutlinedTextField(
                                    value = email.value,
                                    onValueChange = { email.value = it },
                                    label = { Text("Correo") }
                                )
                                //campo de entrada para la contrasena
                                androidx.compose.material3.OutlinedTextField(
                                    value = pass.value,
                                    onValueChange = { pass.value = it },
                                    label = { Text("Contraseña") }
                                )
                                //boton login
                                androidx.compose.material3.Button(onClick = {
                                    //inicio de sesion firebase
                                    auth.signInWithEmailAndPassword(email.value, pass.value)
                                        .addOnCompleteListener { task ->

                                            if (task.isSuccessful) {
                                                // ir al menu
                                                startActivity(
                                                    android.content.Intent(
                                                        this@MainActivity,
                                                        MenuActivity::class.java
                                                    )
                                                )
                                            } else {
                                                Log.e("LOGIN", "Error", task.exception)
                                            }
                                        }

                                }) {
                                    Text("Iniciar sesion")
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })


        enableEdgeToEdge()

    }
}
