package com.stellarbitsapps.androidpdv.ui.registertokens

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentRegisterTokenBinding
import com.stellarbitsapps.androidpdv.util.Utils

class RegisterTokenFragment : Fragment() {
    companion object {
        fun newInstance() = RegisterTokenFragment()
    }

    private val viewModel: RegisterTokenViewModel by activityViewModels {
        RegisterTokenViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao()
        )
    }

    private val binding: FragmentRegisterTokenBinding by lazy {
        FragmentRegisterTokenBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cashEditTextList = listOf(
            binding.edtCashOne,
            binding.edtCashTwo,
            binding.edtCashFour,
            binding.edtCashFive,
            binding.edtCashSix,
            binding.edtCashEight,
            binding.edtCashTen
        )

        binding.edtCashValue.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    binding.edtCashValue.setSelection(binding.edtCashValue.length())
                }, 1)
            }
        }

        binding.edtCashValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, binding.edtCashValue, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        cashEditTextList.forEach { cashEditText ->
            cashEditText.setOnClickListener {
                incCashQty(cashEditText)
            }

            cashEditText.setOnLongClickListener {
                cashEditText.setText("0")
                true
            }
        }

        binding.btClose.setOnClickListener {
            Navigation.findNavController(requireActivity(), this.id).popBackStack()
        }

        binding.btClear.setOnClickListener {
            clearAllFields(cashEditTextList)
        }

        binding.btDeleteAllTokens.setOnClickListener {
            viewModel.deleteTokens()
            Toast.makeText(requireContext(), "Fichas deletadas com sucesso!", Toast.LENGTH_SHORT)
                .show()
        }

        binding.btRegisterToken.setOnClickListener {
            val value = binding.edtCashValue.text.toString()
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toFloat()

            val token = Tokens(
                value = value,
                cashOne = cashEditTextList[0].text.toString().toInt(),
                cashTwo = cashEditTextList[1].text.toString().toInt(),
                cashFour = cashEditTextList[2].text.toString().toInt(),
                cashFive = cashEditTextList[3].text.toString().toInt(),
                cashSix = cashEditTextList[4].text.toString().toInt(),
                cashEight = cashEditTextList[5].text.toString().toInt(),
                cashTen = cashEditTextList[6].text.toString().toInt()
            )

            viewModel.setToken(token)
            Toast.makeText(requireContext(), "Ficha cadastrada com sucesso!", Toast.LENGTH_SHORT)
                .show()

            clearAllFields(cashEditTextList)
        }

        return binding.root
    }

    private fun clearAllFields(cashEditTextList: List<EditText>) {
        cashEditTextList.forEach {
            it.setText("0")
        }
        binding.edtCashValue.setText("")
    }

    @SuppressLint("SetTextI18n")
    private fun incCashQty(textView: TextView) {
        val n = textView.text.toString()
        textView.text = (n.toInt() + 1).toString()
    }
}