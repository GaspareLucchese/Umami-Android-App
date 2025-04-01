package com.example.giapan_beta

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giapan_beta.databinding.ItemListBinding

class AdapterLista(val lista: MutableList<ItemProdotto>, private val viewModel: ViewModelOggetto, private val email: String, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<AdapterLista.MyViewHolder>()
{
    private var onClickListener : OnClickListener? = null

    class MyViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        val temp = lista[position]
        val binding = holder.binding

        binding.nomeOggetto.text = temp.name
        binding.prezzo.text = "${temp.price}€"
        binding.ratingImage.rating = temp.starImage

        Glide.with(binding.imageView)
            .load(temp.resource)
            .fitCenter()
            .into(binding.imageView)

        binding.root.setOnClickListener {
            viewModel.prodottoSelezionato.value = temp
            viewModel.posizione.value = position
            val prodottoFragment = ProdottoFragment()

            val fragmentTransaction = fragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.container_homepage, prodottoFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

        binding.eliminaProdotto.setOnClickListener {
            Log.d("AdapterLista", "Rimozione cliccato per ${temp.name}")
            try {
                viewModel.rimuoviDallaListaDesideri(email, temp.idProdotto)
                lista.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, lista.size)
                Log.d("AdapterLista", "Prodotto rimosso dalla Wishlist: ${temp.name}")
            } catch (e: Exception) {
                Log.e("AdapterLista", "Errore durante la rimozione del prodotto", e)
            }
        }


        binding.aggiuntaButton.setOnClickListener {
            Log.v("HomeFragment", "Button clicked")
            val prodotto = temp
            if (prodotto != null) {
                val quantitaAttuale = viewModel.quantitaProdotti[prodotto.idProdotto] ?: 0
                if (quantitaAttuale < prodotto.nPezzi) {
                    viewModel.aggiungiAlCarrello(temp)
                    Log.d("AdapterLista", "Aggiunta al carrello cliccato per ${temp.name}, pezzi = ${temp.nPezzi}")
                    //Toast.makeText(context, "Prodotto aggiunto al Carrello", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("AdapterLista", "NON aggiunto al carrello cliccato per ${temp.name}, pezzi = ${temp.nPezzi}")
                    //Toast.makeText(context, "Quantità massima raggiunta per questo prodotto", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun getItemCount(): Int
    {
        return lista.size
    }

    fun updateList(newList: List<ItemProdotto>) {
        lista.clear()
        lista.addAll(newList)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener)
    {
        this.onClickListener = onClickListener
    }

    interface OnClickListener
    {
        fun onClick(position: Int, itemViewModel: ItemProdotto)
    }
}