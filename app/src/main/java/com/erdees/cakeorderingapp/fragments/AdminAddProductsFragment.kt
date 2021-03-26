package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.erdees.cakeorderingapp.Constants
import com.erdees.cakeorderingapp.R
import com.erdees.cakeorderingapp.model.Products
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File


class AdminAddProductsFragment: Fragment() {
    val REQUEST_CODE = 149

    private lateinit var filePath: Uri
    lateinit var imageView: ImageView
    private lateinit var firebaseStorage :FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private lateinit var productNameInput : TextInputEditText
    private lateinit var imageUri : Uri
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_add_products_fragment, container, false)

        val listOfIngredients = mutableListOf<String>()
        val listOfTags = mutableListOf<String>()
        var isSpecial: Boolean = false


        /**Binders*/
        val uploadImageButton = view.findViewById<Button>(R.id.admin_add_image_button)
        val isSpecialCheckbox = view.findViewById<CheckBox>(R.id.admin_add_special_checkbox)
        productNameInput =
            view.findViewById<TextInputEditText>(R.id.admin_add_products_name_input)
        val productPriceInput =
            view.findViewById<TextInputEditText>(R.id.admin_add_products_price_input)
        val productDescInput =
            view.findViewById<TextInputEditText>(R.id.admin_add_products_desc_input)
        val addIngredientsBox = view.findViewById<TextInputLayout>(R.id.admin_add_ingredient_box)
        val ingredient =
            view.findViewById<TextInputEditText>(R.id.admin_add_products_ingredient_input)
        val ingredientList = view.findViewById<TextView>(R.id.admin_add_ingredient_list)
        val tagCheckBoxHolder = view.findViewById<LinearLayout>(R.id.admin_add_checkbox_layout)
        val submitButton = view.findViewById<Button>(R.id.admin_add_submit_product)

        imageView = view.findViewById(R.id.admin_add_products_imageview)

         db = Firebase.firestore
         firebaseStorage = FirebaseStorage.getInstance()
         firebaseStorageRef = firebaseStorage.reference




        val availableTags = Constants().tagArray


        val dynamicCheckBoxListener =
            CompoundButton.OnCheckedChangeListener() { buttonView, isChecked ->
                if (isChecked) {
                    val tag = buttonView.text.toString()
                    listOfTags.add(tag)
                } else {
                    val tag = buttonView.text.toString()
                    listOfTags.remove(tag)
                }
                Log.i("TAGS", listOfTags.joinToString(", ") { it })
            }

        val specialCheckBoxListener = CompoundButton.OnCheckedChangeListener() { _, isChecked ->
            isSpecial = isChecked
            Log.i("SPECIAL", isSpecial.toString())
        }


        for (eachTag in availableTags) {
            val newCheckBox = CheckBox(context)
            newCheckBox.setText(eachTag)
            tagCheckBoxHolder.addView(newCheckBox)
            newCheckBox.setOnCheckedChangeListener(dynamicCheckBoxListener)

        }

        addIngredientsBox.setEndIconOnClickListener {
            val inputIngredient = ingredient.text.toString()
            listOfIngredients += inputIngredient
            ingredientList.text = listOfIngredients.joinToString(", ") { it }
            ingredient.text?.clear()
        }


        isSpecialCheckbox.setOnCheckedChangeListener(specialCheckBoxListener)

        uploadImageButton.setOnClickListener {
            openGalleryForImage()
        }



        submitButton.setOnClickListener {

            var productPictureUrl = ""
            uploadImage()
            testImageRef.downloadUrl.addOnSuccessListener {
              productPictureUrl =   it.toString()
                Log.i("TEST",it.toString())
            }
            val productToAdd = Products(
                productNameInput.text.toString(),
                listOfTags,
                productDescInput.text.toString(),
                productPriceInput.text.toString().toDouble(),
                productPictureUrl,
                listOfIngredients,
                isSpecial

            )
            // Create a product Object
            // and then add it to database!

            val collectionRef = db.collection("products")
            collectionRef.add(productToAdd)
        }

        return view
    }


    companion object {
        const val TAG = "AdminAddProductsFragment"
        fun newInstance(): AdminAddProductsFragment = AdminAddProductsFragment()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    /**Made as functions so initialized when called*/
    fun pictureReference() = firebaseStorageRef.child("${productNameInput.text.toString()}.jpg")
    fun pictureImagesRef() = firebaseStorageRef.child("images/${productNameInput.text.toString()}.jpg")
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val testReference = storageRef.child("testpictureupload.jpg")
        val testImageRef = storageRef.child("images/testpictureupload.jpg")

    fun uploadImage(){
        val file = imageUri
        var uploadTask = testImageRef.putFile(file)
        uploadTask.addOnSuccessListener {
Log.i("TEST", "success")
        }
            .addOnFailureListener {
                Log.i("TEST", "fail")

            }

    }

fun makeAsBitmapAndUpload() {
    // Get the data from an ImageView as bytes
    imageView.isDrawingCacheEnabled = true
    imageView.buildDrawingCache()
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    var uploadTask = testReference.putBytes(data)
    uploadTask.addOnFailureListener {
        // Handle unsuccessful uploads
    }.addOnSuccessListener { taskSnapshot ->
        Log.i("TEST",taskSnapshot.uploadSessionUri.toString())
        Log.i("TEST",taskSnapshot.bytesTransferred.toString())
        Log.i("TEST","Success")
    }
}

    // Override onActivityResult method
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            imageView.setImageURI(data?.data) // SET chosen image
            imageUri = data?.data!!
            // Get the Uri of data
            filePath = data.data!!

        }
    }

        }

