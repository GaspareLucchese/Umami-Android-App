package com.example.giapan_beta

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.databinding.FragmentRegistrazioneBinding

class RegistrazioneFragment : Fragment() {

    private lateinit var binding: FragmentRegistrazioneBinding
    companion object {
        fun newInstance() = RegistrazioneFragment()
    }

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrazioneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.registrazione.setText(Html.fromHtml("<u>REGISTRATI</u>"))

        binding.avantiBtn.setOnClickListener {
            val nome = binding.nomeInserito.text.toString()
            val cognome = binding.cognomeInserito.text.toString()
            val email = binding.emailInserita.text.toString()
            val password = binding.passwordInserita.text.toString()
            val confermaPassword = binding.confermaPasswordInserita.text.toString()
            val indirizzo = binding.indirizzoInserito.text.toString()

            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty() || confermaPassword.isEmpty() || indirizzo.isEmpty())
            {
                Toast.makeText(context, "Compilare tutti i campi", Toast.LENGTH_SHORT).show()
            }
            else if(!nome.contains(Regex("^[A-Za-z ]+\$")))
            {
                Toast.makeText(context, "Compilare correttamente il campo nome", Toast.LENGTH_SHORT).show()
                binding.nomeInserito.requestFocus()
            }
            else if(!cognome.contains(Regex("^[A-Za-z ]+\$")))
            {
                Toast.makeText(context, "Compilare correttamente il campo cognome", Toast.LENGTH_SHORT).show()
                binding.cognomeInserito.requestFocus()
            }
            else if((email.length < 8) || (!email.contains(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))))
            {
                Toast.makeText(context, "Inserire un'email valida!", Toast.LENGTH_SHORT).show()
                binding.emailInserita.requestFocus()
            }
            else if ((password.length < 8))
            {
                Toast.makeText(context, "Password troppo corta, inserire almeno 8 caratteri", Toast.LENGTH_SHORT).show()
                binding.passwordInserita.requestFocus()
            }
            else if (!password.contains(Regex(".*[A-Z].*")) || !password.contains(Regex(".*[0-9].*")))
            {
                Toast.makeText(context, "La Password deve contenere almeno una lettera maiuscola e un numero", Toast.LENGTH_SHORT).show()
                binding.passwordInserita.requestFocus()
            }
            else if (password != confermaPassword)
            {
                Toast.makeText(context, "Le due password non corrispondono", Toast.LENGTH_SHORT).show()
                binding.confermaPasswordInserita.requestFocus()
            }
            else
            {
                // Salva i dati nel ViewModel
                viewModel.datiRegistrazione.value = DatiRegistrazione(
                    nome = nome,
                    cognome = cognome,
                    email = email,
                    password = password,
                    indirizzo = indirizzo
                )

                // Carica il secondo Fragment
                val registraCartaFragment = RegistraCartaFragment()
                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()

                fragmentTransaction.replace(R.id.container_login, registraCartaFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }
}