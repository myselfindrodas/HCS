package com.app.hcsassist.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.apimodel.FRSRequest
import com.app.hcsassist.apimodel.PunchinRequest
import com.app.hcsassist.apimodel.PunchoutRequest
import com.app.hcsassist.databinding.FragmentFacerecogBinding
import com.app.hcsassist.modelfactory.PunchinModelFactory
import com.app.hcsassist.modelfactory.RecognizationModelFactory
import com.app.hcsassist.retrofit.ApiClient
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.retrofit.FaceRecogizationApiHelper
import com.app.hcsassist.utils.GetRealPathFromUri
import com.app.hcsassist.utils.Status
import com.app.hcsassist.viewmodel.PunchingViewModel
import com.app.hcsassist.viewmodel.RecognizationViewModel
import com.example.cameraxexample.Constants.FILENAME_FORMAT
import com.example.wemu.internet.CheckConnectivity
import com.example.wemu.session.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_facerecog.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FacerecogFragment : Fragment() {
    lateinit var fragmentFacerecogBinding: FragmentFacerecogBinding
    lateinit var mainActivity: MainActivity
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
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
    lateinit var punchingViewModel: PunchingViewModel
    lateinit var recognizationViewModel: RecognizationViewModel
    var sessionManager: SessionManager? = null
    var formatted_address: String? = ""
    var punch_out_location: String? = ""
    var name: String? = ""
    var REQUEST_CODE = 101
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationManager: LocationManager? = null
    var latitude: String? = null
    var longitude: String? = null
    var placesClient: PlacesClient? = null
    var shiftid: String? = ""



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

        val rocognizationvm: RecognizationViewModel by viewModels {
            RecognizationModelFactory(FaceRecogizationApiHelper(ApiClient.apiService2))
        }


        punchingViewModel = vm
        recognizationViewModel = rocognizationvm





        val bundle = this.arguments
        if (bundle != null) {
            formatted_address = bundle.getString("punch_in_location").toString()
        } else {
            formatted_address = ""
        }

        if (bundle != null) {
            punch_out_location = bundle.getString("punch_out_location").toString()
            println("PUNCH OUT $punch_out_location")
        } else {
            punch_out_location = ""
        }

        if (bundle != null) {
            shiftid = bundle.getString("shiftid").toString()
        } else {
            shiftid = ""
        }


        if (!Places.isInitialized()) {
            Places.initialize(mainActivity, getString(R.string.api_key))
        }
        placesClient = Places.createClient(mainActivity)
        locationManager =
            mainActivity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        if (!locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || !locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
        ) {

            OnGPS()

        } else {
            getLocation()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
//        ImagePicker.with(this)
//            .cameraOnly()    //User can only capture image using Camera
//            .start()

//        val sharedPref =
//            mainActivity.getSharedPreferences("Distance", AppCompatActivity.MODE_PRIVATE)
//        distance = sharedPref.getFloat("distance", 1.00f)
//        registered = readFromSP() //Load saved faces from memory when app starts
//        fragmentFacerecogBinding.textAbovePreview.text = "Recognized Face:"
//        fragmentFacerecogBinding.tvSuccessfully.visibility = View.GONE

        fragmentFacerecogBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        fragmentFacerecogBinding.btnAddface.setOnClickListener {

            takePhoto()

//            fragmentFacerecogBinding.tvSuccessfully.visibility = View.VISIBLE
//            fragmentFacerecogBinding.textView.text = name
//            fragmentFacerecogBinding.tvEmpid.text = "Emp Id : " + sessionManager?.getempcode()
        }

//        //Load model
//        try {
//            tfLite = Interpreter(loadModelFile(mainActivity, modelFile))
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        //Initialize Face Detector
//        val highAccuracyOpts = FaceDetectorOptions.Builder()
//            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//            .build()
//        detector = FaceDetection.getClient(highAccuracyOpts)


        return root
    }

    lateinit var onViewCreated: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreated = view
        val bundle = this.arguments
        if (bundle != null) {
            punch_out_location = bundle.getString("punch_out_location").toString()
        } else {
            punch_out_location = ""
        }

//        fragmentFacerecogBinding.cameraSwitch.setOnClickListener {
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
//
//        cameraBind()

    }

    private fun OnGPS() {
        val builder = android.app.AlertDialog.Builder(mainActivity)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton(
            "Yes"
        ) { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(
                mainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        } else {
            val locationGPS =
                locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (locationGPS != null) {
                val lat = locationGPS.latitude
                val longi = locationGPS.longitude
                latitude = lat.toString()
                longitude = longi.toString()
                sessionManager?.setlogoutLat(latitude)
                sessionManager?.setlogoutLong(longitude)
//                reverseGeocoding(latitude.toString(), longitude.toString())
                Log.d(ContentValues.TAG, "Logoutlatilong-->" + latitude + " , " + longitude)

            } else {
                Toast.makeText(mainActivity, "Unable to find location.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)

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

        }, ContextCompat.getMainExecutor(mainActivity))
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mainActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    picuploadToServer(GetRealPathFromUri.getPathFromUri(mainActivity, savedUri!!)!!)
                    val msg = "Photo capture succeeded: $savedUri"
//                    Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }


    private fun picuploadToServer(pathFromUri: String){


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




        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            recognizationViewModel.face_compare(
                FRSRequest(snapshot_image = sessionManager?.getsnapShot(),
                    verification_image = encodeString,
                    employee_id = sessionManager?.getempcode()))
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                if (resource.data?.status == true) {
                                    if (resource.data?.verified==true){

                                        fragmentFacerecogBinding.tvSuccessfully.visibility = View.VISIBLE
                                        fragmentFacerecogBinding.tvSuccessfully.text = "Verification Successful"
                                        fragmentFacerecogBinding.textView.text = name
                                        fragmentFacerecogBinding.tvSuccessfully.setTextColor(resources.getColor(R.color.green,resources.newTheme()))
                                        fragmentFacerecogBinding.tvSuccessfully.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);

                                        fragmentFacerecogBinding.tvEmpid.text = "Emp Id : " + sessionManager?.getempcode()

                                        val secondsDelayed = 1
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            if (sessionManager?.getPunchin().equals("punchin")) {
                                                punchoutattendance(fragmentFacerecogBinding.root.rootView)
                                            } else {
                                                punchinattendance(fragmentFacerecogBinding.root.rootView)
                                            }
                                        }, (secondsDelayed * 3000).toLong())

                                    }else{

                                        fragmentFacerecogBinding.tvSuccessfully.visibility = View.VISIBLE
                                        fragmentFacerecogBinding.tvSuccessfully.text = "Face Not Matched!"
                                        fragmentFacerecogBinding.tvSuccessfully.setTextColor(resources.getColor(R.color.red,resources.newTheme()))
                                        fragmentFacerecogBinding.tvSuccessfully.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);




                                    }

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

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }




    }




    private fun getOutputDirectory(): File {
        val mediaDir = mainActivity.externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else mainActivity.filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

//    private fun reverseGeocoding(lat: String, long: String) {
//        if (CheckConnectivity.getInstance(mainActivity).isOnline) {
//            val jsonRequest: JsonObjectRequest = object : JsonObjectRequest(
//                Request.Method.GET,
//                "https://maps.googleapis.com/maps/api/geocode/json?" +
//                        "latlng=" + lat + "," + long + "&key=AIzaSyCGRQavtVfIlnBuSkELe98R2MFjXQdnLRc",
//                null,
//                Response.Listener { response: JSONObject ->
//                    Log.i("reverseGeoResponse-->", response.toString())
//                    try {
//                        val result = JSONObject(response.toString())
//                        val status = result.getString("status")
//                        val responseArray = result.getJSONArray("results")
//                        if (status == "OK") {
//                            for (i in 0 until responseArray.length()) {
//                                val resultsobj = responseArray.getJSONObject(4)
//                                punch_out_location = resultsobj.getString("formatted_address")
//                                Log.d(TAG, "punchoutlocation-->"+punch_out_location)
//
//
//                            }
//
//                        } else {
//                            hideProgressDialog()
//                            Toast.makeText(mainActivity, "invalid", Toast.LENGTH_SHORT).show()
//                        }
//
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                },
//                Response.ErrorListener { error ->
//                    val statusCode: Int = error.networkResponse.statusCode
//                    Log.e(ContentValues.TAG, "statuscode-->" + statusCode)
//                    hideProgressDialog()
//
//                }) {
//            }
//            Volley.newRequestQueue(mainActivity).add(jsonRequest)
//        } else {
//            Toast.makeText(
//                mainActivity,
//                "Ooops! Internet Connection Error",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }


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

    //Bind camera and preview view
