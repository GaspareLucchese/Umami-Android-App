package com.example.giapan_beta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterLista.MyViewHolder
import com.example.giapan_beta.AdapterLista.OnClickListener
import com.example.giapan_beta.databinding.ItemInConsegnaBinding
import com.example.giapan_beta.databinding.ItemListBinding

//RecyclerView con ItemHomePage
class AdapterNonConsegnato(val lista: List<Ordine>) : RecyclerView.Adapter<AdapterNonConsegnato.MyViewHolder>()
{
    class MyViewHolder(val binding: ItemInConsegnaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val binding = ItemInConsegnaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder : MyViewHolder, position: Int)
    {
        val binding = holder.binding

        val temp = lista[position]
        val context = binding.image.context
        Glide.with(context)
            .load(temp.resource)
            .fitCenter()
            .into(binding.image)

        Log.d("AdapterConsegnato", "Ordine ID: ${temp.name}")
        binding.nomeConsegna.text = temp.name
        binding.codiceSpedizione.text = temp.codiceSpedizione
        binding.costo.text = "€ ${temp.price}"
        binding.indirizzoSpedizione.text = temp.indirizzo
    }

    override fun getItemCount(): Int
    {
        return lista.size
    }


}