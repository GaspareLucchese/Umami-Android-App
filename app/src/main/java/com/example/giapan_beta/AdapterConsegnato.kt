package com.example.giapan_beta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import java.sql.Date
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemConsegnatoBinding
import com.example.giapan_beta.databinding.ItemListBinding
import java.text.SimpleDateFormat
import java.util.Locale

//RecyclerView con ItemHomePage
class AdapterConsegnato(var lista: List<Ordine>,  private val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterConsegnato.MyViewHolder>()
{
    class MyViewHolder(val binding: ItemConsegnatoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val binding = ItemConsegnatoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterConsegnato.MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding


        val context = binding.image.context
        Glide.with(context)
            .load(temp.resource)
            .fitCenter()
            .into(binding.image)



        binding.nomeConsegnato.text = temp.name
        binding.dataArrivo.text = temp.dataConsegna?.let { formatDate(it) }
        binding.costo.text = "€ ${temp.price}"
        binding.indirizzoSpedizione.text = temp.indirizzo


        if(temp.valutazioneOrdine != null) {
            binding.valutaBtn.visibility = View.GONE
            binding.valutazioneUtente.rating = temp.valutazioneOrdine!!
            binding.valutazioneUtente.visibility = View.VISIBLE
            binding.valutaBtn.setOnClickListener(null)
        }
        else
        {
            binding.valutazioneUtente.visibility = View.GONE
            binding.valutaBtn.visibility = View.VISIBLE
            binding.valutaBtn.setOnClickListener {
                listener.valutaOnClick(temp)
            }
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

    interface OnItemClickListener {
        fun valutaOnClick(ordine : Ordine)
    }


}