package androidx.recyclerview.widget.com.erdees.cakeorderingapp.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.erdees.cakeorderingapp.*
import com.erdees.cakeorderingapp.model.Products
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlin.random.Random


class AdminAddProductsFragment : Fragment() {
    val REQUEST_CODE = 149

    private lateinit var filePath: Uri
    lateinit var imageView: ImageView
    private lateinit var productDescInput: TextInputEditText
    private lateinit var productPriceInput: TextInputEditText
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private lateinit var productNameInput: TextInputEditText
    private lateinit var imageUri: Uri
    private lateinit var generatedId: String
    private lateinit var isSpecialCheckbox : CheckBox

    val listOfIngredients = mutableListOf<String>()
    val listOfTags = mutableListOf<String>()
    var isSpecial: Boolean = false

    private lateinit var testReference: StorageReference
    private lateinit var testImageRef: StorageReference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_add_products_fragment, container, false)

        /**Randomize id for new picture
         * Every time this screen is open new Id is generated
         * I'm using this approach so After adding picture to storage I have reference to that picture right here in [generatedId] */
        val randomSeed = Random(System.currentTimeMillis())
        val randomNumber = randomSeed.nextLong(9999999L, 999999999L)
        generatedId = randomNumber.toString()

        /**Binders*/
        val uploadImageButton = view.findViewById<Button>(R.id.admin_add_image_button)
         isSpecialCheckbox = view.findViewById(R.id.admin_add_special_checkbox)
        productNameInput =
            view.findViewById(R.id.admin_add_products_name_input)
        productPriceInput =
            view.findViewById(R.id.admin_add_products_price_input)
        productDescInput =
            view.findViewById(R.id.admin_add_products_desc_input)
        val addIngredientsBox = view.findViewById<TextInputLayout>(R.id.admin_add_ingredient_box)
        val ingredient =
            view.findViewById<TextInputEditText>(R.id.admin_add_products_ingredient_input)
        val ingredientList = view.findViewById<TextView>(R.id.admin_add_ingredient_list)
        val tagCheckBoxHolder = view.findViewById<LinearLayout>(R.id.admin_add_checkbox_layout)
        val submitButton = view.findViewById<Button>(R.id.admin_add_submit_product)
        val deleteLastTagButton = view.findViewById<TextView>(R.id.admin_add_delete_last_tag)


        imageView = view.findViewById(R.id.admin_add_products_imageview)

        db = Firebase.firestore
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseStorageRef = firebaseStorage.reference

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        testReference = storageRef.child("$generatedId.jpg")
        testImageRef = storageRef.child("images/$generatedId.jpg")


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
            deleteLastTagButton.makeVisible()
        }


        deleteLastTagButton.setOnClickListener {
            listOfIngredients.remove(listOfIngredients.last())
            ingredientList.text = listOfIngredients.joinToString(",") { it  }
            if(listOfIngredients.size == 0) deleteLastTagButton.makeGone()
        }

        isSpecialCheckbox.setOnCheckedChangeListener(specialCheckBoxListener)

        uploadImageButton.setOnClickListener {
            openGalleryForImage()
        }



        submitButton.setOnClickListener {
            if(productNameInput.text.isNullOrBlank() || productDescInput.text.isNullOrBlank() || productPriceInput.text.isNullOrBlank() || imageView.drawable == null){
                Toast.makeText(
                    context,
                    "You must provide all product information and picture!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            uploadImage()
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



    fun uploadImage() {
        val file = imageUri
        var uploadTask = testImageRef.putFile(file)
        uploadTask.addOnSuccessListener {
            Log.i("TEST", "success")
            testImageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.i("TEST GOT IT? ", uri.toString())
                uploadProduct(uri)
            }
        }
            .addOnFailureListener {
                Log.i("TEST", "fail")
            }
    }

    fun uploadProduct(uri: Uri) {
        val productToAdd = Products(
            productNameInput.text.toString(),
            listOfTags,
            productDescInput.text.toString(),
            productPriceInput.text.toString().toDouble(),
            uri.toString(),
            listOfIngredients,
            isSpecial
        )
        val collectionRef = db.collection("products")
        collectionRef.add(productToAdd).addOnSuccessListener {
            showDialog()
        }
        Log.i("TEST", "Should be added.")
    }


    fun showDialog() {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage("Product is added to server.")
            .setNegativeButton("Back", null)
            .show()

        alertDialog.setOnDismissListener {
            recreate()
            //IDK BUT TEXTFIELD DONT RESTART WHEN FRAGMENT IS RECREATED, FOR NOW ILL JUST RESTART THEM MANUALLY IN HERE, LATER ILL FIGURE SMTHN OUT.
            productNameInput.text?.clear()
            productDescInput.text?.clear()
            productPriceInput.text?.clear()
            isSpecialCheckbox.isChecked = false

        }
    }

    fun recreate() {
        val ft: FragmentTransaction = this.fragmentManager!!.beginTransaction()
        ft.detach(this)
        ft.attach(this)
        ft.commit()
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

            data.data?.let { uri ->
                launchImageCrop(uri)
            }
           // imageView.setImageURI(data.data) // SET chosen image
           // imageUri = data.data!!
            // Get the Uri of data
            //filePath = data.data!!
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                setImage(resultUri)
                imageUri = resultUri
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }

        }
    }

    fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(imageView)
    }

    fun launchImageCrop(uri : Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(450,350)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(context!!, this);
    }
/**
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                imageView.setImageURI(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
*/



}

