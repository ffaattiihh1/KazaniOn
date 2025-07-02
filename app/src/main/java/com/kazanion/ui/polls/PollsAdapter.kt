package com.kazanion.ui.polls

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.kazanion.databinding.ListItemPollBinding
import com.kazanion.model.Poll
import com.kazanion.network.ApiService
import com.kazanion.network.CompletePollRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PollsAdapter(private var polls: List<Poll>) : RecyclerView.Adapter<PollsAdapter.PollViewHolder>() {

    private val apiService = ApiService.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollViewHolder {
        val binding = ListItemPollBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PollViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PollViewHolder, position: Int) {
        holder.bind(polls[position])
    }

    override fun getItemCount(): Int = polls.size

    fun updatePolls(newPolls: List<Poll>) {
        // DiffUtil kullanmak yerine basit kontrol - daha hızlı
        if (this.polls != newPolls) {
            this.polls = newPolls
            notifyDataSetChanged()
        }
    }

    inner class PollViewHolder(private val binding: ListItemPollBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(poll: Poll) {
            binding.textViewTitle.text = poll.title
            binding.textViewDescription.text = poll.description
            binding.textViewPoints.text = "${poll.points} Puan"
            
            // Ankete Katıl butonuna tıklama dinleyicisi
            binding.buttonJoinPoll.setOnClickListener {
                showPollCompletionDialog(poll)
            }
        }

        private fun showPollCompletionDialog(poll: Poll) {
            val dialog = AlertDialog.Builder(itemView.context)
                .setTitle("Anket Tamamlama")
                .setMessage("Anketi tamamladınız mı? Tamamladıysanız puanlarınızı kazanabilirsiniz.")
                .setPositiveButton("Evet, Tamamladım") { _, _ ->
                    completePoll(poll)
                }
                .setNegativeButton("Hayır, Henüz Değil") { _, _ ->
                    // Anket linkini aç
                    poll.link?.let { link ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        itemView.context.startActivity(intent)
                    }
                }
                .setNeutralButton("İptal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            
            dialog.show()
        }

        private fun completePoll(poll: Poll) {
            val username = "yeni_kullanici"
            
            // Memory leak'i önlemek için IO scope kullan
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = apiService.getUserByUsername(username)
                    val request = CompletePollRequest(user.id)
                    val response = apiService.completePoll(poll.id.toLong(), request)
                    
                    // UI güncellemelerini Main thread'de yap
                    withContext(Dispatchers.Main) {
                        if (response.success) {
                            Toast.makeText(itemView.context, "Anket tamamlandı! ${response.pointsEarned} puan kazandınız.", Toast.LENGTH_SHORT).show()
                            checkAchievements(user.id)
                        } else {
                            Toast.makeText(itemView.context, "Anket tamamlanamadı: ${response.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(itemView.context, "Ağ hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun checkAchievements(userId: Long) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val achievements = apiService.checkAchievements(userId)
                    if (achievements.isNotEmpty()) {
                        val achievementMessages = achievements.values.joinToString("\n")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(itemView.context, "Yeni başarımlar kazandınız!\n$achievementMessages", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    // Başarımlar kontrol edilemedi, sessizce geç
                }
            }
        }
    }
} 