package pe.idat.e_commerce_ef.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pe.idat.e_commerce_ef.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupViews()
        loadUserData()
    }

    private fun setupViews() {
        binding.btnBackProfile.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser

        if (user != null) {
            binding.tvProfileEmail.text = user.email ?: "No especificado"
            binding.tvProfileUid.text = user.uid
            binding.tvProfileName.text = user.displayName ?: "Usuario"
            binding.tvProfilePassword.text = "********"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}