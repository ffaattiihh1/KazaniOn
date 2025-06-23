package com.kazanion.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.databinding.ItemStoreProductBinding

class StoreProductAdapter(
    private val onBuyClick: (StoreProduct) -> Unit
) : RecyclerView.Adapter<StoreProductAdapter.ProductViewHolder>() {
    private var products: List<StoreProduct> = emptyList()

    fun submitList(list: List<StoreProduct>) {
        products = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemStoreProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

    inner class ProductViewHolder(private val binding: ItemStoreProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: StoreProduct) {
            binding.textProductName.text = product.name
            binding.textProductDesc.text = product.description
            binding.textProductPrice.text = "${product.price} Puan"
            binding.textProductStock.text = "Stok: ${product.stock}"
            binding.buttonBuy.setOnClickListener { onBuyClick(product) }
        }
    }
} 