package pe.idat.e_commerce_ef.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import pe.idat.e_commerce_ef.data.AppRepository
import pe.idat.e_commerce_ef.databinding.ActivityLoginBinding
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModel
import pe.idat.e_commerce_ef.presentation.viewmodel.AppViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AppRepository(applicationContext)
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (validateInputs(email, password)) {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.text = "Iniciando..."

                viewModel.login(email, password) { result ->
                    runOnUiThread {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = "Iniciar Sesión"

                        if (result.isSuccess) {
                            goToMainActivity()
                        } else {
                            Toast.makeText(
                                this,
                                result.exceptionOrNull()?.message ?: "Error de inicio de sesión",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}