package com.example.handmadeecommerce

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts // <-- IMPORTAR
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide // <-- IMPORTAR GLIDE
import com.example.handmadeecommerce.controller.ProductFormState
import com.example.handmadeecommerce.controller.ProductViewModel
import com.example.handmadeecommerce.controller.ProductViewModelFactory
import com.example.handmadeecommerce.databinding.ActivityProductFormBinding
import com.example.handmadeecommerce.model.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductFormBinding
    private var selectedImageUri: Uri? = null // <-- Armazena o Uri da imagem

    private val viewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as HandmadeEcommerceApp).productRepository)
    }

    // 👇 Registra o "Image Picker"
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Salva o Uri
            // Mostra o preview da imagem
            Glide.with(this)
                .load(it)
                .into(binding.productImagePreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // ... (seu código edgeToEdge e ViewCompat) ...
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.formTitle.text = "Adicionar Produto"
        binding.btnAdd.text = "Adicionar Produto"

        // Listener para o novo botão "Selecionar Imagem"
        binding.btnSelectImage.setOnClickListener {
            imagePicker.launch("image/*") // Abre a galeria
        }

        binding.btnAdd.setOnClickListener {
            validateAndAddProduct()
        }

        observeFormState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetFormState() // Reseta o estado ao voltar para a tela
    }

    private fun validateAndAddProduct() {
        val name = binding.txtProductName.text.toString()
        val priceStr = binding.txtProductPrice.text.toString()
        val description = binding.txtProductDescription.text.toString()

        if (name.isBlank() || priceStr.isBlank() || description.isBlank()) {
            showErrorDialog("Nome, Preço e Descrição são obrigatórios.")
            return
        }

        if (selectedImageUri == null) {
            showErrorDialog("Por favor, selecione uma imagem.")
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            binding.productValueLayout.error = "Insira um preço válido"
            return
        }

        binding.productValueLayout.error = null

        val product = Product(
            name = name,
            price = price,
            description = description
            // imageUrl será preenchida pelo ViewModel
        )

        // Passa o produto E o Uri da imagem
        viewModel.addProduct(product, selectedImageUri)
    }

    private fun observeFormState() {
        viewModel.productFormState.observe(this) { state ->
            when (state) {
                is ProductFormState.Loading -> {
                    binding.btnAdd.isEnabled = false
                    binding.btnAdd.text = "Adicionando..."
                }
                is ProductFormState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    finish() // Volta para a tela anterior
                }
                is ProductFormState.Error -> {
                    binding.btnAdd.isEnabled = true
                    binding.btnAdd.text = "Adicionar Produto"
                    showErrorDialog(state.message)
                }
                is ProductFormState.Idle -> {
                    binding.btnAdd.isEnabled = true
                    binding.btnAdd.text = "Adicionar Produto"
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Erro")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}