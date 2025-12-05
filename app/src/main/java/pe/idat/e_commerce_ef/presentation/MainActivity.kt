package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityMainBinding
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        checkAuthentication()
    }

    private fun checkAuthentication() {
        val user = viewModel.getCurrentUser()
        if (user == null) {
            goToLogin()
        } else {
            setupUI()
        }
    }

    private fun setupUI() {
        binding.btnProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            goToLogin()
        }

        val user = viewModel.getCurrentUser()
        binding.tvUser.text = user?.email ?: "Usuario"
        binding.tvWelcome.text = "¡Hola, ${user?.displayName?.split(" ")?.firstOrNull() ?: "Usuario"}!"
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val user = viewModel.getCurrentUser()
        if (user == null && !isFinishing) {
            goToLogin()
        }
    }
}