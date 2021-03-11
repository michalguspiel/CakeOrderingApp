package com.erdees.cakeorderingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MailLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, " TEST ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_login_activity)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val signUpText = findViewById<TextView>(R.id.mail_sign_up_text)
        val signInBtn = findViewById<Button>(R.id.mail_create_account_button)

        signUpText.setOnClickListener {
            val signUpActivity = Intent(this, SignUpActivity::class.java)
            startActivity(signUpActivity)
        }

        signInBtn.setOnClickListener {
            val mail = findViewById<EditText>(R.id.edittext_mail)
            val password = findViewById<EditText>(R.id.edittext_password)

            auth.signInWithEmailAndPassword(mail.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        this.finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        // ...
                    }

                    // ...
                }

        }

    }

    companion object {
        const val TAG = "MailLoginActivity"
    }
}