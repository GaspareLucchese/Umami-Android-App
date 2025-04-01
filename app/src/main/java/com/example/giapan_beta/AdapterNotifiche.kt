package com.example.giapan_beta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.giapan_beta.AdapterLista.MyViewHolder
import com.example.giapan_beta.databinding.ItemListBinding
import com.example.giapan_beta.databinding.ItemNotificheBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterNotifiche(val lista: MutableList<ItemNotifica>) : RecyclerView.Adapter<AdapterNotifiche.MyViewHolder>()
{
    class MyViewHolder(val binding: ItemNotificheBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val binding = ItemNotificheBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding

        binding.messaggio.text = temp.messaggio
        binding.dataMessaggio.text = formatDate(temp.dataInvio)

        // Aggiungi log per vedere i dati passati all'adapter
        Log.d("AdapterNotifiche", "Notifica: $temp")

        if (temp.dataInvio != null) {
            binding.dataMessaggio.text = formatDate(temp.dataInvio)
        } else {
            binding.dataMessaggio.text = "Data non disponibile"
        }
    }

    private fun formatDate(date: Date): String {
        // Formattare la data nel modo desiderato
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    override fun getItemCount(): Int
    {
        return lista.size
    }

    fun updateData(newData: List<ItemNotifica>) {
        lista.clear()
        lista.addAll(newData)
        notifyDataSetChanged()
    }
}