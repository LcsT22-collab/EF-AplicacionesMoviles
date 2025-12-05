package pe.idat.e_commerce_ef

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import pe.idat.e_commerce_ef.util.CartManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        performLogout()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_COMPLETE) {
            performLogout()
        }
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()
        CartManager.clearCart()
    }
}