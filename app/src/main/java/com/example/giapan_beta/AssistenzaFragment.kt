import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.giapan_beta.PagamentoFragment
import com.example.giapan_beta.R
import com.example.giapan_beta.SharedViewModel
import com.example.giapan_beta.databinding.FragmentAssistenzaBinding
import com.example.giapan_beta.databinding.FragmentModificaProfiloBinding

class AssistenzaFragment : Fragment() {

    private lateinit var binding: FragmentAssistenzaBinding
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssistenzaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "Email non trovata")

        binding.assistenzaTitolo.setText(Html.fromHtml("<u>ASSISTENZA CLIENTI</u>"))
        binding.assistenzaTxt.setText(Html.fromHtml("<u>SCRIVI IL MOTIVO DELLA RICHIESTA DI ASSISTENZA</u>"))


        binding.assistenzaButton.setOnClickListener {
            val messaggio = binding.assistenzaText.text.toString()
            if (messaggio.isNotEmpty() && email != null) {
                viewModel.inviaAssistenza(requireContext(), email, messaggio)

                val fragmentManager = parentFragmentManager
                val pagamentoFragment = PagamentoFragment()
                val fragmentTransaction = fragmentManager.beginTransaction()

                fragmentManager.popBackStack()
                fragmentTransaction.remove(pagamentoFragment)
                fragmentTransaction.commit()

            }
            else {
                Toast.makeText(requireContext(), "Inserisci un messaggio valido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
