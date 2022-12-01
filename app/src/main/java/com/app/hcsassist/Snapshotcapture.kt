package com.app.hcsassist

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.hcsassist.apimodel.FRSuploadRequest
import com.app.hcsassist.databinding.ActivitySnapshotcaptureBinding
import com.app.hcsassist.modelfactory.FRSModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.FRSViewModel
import com.example.cameraxexample.Constants
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_facerecog.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Snapshotcapture : AppCompatActivity() {
    private lateinit var frsViewModel: FRSViewModel
    var sessionManager: SessionManager? = null
    lateinit var snapshotcaptureBinding: ActivitySnapshotcaptureBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    var REQUEST_CODE = 101


//    var previewView: PreviewView? = null
//    var tfLite: Interpreter? = null
//    var cameraSelector: CameraSelector? = null
//    var cam_face = CameraSelector.LENS_FACING_FRONT //Default Back Camera
//    var cameraProvider: ProcessCameraProvider? = null
//    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
//    var flipX = false
//    var start = true
//    private var registered = HashMap<String?, SimilarityClassifier.Recognition>() //saved Faces
//    var distance = 1.0f
//    var developerMode = false
//    var IMAGE_MEAN = 128.0f
//    var IMAGE_STD = 128.0f
//    var OUTPUT_SIZE = 192 //Output size of model
//    var detector: FaceDetector? = null
//    var modelFile = "mobile_face_net.tflite" //model name
//    var inputSize = 112 //Input size for model
//    lateinit var intValues: IntArray
//    lateinit var embeedings: Array<FloatArray>
//    var isModelQuantized = false
//    var jsonString:String?=""
//    var username:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snapshotcaptureBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_snapshotcapture)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        sessionManager = SessionManager(this)
//        val sharedPref = getSharedPreferences("Distance", AppCompatActivity.MODE_PRIVATE)
//        distance = sharedPref.getFloat("distance", 1.00f)
//        registered = readFromSP() //Load saved faces from memory when app starts
//        cameraBind()

        val vm: FRSViewModel by viewModels {
            FRSModelFactory(ApiHelper(ApiClient.apiService))
        }

        frsViewModel = vm

//        val intent = intent
//        username = intent.getStringExtra("username")
//
//        snapshotcaptureBinding.textAbovePreview.setText("Face Preview: ")
//        snapshotcaptureBinding.textView.setVisibility(View.INVISIBLE)
//        snapshotcaptureBinding.imageView.setVisibility(View.VISIBLE)
//        snapshotcaptureBinding.textView2.setText("1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face.")



//        snapshotcaptureBinding.cameraSwitch.setOnClickListener {
//
//            if (cam_face == CameraSelector.LENS_FACING_BACK) {
//                cam_face = CameraSelector.LENS_FACING_FRONT
//                flipX = true
//            } else {
//                cam_face = CameraSelector.LENS_FACING_BACK
//                flipX = false
//            }
//            cameraProvider!!.unbindAll()
//            cameraBind()
//        }


//        snapshotcaptureBinding.btnactions.setOnClickListener {
//
//            addFace()
//
//
//        }
//
//        registered.clear()
//        insertToSP(registered, 1)
//
//        //Load model
//        try {
//            tfLite = Interpreter(loadModelFile(this, modelFile))
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        //Initialize Face Detector
//        val highAccuracyOpts = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//            .build()
//        detector = FaceDetection.getClient(highAccuracyOpts)

//        ImagePicker.with(this)
//            .cameraOnly() //User can only capture image using Camera
//            .start()

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()


        snapshotcaptureBinding.btnCaptureface.setOnClickListener {

            takePhoto()

        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                Constants.FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    picuploadToServer(GetRealPathFromUri.getPathFromUri(this@Snapshotcapture, savedUri!!)!!)
                    val msg = "Photo capture succeeded: $savedUri"
//                    Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }




//    @TargetApi(Build.VERSION_CODES.CUPCAKE)
//    @Throws(IOException::class)
//    private fun loadModelFile(activity: Activity, MODEL_FILE: String): MappedByteBuffer {
//        val fileDescriptor = activity.assets.openFd(MODEL_FILE)
//        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
//        val fileChannel = inputStream.channel
//        val startOffset = fileDescriptor.startOffset
//        val declaredLength = fileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }


//    private fun cameraBind() {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        previewView = snapshotcaptureBinding.previewView
//        cameraProviderFuture!!.addListener({
//            try {
//                cameraProvider = cameraProviderFuture!!.get()
//                bindPreview(cameraProvider!!)
//            } catch (e: ExecutionException) {
//                // No errors need to be handled for this in Future.
//                // This should never be reached.
//            } catch (e: InterruptedException) {
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }


//    fun bindPreview(cameraProvider: ProcessCameraProvider) {
//        val preview = Preview.Builder()
//            .build()
//        cameraSelector = CameraSelector.Builder()
//            .requireLensFacing(cam_face)
//            .build()
//        preview.setSurfaceProvider(previewView!!.surfaceProvider)
//        val imageAnalysis = ImageAnalysis.Builder()
//            .setTargetResolution(Size(640, 480))
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //Latest frame is shown
//            .build()
//        val executor: Executor = Executors.newSingleThreadExecutor()
//        imageAnalysis.setAnalyzer(executor) { imageProxy ->
//            try {
//                Thread.sleep(0) //Camera preview refreshed every 10 millisec(adjust as required)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            var image: InputImage? = null
//
//            @SuppressLint(
//                "UnsafeExperimentalUsageError",
//                "UnsafeOptInUsageError"
//            ) val mediaImage// Camera Feed-->Analyzer-->ImageProxy-->mediaImage-->InputImage(needed for ML kit face detection)
//                    = imageProxy.image
//            if (mediaImage != null) {
//                image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                //                    System.out.println("Rotation "+imageProxy.getImageInfo().getRotationDegrees());
//            }
//
////                System.out.println("ANALYSIS");
//
//            //Process acquired image to detect faces
//            val result = detector!!.process(
//                image!!
//            )
//                .addOnSuccessListener { faces ->
//                    if (faces.size != 0) {
//                        val face = faces[0] //Get first face from detected faces
//                        //                                                    System.out.println(face);
//
//                        //mediaImage to Bitmap
//                        val frame_bmp = toBitmap(mediaImage)
//                        val rot = imageProxy.imageInfo.rotationDegrees
//
//                        //Adjust orientation of Face
//                        val frame_bmp1 =
//                            FacerecogFragment.rotateBitmap(frame_bmp, rot, false, false)
//
//
//                        //Get bounding box of face
//                        val boundingBox = RectF(face.boundingBox)
//
//                        //Crop out bounding box from whole Bitmap(image)
//                        var cropped_face =
//                            FacerecogFragment.getCropBitmapByCPU(frame_bmp1, boundingBox)
//                        if (flipX) cropped_face =
//                            FacerecogFragment.rotateBitmap(cropped_face, 0, flipX, false)
//                        //Scale the acquired Face to 112*112 which is required input for model
//                        val scaled = getResizedBitmap(cropped_face, 112, 112)
//                        if (start) recognizeImage(scaled) //Send scaled bitmap to create face embeddings.
//                        //                                                    System.out.println(boundingBox);
//                    } else {
//                        if (registered.isEmpty()) snapshotcaptureBinding.textView.text =
//                            "Add Face" else snapshotcaptureBinding.textView.text =
//                            "No Face Detected!"
//                    }
//                }
//                .addOnFailureListener {
//                    // Task failed with an exception
//                    // ...
//                }
//                .addOnCompleteListener {
//                    imageProxy.close() //v.important to acquire next frame for analysis
//                }
//        }
//        cameraProvider.bindToLifecycle(
//            (this as LifecycleOwner),
//            cameraSelector!!,
//            imageAnalysis,
//            preview
//        )
//    }

//    private fun insertToSP(jsonMap: HashMap<String?, SimilarityClassifier.Recognition>, mode: Int) {
//        if (mode == 1) //mode: 0:save all, 1:clear all, 2:update all
//            jsonMap.clear() else if (mode == 0) jsonMap.putAll(readFromSP())
//         jsonString = Gson().toJson(jsonMap)
//        println(jsonString)
//        //        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
////        {
////            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
////        }
//        val sharedPreferences = getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("map", jsonString)
//        //System.out.println("Input josn"+jsonString.toString());
//        editor.apply()
//        Toast.makeText(this, "Recognitions Saved", Toast.LENGTH_SHORT).show()
//    }


//    private fun readFromSP(): HashMap<String?, SimilarityClassifier.Recognition> {
//        val sharedPreferences = getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
//        val defValue = Gson().toJson(HashMap<String, SimilarityClassifier.Recognition>())
//        val json = sharedPreferences.getString("map", defValue)
//         System.out.println("Output json"+json.toString());
//        val token: TypeToken<HashMap<String?, SimilarityClassifier.Recognition?>?> =
//            object : TypeToken<HashMap<String?, SimilarityClassifier.Recognition?>?>() {}
//        val retrievedMap =
//            Gson().fromJson<HashMap<String?, SimilarityClassifier.Recognition>>(json, token.type)
//        // System.out.println("Output map"+retrievedMap.toString());
//
//        //During type conversion and save/load procedure,format changes(eg float converted to double).
//        //So embeddings need to be extracted from it in required format(eg.double to float).
//        for ((_, value) in retrievedMap) {
//            val output = Array(1) { FloatArray(OUTPUT_SIZE) }
//            var arrayList = value.extra as ArrayList<*>
//            arrayList = arrayList[0] as ArrayList<*>
//            for (counter in arrayList.indices) {
//                output[0][counter] = (arrayList[counter] as Double).toFloat()
//            }
//            value.extra = output
//
//            //System.out.println("Entry output "+entry.getKey()+" "+entry.getValue().getExtra() );
//        }
//        //        System.out.println("OUTPUT"+ Arrays.deepToString(outut));
//        Toast.makeText(this, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
//        return retrievedMap
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == FacerecogFragment.MY_CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
//            }
//        }
//    }


//
//    @Throws(IOException::class)
//    private fun getBitmapFromUri(uri: Uri?): Bitmap {
//        val parcelFileDescriptor = contentResolver.openFileDescriptor(
//            uri!!, "r"
//        )
//        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
//        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
//        parcelFileDescriptor.close()
//        return image
//    }


//    private fun findNearest(emb: FloatArray): List<Pair<String?, Float>?> {
//        val neighbour_list: MutableList<Pair<String?, Float>?> = ArrayList()
//        var ret: Pair<String?, Float>? = null //to get closest match
//        var prev_ret: Pair<String?, Float>? = null //to get second closest match
//        for ((name, value) in registered) {
//            val knownEmb = (value.extra as Array<FloatArray>)[0]
//            var distance = 0f
//            for (i in emb.indices) {
//                val diff = emb[i] - knownEmb[i]
//                distance += diff * diff
//            }
//            distance = Math.sqrt(distance.toDouble()).toFloat()
//            if (ret == null || distance < ret.second) {
//                prev_ret = ret
//                ret = Pair(name, distance)
//            }
//        }
//        if (prev_ret == null) prev_ret = ret
//        neighbour_list.add(ret)
//        neighbour_list.add(prev_ret)
//        return neighbour_list
//    }


//    private fun addFace() {
//        run {
//            start = false
//            //Create and Initialize new object with Face embeddings and Name.
//            val result = SimilarityClassifier.Recognition(
//                "0", "", -1f
//            )
//            result.extra = embeedings
//            registered[username] = result
//            start = true
//            insertToSP(registered, 0)
//
//            picuploadToServer()
//
//
//
////            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
////            builder.setTitle("Enter Name")
////
////            // Set up the input
////            val input = EditText(this)
////            input.inputType = InputType.TYPE_CLASS_TEXT
////            builder.setView(input)
////
////            // Set up the buttons
////            builder.setPositiveButton("ADD") { dialog, which -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();
////
////                //Create and Initialize new object with Face embeddings and Name.
////                val result = SimilarityClassifier.Recognition(
////                    "0", "", -1f
////                )
////                result.extra = embeedings
////                registered[input.text.toString()] = result
////                start = true
////                insertToSP(registered, 0)
////                val intent = Intent(this, MainActivity::class.java)
////                startActivity(intent)
////                finish()
////                dialog.cancel()
////
////            }
////            builder.setNegativeButton("Cancel") { dialog, which ->
////                start = true
////                dialog.cancel()
////            }
////            builder.show()
//        }
//    }


//    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
//        val width = bm.width
//        val height = bm.height
//        val scaleWidth = newWidth.toFloat() / width
//        val scaleHeight = newHeight.toFloat() / height
//        // CREATE A MATRIX FOR THE MANIPULATION
//        val matrix = Matrix()
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight)
//
//        // "RECREATE" THE NEW BITMAP
//        val resizedBitmap = Bitmap.createBitmap(
//            bm, 0, 0, width, height, matrix, false
//        )
//        bm.recycle()
//        return resizedBitmap
//    }


