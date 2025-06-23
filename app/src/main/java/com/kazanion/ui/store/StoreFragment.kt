package com.kazanion.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kazanion.databinding.FragmentStoreBinding
import com.kazanion.ui.store.StoreProductAdapter

class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid
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
        db.collection("store_products").get().addOnSuccessListener { result ->
            val products = result.documents.mapNotNull { it.toObject(StoreProduct::class.java)?.copy(id = it.id) }
            adapter.submitList(products)
        }
    }

    private fun purchaseProduct(userId: String, product: StoreProduct) {
        val userRef = db.collection("users").document(userId)
        val productRef = db.collection("store_products").document(product.id)
        db.runTransaction { transaction ->
            val userSnap = transaction.get(userRef)
            val productSnap = transaction.get(productRef)
            val userPoints = userSnap.getLong("points") ?: 0
            val stock = productSnap.getLong("stock") ?: 0
            if (userPoints < product.price) throw Exception("Yetersiz puan!")
            if (stock <= 0) throw Exception("Stokta ürün yok!")
            transaction.update(userRef, "points", userPoints - product.price)
            transaction.update(productRef, "stock", stock - 1)
        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "Satın alma başarılı!", Toast.LENGTH_SHORT).show()
            loadProducts()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), e.message ?: "Satın alma başarısız", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 