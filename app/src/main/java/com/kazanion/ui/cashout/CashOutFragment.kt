package com.kazanion.ui.cashout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kazanion.databinding.FragmentCashOutBinding
import com.kazanion.network.ApiService
import com.kazanion.network.UserBalance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CashOutFragment : Fragment() {

    private var _binding: FragmentCashOutBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = ApiService.create()
        loadUserBalance()

        binding.buttonConfirmCashOut.setOnClickListener {
            val amountString = binding.editTextAmount.text.toString()
            if (amountString.isNotEmpty()) {
                val amount = amountString.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    Toast.makeText(context, "${amount} TL nakit çekme talebi alındı.", Toast.LENGTH_SHORT).show()
                    // Burada nakit çekme işlemini gerçekleştirecek backend veya başka bir mantık eklenebilir.
                    // Örneğin, bir API çağrısı yapılabilir.
                } else {
                    Toast.makeText(context, "Geçerli bir tutar girin.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Lütfen çekmek istediğiniz tutarı girin.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserBalance() {
        // Yeni kullanıcı için "yeni_kullanici" kullanıyoruz
        lifecycleScope.launch {
            try {
                val userBalance = apiService.getUserBalance("yeni_kullanici")
                binding.textViewCurrentBalance.text = "%.2f TL".format(userBalance.balance)
                binding.textViewAvailablePoints.text = "${userBalance.points} Puan"
            } catch (e: Exception) {
                Log.e("CashOutFragment", "Bakiye yükleme hatası", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 