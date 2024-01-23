package com.stellarbitsapps.androidpdv.ui.startscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.databinding.FragmentStartScreenBinding
import com.stellarbitsapps.androidpdv.ui.MainActivity
import com.stellarbitsapps.androidpdv.ui.custom.dialog.ProgressHUD

class StartScreenFragment : Fragment() {

    companion object {
        fun newInstance() = StartScreenFragment()
    }

    private val viewModel: StartScreenViewModel by activityViewModels {
        StartScreenViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.reportDao()
        )
    }

    private val binding: FragmentStartScreenBinding by lazy {
        FragmentStartScreenBinding.inflate(layoutInflater)
    }

    private lateinit var progressHUD: ProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        progressHUD = ProgressHUD.show(
            context, "Por favor aguarde.",
            cancelable = false,
            spinnerGone = false
        )

        progressHUD.show()

        viewModel.getLastReportId()
        viewModel.lastReportId.observe(viewLifecycleOwner) {
            MainActivity.currentReportId = it + 1
        }

        viewModel.cashRegisterIsOpen()
        viewModel.cashRegisterIsOpen.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.tokensFragment)
                progressHUD.dismiss()
            } else {
                findNavController().navigate(R.id.initialCashFragment)
                progressHUD.dismiss()
            }
        }

        return binding.root
    }

}