package com.example.lab10
import android.os.Bundle
import androidx.activity.ComponentActivity

enum class ProviderType {
    BASIC
}
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}