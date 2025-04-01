package com.example.giapan_beta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemListBinding
import com.example.giapan_beta.databinding.ItemPagamentoBinding

//RecyclerView con ItemCarrello
class AdapterPagamento(private var lista: List<ItemProdotto>) : RecyclerView.Adapter<AdapterPagamento.PagamentoViewHolder>() {

    class PagamentoViewHolder(val binding: ItemPagamentoBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<ItemProdotto>) {
        lista = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagamentoViewHolder {
        val binding = ItemPagamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagamentoViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding

        val context = binding.imagePagamento.context
        Glide.with(context)
            .load(temp.resource)
            .fitCenter()
            .into(binding.imagePagamento)
        binding.nomePagamento.text=temp.name
        binding.costo.text="€ ${temp.price}"
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}