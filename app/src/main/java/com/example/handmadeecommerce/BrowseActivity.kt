package com.example.handmadeecommerce

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityBrowseBinding
import com.example.handmadeecommerce.utils.switchActivity

class BrowseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrowseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBrowseBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuCart -> {
                    switchActivity(this, ShoppingCartActivity::class.java)
                    true
                }

                R.id.menuLogout -> {
                    switchActivity(this, LoginActivity::class.java)
                    true
                }

                else -> false
            }
        }
    }
}