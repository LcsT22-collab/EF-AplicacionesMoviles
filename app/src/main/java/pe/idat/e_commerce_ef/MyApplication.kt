package pe.idat.e_commerce_ef

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import pe.idat.e_commerce_ef.util.CartManager

class MyApplication : Application() {

    override fun onTerminate() {
        super.onTerminate()
        FirebaseAuth.getInstance().signOut()
        CartManager.clearCart()
    }
}