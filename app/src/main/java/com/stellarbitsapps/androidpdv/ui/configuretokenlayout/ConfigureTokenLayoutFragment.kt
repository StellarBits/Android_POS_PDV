package com.stellarbitsapps.androidpdv.ui.configuretokenlayout

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.coroutineScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.application.AndroidPdvApplication
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.databinding.FragmentConfigureTokenLayoutBinding
import kotlinx.coroutines.launch

// Storage Permissions
const val REQUEST_EXTERNAL_STORAGE = 1
val PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

const val PICK_IMAGE_REQUEST = 1

class ConfigureTokenLayoutFragment : Fragment() {

    companion object {
        fun newInstance() = ConfigureTokenLayoutFragment()
    }

    private val viewModel: ConfigureTokenLayoutViewModel by activityViewModels {
        ConfigureTokenLayoutViewModelFactory(
            (requireActivity().application as AndroidPdvApplication).database.layoutSettingsDao()
        )
    }

    private val binding: FragmentConfigureTokenLayoutBinding by lazy {
        FragmentConfigureTokenLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadTokenLayoutSettings()

        binding.btClose.setOnClickListener {
            findNavController().navigate(R.id.initialCashFragment)
        }

        binding.btSave.setOnClickListener {
            viewModel.updateConfigs(
                LayoutSettings(
                    header = binding.edtHeader.text.toString(),
                    footer = binding.edtFooter.text.toString(),
                    image = binding.edtImageUri.text.toString()
                )
            )
        }

        binding.btLoadImage.setOnClickListener {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            } else {
                openGallery()
            }
        }

        return binding.root
    }

    private fun loadTokenLayoutSettings() {
        viewModel.getConfigs()
        viewModel.layoutSettings.observe(viewLifecycleOwner) {
            binding.edtHeader.setText(it.header)
            binding.edtFooter.setText(it.footer)
            loadImage(it.image.toUri())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            loadImage(selectedImageUri)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun loadImage(imageUri: Uri?) {
        if (imageUri.toString().isNotEmpty()) {
            binding.edtImageUri.setText(imageUri.toString())

            val bitmap = BitmapFactory.decodeStream(
                requireActivity().contentResolver.openInputStream(imageUri!!)
            )
            binding.imgTokenImage.setImageBitmap(bitmap)
        }
    }
}