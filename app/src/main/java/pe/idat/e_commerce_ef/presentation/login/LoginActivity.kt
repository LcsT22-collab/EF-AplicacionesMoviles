package pe.idat.e_commerce_ef.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import pe.idat.e_commerce_ef.presentation.main.MainActivity
import pe.idat.e_commerce_ef.presentation.register.RegisterActivity
import pe.idat.e_commerce_ef.databinding.ActivityLoginBinding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this, Observer { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Ingresando..."
                }
                is LoginState.Success -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Iniciar Sesión"
                    goToMainActivity()
                }
                is LoginState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Iniciar Sesión"
                    showToast(state.message)
                }
                else -> {}
            }
        })
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvGoRegister.setOnClickListener {
            goToRegisterActivity()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (!validateInputs(email, password)) {
            return
        }

        viewModel.login(email, password)
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Completa email y contraseña")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email inválido")
            return false
        }

        return true
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
}