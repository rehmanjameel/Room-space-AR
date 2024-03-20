package com.codebase.fragments.shopping

import android.Manifest
import android.R.attr.data
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.codebase.adapters.ColorAdapter
import com.codebase.aroom.R
import com.codebase.aroom.databinding.FragmentAddProductBinding
import com.nicos.imagepickerandroid.image_picker.ImagePicker
import com.nicos.imagepickerandroid.image_picker.ImagePickerInterface
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import java.util.Locale


class AddProductFragment : Fragment(R.layout.fragment_add_product), ImagePickerInterface {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var selectedColors: List<Int>
    private var interestsTopicsList: ArrayList<String> = ArrayList()
    private var langList: ArrayList<Int> = ArrayList()
    private lateinit var selectedLanguage: BooleanArray

    private var imagePicker: ImagePicker? = null

    var mArrayUri: ArrayList<Uri>? = null
    var position = 0
    var imagesEncodedList: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFurnitureSize()
        initImagePicker()

        binding.colorsMenu.setEndIconOnClickListener {
            dialogOfColors()
        }

        binding.addProductsButton.setOnClickListener {
            validations()
        }

        binding.modelImagesTIL.setEndIconOnClickListener {
            imagePicker?.pickMultipleImagesFromGallery()
//            pickImage()
        }

        binding.modelTIL.setEndIconOnClickListener {
            openFiles()
        }

    }

    private fun validations() {

        val productName = binding.productNameTIET.text.toString()
        val selectCategory = binding.categoryDropDown.text.toString()
        val model = binding.modelTIET.text.toString()
        val images = binding.modelImagesTIET.text.toString()
        val description = binding.descriptionTIET.text.toString()
        val price = binding.priceTIET.text.toString()
        val offerPercentage = binding.offerPercentageTIET.text.toString()
        val size = binding.sizeDropDown.text.toString()
        val color = binding.colorsDropDown.text.toString()

        if (productName.isEmpty() && (selectCategory.isEmpty() || selectCategory == "Select Category")
            && model.isEmpty() && images.isEmpty() && description.isEmpty() && price.isEmpty() && offerPercentage.isEmpty()
            && size.isEmpty() && color.isEmpty()) {

            binding.productNameTIET.error = "Field required"
            binding.categoryDropDown.error = "Field required"
            binding.modelTIET.error = "Field required"
            binding.modelImagesTIET.error = "Field required"
            binding.descriptionTIET.error = "Field required"
            binding.priceTIET.error = "Field required"
            binding.offerPercentageTIET.error = "Field required"
            binding.sizeDropDown.error = "Field required"
            binding.colorsDropDown.error = "Field required"

        } else if (productName.isEmpty()) {
            binding.productNameTIET.error = "Field required"

        } else if ((selectCategory.isEmpty() || selectCategory == "Select Category")) {
            binding.categoryDropDown.error = "Field required"

        }else if (model.isEmpty()) {
            binding.modelTIET.error = "Field required"

        }else if (images.isEmpty()) {
            binding.modelImagesTIET.error = "Field required"

        }else if (description.isEmpty()) {
            binding.descriptionTIET.error = "Field required"

        }else if (price.isEmpty()) {
            binding.priceTIET.error = "Field required"

        }else if (offerPercentage.isEmpty()) {
            binding.offerPercentageTIET.error = "Field required"

        }else if (size.isEmpty()) {
            binding.sizeDropDown.error = "Field required"

        }else if (color.isEmpty()) {
            binding.colorsDropDown.error = "Field required"

        } else {

        }

    }

    private fun setUpFurnitureSize() {

        // add interest in array list to show dropdown
        interestsTopicsList.add("S")
        interestsTopicsList.add("M")
        interestsTopicsList.add("L")
        interestsTopicsList.add("XL")

        // initialize selected language array
        selectedLanguage = BooleanArray(interestsTopicsList.size)
        binding.sizeMenu.setEndIconOnClickListener {
            alertDialog()
        }
    }

    private fun alertDialog() {
        // Initialize alert dialog
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireActivity())

        // set title
        builder.setTitle("Select Size")

        // set dialog non cancelable
        builder.setCancelable(false)

        builder.setMultiChoiceItems(
            interestsTopicsList.toTypedArray<CharSequence>(),
            selectedLanguage,
            OnMultiChoiceClickListener { dialogInterface, i, b ->
                // check condition
                if (b) {
                    // when checkbox selected
                    // Add position in lang list
                    langList.add(i)
                    // Sort array list
                    langList.sort()
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    langList.removeAt(i)
                }
            })
        builder.setPositiveButton(
            "OK"
        ) { dialogInterface, i -> // Initialize string builder
            val stringBuilder = StringBuilder()
            // use for loop
            for (j in langList.indices) {
                // concat array value
                stringBuilder.append(interestsTopicsList[langList[j]])
                // check condition
                if (j != langList.size - 1) {
                    // When j value  not equal
                    // to lang list size - 1
                    // add comma
                    stringBuilder.append(", ")
                }
            }
            // set text on textView
            binding.sizeDropDown.setText(stringBuilder.toString())
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> // dismiss dialog
            dialogInterface.dismiss()
        }
