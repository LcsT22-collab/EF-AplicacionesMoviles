package pe.idat.e_commerce_ef

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnGoToStore: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        auth = Firebase.auth

        // Enlazar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvEmail = findViewById(R.id.tvEmail)
        btnLogout = findViewById(R.id.btnLogout)
        btnGoToStore = findViewById(R.id.btnGoToStore) // ✅ MOVIDO AQUÍ

        // Verificar autenticación
        checkAuthentication()

        // Configurar botones
        setupButtons() // ✅ NUEVO MÉTODO
    }

    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            goToLoginActivity()
        } else {
            showUserInfo(currentUser)
        }
    }

    private fun showUserInfo(user: com.google.firebase.auth.FirebaseUser) {
        val welcomeText = if (user.displayName?.isNotEmpty() == true) {
            "¡Bienvenido, ${user.displayName}!"
        } else {
            "¡Bienvenido!"
        }

        tvWelcome.text = welcomeText
        tvEmail.text = "Email: ${user.email}"
    }

    private fun setupButtons() {
        btnLogout.setOnClickListener {
            auth.signOut()
            showToast("Sesión cerrada")
            goToLoginActivity()
        }

        btnGoToStore.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // Minimiza la app en lugar de cerrarla
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}