package com.kazanion.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.kazanion.databinding.FragmentStoreBinding
import com.kazanion.ui.store.StoreProductAdapter

class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private val userId get() = requireContext().getSharedPreferences("login_prefs", android.content.Context.MODE_PRIVATE)
        .getString("userId", null)
    private lateinit var adapter: StoreProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StoreProductAdapter(onBuyClick = { product ->
            if (userId == null) return@StoreProductAdapter
            purchaseProduct(userId!!, product)
        })
        binding.recyclerViewStore.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStore.adapter = adapter
        loadProducts()
    }

    private fun loadProducts() {
        // TODO: Backend'den ürünleri yükle
        Toast.makeText(requireContext(), "Mağaza özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
    }

    private fun purchaseProduct(userId: String, product: StoreProduct) {
        // TODO: Backend'e satın alma isteği gönder
        Toast.makeText(requireContext(), "Satın alma özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 