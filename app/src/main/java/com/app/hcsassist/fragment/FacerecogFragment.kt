package com.app.hcsassist.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.InputType
import android.util.Pair
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.PunchinRequest
import com.app.hcsassist.databinding.FragmentFacerecogBinding
import com.app.hcsassist.modelfactory.PunchinModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.SimilarityClassifier
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.PunchingViewModel
import com.example.wemu.session.SessionManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.ReadOnlyBufferException
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.experimental.inv

class FacerecogFragment : Fragment() {
    lateinit var fragmentFacerecogBinding: FragmentFacerecogBinding
    lateinit var mainActivity: MainActivity
    var previewView: PreviewView? = null
    var tfLite: Interpreter? = null
    var cameraSelector: CameraSelector? = null
    var cam_face = CameraSelector.LENS_FACING_FRONT //Default Back Camera
    var cameraProvider: ProcessCameraProvider? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    var flipX = false
    var start = true
    private var registered = HashMap<String?, SimilarityClassifier.Recognition>() //saved Faces
    var distance = 1.0f
    var developerMode = false
    var IMAGE_MEAN = 128.0f
    var IMAGE_STD = 128.0f
    var OUTPUT_SIZE = 192 //Output size of model
    var detector: FaceDetector? = null
    var modelFile = "mobile_face_net.tflite" //model name
    var inputSize = 112 //Input size for model
    lateinit var intValues: IntArray
    lateinit var embeedings: Array<FloatArray>
    var isModelQuantized = false
    lateinit var punchingViewModel: PunchingViewModel
    var sessionManager: SessionManager? = null
    var formatted_address: String? = ""
    var name:String?=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity()?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFacerecogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_facerecog, container, false)
        val root = fragmentFacerecogBinding.root
        mainActivity = activity as MainActivity
        sessionManager = SessionManager(mainActivity)
        val vm: PunchingViewModel by viewModels {
            PunchinModelFactory(ApiHelper(ApiClient.apiService))
        }
        punchingViewModel = vm
        val bundle = this.arguments
        if (bundle != null) {
            formatted_address = bundle.getString("punch_in_location").toString()
        }else{
            formatted_address = ""
        }

        val sharedPref = mainActivity.getSharedPreferences("Distance", AppCompatActivity.MODE_PRIVATE)
        distance = sharedPref.getFloat("distance", 1.00f)
        registered = readFromSP() //Load saved faces from memory when app starts
        fragmentFacerecogBinding.textAbovePreview.text = "Recognized Face:"


//        fragmentFacerecogBinding.imageButton.setVisibility(View.INVISIBLE)
//        fragmentFacerecogBinding.imageView.setVisibility(View.INVISIBLE)
        fragmentFacerecogBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

//        fragmentFacerecogBinding.imageButton.setOnClickListener(View.OnClickListener { addFace() })

        cameraBind()

//        val secondsDelayed = 1
//        Handler().postDelayed({
//
//
//            val navController = view?.let { Navigation.findNavController(it) }
//            navController?.navigate(R.id.nav_home)
//
//
//        }, (secondsDelayed * 5000).toLong())


        fragmentFacerecogBinding.cameraSwitch.setOnClickListener {

            if (cam_face == CameraSelector.LENS_FACING_BACK) {
                cam_face = CameraSelector.LENS_FACING_FRONT
                flipX = true
            } else {
                cam_face = CameraSelector.LENS_FACING_BACK
                flipX = false
            }
            cameraProvider!!.unbindAll()
            cameraBind()
        }