//    private fun cameraBind() {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
//        previewView = fragmentFacerecogBinding.previewView
//        cameraProviderFuture!!.addListener({
//            try {
//                cameraProvider = cameraProviderFuture!!.get()
//                bindPreview(cameraProvider!!)
//            } catch (e: ExecutionException) {
//                // No errors need to be handled for this in Future.
//                // This should never be reached.
//            } catch (e: InterruptedException) {
//            }
//
//        }, ContextCompat.getMainExecutor(mainActivity))
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
//                Thread.sleep(10) //Camera preview refreshed every 10 millisec(adjust as required)
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
//                        val frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false)
//
//
//                        //Get bounding box of face
//                        val boundingBox = RectF(face.boundingBox)
//
//                        //Crop out bounding box from whole Bitmap(image)
//                        var cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
//                        if (flipX) cropped_face = rotateBitmap(cropped_face, 0, flipX, false)
//                        //Scale the acquired Face to 112*112 which is required input for model
//                        val scaled = getResizedBitmap(cropped_face, 112, 112)
//                        if (start) recognizeImage(scaled) //Send scaled bitmap to create face embeddings.
//                        //                                                    System.out.println(boundingBox);
//                    } else {
//                        if (registered.isEmpty()) fragmentFacerecogBinding.textView.text =
//                            "Add Face" else fragmentFacerecogBinding.textView.text =
//                            "No Face Detected!"
//                        fragmentFacerecogBinding.tvSuccessfully.visibility = View.GONE
//
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
//        val jsonString = Gson().toJson(jsonMap)
//        //        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : jsonMap.entrySet())
////        {
////            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
////        }
//        val sharedPreferences =
//            mainActivity.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("map", jsonString)
//        //System.out.println("Input josn"+jsonString.toString());
//        editor.apply()
//        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show()
//    }

