package com.stellarbitsapps.androidpdv.ui.tokens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.databinding.FragmentTokensBinding
import com.stellarbitsapps.androidpdv.ui.MainActivity
import com.stellarbitsapps.androidpdv.ui.adapter.TokensAdapter
import com.stellarbitsapps.androidpdv.ui.custom.dialog.ProgressHUD
import com.stellarbitsapps.androidpdv.util.Utils


class TokensFragment : Fragment() {

    companion object {
        fun newInstance() = TokensFragment()
    }

    private val viewModel: TokensViewModel by activityViewModels {
        TokensViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.tokensDao(),
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.layoutSettingsDao(),
            (requireActivity().application as AndroidPdvApplication).database.sangriaDao(),
            (requireActivity().application as AndroidPdvApplication).database.reportErrorDao()
        )
    }

    private val binding: FragmentTokensBinding by lazy {
        FragmentTokensBinding.inflate(layoutInflater)
    }

    private lateinit var progressHUD: ProgressHUD

    private lateinit var tokensAdapter: TokensAdapter

    private lateinit var recyclerView: RecyclerView

    private var tokenSum = 0f

    private var previousTokenSum = 0f

    private var selectedTokensList = arrayListOf<Tokens>()

    private var previousSelectedTokensList = arrayListOf<Tokens>()

    private var previousTokenValues = arrayOf<Float>()

    private var tokenSettings = LayoutSettings()

    private var isPrinting = false

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initRecyclerView()

        loadTokenLayoutSettings()

        binding.btClear.setOnClickListener {
            clearFields()
            clearPreviousFields()
        }

        binding.btClose.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Atenção!")
                .setMessage("Você realmente deseja sair para fechar o caixa?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sim") { _, _ ->
                    findNavController().navigate(R.id.finalCashFragment)
                }
                .setNegativeButton("Não", null).show()
        }

        binding.btError.setOnClickListener {
            if (previousTokenSum > 0 && previousSelectedTokensList.isNotEmpty()) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Atenção!")
                    .setMessage("Deseja realmente reportar um erro de R$ ${String.format("%.2f", previousTokenSum)} na impressão\n" +
                            "e reimprimir as fichas?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Sim") { _, _ ->
                        tokenPayment(viewModel, tokenSettings, previousTokenValues, previousSelectedTokensList, this)
                        viewModel.insertError(previousTokenSum, MainActivity.currentReportId)
                    }
                    .setNegativeButton("Não", null).show()
            } else {
                Toast.makeText(requireContext(), "Nenhuma impressão realizada anteriormente", Toast.LENGTH_LONG).show()
            }
        }

        binding.btCash.setOnClickListener {
            if (!isPrinting) {
                isPrinting = true

                clearPreviousFields()
                previousTokenSum = tokenSum
                selectedTokensList.forEach { previousSelectedTokensList.add(it) }

                // Cash, Pix, Debit, Credit in this order
                val tokenValues = arrayOf(tokenSum, 0f, 0f, 0f)
                previousTokenValues = tokenValues
                tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
            }
        }

        binding.btPix.setOnClickListener {
            if (!isPrinting) {
                isPrinting = true

                clearPreviousFields()
                previousTokenSum = tokenSum
                selectedTokensList.forEach { previousSelectedTokensList.add(it) }

                // Cash, Pix, Debit, Credit in this order
                val tokenValues = arrayOf(0f, tokenSum, 0f, 0f)
                previousTokenValues = tokenValues
                tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
            }
        }

        binding.btDebit.setOnClickListener {
            if (!isPrinting) {
                isPrinting = true

                clearPreviousFields()
                previousTokenSum = tokenSum
                selectedTokensList.forEach { previousSelectedTokensList.add(it) }

                // Cash, Pix, Debit, Credit in this order
                val tokenValues = arrayOf(0f, 0f, tokenSum, 0f)
                previousTokenValues = tokenValues
                tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
            }
        }

        binding.btCredit.setOnClickListener {
            if (!isPrinting) {
                isPrinting = true

                clearPreviousFields()
                previousTokenSum = tokenSum
                selectedTokensList.forEach { previousSelectedTokensList.add(it) }

                // Cash, Pix, Debit, Credit in this order
                val tokenValues = arrayOf(0f, 0f, 0f, tokenSum)
                previousTokenValues = tokenValues
                tokenPayment(viewModel, tokenSettings, tokenValues, selectedTokensList, this)
            }
        }

        binding.btChange.setOnClickListener {
            Utils.showCashDialog(this, viewModel, false, if (tokenSum == 0f) previousTokenSum else tokenSum)
        }

        binding.btSangria.setOnClickListener {
            Utils.showCashDialog(this, viewModel, true, tokenSum)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {
        tokensAdapter = TokensAdapter(this)

        recyclerView = binding.rvCashButtons

        recyclerView.adapter = tokensAdapter

        viewModel.getTokens()
        viewModel.tokensList.observe(viewLifecycleOwner) {
            tokensAdapter.submitList(it)
        }
    }

    private fun loadTokenLayoutSettings() {
        viewModel.getConfigs()
        viewModel.layoutSettings.observe(viewLifecycleOwner) {
            tokenSettings = it
        }
    }

    private fun tokenPayment(
        viewModel: TokensViewModel,
        tokenSettings: LayoutSettings,
        tokenValues: Array<Float>,
        selectedTokensList: ArrayList<Tokens>,
        fragment: TokensFragment
    ) {
        val paymentMethod =
            if (tokenValues[0] > 0) "Dinheiro"
            else if (tokenValues[1] > 0) "Pix"
            else if (tokenValues[2] > 0) "Débito"
            else if (tokenValues[3] > 0) "Crédito"
            else "Forma de pagamento inválida"

        if (tokenSettings.header.isEmpty() || tokenSettings.footer.isEmpty() || tokenSettings.image.isEmpty()) {
            val builder = AlertDialog.Builder(fragment.requireContext())

            builder.setTitle("Atenção!")
            builder.setMessage("O layout das fichas não foi configurado corretamente." +
                    "\nDeseja imprimir mesmo assim?\n\nEm caso negativo, a operação será cancelada e o caixa reiniciado.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("Sim") { dialog, _ ->
                dialog.dismiss()

                object : Thread() {
                    @RequiresApi(Build.VERSION_CODES.N_MR1)
                    override fun run() {
                        try {
                            Utils.prepareAndPrintToken(
                                viewModel,
                                tokenSettings,
                                paymentMethod,
                                tokenValues,
                                selectedTokensList,
                                fragment
                            )

                            // Update UI
                            requireActivity().runOnUiThread {
                                clearFields()
                                progressHUD.dismiss()
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }.start()

                progressHUD = ProgressHUD.show(
                    context, "Imprimindo fichas",
                    cancelable = false,
                    spinnerGone = false
                )
                progressHUD.show()
            }

            builder.setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
                viewModel.deleteReport()
                fragment.findNavController().navigate(R.id.configureTokenLayoutFragment)
            }

            val alertDialog = builder.create()
            alertDialog.show()
        } else {
            object : Thread() {
                @RequiresApi(Build.VERSION_CODES.N_MR1)
                override fun run() {
                    try {
                        Utils.prepareAndPrintToken(
                            viewModel,
                            tokenSettings,
                            paymentMethod,
                            tokenValues,
                            selectedTokensList,
                            fragment
                        )

                        // Update UI
                        requireActivity().runOnUiThread {
                            clearFields()
                            progressHUD.dismiss()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }.start()

            progressHUD = ProgressHUD.show(
                context, "Imprimindo fichas",
                cancelable = false,
                spinnerGone = false
            )
            progressHUD.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearFields() {
        isPrinting = false
        binding.tvTotaValue.text = "R$ 0,00"
        tokenSum = 0f
        selectedTokensList.clear()

        tokensAdapter.notifyDataSetChanged()
    }

    private fun clearPreviousFields() {
        previousSelectedTokensList.clear()
        previousTokenSum = 0f
        previousTokenValues = arrayOf()
    }

    @SuppressLint("SetTextI18n")
    fun tokenClicked(token: Tokens) {
        selectedTokensList.add(token)
        tokenSum += token.value
        previousTokenSum = tokenSum
        binding.tvTotaValue.text = "R$ " + String.format("%.2f", tokenSum)
    }

    @SuppressLint("SetTextI18n")
    fun tokenCounterClicked(token: Tokens) {
        selectedTokensList.remove(token)
        tokenSum -= token.value
        previousTokenSum = tokenSum
        binding.tvTotaValue.text = "R$ " + String.format("%.2f", tokenSum)
    }
}