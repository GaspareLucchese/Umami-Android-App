package com.example.giapan_beta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemCarrelloBinding
import com.example.giapan_beta.databinding.ItemListBinding

//RecyclerView con ItemCarrello
class AdapterCarrello(private var lista: List<ItemProdotto>, private val viewModel : ViewModelOggetto) : RecyclerView.Adapter<AdapterCarrello.CarrelloViewHolder>() {


    class CarrelloViewHolder(val binding: ItemCarrelloBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<ItemProdotto>) {
        lista = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrelloViewHolder {
        val binding = ItemCarrelloBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarrelloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrelloViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding

        val context = binding.imageView.context
        Glide.with(context)
            .load(temp.resource)
            .fitCenter()
            .into(binding.imageView)

        binding.descrizione.text=temp.name
        binding.prezzo.text="€ ${temp.price}"

        binding.selezione.setOnCheckedChangeListener(null)
        binding.selezione.isChecked = viewModel.prodottiSelezionati.value?.contains(temp) ?: false
        binding.selezione.setOnCheckedChangeListener { _, isChecked ->
            viewModel.selezionaOggetto(temp, isChecked)
        }

        binding.eliminaCart.setOnClickListener {
            viewModel.rimuoviCarrello(temp)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}