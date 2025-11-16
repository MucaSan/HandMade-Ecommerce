package com.example.handmadeecommerce

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityProductFormBinding

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Definir o título e o texto do botão para "Edit product"
        binding.formTitle.text = "Edit product"
        binding.actionButton.text = "Save Changes"

        // **1. Receber dados do produto a ser editado (se houver)**
        // A ArtisanProductsActivity enviaria estes dados
        productId = intent.getStringExtra("PRODUCT_ID")
        val currentName = intent.getStringExtra("PRODUCT_NAME")
        val currentValue = intent.getStringExtra("PRODUCT_VALUE")
        val currentDescription = intent.getStringExtra("PRODUCT_DESCRIPTION")
        val currentImage = intent.getStringExtra("PRODUCT_IMAGE")

        // Preencher os campos com os dados existentes
        binding.productNameEdittext.setText(currentName)
        binding.productValueEdittext.setText(currentValue)
        binding.productDescriptionEdittext.setText(currentDescription)
        binding.productImageEdittext.setText(currentImage)

        binding.actionButton.setOnClickListener {
            val updatedName = binding.productNameEdittext.text.toString()
            val updatedValue = binding.productValueEdittext.text.toString()
            val updatedDescription = binding.productDescriptionEdittext.text.toString()
            val updatedImage = binding.productImageEdittext.text.toString()

            if (updatedName.isBlank() || updatedValue.isBlank() || updatedDescription.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Aqui você implementaria a lógica para ATUALIZAR o produto no Firebase
                // Usaria o 'productId' para saber qual produto atualizar
                Toast.makeText(this, "Produto atualizado: $updatedName (ID: $productId)", Toast.LENGTH_SHORT).show()
                finish() // Volta para a tela anterior após salvar
            }
        }
    }
}