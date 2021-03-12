package com.erdees.cakeorderingapp

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

class SignUpActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mail_signup_activity)
        auth = Firebase.auth

        val signInButton = findViewById<TextView>(R.id.mail_sign_in_text)
        val createUserButton = findViewById<Button>(R.id.mail_create_account_button)

        signInButton.setOnClickListener {
            this.finish()
        }

        createUserButton.setOnClickListener {
            val mail = findViewById<EditText>(R.id.edittext_mail)
            val password = findViewById<EditText>(R.id.edittext_password)
            val password2 = findViewById<EditText>(R.id.edittext_password2)
            if (password.text.toString() != password2.text.toString()) {
                Log.i(TAG,password.text.toString() + password2.text.toString())
                Toast.makeText(this, "Passwords are not same.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(mail.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(MailLoginActivity.TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(this, "Loged in", Toast.LENGTH_SHORT).show()
                        this.finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(MailLoginActivity.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        password.text.clear()
                    }
                }
        }
    }


    companion object {
        const val TAG = "SignUpActivity"
    }
}