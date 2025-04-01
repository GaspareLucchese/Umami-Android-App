package com.example.giapan_beta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.giapan_beta.AdapterNotifiche.MyViewHolder
import com.example.giapan_beta.databinding.ItemBannerBinding
import com.example.giapan_beta.databinding.ItemListBinding

class AdapterBanner (val listaBanner: List<ItemBanner>) : RecyclerView.Adapter<AdapterBanner.BannerViewHolder>()
{
    class BannerViewHolder(val binding: ItemBannerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaBanner.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val temp = listaBanner[position]
        val binding = holder.binding

        binding.bannerImg.setImageResource(temp.bannerImage)
    }
}