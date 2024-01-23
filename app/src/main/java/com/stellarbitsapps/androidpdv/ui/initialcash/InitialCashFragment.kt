package com.stellarbitsapps.androidpdv.ui.initialcash

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.sublcdlibrary.SubLcdHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.databinding.FragmentInitialCashBinding
import com.stellarbitsapps.androidpdv.util.Utils
import java.util.Calendar


class InitialCashFragment : Fragment(), SubLcdHelper.VuleCalBack {

    companion object {
        fun newInstance() = InitialCashFragment()
    }

    private val viewModel: InitialCashViewModel by activityViewModels {
        InitialCashViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao(),
            (requireActivity().application as AndroidPdvApplication).database.layoutSettingsDao(),
        )
    }

    private val binding: FragmentInitialCashBinding by lazy {
        FragmentInitialCashBinding.inflate(layoutInflater)
    }

    private lateinit var subLcdHelper: SubLcdHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        loadTokenLayoutSettings()

        subLcdHelper = SubLcdHelper.getInstance()
        subLcdHelper.init(requireContext())
        subLcdHelper.SetCalBack { s: String?, cmd: Int ->
            datatrigger(s, cmd)
        }

        binding.button.setOnClickListener {

            val initialCash = if (binding.edtInitialCash.text.toString().isEmpty()) 0f else {
                binding.edtInitialCash.text.toString()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim()
                    .toFloat()
            }

            viewModel.addReport(
                Report(
                    initialCash = initialCash,
                    initialDate = Calendar.getInstance().timeInMillis
                )
            )

            findNavController().navigate(R.id.tokensFragment)
        }

        binding.edtInitialCash.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Handler().postDelayed({
                    binding.edtInitialCash.setSelection(binding.edtInitialCash.length())
                }, 1)
            }
        }

        binding.edtInitialCash.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Utils.formatCashTextMask(s, binding.edtInitialCash, this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.config_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.register_tokens -> {
                findNavController().navigate(R.id.registerTokenFragment)
                true
            }

            R.id.configure_tokens_layout -> {
                findNavController().navigate(R.id.configureTokenLayoutFragment)
                true
            }

            R.id.reprint_report -> {
                findNavController().navigate(R.id.reprintFragment)
                true
            }

            R.id.about_us -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun datatrigger(p0: String?, p1: Int) {
        TODO("Not yet implemented")
    }

    private fun loadTokenLayoutSettings() {
        viewModel.getConfigs()
        viewModel.layoutSettings.observe(viewLifecycleOwner) {
            Utils.showInSubDisplay(subLcdHelper, this, it)
        }
    }

    private fun showAboutDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogLayout: View =
            inflater.inflate(
                R.layout.about_us_dialog_layout,
                requireActivity().findViewById(R.id.token_layout) as ViewGroup?
            )

        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setView(dialogLayout)
        alertDialogBuilder.show()
    }
}