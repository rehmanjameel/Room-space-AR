package com.codebase.fragments.shopping

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
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
import android.widget.ArrayAdapter
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nicos.imagepickerandroid.image_picker.ImagePicker
import com.nicos.imagepickerandroid.image_picker.ImagePickerInterface
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import java.util.Locale
import java.util.UUID


class AddProductFragment : Fragment(R.layout.fragment_add_product), ImagePickerInterface {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var selectedColors: List<Int>
    private lateinit var selectedSizes: List<String>
    private var interestsTopicsList: ArrayList<String> = ArrayList()
    private var langList: ArrayList<Int> = ArrayList()
    private lateinit var selectedLanguage: BooleanArray
    private var categoryList: ArrayList<String> = ArrayList()
    private val storedImagesURl: ArrayList<String> = ArrayList()
    // Initialize Firebase Storage
    val storage = Firebase.storage
    val storageRef = storage.reference
    // Initialize Firestore
    val db = Firebase.firestore
    private var imageUri: Uri? = null
    private var glbModelUri: Uri? = null

    private var imagePicker: ImagePicker? = null

    private var mArrayUri: ArrayList<Uri>? = null
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
//        initImagePicker()

        setFurnitureCategoryList()

        binding.colorsMenu.setEndIconOnClickListener {
            dialogOfColors()
        }

        binding.addProductsButton.setOnClickListener {
            validations()
        }

