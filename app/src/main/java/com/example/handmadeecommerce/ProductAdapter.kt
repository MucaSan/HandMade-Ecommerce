package com.example.handmadeecommerce

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // <-- IMPORTAR O GLIDE
import com.example.handmadeecommerce.databinding.ArtisanProductItemBinding
import com.example.handmadeecommerce.model.Product

class ProductAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ArtisanProductItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ArtisanProductItemBinding,
        private val onEditClick: (Product) -> Unit,
        private val onDeleteClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name
            // Formata o preço para R$ 0,00
            binding.productValue.text = "R$${"%.2f".format(product.price)}"
            binding.productDescription.text = product.description

            // 👇 CARREGAR IMAGEM COM GLIDE
            Glide.with(binding.root.context)
                .load(product.imageUrl) // Carrega a URL do Firestore
                .placeholder(R.drawable.hero) // Placeholder enquanto carrega
                .error(R.drawable.hero) // Imagem de erro se falhar (ex: drawable/hero)
                .into(binding.productImage) // Onde a imagem será exibida

            binding.btnEdit.setOnClickListener {
                onEditClick(product)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }
}

// ... (Seu ProductDiffCallback existente, se não o tiver, adicione-o)
class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}