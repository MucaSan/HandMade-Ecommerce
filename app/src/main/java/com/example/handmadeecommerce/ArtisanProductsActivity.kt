package com.example.handmadeecommerce

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.handmadeecommerce.controller.ProductListState
import com.example.handmadeecommerce.controller.ProductViewModel
import com.example.handmadeecommerce.controller.ProductViewModelFactory
import com.example.handmadeecommerce.databinding.ActivityArtisanProductsBinding
import com.example.handmadeecommerce.model.Product
import com.example.handmadeecommerce.utils.switchActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ArtisanProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArtisanProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var auth: FirebaseAuth

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as HandmadeEcommerceApp).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        auth = (application as HandmadeEcommerceApp).auth
        if (auth.currentUser == null) {
            switchActivity(this, LoginActivity::class.java)
            finish()
            return
        }


        binding = ActivityArtisanProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchArtisanProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onEditClick = { product ->

                val intent = Intent(this, EditProductActivity::class.java).apply {
                    putExtra("PRODUCT_TO_EDIT", product)
                }
                startActivity(intent)
            },
            onDeleteClick = { product ->

                showDeleteConfirmationDialog(product)
            }
        )

        binding.productsRecyclerview.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@ArtisanProductsActivity)
        }
    }

    private fun setupListeners() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.menuLogout -> {
                    auth.signOut()
                    switchActivity(this, LandingPageActivity::class.java)
                    finishAffinity()
                    true
                }
                else -> false
            }
        }


        binding.btnAddProduct.setOnClickListener {
            switchActivity(this, AddProductActivity::class.java)
        }

        binding.txtSearchField.addTextChangedListener { query ->
            viewModel.searchProducts(query.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.productListState.observe(this) { state ->
            when (state) {
                is ProductListState.Loading -> {
                    binding.productsRecyclerview.visibility = View.GONE
                }
                is ProductListState.Success -> {
                    binding.productsRecyclerview.visibility = View.VISIBLE
                    productAdapter.submitList(state.products)
                }
                is ProductListState.Error -> {
                    binding.productsRecyclerview.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja deletar o produto '${product.name}'?")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Deletar") { dialog, _ ->
                viewModel.deleteProduct(product.productId)
                dialog.dismiss()
            }
            .show()
    }
}