package com.example.giapan_beta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.AdapterLista.OnClickListener
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemHomePageBinding
import com.example.giapan_beta.databinding.ItemListBinding

//RecyclerView con ItemHomePage
class AdapterHomePage(var lista: List<ItemProdotto>) : RecyclerView.Adapter<AdapterHomePage.MyViewHolder>()
{
    private var onClickListener : AdapterHomePage.OnClickListener? = null

    class MyViewHolder(val binding: ItemHomePageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val binding = ItemHomePageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding

        val context = binding.imageView.context
        Glide.with(context)
            .load(temp.resource)
            .fitCenter()
            .into(binding.imageView)
        binding.descrizione.text = temp.name
        binding.prezzo.text = "€ ${temp.price}"
        binding.valutazioneProdotto.rating = temp.starImage

        binding.root.setOnClickListener{
            if (onClickListener != null)
                onClickListener!!.onClick(position, temp)
        }
    }

    override fun getItemCount(): Int
    {
        return lista.size
    }

    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener
    {
        fun onClick(position: Int, itemViewModel: ItemProdotto)
    }

    fun submitList(newList: List<ItemProdotto>) {
        lista = newList
        notifyDataSetChanged()
    }
}