//        fragmentFacerecogBinding.btnaddface.setOnClickListener {
//
//            if (fragmentFacerecogBinding.btnaddface.getText().toString() == "Recognize") {
//                start = true
//                fragmentFacerecogBinding.textAbovePreview.setText("Recognized Face:")
//                fragmentFacerecogBinding.btnaddface.setText("Add Face")
//                fragmentFacerecogBinding.imageButton.setVisibility(View.INVISIBLE)
//                fragmentFacerecogBinding.textView.setVisibility(View.VISIBLE)
//                fragmentFacerecogBinding.imageView.setVisibility(View.INVISIBLE)
//                fragmentFacerecogBinding.textView2.setText("")
//                //preview_info.setVisibility(View.INVISIBLE);
//            } else {
//                fragmentFacerecogBinding.textAbovePreview.setText("Face Preview: ")
//                fragmentFacerecogBinding.btnaddface.setText("Recognize")
//                fragmentFacerecogBinding.imageButton.setVisibility(View.VISIBLE)
//                fragmentFacerecogBinding.textView.setVisibility(View.INVISIBLE)
//                fragmentFacerecogBinding.imageView.setVisibility(View.VISIBLE)
//                fragmentFacerecogBinding.textView2.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.")
//            }
//        }

        //Load model
        try {
            tfLite = Interpreter(loadModelFile(mainActivity, modelFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //Initialize Face Detector
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
        detector = FaceDetection.getClient(highAccuracyOpts)


//        fragmentFacerecogBinding.btnactions.setOnClickListener {
//
//            val builder = AlertDialog.Builder(mainActivity)
//            builder.setTitle("Select Action:")
//
//            // add a checkbox list
//            val names = arrayOf(
//                "View Recognition List",
//                "Update Recognition List",
//                "Save Recognitions",
//                "Load Recognitions",
//                "Clear All Recognitions",
//                "Import Photo (Beta)",
//                "Hyperparameters",
//                "Developer Mode"
//            )
//            builder.setItems(names) { dialog, which ->
//                when (which) {
//                    0 -> displaynameListview()
//                    1 -> updatenameListview()
//                    2 -> insertToSP(registered, 0) //mode: 0:save all, 1:clear all, 2:update all
//                    3 -> registered.putAll(readFromSP())
//                    4 -> clearnameList()
//                    5 -> loadphoto()
//                }
//            }
//            builder.setPositiveButton("OK") { dialog, which -> }
//            builder.setNegativeButton("Cancel", null)
//
//            // create and show the alert dialog
//            val dialog = builder.create()
//            dialog.show()
//        }


        return root
    }


    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, MODEL_FILE: String): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    //Bind camera and preview view
    private fun cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        previewView = fragmentFacerecogBinding.previewView
        cameraProviderFuture!!.addListener({
            try {
                cameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider!!)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this in Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(mainActivity))
    }


    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cam_face)
            .build()
        preview.setSurfaceProvider(previewView!!.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
            .build()
        val executor: Executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor) { imageProxy ->
            try {
                Thread.sleep(0) //Camera preview refreshed every 10 millisec(adjust as required)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            var image: InputImage? = null

            @SuppressLint(
                "UnsafeExperimentalUsageError",
                "UnsafeOptInUsageError"
            ) val mediaImage// Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
                    = imageProxy.image
            if (mediaImage != null) {
                image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                //                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
            }

//                System.out.println("ANALYSIS");

            //Process acquired image to detect faces
            val result = detector!!.process(
                image!!
            )
                .addOnSuccessListener { faces ->
                    if (faces.size != 0) {
                        val face = faces[0] //Get first face from detected faces
                        //                                                    System.out.println(face);

                        //mediaImage to Bitmap
                        val frame_bmp = toBitmap(mediaImage)
                        val rot = imageProxy.imageInfo.rotationDegrees

                        //Adjust orientation of Face
                        val frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false)


                        //Get bounding box of face
                        val boundingBox = RectF(face.boundingBox)

                        //Crop out bounding box from whole Bitmap(image)
                        var cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
                        if (flipX) cropped_face = rotateBitmap(cropped_face, 0, flipX, false)
                        //Scale the acquired Face to 112*112 which is required input for model
                        val scaled = getResizedBitmap(cropped_face, 112, 112)
                        if (start) recognizeImage(scaled) //Send scaled bitmap to create face embeddings.
                        //                                                    System.out.println(boundingBox);
                    } else {
                        if (registered.isEmpty()) fragmentFacerecogBinding.textView.text =
                            "Add Face" else fragmentFacerecogBinding.textView.text =
                            "No Face Detected!"
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    imageProxy.close() //v.important to acquire next frame for analysis
                }
        }
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector!!,
            imageAnalysis,
            preview
        )
    }


    private fun displaynameListview() {
        val builder = AlertDialog.Builder(mainActivity)
        // System.out.println("Registered"+registered);
        if (registered.isEmpty()) builder.setTitle("No Faces Added!!") else builder.setTitle("Recognitions:")

        // add a checkbox list
        val names = arrayOfNulls<String>(registered.size)
        val checkedItems = BooleanArray(registered.size)
        var i = 0
        for ((key) in registered) {
            //System.out.println("NAME"+entry.getKey());
            names[i] = key
            checkedItems[i] = false
            i = i + 1
        }
        builder.setItems(names, null)
        builder.setPositiveButton("OK") { dialog, which -> }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun updatenameListview() {
        val builder = AlertDialog.Builder(mainActivity)
        if (registered.isEmpty()) {
            builder.setTitle("No Faces Added!!")
            builder.setPositiveButton("OK", null)
        } else {
            builder.setTitle("Select Recognition to delete:")

            // add a checkbox list
            val names = arrayOfNulls<String>(registered.size)
            val checkedItems = BooleanArray(registered.size)
            var i = 0
            for ((key) in registered) {
                //System.out.println("NAME"+entry.getKey());
                names[i] = key
                checkedItems[i] = false
                i = i + 1
            }
            builder.setMultiChoiceItems(
                names,
                checkedItems
            ) { dialog, which, isChecked -> // user checked or unchecked a box
                //Toast.makeText(MainActivity.this, names[which], Toast.LENGTH_SHORT).show();
                checkedItems[which] = isChecked
            }
            builder.setPositiveButton("OK") { dialog, which -> // System.out.println("status:"+ Arrays.toString(checkedItems));
                for (i in checkedItems.indices) {
                    //System.out.println("status:"+checkedItems[i]);
                    if (checkedItems[i]) {
//                                Toast.makeText(MainActivity.this, names[i], Toast.LENGTH_SHORT).show();
                        registered.remove(names[i])
                    }
                }
                insertToSP(registered, 2) //mode: 0:save all, 1:clear all, 2:update all
                Toast.makeText(context, "Recognitions Updated", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun insertToSP(jsonMap: HashMap<String?, SimilarityClassifier.Recognition>, mode: Int) {
        if (mode == 1) //mode: 0:save all, 1:clear all, 2:update all
            jsonMap.clear() else if (mode == 0) jsonMap.putAll(readFromSP())
        val jsonString = Gson().toJson(jsonMap)
        //        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        val sharedPreferences =
            mainActivity.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("map", jsonString)
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply()
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show()
    }

    private fun readFromSP(): HashMap<String?, SimilarityClassifier.Recognition> {
        val sharedPreferences =
            mainActivity.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
        val defValue = Gson().toJson(HashMap<String, SimilarityClassifier.Recognition>())
        val json = sharedPreferences.getString("map", defValue)
        // System.out.println("Output json"+json.toString());
        val token: TypeToken<HashMap<String?, SimilarityClassifier.Recognition?>?> =
            object : TypeToken<HashMap<String?, SimilarityClassifier.Recognition?>?>() {}
        val retrievedMap =
            Gson().fromJson<HashMap<String?, SimilarityClassifier.Recognition>>(json, token.type)
        // System.out.println("Output map"+retrievedMap.toString());

        //During type conversion and save/load procedure,format changes(eg float converted to double).
        //So embeddings need to be extracted from it in required format(eg.double to float).
        for ((_, value) in retrievedMap) {
            val output = Array(1) { FloatArray(OUTPUT_SIZE) }
            var arrayList = value.extra as ArrayList<*>
            arrayList = arrayList[0] as ArrayList<*>
            for (counter in arrayList.indices) {
                output[0][counter] = (arrayList[counter] as Double).toFloat()
            }
            value.extra = output

            //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );
        }
        //        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
        return retrievedMap
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mainActivity, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(mainActivity, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }


    //Load Photo from phone storage
    private fun loadphoto() {
        start = false
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    //Similar Analyzing Procedure
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri = data!!.data
                try {
                    val impphoto = InputImage.fromBitmap(getBitmapFromUri(selectedImageUri), 0)
                    detector!!.process(impphoto).addOnSuccessListener { faces ->
                        if (faces.size != 0) {


//                            fragmentFacerecogBinding.btnaddface.text = "Recognize"
//                            fragmentFacerecogBinding.imageButton.visibility = View.VISIBLE
                            fragmentFacerecogBinding.textView.visibility = View.INVISIBLE
//                            fragmentFacerecogBinding.imageView.visibility = View.VISIBLE
//                            fragmentFacerecogBinding.textView2.text =
//                                "1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face."
                            val face = faces[0]
                            //                                System.out.println(face);

                            //write code to recreate bitmap from source
                            //Write code to show bitmap to canvas
                            var frame_bmp: Bitmap? = null
                            try {
                                frame_bmp = getBitmapFromUri(selectedImageUri)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false)

                            //face_preview.setImageBitmap(frame_bmp1);
                            val boundingBox = RectF(face.boundingBox)
                            val cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
                            val scaled = getResizedBitmap(cropped_face, 112, 112)
                            // face_preview.setImageBitmap(scaled);
                            recognizeImage(scaled)
                            addFace()
                            //                                System.out.println(boundingBox);
                            try {
                                Thread.sleep(100)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }.addOnFailureListener {
                        start = true
                        Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                    }
//                    fragmentFacerecogBinding.imageView.setImageBitmap(getBitmapFromUri(selectedImageUri))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri?): Bitmap {
        val parcelFileDescriptor = mainActivity.contentResolver.openFileDescriptor(
            uri!!, "r"
        )
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }

    private fun findNearest(emb: FloatArray): List<Pair<String?, Float>?> {
        val neighbour_list: MutableList<Pair<String?, Float>?> = ArrayList()
        var ret: Pair<String?, Float>? = null //to get closest match
        var prev_ret: Pair<String?, Float>? = null //to get second closest match
        for ((name, value) in registered) {
            val knownEmb = (value.extra as Array<FloatArray>)[0]
            var distance = 0f
            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = Math.sqrt(distance.toDouble()).toFloat()
            if (ret == null || distance < ret.second) {
                prev_ret = ret
                ret = Pair(name, distance)
            }
        }
        if (prev_ret == null) prev_ret = ret
        neighbour_list.add(ret)
        neighbour_list.add(prev_ret)
        return neighbour_list
    }


    private fun addFace() {
        run {
            start = false
            val builder = AlertDialog.Builder(mainActivity)
            builder.setTitle("Enter Name")

            // Set up the input
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            // Set up the buttons
            builder.setPositiveButton("ADD") { dialog, which -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();

                //Create and Initialize new object with Face embeddings and Name.
                val result = SimilarityClassifier.Recognition(
                    "0", "", -1f
                )
                result.extra = embeedings
                registered[input.text.toString()] = result
                start = true
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                start = true
                dialog.cancel()
            }
            builder.show()
        }
    }

    private fun clearnameList() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.setTitle("Do you want to delete all Recognitions?")
        builder.setPositiveButton("Delete All") { dialog, which ->
            registered.clear()
            Toast.makeText(context, "Recognitions Cleared", Toast.LENGTH_SHORT).show()
        }
        insertToSP(registered, 1)
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }


    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }


    private fun toBitmap(image: Image?): Bitmap {
        val nv21 = YUV_420_888toNV21(image)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image!!.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        //System.out.println("bytes"+ Arrays.toString(imageBytes));

        //System.out.println("FORMAT"+image.getFormat());
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    fun recognizeImage(bitmap: Bitmap) {

        // set Face to Preview
//        fragmentFacerecogBinding.imageView.setImageBitmap(bitmap)

        //Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        intValues = IntArray(inputSize * inputSize)

        //get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else { // Float model
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        //imgData is input to our model
        val inputArray = arrayOf<Any>(imgData)
        val outputMap: MutableMap<Int, Any> = HashMap()
        embeedings =
            Array(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
        outputMap[0] = embeedings
        tfLite!!.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
        var distance_local = Float.MAX_VALUE
        val id = "0"
        val label = "?"

        //Compare new face with saved Faces.
        if (registered.size > 0) {
            val nearest = findNearest(embeedings[0]) //Find 2 closest matching face
            if (nearest[0] != null) {
                name = nearest[0]!!.first //get name and distance of closest matching face
                // label = name;
                distance_local = nearest[0]!!.second

                if (distance_local < distance) {
                    fragmentFacerecogBinding.textView.text = name



                    //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                } else fragmentFacerecogBinding.textView.text = "Unknown"
                //                    System.out.println("nearest: " + name + " - distance: " + distance_local);

            }
        }


//            final int numDetectionsOutput = 1;
//            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
//            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
//                    id,
//                    label,
//                    distance);
//
//            recognitions.add( rec );
    }


    override fun onResume() {
        if (name.equals(sessionManager?.getinputName())){
            val secondsDelayed = 1
            Handler().postDelayed({
                punchinattendance(fragmentFacerecogBinding.root.rootView)

            }, (secondsDelayed * 2000).toLong())
        }
        super.onResume()
    }


    private fun punchinattendance(view: View) {
        val deviceinfo = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID)

        punchingViewModel.punchin(
            authtoken = "Bearer " + sessionManager?.getToken(),
            PunchinRequest(
                punch_in_location = formatted_address, device_type = "android",
                imei_no = deviceinfo, device_info = Build.MODEL
            )
        )
            .observe(mainActivity) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status == true) {
                                sessionManager?.setPunchin("punchin")
                                sessionManager?.setpunchinId(resource.data?.data?.id)
                                val navController = Navigation.findNavController(view)
                                navController.navigate(R.id.nav_home)
                            }


                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            val builder = android.app.AlertDialog.Builder(mainActivity)
                            builder.setMessage(it.message)
                            builder.setPositiveButton(
                                "Ok"
                            ) { dialog, which ->

                                dialog.cancel()

                            }
                            val alert = builder.create()
                            alert.show()
                        }

                        Status.LOADING -> {
                            showProgressDialog()
                        }

                    }

                }
            }
    }


    companion object {
        const val SELECT_PICTURE = 1
        const val MY_CAMERA_REQUEST_CODE = 100
        fun getCropBitmapByCPU(source: Bitmap?, cropRectF: RectF): Bitmap {
            val resultBitmap = Bitmap.createBitmap(
                cropRectF.width().toInt(),
                cropRectF.height().toInt(),
                Bitmap.Config.ARGB_8888
            )
            val cavas = Canvas(resultBitmap)

            // draw background
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            paint.color = Color.WHITE
            cavas.drawRect(
                RectF(0F, 0F, cropRectF.width(), cropRectF.height()),
                paint
            )
            val matrix = Matrix()
            matrix.postTranslate(-cropRectF.left, -cropRectF.top)
            cavas.drawBitmap(source!!, matrix, paint)
            if (source != null && !source.isRecycled) {
                source.recycle()
            }
            return resultBitmap
        }

        fun rotateBitmap(
            bitmap: Bitmap?, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
        ): Bitmap {
            val matrix = Matrix()

            // Rotate the image back to straight.
            matrix.postRotate(rotationDegrees.toFloat())

            // Mirror the image along the X or Y axis.
            matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
            val rotatedBitmap =
                Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)

            // Recycle the old bitmap if it has changed.
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            return rotatedBitmap
        }

        //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
        fun YUV_420_888toNV21(image: Image?): ByteArray {
            val width = image!!.width
            val height = image.height
            val ySize = width * height
            val uvSize = width * height / 4
            val nv21 = ByteArray(ySize + uvSize * 2)
            val yBuffer = image.planes[0].buffer // Y
            val uBuffer = image.planes[1].buffer // U
            val vBuffer = image.planes[2].buffer // V
            var rowStride = image.planes[0].rowStride
            assert(image.planes[0].pixelStride == 1)
            var pos = 0
            if (rowStride == width) { // likely
                yBuffer[nv21, 0, ySize]
                pos += ySize
            } else {
                var yBufferPos = -rowStride.toLong() // not an actual position
                while (pos < ySize) {
                    yBufferPos += rowStride.toLong()
                    yBuffer.position(yBufferPos.toInt())
                    yBuffer[nv21, pos, width]
                    pos += width
                }
            }
            rowStride = image.planes[2].rowStride
            val pixelStride = image.planes[2].pixelStride
            assert(rowStride == image.planes[1].rowStride)
            assert(pixelStride == image.planes[1].pixelStride)
            if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
                // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
                val savePixel = vBuffer[1]
                try {
                    vBuffer.put(1, savePixel.inv().toByte())
                    if (uBuffer[0] == savePixel.inv().toByte()) {
                        vBuffer.put(1, savePixel)
                        vBuffer.position(0)
                        uBuffer.position(0)
                        vBuffer[nv21, ySize, 1]
                        uBuffer[nv21, ySize + 1, uBuffer.remaining()]
                        return nv21 // shortcut
                    }
                } catch (ex: ReadOnlyBufferException) {
                    // unfortunately, we cannot check if vBuffer and uBuffer overlap
                }

                // unfortunately, the check failed. We must save U and V pixel by pixel
                vBuffer.put(1, savePixel)
            }

            // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
            // but performance gain would be less significant
            for (row in 0 until height / 2) {
                for (col in 0 until width / 2) {
                    val vuPos = col * pixelStride + row * rowStride
                    nv21[pos++] = vBuffer[vuPos]
                    nv21[pos++] = uBuffer[vuPos]
                }
            }
            return nv21
        }
    }


    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(mainActivity)
            mProgressDialog!!.setMessage("Loading...")
            mProgressDialog!!.isIndeterminate = true
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }


}