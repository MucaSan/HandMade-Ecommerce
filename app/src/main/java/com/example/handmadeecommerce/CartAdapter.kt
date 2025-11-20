package com.example.handmadeecommerce

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.handmadeecommerce.databinding.CartItemBinding
import com.example.handmadeecommerce.model.CartItem

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, onIncrease, onDecrease)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        private val binding: CartItemBinding,
        private val onIncrease: (CartItem) -> Unit,
        private val onDecrease: (CartItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.productName.text = item.product.name
            binding.productSubtitle.text = "Qtd: ${item.quantity}"


            binding.textQuantity.text = item.quantity.toString()

            val totalItemPrice = item.product.price * item.quantity
            binding.itemTotalPrice.text = "R$${"%.2f".format(totalItemPrice)}"

            Glide.with(binding.root.context)
                .load(item.product.imageUrl)
                .placeholder(R.drawable.hero)
                .into(binding.productImage)

            binding.btnIncrease.setOnClickListener { onIncrease(item) }
            binding.btnDecrease.setOnClickListener { onDecrease(item) }
        }
    }
}




class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {

        return oldItem.product.productId == newItem.product.productId
    }
    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {


        return oldItem == newItem
    }
}