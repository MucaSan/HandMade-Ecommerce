package com.example.handmadeecommerce

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.handmadeecommerce.controller.CartState
import com.example.handmadeecommerce.controller.CartViewModel
import com.example.handmadeecommerce.controller.CartViewModelFactory
import com.example.handmadeecommerce.databinding.ActivityShoppingCartBinding
import com.example.handmadeecommerce.utils.switchActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingCartBinding
    private lateinit var adapter: CartAdapter

    private val viewModel: CartViewModel by viewModels {
        val app = application as HandmadeEcommerceApp
        CartViewModelFactory(app.cartRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        setupObservers()

       binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogout -> {
                    (application as HandmadeEcommerceApp).auth.signOut()
                    switchActivity(this, LandingPageActivity::class.java)
                    finishAffinity()
                    true
                }
                else -> false
            }
        }

        binding.checkoutButton.setOnClickListener {
            showConfirmationDialog()
        }

        binding.continueShoppingLink.setOnClickListener {
            finish()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.setOnMenuItemClickListener {
             false
        }
    }

    private fun setupRecyclerView() {

        adapter = CartAdapter(
            onIncrease = { item -> viewModel.increaseQuantity(item) },
            onDecrease = { item -> viewModel.decreaseQuantity(item) }
        )

        binding.cartRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ShoppingCartActivity)
            adapter = this@ShoppingCartActivity.adapter

            setHasFixedSize(false)
        }
    }

    private fun setupObservers() {

        viewModel.cartItems.observe(this) { items ->



            adapter.submitList(items.toList())


            updateEmptyState(items.isNotEmpty())
        }


        viewModel.totalPrice.observe(this) { total ->
            val formattedPrice = "R$${"%.2f".format(total)}"
            binding.subtotalPriceTextview.text = formattedPrice
        }


        viewModel.cartState.observe(this) { state ->
            handleCartState(state)
        }
    }

    /**
     * Controla a visibilidade dos elementos se o carrinho estiver vazio.
     * Isso evita que o usuário tente comprar "nada".
     */
    private fun updateEmptyState(hasItems: Boolean) {
        binding.checkoutButton.isEnabled = hasItems


        binding.checkoutButton.alpha = if (hasItems) 1.0f else 0.5f


        binding.subtotalLayout.visibility = if (hasItems) View.VISIBLE else View.GONE

        if (!hasItems) {
            binding.subtotalPriceTextview.text = "R$0,00"

        }
    }

    private fun handleCartState(state: CartState) {
        when(state) {
            is CartState.Loading -> {
                binding.checkoutButton.isEnabled = false
                binding.checkoutButton.text = "Processando..."
                binding.checkoutButton.alpha = 0.7f
            }
            is CartState.CheckoutSuccess -> {
                binding.checkoutButton.isEnabled = true
                binding.checkoutButton.text = "Checkout"
                binding.checkoutButton.alpha = 1.0f

                MaterialAlertDialogBuilder(this)
                    .setTitle("Sucesso!")
                    .setMessage("Sua compra foi realizada com sucesso!\n\nID do Pedido:\n${state.orderId}")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()
            }
            is CartState.Error -> {
                binding.checkoutButton.isEnabled = true
                binding.checkoutButton.text = "Checkout"
                binding.checkoutButton.alpha = 1.0f

                MaterialAlertDialogBuilder(this)
                    .setTitle("Erro")
                    .setMessage(state.message)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
            is CartState.Idle -> {


                val hasItems = adapter.currentList.isNotEmpty()
                binding.checkoutButton.isEnabled = hasItems
                binding.checkoutButton.text = "Checkout"
                binding.checkoutButton.alpha = if (hasItems) 1.0f else 0.5f
            }
        }
    }

    private fun showConfirmationDialog() {
        val total = binding.subtotalPriceTextview.text

        MaterialAlertDialogBuilder(this)
            .setTitle("Finalizar Compra")
            .setMessage("Deseja confirmar o pedido no valor de $total?")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Confirmar") { dialog, _ ->
                viewModel.checkout()
                dialog.dismiss()
            }
            .show()
    }
}