package com.example.giapan_beta

import AssistenzaFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.giapan_beta.databinding.FragmentImpostazioniBinding

class ImpostazioniFragment : Fragment()
{
    private lateinit var binding : FragmentImpostazioniBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImpostazioniBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.impostazioniText.setText(Html.fromHtml("<u>IMPOSTAZIONI</u>"))

        view.findViewById<Button>(R.id.logout_button).setOnClickListener {
            // Rimuovere lo stato di accesso

            val sharedPref = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean("isLoggedIn", false)
                putString("email", null)
                putString("profilo", null)
                apply()
            }

           // Reindirizzare alla schermata di login
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }

        val fragmentManager = parentFragmentManager

        binding.modificaProfiloButton.setOnClickListener()
        {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val modificaProfiloFragment = ModificaProfiloFragment()

            fragmentTransaction.replace(R.id.container_homepage, modificaProfiloFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

        binding.PagamentiTransazioniButton.setOnClickListener()
        {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val transazioniFragment = TransazioniFragment()

            fragmentTransaction.replace(R.id.container_homepage, transazioniFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

        binding.AssistenzaClientiButton.setOnClickListener()
        {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val assistenzaFragment = AssistenzaFragment()

            fragmentTransaction.replace(R.id.container_homepage, assistenzaFragment)
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.commit()
        }

    }
}