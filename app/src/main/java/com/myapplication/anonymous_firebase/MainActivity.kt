package com.myapplication.anonymous_firebase

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.myapplication.anonymous_firebase.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var myRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        auth = Firebase.auth
        signInAnonymous()
    }

    private fun signInAnonymous() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (user != null) {
                        val database =
                            FirebaseDatabase.getInstance("https://anonymous-firebase-default-rtdb.firebaseio.com/")
                        myRef = database.reference
                        fetchFirebaseData()
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Firebase Exception", it.message.toString())
            }
    }
    private fun fetchFirebaseData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                try {
                    val value = dataSnapshot.value
                    value?.let {
                        val countryJson = Gson().toJson(value)
                        Log.d("Value", countryJson)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Exception Firebase Access", error.message)
            }
        })
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}