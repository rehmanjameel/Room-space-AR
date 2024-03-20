package com.codebase.fragments.loginRegister

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codebase.ARoomApplication
import com.codebase.aroom.R
import com.codebase.aroom.databinding.FragmentUsereOptionBinding
import java.util.Locale


class UserOptionFragment : Fragment(R.layout.fragment_usere_option) {

    private lateinit var binding: FragmentUsereOptionBinding

    private val aRoomApplication = ARoomApplication()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUsereOptionBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val bundle = Bundle()

            adminButton.setOnClickListener {
                bundle.putString("role", "admin")
                Log.e("rolee", "admin")
                aRoomApplication.saveString("role", "admin")
//                ARoomApplication.isAdmin = true
//                ARoomApplication.isUser = false
//                openFiles()
                findNavController().navigate(R.id.action_userOptionFragment_to_accountOptionsFragment, bundle)
            }

            userButton.setOnClickListener {
                bundle.putString("role", "user")
                ARoomApplication.isUser = true
                ARoomApplication.isAdmin = false
                aRoomApplication.saveString("role", "user")

                findNavController().navigate(R.id.action_userOptionFragment_to_accountOptionsFragment, bundle)
            }
        }
    }

//    private val requestMultiplePermissions =
//        registerForActivityResult<Array<String>, Map<String, Boolean>>(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions: Map<String, Boolean> ->
//            for ((key, value) in permissions) {
//                Log.d("DEBUG", "$key = $value")
//            }
//            // Check if all required permissions are granted
//            var allPermissionsGranted = true
//            for (granted in permissions.values) {
//                if (!granted) {
//                    allPermissionsGranted = false
//                    showDialogOK(
//                        "Gallery Permission required for this app"
//                    ) { dialog: DialogInterface?, which: Int ->
//                        when (which) {
//                            DialogInterface.BUTTON_POSITIVE -> requestPermissions()
//                            DialogInterface.BUTTON_NEGATIVE -> {}
//                        }
//                    }
//                    break
//                }
//            }
//
//            // If all permissions are granted, open the gallery
//            if (allPermissionsGranted) {
//                openFiles()
//            }
//        }
//
//    private fun requestPermissions() {
//        // Launch the permission request
//        val permissionsToRequest: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            arrayOf(
//                Manifest.permission.READ_MEDIA_IMAGES
//            )
//        } else {
//            arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//        }
//        requestMultiplePermissions.launch(permissionsToRequest)
//    }
//
//    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
//        AlertDialog.Builder(requireActivity())
//            .setMessage(message)
//            .setPositiveButton("OK", okListener)
//            .setNegativeButton("Cancel", okListener)
//            .create()
//            .show()
//    }
}