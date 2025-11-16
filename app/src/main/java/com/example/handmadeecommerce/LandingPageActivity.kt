package com.example.handmadeecommerce

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityLandingPageBinding
import com.example.handmadeecommerce.utils.switchActivity

class LandingPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLandingPageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnClient.setOnClickListener {
            switchActivity(this, RegisterActivity::class.java)
        }

        binding.btnArtisan.setOnClickListener {
            switchActivity(this, RegisterActivity::class.java)
        }

        binding.btnLogin.setOnClickListener{
            switchActivity(this, LoginActivity::class.java)
        }
    }
}