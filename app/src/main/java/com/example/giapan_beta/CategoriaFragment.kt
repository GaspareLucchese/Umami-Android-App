package com.example.giapan_beta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.giapan_beta.databinding.FragmentCategoriaBinding

class CategoriaFragment : Fragment()
{
    private lateinit var binding: FragmentCategoriaBinding

    private val viewModel : ViewModelOggetto by activityViewModels()

    private lateinit var listaCategorieSecondarie : Map<String, List<String>>
    private lateinit var listaCategoriePrimarie : List<String>
    private lateinit var adapterListaCategorie : AdapterListaCategorie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaCategorie : ExpandableListView = binding.listacategorie
        listaCategorieSecondarie = Categoria.data
        listaCategoriePrimarie = ArrayList(listaCategorieSecondarie.keys)

        val listaIconeCategorie = mapOf("Piatti" to R.drawable.category_plate, "Ciotole" to R.drawable.category_bowl, "Posate" to R.drawable.category_cutlery, "Vassoi e Bento" to R.drawable.category_tray,
            "Tè" to R.drawable.category_tea, "Sake" to R.drawable.category_sake, "Coltelli" to R.drawable.category_knife, "Pentole e Padelle" to R.drawable.category_pot, "Utensili" to R.drawable.category_tool,
            "Decorazioni Interne" to R.drawable.category_decoration, "Ingredienti" to R.drawable.category_ingredient)

        adapterListaCategorie = AdapterListaCategorie(requireContext(), listaCategoriePrimarie, listaCategorieSecondarie, listaIconeCategorie)
        listaCategorie.setAdapter(adapterListaCategorie)

        listaCategorie.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val categoriaSelezionata = listaCategorieSecondarie[listaCategoriePrimarie[groupPosition]]?.get(childPosition)
            categoriaSelezionata?.let {
                viewModel.categoria = it
                viewModel.tipoLista = "categoria"
                Toast.makeText(context, viewModel.categoria, Toast.LENGTH_SHORT).show()
                val risultatoFragment = RisultatoFragment()

                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                fragmentTransaction.replace(R.id.container_homepage, risultatoFragment)
                fragmentTransaction.addToBackStack(null)

                fragmentTransaction.commit()
            }
            true
        }
    }


}