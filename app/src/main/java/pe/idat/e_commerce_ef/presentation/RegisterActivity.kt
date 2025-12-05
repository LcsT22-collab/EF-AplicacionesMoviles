package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import pe.idat.e_commerce_ef.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmailReg)
        etPassword = findViewById(R.id.etPasswordReg)
        btnRegister = findViewById(R.id.btnSubmit)

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
                    Toast.makeText(this, task.exception?.message ?: "Error en registro", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre completo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    Toast.makeText(this, "Error al guardar nombre", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}
