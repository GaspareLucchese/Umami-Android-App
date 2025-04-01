package com.example.giapan_beta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.databinding.FragmentModificaProfiloBinding

class ModificaProfiloFragment : Fragment() {

    private lateinit var binding: FragmentModificaProfiloBinding
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModificaProfiloBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.modificaProfiloTitle.setText(Html.fromHtml("<u>MODIFICA I TUOI DATI</u>"))

        sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        if (email != null) {
            Log.d("ModificaProfiloFragment", "Email trovata: $email")
            viewModel.getUserData(email)
        } else {
            Toast.makeText(requireContext(), "Email non trovata", Toast.LENGTH_SHORT).show()
        }

        viewModel.datiRegistrazione.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                binding.nomeText.setText(userData.nome)
                binding.cognomeText.setText(userData.cognome)
                binding.indirizzoText.setText(userData.indirizzo)
                binding.passwordText.setText(userData.password)
                binding.confermaPasswordText.setText(userData.password)
                Log.d("ModificaProfiloFragment", "UserData non è null")
            } else {
                Log.d("ModificaProfiloFragment", "UserData è null")
            }
        }


        binding.modificaBtn.setOnClickListener {
            val nome = binding.nomeText.text.toString()
            val cognome = binding.cognomeText.text.toString()
            val indirizzo = binding.indirizzoText.text.toString()
            val password = binding.passwordText.text.toString()
            val confermaPassword = binding.confermaPasswordText.text.toString()

            if (nome.isEmpty() || cognome.isEmpty() || password.isEmpty() || confermaPassword.isEmpty() || indirizzo.isEmpty())
            {
                Toast.makeText(context, "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
            }
            else if(!nome.contains(Regex("^[A-Za-z ]+\$")))
            {
                Toast.makeText(context, "Compilare correttamente il campo nome", Toast.LENGTH_SHORT).show()
                binding.nomeText.requestFocus()
            }
            else if(!cognome.contains(Regex("^[A-Za-z ]+\$")))
            {
                Toast.makeText(context, "Compilare correttamente il campo cognome", Toast.LENGTH_SHORT).show()
                binding.cognomeText.requestFocus()
            }
            else if ((password.length < 8))
            {
                Toast.makeText(context, "Password troppo corta, inserire almeno 8 caratteri", Toast.LENGTH_SHORT).show()
                binding.passwordText.requestFocus()
            }
            else if (!password.contains(Regex(".*[A-Z].*")) || !password.contains(Regex(".*[0-9].*")))
            {
                Toast.makeText(context, "La Password deve contenere almeno una lettera maiuscola e un numero", Toast.LENGTH_SHORT).show()
                binding.passwordText.requestFocus()
            }
            else if (password != confermaPassword)
            {
                Toast.makeText(context, "Le due password non corrispondono", Toast.LENGTH_SHORT).show()
                binding.confermaPasswordText.requestFocus()
            }
            else{
                Log.d("ModificaProfiloFragment", "Invio richiesta di aggiornamento per email: $email")
                val email = sharedPref.getString("email", null)
                if (email != null) {
                    val profilo = "${nome} ${cognome}"
                    with(sharedPref.edit()) {
                        putString("profilo", profilo)
                        commit()
                    }
                    viewModel.updateUserData(email, nome, cognome, indirizzo, password)
                    val fragmentManager = parentFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    fragmentManager.popBackStack()
                    fragmentTransaction.commit()
                } else {
                    Toast.makeText(requireContext(), "Email non trovata", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
