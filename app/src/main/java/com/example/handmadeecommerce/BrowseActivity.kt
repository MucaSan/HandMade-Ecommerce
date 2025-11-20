package com.example.handmadeecommerce

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.handmadeecommerce.controller.BrowseViewModel
import com.example.handmadeecommerce.controller.BrowseViewModelFactory
import com.example.handmadeecommerce.databinding.ActivityBrowseBinding
import com.example.handmadeecommerce.utils.switchActivity

class BrowseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrowseBinding
    private lateinit var adapter: BrowseAdapter

    private val viewModel: BrowseViewModel by viewModels {
        val app = application as HandmadeEcommerceApp
        BrowseViewModelFactory(app.productRepository, app.cartRepository)
    }

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

        setupRecyclerView()
        setupObservers()
        setupMenu()
    }

    private fun setupRecyclerView() {
        adapter = BrowseAdapter { product ->
            viewModel.addToCart(product)
        }
        binding.productsRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerview.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { list ->
            adapter.submitList(list)
        }
        viewModel.message.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupMenu() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuCart -> {
                    switchActivity(this, ShoppingCartActivity::class.java)
                    true
                }
                R.id.menuLogout -> {
                    (application as HandmadeEcommerceApp).auth.signOut()
                    switchActivity(this, LandingPageActivity::class.java)
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
    }
}