//    private fun toBitmap(image: Image?): Bitmap {
//        val nv21 = FacerecogFragment.YUV_420_888toNV21(image)
//        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image!!.width, image.height, null)
//        val out = ByteArrayOutputStream()
//        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
//        val imageBytes = out.toByteArray()
//        //System.out.println("bytes"+ Arrays.toString(imageBytes));
//
//        //System.out.println("FORMAT"+image.getFormat());
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            //Image Uri will not be null for RESULT_OK
//            val fileUri = data!!.data
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
//        }
//
////        if (requestCode == FacerecogFragment.SELECT_PICTURE) {
////            val selectedImageUri = data!!.data
////            try {
////                val impphoto = InputImage.fromBitmap(getBitmapFromUri(selectedImageUri), 0)
////                detector!!.process(impphoto).addOnSuccessListener { faces ->
////                    if (faces.size != 0) {
////
////                        val face = faces[0]
////                        //                                System.out.println(face);
////
////                        //write code to recreate bitmap from source
////                        //Write code to show bitmap to canvas
////                        var frame_bmp: Bitmap? = null
////                        try {
////                            frame_bmp = getBitmapFromUri(selectedImageUri)
////                        } catch (e: IOException) {
////                            e.printStackTrace()
////                        }
////                        val frame_bmp1 = FacerecogFragment.rotateBitmap(frame_bmp, 0, flipX, false)
////
////                        //face_preview.setImageBitmap(frame_bmp1);
////                        val boundingBox = RectF(face.boundingBox)
////                        val cropped_face =
////                            FacerecogFragment.getCropBitmapByCPU(frame_bmp1, boundingBox)
////                        val scaled = getResizedBitmap(cropped_face, 112, 112)
////                        recognizeImage(scaled)
////                        addFace()
////                        //                                System.out.println(boundingBox);
////                        try {
////                            Thread.sleep(100)
////                        } catch (e: InterruptedException) {
////                            e.printStackTrace()
////                        }
////                    }
////                }.addOnFailureListener {
////                    start = true
////                    Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show()
////                }
////                snapshotcaptureBinding.imageView.setImageBitmap(getBitmapFromUri(selectedImageUri))
////            } catch (e: IOException) {
////                e.printStackTrace()
////            }
////
////        }
//    }


    private fun picuploadToServer(pathFromUri: String) {

        if (CheckConnectivity.getInstance(this).isOnline) {
            var bmp: Bitmap? = null
            var bos: ByteArrayOutputStream? = null
            var bt: ByteArray? = null
            var encodeString: String? = null
            try {
                bmp = BitmapFactory.decodeFile(pathFromUri)
                bos = ByteArrayOutputStream()
                val exif = ExifInterface(pathFromUri)
                val orientation: Int = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
                    else -> {}
                }
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true)

                bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos)
                bt = bos.toByteArray()
                encodeString = Base64.encodeToString(bt, Base64.DEFAULT)
                Log.d(TAG, "base64-->"+encodeString)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            frsViewModel.uploadfrs(
                authtoken = "Bearer " + sessionManager?.getToken(),
                FRSuploadRequest(image = encodeString)
            ).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            hideProgressDialog()
                            if (resource.data?.status == true) {
                                Toast.makeText(this, resource.data.message, Toast.LENGTH_SHORT).show()
//                                registered.clear()
//                                insertToSP(registered, 1)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {

                                val builder = AlertDialog.Builder(this)
                                builder.setMessage(resource.data?.message)
                                builder.setPositiveButton(
                                    "Ok"
                                ) { dialog, which ->

                                    dialog.cancel()

                                }
                                val alert = builder.create()
                                alert.show()

                            }


                        }
                        Status.ERROR -> {
                            hideProgressDialog()
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage(it.message)
                            builder.setPositiveButton(
                                "Ok"
                            ) { dialog, which ->

                                dialog.cancel()

                            }
                            val alert = builder.create()
                            alert.show()
//                        Toast.makeText(mainActivity, it.message, Toast.LENGTH_SHORT).show()

                        }

                        Status.LOADING -> {
                            showProgressDialog()
                        }

                    }

                }
            }
        }else{
            Toast.makeText(this, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }


    }


    private fun getOutputDirectory(): File {
        val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this.filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

//    fun recognizeImage(bitmap: Bitmap) {
//
//        // set Face to Preview
//        snapshotcaptureBinding.imageView.setImageBitmap(bitmap)
//
//        //Create ByteBuffer to store normalized image
//        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
//        imgData.order(ByteOrder.nativeOrder())
//        intValues = IntArray(inputSize * inputSize)
//
//        //get pixel values from Bitmap to normalize
//        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
//        imgData.rewind()
//        for (i in 0 until inputSize) {
//            for (j in 0 until inputSize) {
//                val pixelValue = intValues[i * inputSize + j]
//                if (isModelQuantized) {
//                    // Quantized model
//                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
//                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
//                    imgData.put((pixelValue and 0xFF).toByte())
//                } else { // Float model
//                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
//                }
//            }
//        }
//        //imgData is input to our model
//        val inputArray = arrayOf<Any>(imgData)
//        val outputMap: MutableMap<Int, Any> = HashMap()
//        embeedings =
//            Array(1) { FloatArray(OUTPUT_SIZE) } //output of model will be stored in this variable
//        outputMap[0] = embeedings
//        tfLite!!.runForMultipleInputsOutputs(inputArray, outputMap) //Run model
//        var distance_local = Float.MAX_VALUE
//        val id = "0"
//        val label = "?"
//
//        //Compare new face with saved Faces.
//        if (registered.size > 0) {
//            val nearest = findNearest(embeedings[0]) //Find 2 closest matching face
//            if (nearest[0] != null) {
//                val name = nearest[0]!!.first //get name and distance of closest matching face
//                // label = name;
//                distance_local = nearest[0]!!.second
//
//                if (distance_local < distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                    snapshotcaptureBinding.textView.text =
//                        name else snapshotcaptureBinding.textView.text = "Unknown"
//                //                    System.out.println("nearest: " + name + " - distance: " + distance_local);
//
//            }
//        }
//
//
////            final int numDetectionsOutput = 1;
////            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
////            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
////                    id,
////                    label,
////                    distance);
////
////            recognitions.add( rec );
//    }


//    companion object {
//        private const val SELECT_PICTURE = 1
//        const val MY_CAMERA_REQUEST_CODE = 100
//        fun getCropBitmapByCPU(source: Bitmap?, cropRectF: RectF): Bitmap {
//            val resultBitmap = Bitmap.createBitmap(
//                cropRectF.width().toInt(),
//                cropRectF.height().toInt(),
//                Bitmap.Config.ARGB_8888
//            )
//            val cavas = Canvas(resultBitmap)
//
//            // draw background
//            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
//            paint.color = Color.WHITE
//            cavas.drawRect(
//                RectF(0F, 0F, cropRectF.width(), cropRectF.height()),
//                paint
//            )
//            val matrix = Matrix()
//            matrix.postTranslate(-cropRectF.left, -cropRectF.top)
//            cavas.drawBitmap(source!!, matrix, paint)
//            if (source != null && !source.isRecycled) {
//                source.recycle()
//            }
//            return resultBitmap
//        }
//
//        fun rotateBitmap(
//            bitmap: Bitmap?, rotationDegrees: Int, flipX: Boolean, flipY: Boolean
//        ): Bitmap {
//            val matrix = Matrix()
//
//            // Rotate the image back to straight.
//            matrix.postRotate(rotationDegrees.toFloat())
//
//            // Mirror the image along the X or Y axis.
//            matrix.postScale(if (flipX) -1.0f else 1.0f, if (flipY) -1.0f else 1.0f)
//            val rotatedBitmap =
//                Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
//
//            // Recycle the old bitmap if it has changed.
//            if (rotatedBitmap != bitmap) {
//                bitmap.recycle()
//            }
//            return rotatedBitmap
//        }
//
//        //IMPORTANT. If conversion not done ,the toBitmap conversion does not work on some devices.
//        fun YUV_420_888toNV21(image: Image?): ByteArray {
//            val width = image!!.width
//            val height = image.height
//            val ySize = width * height
//            val uvSize = width * height / 4
//            val nv21 = ByteArray(ySize + uvSize * 2)
//            val yBuffer = image.planes[0].buffer // Y
//            val uBuffer = image.planes[1].buffer // U
//            val vBuffer = image.planes[2].buffer // V
//            var rowStride = image.planes[0].rowStride
//            assert(image.planes[0].pixelStride == 1)
//            var pos = 0
//            if (rowStride == width) { // likely
//                yBuffer[nv21, 0, ySize]
//                pos += ySize
//            } else {
//                var yBufferPos = -rowStride.toLong() // not an actual position
//                while (pos < ySize) {
//                    yBufferPos += rowStride.toLong()
//                    yBuffer.position(yBufferPos.toInt())
//                    yBuffer[nv21, pos, width]
//                    pos += width
//                }
//            }
//            rowStride = image.planes[2].rowStride
//            val pixelStride = image.planes[2].pixelStride
//            assert(rowStride == image.planes[1].rowStride)
//            assert(pixelStride == image.planes[1].pixelStride)
//            if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
//                // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
//                val savePixel = vBuffer[1]
//                try {
//                    vBuffer.put(1, savePixel.inv().toByte())
//                    if (uBuffer[0] == savePixel.inv().toByte()) {
//                        vBuffer.put(1, savePixel)
//                        vBuffer.position(0)
//                        uBuffer.position(0)
//                        vBuffer[nv21, ySize, 1]
//                        uBuffer[nv21, ySize + 1, uBuffer.remaining()]
//                        return nv21 // shortcut
//                    }
//                } catch (ex: ReadOnlyBufferException) {
//                    // unfortunately, we cannot check if vBuffer and uBuffer overlap
//                }
//
//                // unfortunately, the check failed. We must save U and V pixel by pixel
//                vBuffer.put(1, savePixel)
//            }
//
//            // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
//            // but performance gain would be less significant
//            for (row in 0 until height / 2) {
//                for (col in 0 until width / 2) {
//                    val vuPos = col * pixelStride + row * rowStride
//                    nv21[pos++] = vBuffer[vuPos]
//                    nv21[pos++] = uBuffer[vuPos]
//                }
//            }
//            return nv21
//        }
//    }



    var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
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