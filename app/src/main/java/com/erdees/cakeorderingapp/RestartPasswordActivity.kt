package com.erdees.cakeorderingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RestartPasswordActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restart_password_activity)

        val goBackBtn = findViewById<TextView>(R.id.mail_sign_in_text)
        val emailField = findViewById<EditText>(R.id.reset_mail_edittext)
        val resetBtn = findViewById<Button>(R.id.mail_reset_password_button)
        auth = Firebase.auth

        goBackBtn.setOnClickListener {
            this.finish() // activity gets closed and focus comes back on signin activity
        }

        resetBtn.setOnClickListener {
            val emailAddress = emailField.text.toString()
            if (emailAddress.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                auth.sendPasswordResetEmail(emailAddress).addOnSuccessListener {
                    Toast.makeText(this,"Restart password mail sent!",Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }
            else Toast.makeText(this,"doesn't match or empty",Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        const val TAG = "RestartPasswordActivity"
    }

}