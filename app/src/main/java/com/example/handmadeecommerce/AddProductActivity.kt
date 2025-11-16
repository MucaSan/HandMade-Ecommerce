package com.example.handmadeecommerce

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.handmadeecommerce.databinding.ActivityProductFormBinding

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding

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

        binding.formTitle.text = "Add product"
        binding.actionButton.text = "Add Product"

        binding.actionButton.setOnClickListener {
            val productName = binding.productNameEdittext.text.toString()
            val productValue = binding.productValueEdittext.text.toString()
            val productDescription = binding.productDescriptionEdittext.text.toString()
            val productImage = binding.productImageEdittext.text.toString()

            if (productName.isBlank() || productValue.isBlank() || productDescription.isBlank()) {
                Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Produto adicionado: $productName", Toast.LENGTH_SHORT).show()
                finish() // Volta para a tela anterior após adicionar
            }
        }
    }
}