//    private fun readFromSP(): HashMap<String?, SimilarityClassifier.Recognition> {
////        val sharedPreferences =
////            mainActivity.getSharedPreferences("HashMap", AppCompatActivity.MODE_PRIVATE)
////        val defValue = Gson().toJson(HashMap<String, SimilarityClassifier.Recognition>())
//////        val json = sharedPreferences.getString("map", defValue)
//        val json = sessionManager?.getsnapShot()
//        // System.out.println("Output json"+json.toString());
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
//        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
//        return retrievedMap
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == MY_CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(mainActivity, "camera permission granted", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(mainActivity, "camera permission denied", Toast.LENGTH_LONG).show()
//            }
//        }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            //Image Uri will not be null for RESULT_OK
//            val fileUri = data!!.data
////            fragmentFacerecogBinding.llrecognization.visibility = View.VISIBLE
////            fragmentFacerecogBinding.cameraview.setImageURI(fileUri)
//
//        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            Toast.makeText(mainActivity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(mainActivity, "Task Cancelled", Toast.LENGTH_SHORT).show()
//        }
//    }

//    //Similar Analyzing Procedure
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == AppCompatActivity.RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                val selectedImageUri = data!!.data
//                try {
//                    val impphoto = InputImage.fromBitmap(getBitmapFromUri(selectedImageUri), 0)
//                    detector!!.process(impphoto).addOnSuccessListener { faces ->
//                        if (faces.size != 0) {
//
//
////                            fragmentFacerecogBinding.btnaddface.text = "Recognize"
////                            fragmentFacerecogBinding.imageButton.visibility = View.VISIBLE
//                            fragmentFacerecogBinding.textView.visibility = View.INVISIBLE
////                            fragmentFacerecogBinding.imageView.visibility = View.VISIBLE
////                            fragmentFacerecogBinding.textView2.text =
////                                "1.Bring Face in view of Camera.\n\n2.Your Face preview will appear here.\n\n3.Click Add button to save face."
//                            val face = faces[0]
//                            //                                System.out.println(face);
//
//                            //write code to recreate bitmap from source
//                            //Write code to show bitmap to canvas
//                            var frame_bmp: Bitmap? = null
//                            try {
//                                frame_bmp = getBitmapFromUri(selectedImageUri)
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                            val frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false)
//
//                            //face_preview.setImageBitmap(frame_bmp1);
//                            val boundingBox = RectF(face.boundingBox)
//                            val cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox)
//                            val scaled = getResizedBitmap(cropped_face, 112, 112)
//                            // face_preview.setImageBitmap(scaled);
//                            recognizeImage(scaled)
//                            //addFace()
//                            //                                System.out.println(boundingBox);
//                            try {
//                                Thread.sleep(100)
//                            } catch (e: InterruptedException) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }.addOnFailureListener {
//                        start = true
//                        Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
//                    }
////                    fragmentFacerecogBinding.imageView.setImageBitmap(getBitmapFromUri(selectedImageUri))
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

