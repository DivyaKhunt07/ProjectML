package com.example.imagerecognise

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.imagerecognise.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

open class MainActivity : ComponentActivity() {
    lateinit var mBinding: ActivityMainBinding
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        mBinding.btnSnap.setOnClickListener {
            dispatchTakePictureIntent()
        }

        mBinding.bntLable.setOnClickListener {
            labelImage()
        }
    }


    private fun dispatchTakePictureIntent() {
        // inside this method we are calling an implicit intent to capture an image.
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // calling a start activity for result when image is captured.
            resultLauncher.launch(takePictureIntent)
          //  startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val extras = data?.extras
            imageBitmap = extras!!["data"] as Bitmap?
            // on below line we are setting our
            // bitmap to our image view.
            mBinding.imageData.setImageBitmap(imageBitmap)
        }
    }


//    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // inside on activity result method we are setting
//        // our image to our image view from bitmap.
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val extras = data.extras
//            imageBitmap = extras!!["data"] as Bitmap?
//            // on below line we are setting our
//            // bitmap to our image view.
//            mBinding.imageData.setImageBitmap(imageBitmap)
//        }
//    }

    private fun labelImage() {

        // inside the label image method we are calling a
        // firebase vision image and passing our image bitmap to it.
        val image = imageBitmap?.let { FirebaseVisionImage.fromBitmap(it.copy(Bitmap.Config.ARGB_8888, true)) }

        // on below line we are creating a labeler for our image bitmap
        // and creating a variable for our firebase vision image labeler.
       val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
      //  val labeler = FirebaseVision.getInstance().cloudImageLabeler

      //  val options = optionsBuilder.setConfidenceThreshold(0.0f).build()
        val labeler1 = FirebaseVision.getInstance().onDeviceObjectDetector

        // calling a method to process an image and adding on success listener method to it.
        Log.d("TAG","firebaseVisionImageLabels: ${labeler.imageLabelerType}")
        if (image != null) {
            labeler.processImage(image).addOnSuccessListener { firebaseVisionImageLabels ->
                // inside on success method we are running a loop to get the data from our list.
                Log.d("TAG","firebaseVisionImageLabels: ${firebaseVisionImageLabels}")
                for (label in firebaseVisionImageLabels) {

                    // on below line we are getting text from our list.
                    val text = label.text
                    // on below line we are getting its entity id
                    val entityId = label.entityId
                    // on below line we are getting the
                    // confidence level of our modal.
                    val confidence = label.confidence

                    Log.d("TAG", "labelImage: $text / $confidence")

                    // after getting all data we are passing it to our array list.
                   // dataModalArrayList.add(DataModal(text, confidence))

                    // after adding a new data we are notifying
                    // our adapter that data has been updated.
                   // resultRvAdapter.notifyDataSetChanged()
                }
            }.addOnFailureListener { // error handling for on failure method
                Toast.makeText(this@MainActivity, "Fail to get data..", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

