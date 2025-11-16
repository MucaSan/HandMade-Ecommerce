package com.example.handmadeecommerce

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityRegisterBinding
import com.example.handmadeecommerce.utils.switchActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val profiles = listOf("Cliente", "Artesão")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            profiles
        )

        binding.autoCompleteProfile.setAdapter(adapter)

        binding.btnLogin.setOnClickListener {
            switchActivity(this, LoginActivity::class.java)
        }

        binding.btnRegisterAccount.setOnClickListener {
            if (binding.autoCompleteProfile.text.toString() == "Cliente"){
                switchActivity(this, BrowseActivity::class.java)
            }
            else
                switchActivity(this, ArtisanProductsActivity::class.java)
        }
    }
}