//    @Throws(IOException::class)
//    private fun getBitmapFromUri(uri: Uri?): Bitmap {
//        val parcelFileDescriptor = mainActivity.contentResolver.openFileDescriptor(
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
//            val builder = AlertDialog.Builder(mainActivity)
//            builder.setTitle("Enter Name")
//
//            // Set up the input
//            val input = EditText(context)
//            input.inputType = InputType.TYPE_CLASS_TEXT
//            builder.setView(input)
//
//            // Set up the buttons
//            builder.setPositiveButton("ADD") { dialog, which -> //Toast.makeText(context, input.getText().toString(), Toast.LENGTH_SHORT).show();
//
//                //Create and Initialize new object with Face embeddings and Name.
//                val result = SimilarityClassifier.Recognition(
//                    "0", "", -1f
//                )
//                result.extra = embeedings
//                registered[input.text.toString()] = result
//                start = true
//            }
//            builder.setNegativeButton("Cancel") { dialog, which ->
//                start = true
//                dialog.cancel()
//            }
//            builder.show()
//        }
//    }

//    private fun clearnameList() {
//        val builder = AlertDialog.Builder(mainActivity)
//        builder.setTitle("Do you want to delete all Recognitions?")
//        builder.setPositiveButton("Delete All") { dialog, which ->
//            registered.clear()
//            Toast.makeText(context, "Recognitions Cleared", Toast.LENGTH_SHORT).show()
//        }
////        insertToSP(registered, 1)
//        builder.setNegativeButton("Cancel", null)
//        val dialog = builder.create()
//        dialog.show()
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
//        val nv21 = YUV_420_888toNV21(image)
//        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image!!.width, image.height, null)
//        val out = ByteArrayOutputStream()
//        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
//        val imageBytes = out.toByteArray()
//        //System.out.println("bytes"+ Arrays.toString(imageBytes));
//
//        //System.out.println("FORMAT"+image.getFormat());
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//    }


//    fun recognizeImage(bitmap: Bitmap) {
//
//        // set Face to Preview
////        fragmentFacerecogBinding.imageView.setImageBitmap(bitmap)
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
//                name = nearest[0]!!.first //get name and distance of closest matching face
//                // label = name;
//                distance_local = nearest[0]!!.second
//
//                if (distance_local < distance) {
//                    fragmentFacerecogBinding.tvSuccessfully.visibility = View.VISIBLE
//                    fragmentFacerecogBinding.textView.text = name
//                    fragmentFacerecogBinding.tvEmpid.text = "Emp Id : " +sessionManager?.getempcode()
//                    detector?.close()
//                    val secondsDelayed = 1
//                    Handler(Looper.getMainLooper()).postDelayed({
//
//                        if (sessionManager?.getPunchin().equals("punchin")) {
//                            punchoutattendance(fragmentFacerecogBinding.root.rootView)
//                        } else {
//                            punchinattendance(fragmentFacerecogBinding.root.rootView)
//                        }
////                        if (punch_out_location?.isNotEmpty() == true){
////                            punchoutattendance(fragmentFacerecogBinding.root.rootView)
////                        }else{
////                            punchinattendance(fragmentFacerecogBinding.root.rootView)
////                        }
//
//                    }, (secondsDelayed * 3000).toLong())
//
//
//                    //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                } else {
//                    fragmentFacerecogBinding.textView.text = "Unknown"
//                    fragmentFacerecogBinding.tvSuccessfully.visibility = View.GONE
//
//                }
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


    private fun punchinattendance(view: View) {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            val deviceinfo = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID)

            punchingViewModel.punchin(
                authtoken = "Bearer " + sessionManager?.getToken(),
                PunchinRequest(
                    punch_in_location = formatted_address, device_type = "android",
                    imei_no = deviceinfo, device_info = Build.MODEL, shift_id = shiftid
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
//                                    registered.clear()
//                                    insertToSP(registered, 1)
                                    val navController = Navigation.findNavController(onViewCreated)
                                    navController.popBackStack(R.id.nav_home, false)
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

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }



    }


    private fun punchoutattendance(view: View) {

        if (CheckConnectivity.getInstance(mainActivity).isOnline) {

            println("PUNCH OUT   $punch_out_location")
            Log.d(TAG, "PUNCH OUT-->  " + punch_out_location)
            punchingViewModel.punchout(
                authtoken = "Bearer " + sessionManager?.getToken(),
                PunchoutRequest(punch_out_location = sessionManager?.getpunchoutLocation()),
                id = sessionManager?.getpunchinId().toString()
            )
                .observe(mainActivity) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                hideProgressDialog()
                                if (resource.data?.status == true) {
                                    sessionManager?.setPunchin("punchout")
//                                    registered.clear()
//                                    insertToSP(registered, 1)
                                    val navController = Navigation.findNavController(onViewCreated)
                                    navController.popBackStack(R.id.nav_home, false)
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

        }else{
            Toast.makeText(mainActivity, "Ooops! Internet Connection Error", Toast.LENGTH_SHORT).show()
        }





    }


//    companion object {
//        const val SELECT_PICTURE = 1
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