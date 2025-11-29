package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase
        auth = Firebase.auth

        // Enlazar vistas
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmailReg)
        etPassword = findViewById(R.id.etPasswordReg)
        btnRegister = findViewById(R.id.btnSubmit)

        // Configurar botón de registro
        btnRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (!validateInputs(name, email, password)) {
            return
        }

        btnRegister.isEnabled = false
        btnRegister.text = "Registrando..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                btnRegister.isEnabled = true
                btnRegister.text = "Registrarse"

                if (task.isSuccessful) {
                    updateUserProfile(name)
                } else {
                    val errorMessage = task.exception?.message ?: "Error en registro"
                    showToast(errorMessage)
                }
            }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            showToast("Ingresa tu nombre completo")
            return false
        }

        if (email.isEmpty()) {
            showToast("Ingresa tu email")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email inválido")
            return false
        }

        if (password.length < 6) {
            showToast("La contraseña debe tener mínimo 6 caracteres")
            return false
        }

        return true
    }

    private fun updateUserProfile(name: String) {
        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Registro exitoso")
                    goToMainActivity()
                } else {
                    showToast("Registro completado, pero error al guardar nombre")
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}