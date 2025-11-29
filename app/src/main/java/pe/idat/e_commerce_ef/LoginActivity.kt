package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoRegister: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        auth = Firebase.auth

        // Enlazar vistas
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoRegister = findViewById(R.id.tvGoRegister)

        // Verificar si ya está logueado
        if (auth.currentUser != null) {
            goToMainActivity()
            return
        }

        // Configurar botón de login
        btnLogin.setOnClickListener {
            performLogin()
        }

        // Configurar texto para ir a registro
        tvGoRegister.setOnClickListener {
            goToRegisterActivity()
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Completa email y contraseña")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email inválido")
            return
        }

        btnLogin.isEnabled = false
        btnLogin.text = "Ingresando..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                btnLogin.text = "Iniciar Sesión"

                if (task.isSuccessful) {
                    showToast("Login exitoso")
                    goToMainActivity()
                } else {
                    val errorMessage = task.exception?.message ?: "Error en autenticación"
                    showToast(errorMessage)
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            goToMainActivity()
        }
    }
}