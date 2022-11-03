package com.app.hcsassist.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.app.hcsassist.Login
import com.app.hcsassist.MainActivity
import com.app.hcsassist.R
import com.app.hcsassist.databinding.FragmentFacerecogBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import java.nio.ReadOnlyBufferException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.experimental.inv

class FacerecogFragment : Fragment() {
    lateinit var fragmentFacerecogBinding: FragmentFacerecogBinding
    lateinit var mainActivity: MainActivity
    var previewView: PreviewView? = null
    var keyItem = ""
    var cameraSelector: CameraSelector? = null
    var cam_face = CameraSelector.LENS_FACING_FRONT //Default Back Camera
    var cameraProvider: ProcessCameraProvider? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity()?.getWindow()?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFacerecogBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_facerecog,container,false)
        val root = fragmentFacerecogBinding.root
        mainActivity=activity as MainActivity

        fragmentFacerecogBinding.btnBack.setOnClickListener {

            mainActivity.onBackPressed()
        }

        cameraBind()

        val secondsDelayed = 1
        Handler().postDelayed({


            val navController = view?.let { Navigation.findNavController(it) }
            navController?.navigate(R.id.nav_home)


        }, (secondsDelayed * 5000).toLong())


        return root
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
//                        if (registered.isEmpty()) reco_name!!.text =
//                            "Add Face" else reco_name!!.text = "No Face Detected!"
//                    }
//                }
//                .addOnFailureListener {
//                    // Task failed with an exception
//                    // ...
//                }
//                .addOnCompleteListener {
//                    imageProxy.close() //v.important to acquire next frame for analysis
//                }
        }
        cameraProvider.bindToLifecycle(
            (this as LifecycleOwner),
            cameraSelector!!,
            imageAnalysis,
            preview
        )
    }






}