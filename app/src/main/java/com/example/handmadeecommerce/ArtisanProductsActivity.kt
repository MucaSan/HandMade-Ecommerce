package com.example.handmadeecommerce

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityArtisanProductsBinding
import com.example.handmadeecommerce.utils.switchActivity

class ArtisanProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtisanProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityArtisanProductsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogout -> {
                    switchActivity(this, LandingPageActivity::class.java)
                    true
                }
                else -> false
            }
        }

        binding.btnAddProduct.setOnClickListener {
            switchActivity(this, AddProductActivity::class.java)
        }
    }
}