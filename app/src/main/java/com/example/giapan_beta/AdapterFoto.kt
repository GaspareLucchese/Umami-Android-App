package com.example.giapan_beta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemImmagineBinding
import com.example.giapan_beta.databinding.ItemListBinding

class AdapterFoto(private var lista: List<ItemFoto>, private val viewModel : ViewModelOggetto): RecyclerView.Adapter<AdapterFoto.FotoViewHolder>() {

    class FotoViewHolder(val binding: ItemImmagineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {
        val binding = ItemImmagineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int)
    {
        val temp = lista[position]

        val binding = holder.binding
        val context = binding.imageView.context
        Glide.with(context)
            .load(temp.foto)
            .fitCenter()
            .into(binding.imageView)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun updateData(newLista: List<ItemFoto>) {
        lista = newLista
        notifyDataSetChanged()
    }
}