//        builder.setNeutralButton(
//            "Clear All"
//        ) { dialogInterface, i ->
//            // use for loop
//            for (j in selectedLanguage.indices) {
//                // remove all selection
//                selectedLanguage.get(j) = false
//                // clear language list
//                langList.clear()
//                // clear text view value
//                binding.interestDropDown.setText("")
//            }
//        }
        // show dialog
        builder.show()
    }


    private fun dialogOfColors() {
        val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_color_picker, null)
        val listView = dialogView.findViewById<ListView>(R.id.listView)

        val colors = arrayOf(
            Pair("Red", -65536),
            Pair("Green", -16711936),
            Pair("Blue", -16776961),
            Pair("Brown", -6724096),
            Pair("Black", -16777216),
            Pair("White", -1),
            Pair("Gray", -7829368),
            Pair("Beige", -657931),
            Pair("Navy", -16777088),
            Pair("Teal", -16738680),
            Pair("Orange", -23296)
        )

        val adapter = ColorAdapter(requireActivity(), colors)
        listView.adapter = adapter

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(dialogView)
            .setTitle("Select Colors")
            .setPositiveButton("OK") { dialog, _ ->
                // Handle selected colors
                selectedColors = adapter.getSelectedColorCodes()

                binding.colorsDropDown.setText(selectedColors.toString())
                // Do something with selected colors
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()

    }

    // image picker launcher
    private var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    if (intent.clipData != null) {
                        val mClipData: ClipData = intent.clipData!!
                        val count: Int = mClipData.itemCount
                        for (i in 0 until count) {
                            val imageUri: Uri = mClipData.getItemAt(i).uri
                            Log.e("image uri", imageUri.toString())
                            mArrayUri?.add(imageUri)
                        }
                        binding.modelImagesTIET.setText(mArrayUri.toString())
                        position = 0
                    } else if (intent.data != null) {
                        val imageUri: Uri = intent.data!!
                        Log.e("image uri12", imageUri.toString())

                        mArrayUri?.add(imageUri)
                        Log.e("image uri123", imageUri.toString())

                        binding.modelImagesTIET.setText(mArrayUri.toString())
                        position = 0
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "No image selected", Toast.LENGTH_LONG).show()
            }
        }

    fun initImagePicker() {
        //Builder
        //Note: fragmentActivity or fragment are mandatory one of them
        imagePicker = ImagePicker(
            fragment = this, // fragment instance - private
            coroutineScope = lifecycleScope, // mandatory - coroutine scope from activity or fragment - private
            scaleBitmapModelForSingleImage = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            scaleBitmapModelForMultipleImages = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            scaleBitmapModelForCameraImage = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            enabledBase64ValueForSingleImage = true, // optional, by default is false - private
            enabledBase64ValueForMultipleImages = true, // optional, by default is false - private
            enabledBase64ValueForCameraImage = true, // optional, by default is false - private
            imagePickerInterface = this // call back interface
        )
        imagePicker?.initPickMultipleImagesFromGalleryResultLauncher(maxNumberOfImages = 5)

        //...other image picker initialization method(s)
    }

    override fun onMultipleGalleryImagesWithBase64Value(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
        Log.e("uri list", uriList.toString())
        binding.modelImagesTIET.setText(uriList.toString())
//        if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        base64AsStringList?.forEach {
            it.let { Log.d("base64AsString", it) }
        }
        super.onMultipleGalleryImagesWithBase64Value(bitmapList, uriList, base64AsStringList)
    }


    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.wtf("here", "version")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions()
                } else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.setType("image/*")
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    activityResultLauncher.launch(Intent.createChooser(intent,"Select images"))
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_DENIED
                ) {
                    requestPermissions()
                } else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.setType("image/*")
                    activityResultLauncher.launch(Intent.createChooser(intent,"Select images"))
                }
            }
        } else {
            Log.wtf("Here", "Pick image")
            val intent = Intent()
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setAction(Intent.ACTION_GET_CONTENT)
            activityResultLauncher.launch(intent)
        }
    }

    // gallery permission launcher
    private val requestMultiplePermissions =
        registerForActivityResult<Array<String>, Map<String, Boolean>>(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions: Map<String, Boolean> ->
            for ((key, value) in permissions) {
                Log.d("DEBUG", "$key = $value")
            }
            // Check if all required permissions are granted
            var allPermissionsGranted = true
            for (granted in permissions.values) {
                if (!granted) {
                    allPermissionsGranted = false
                    showDialogOK(
                        "Gallery Permission required for this app"
                    ) { dialog: DialogInterface?, which: Int ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> requestPermissions()
                            DialogInterface.BUTTON_NEGATIVE -> {}
                        }
                    }
                    break
                }
            }

            // If all permissions are granted, open the gallery
            if (allPermissionsGranted) {
                pickImage()
            }
        }

    private fun requestPermissions() {
        // Launch the permission request
        val permissionsToRequest: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        requestMultiplePermissions.launch(permissionsToRequest)
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        androidx.appcompat.app.AlertDialog.Builder(requireActivity())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedFileUri = data?.data
            Log.e("selected glb", selectedFileUri.toString())
            if (selectedFileUri != null) {
                getMimeType(selectedFileUri)
                binding.modelTIET.setText(selectedFileUri.toString())
                Log.e("typeee12", getMimeType(selectedFileUri).toString())

            }
            // Now you can use selectedFileUri for uploading or processing
        }
    }

    private fun openFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/octet-stream" // specify MIME type for .glb files
        pickFileLauncher.launch(intent)
    }

    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = requireActivity().contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault()))
        }
        Log.e("typeee", mimeType.toString())
        return mimeType
    }

}