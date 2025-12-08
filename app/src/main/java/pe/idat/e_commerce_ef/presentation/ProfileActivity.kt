package pe.idat.e_commerce_ef.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pe.idat.e_commerce_ef.R
import pe.idat.e_commerce_ef.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        loadUserData()
    }

    private fun setupViews() {
        binding.btnBackProfile.setOnClickListener { onBackPressed() }
    }

    private fun loadUserData() {
        val user = auth.currentUser

        if (user != null) {
            binding.tvProfileEmail.text = user.email ?: getString(R.string.not_specified)
            binding.tvProfileUid.text = user.uid
            binding.tvProfileName.text = user.displayName ?: getString(R.string.default_user)
            binding.tvProfilePassword.text = getString(R.string.password_display)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}