        binding.modelImagesTIL.setEndIconOnClickListener {
//            imagePicker?.pickMultipleImagesFromGallery()
            pickImage()
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

        } else if (imageUri == null) {
            Toast.makeText(requireContext(), "Please select the image", Toast.LENGTH_LONG).show()
        }else {

            Log.e("model uri", glbModelUri.toString())
            Log.e("model image uri", mArrayUri.toString())
            Log.e("model image uri size", selectedSizes.toString())
            Log.e("model image uri color", selectedColors.toString())
            showLoading()
            uploadGLBFileAndImagesToFirebase(productName, selectCategory, glbModelUri!!,
                imageUri!!,
                description, price.toDouble(), offerPercentage.toDouble(), selectedSizes, selectedColors)
//            mArrayUri?.let {
//            }
        }

    }

    private fun setFurnitureCategoryList() {
        categoryList.add("Chair")
        categoryList.add("Table")
        categoryList.add("Sofa")
        categoryList.add("Bed")

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            categoryList
        )

        binding.categoryDropDown.setAdapter(categoryAdapter)
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
                    Log.e("lang list", langList.toString())
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
                Log.e("string builder", stringBuilder.toString())
                Log.e("string builder000", interestsTopicsList[langList[j]])

                // check condition
                if (j != langList.size - 1) {
                    // When j value  not equal
                    // to lang list size - 1
                    // add comma
                    stringBuilder.append(", ")
                    Log.e("string builder12", stringBuilder.toString())

                }
            }
            // set text on textView
            selectedSizes = convertStringToListOfString(stringBuilder.toString())
            Log.e("selected sizes", selectedSizes.toString())
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

    private fun convertStringToListOfString(text: String): List<String> {
        // Split the string by comma
        val elements = text.split(",").map { it.trim() }
        return elements
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
                    imageUri =intent.data
                    binding.modelImagesTIET.setText(imageUri.toString())
//                    if (intent.clipData != null) {
//                        val mClipData: ClipData = intent.clipData!!
//                        val count: Int = mClipData.itemCount
//                        for (i in 0 until count) {
//                            val imageUri: Uri = mClipData.getItemAt(i).uri
//                            Log.e("image uri", imageUri.toString())
//                            mArrayUri?.add(imageUri)
//                            Log.e("image uri012", mArrayUri.toString())
//                        }
//                        binding.modelImagesTIET.setText(mArrayUri.toString())
//                        position = 0
//                    } else if (intent.data != null) {
//                        val imageUri: Uri = intent.data!!
//                        Log.e("image uri12", imageUri.toString())
//
//                        mArrayUri?.add(imageUri)
//                        Log.e("image uri123", imageUri.toString())
//
//                        Log.e("image uri01221212", mArrayUri.toString())
//
//                        binding.modelImagesTIET.setText(mArrayUri.toString())
//                        position = 0
//                    }
                }
            } else {
                Toast.makeText(requireActivity(), "No image selected", Toast.LENGTH_LONG).show()
            }
        }

    private fun initImagePicker() {
        //Builder
        //Note: fragmentActivity or fragment are mandatory one of them
        imagePicker = ImagePicker(
            fragment = this, // fragment instance - private
            coroutineScope = lifecycleScope, // mandatory - coroutine scope from activity or fragment - private
//            scaleBitmapModelForSingleImage = ScaleBitmapModel(
//                height = 100,
//                width = 100
//            ), // optional, change the scale for image, by default is null
            scaleBitmapModelForMultipleImages = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
//            scaleBitmapModelForCameraImage = ScaleBitmapModel(
//                height = 100,
//                width = 100
//            ), // optional, change the scale for image, by default is null
//            enabledBase64ValueForSingleImage = true, // optional, by default is false - private
            enabledBase64ValueForMultipleImages = true, // optional, by default is false - private
//            enabledBase64ValueForCameraImage = true, // optional, by default is false - private
            imagePickerInterface = this // call back interface
        )
        imagePicker?.initPickMultipleImagesFromGalleryResultLauncher(maxNumberOfImages = 5)

        //...other image picker initialization method(s)
    }

    // Function to upload GLB file to Firebase Storage
    private fun uploadGLBFileAndImagesToFirebase(
        productName: String, category: String,
        glbFileUri: Uri, imageUris: Uri,
        description: String, price: Double, offerPercentage: Double,
        sizes: List<String>, colors: List<Int>
    ) {
        // Upload the GLB file
//        val glbFileName = "your_glb_file.glb"
        val glbRef = storageRef.child("glb_files/")
        glbRef.putFile(glbFileUri)
            .addOnSuccessListener { taskSnapshot ->
                // GLB file uploaded successfully
                glbRef.downloadUrl.addOnSuccessListener { glbUri ->
                    val glbDownloadUrl = glbUri.toString()

                    showToast(requireContext(), "model uploaded!")
                    // Now upload each associated image
//                    for ((index, imageUri) in imageUris.withIndex()) {
//                        val imageName = "image_$index.jpg" // Set your desired image name here
                        val imageRef = storageRef.child("images/${UUID.randomUUID()}")
                        imageRef.putFile(imageUris)
                            .addOnSuccessListener { imageTaskSnapshot ->
                                // Image uploaded successfully
                                imageRef.downloadUrl.addOnSuccessListener { imageUri ->
                                    val imageDownloadUrl = imageUri.toString()

                                    storedImagesURl.add(imageDownloadUrl)
                                    showToast(requireContext(), "image uploaded!")
                                    saveProductDataToFirestore(productName, category, description, price,
                                        offerPercentage, sizes, colors, glbDownloadUrl, storedImagesURl)
                                    // Update the GLB file to reference the uploaded image URL
                                    // This step depends on how your GLB file references images
                                    // You may need to parse the GLB file and update the URLs accordingly
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle image upload failure
                                hideLoading()
                                showToast(requireContext(), "Product not added!")

                            }
//                    }

                }
            }
            .addOnFailureListener { exception ->
                // Handle GLB file upload failure
                showToast(requireContext(), "Product not added!")
                hideLoading()
            }
    }

    // Function to save product data to Firestore
    private fun saveProductDataToFirestore(
        productName: String,
        productCategory: String,
        productDescription: String,
        price: Double,
        offerPercentage: Double,
        sizes: List<String>,
        colors: List<Int>,
        glbFileUrl: String,
        imageUrls: List<String>,
//        imageUrls: String
    ) {
        // Create a new product document
        val productData = hashMapOf(
            "name" to productName,
            "category" to productCategory,
            "description" to productDescription,
            "price" to price,
            "offerPercentage" to offerPercentage,
            "sizes" to sizes,
            "colors" to colors,
            "model" to glbFileUrl,
            "images" to imageUrls
        )

        // Add the product data to Firestore
        db.collection("Products")
            .add(productData)
            .addOnSuccessListener { documentReference ->
                // Product data added successfully
                // You can perform any additional actions here if needed
                showToast(requireContext(), "Product added successfully!")
                binding.productNameTIET.setText("")
                binding.priceTIET.setText("")
                binding.descriptionTIET.setText("")
                binding.modelImagesTIET.setText("")
                binding.modelTIET.setText("")
                binding.offerPercentageTIET.setText("")
                binding.sizeDropDown.setText("")
                binding.colorsDropDown.setText("")
                binding.categoryDropDown.setText("")
                hideLoading()
            }
            .addOnFailureListener { e ->
                // Handle errors
                showToast(requireContext(), "Product not added!")
            }
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
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.setType("image/*")
                    activityResultLauncher.launch(Intent.createChooser(intent,"Select images"))
                }
            }
        } else {
            Log.wtf("Here", "Pick image")
            val intent = Intent()
            intent.setType("image/*")
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
            glbModelUri = data?.data
            Log.e("selected glb", glbModelUri.toString())
            if (glbModelUri != null) {
                getMimeType(glbModelUri!!)
                binding.modelTIET.setText(glbModelUri.toString())
                Log.e("typeee12", getMimeType(glbModelUri!!).toString())

            }
            // Now you can use glbModelUri for uploading or processing
        }
    }

    private fun openFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/octet-stream" // specify MIME type for .glb files
        pickFileLauncher.launch(intent)
    }

    private fun getMimeType(uri: Uri): String? {
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

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}