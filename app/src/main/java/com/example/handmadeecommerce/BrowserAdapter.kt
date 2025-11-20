package com.example.handmadeecommerce

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.handmadeecommerce.databinding.ProductCardItemBinding
import com.example.handmadeecommerce.model.Product

class BrowseAdapter(
    private val onAddToCartClick: (Product) -> Unit
) : ListAdapter<Product, BrowseAdapter.BrowseViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseViewHolder {
        val binding = ProductCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BrowseViewHolder(binding, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: BrowseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BrowseViewHolder(
        private val binding: ProductCardItemBinding,
        private val onAddToCartClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.name
            binding.productPrice.text = "R$${"%.2f".format(product.price)}"
            binding.productDescription.text = product.description

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.hero)
                .into(binding.productImage)

            binding.btnAddToCart.setOnClickListener {
                onAddToCartClick(product)
            }
        }
    }
}