package com.basebox.smartvendor.ui.screens.camera

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.ERROR_CAPTURE_FAILED
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID


@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val cameraPermission = android.Manifest.permission.CAMERA
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(cameraPermission)
    }

    if (hasPermission) {
        CameraCapture(onPhotoCaptured, onClose)
    }
}

@Composable
fun CameraCapture(
    onPhotoCaptured: (Uri) -> Unit,
    onClose: () -> Unit
) {
    val cameraPermission = android.Manifest.permission.CAMERA
    val context = LocalContext.current
    val cameraController = remember { LifecycleCameraController(context) }
    cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
    val imageFile = remember {
        File(context.cacheDir, "receipt-${UUID.randomUUID()}.jpg")
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val executor = ContextCompat.getMainExecutor(context)
    LaunchedEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }
    AndroidView(
        factory = {
            PreviewView(it).apply {
                controller = cameraController
            }
        },
        modifier = Modifier.fillMaxSize()
    )


    val output = ImageCapture.OutputFileOptions.Builder(
        imageFile
    ).build()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
            Text("Cancel")

        }

        Button(onClick = {

            cameraController.takePicture(
                output,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(result: ImageCapture.OutputFileResults) {
//                        val uri = FileProvider.getUriForFile(
//                            context,
//                            "${context.packageName}.provider",
//                            imageFile
//                        )
                        onPhotoCaptured(result.savedUri!!)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CAM", "Capture Error", exception)
                    }
                }
            )
        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
            Text("Capture")
        }
    }
//    cameraController.takePicture(
//        output, executor,
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
//                onPhotoCaptured(result.savedUri!!)
//            }
//
//            override fun onError(e: ImageCaptureException) {
//                Log.e("CAM", "Capture failed", e)
//            }
//        }
//    )
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success ->
//        if (success) {
//            onPhotoCaptured(uri)
//        } else {
//            onError(ImageCaptureException(
//                ERROR_CAPTURE_FAILED
//                ,"Photo capture failed",
//                null)
//            )
//        }
//    }

//    // Launch camera immediately
//    LaunchedEffect(Unit) {
//        launcher.launch(uri)
//    }
}


