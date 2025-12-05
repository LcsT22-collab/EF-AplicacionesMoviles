package pe.idat.e_commerce_ef.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pe.idat.e_commerce_ef.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupViews()
        loadUserData()
    }

    private fun setupViews() {
        // Botón para regresar
        binding.btnBackProfile.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser

        if (user != null) {
            // Mostrar email
            binding.tvProfileEmail.text = user.email ?: "No especificado"

            // Mostrar ID de usuario (UID)
            binding.tvProfileUid.text = user.uid

            // Mostrar nombre si está disponible
            binding.tvProfileName.text = user.displayName ?: "Usuario"

            // Mostrar contraseña en asteriscos (solo visual)
            // Nota: Firebase NO almacena contraseñas visibles
            binding.tvProfilePassword.text = "********"

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}