package com.example.artsavvy.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artsavvy.ui.components.TopBar
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeWriter


class QRCode {

    companion object{
        private fun generateQRCode(text: String): Bitmap? {
            val writer = QRCodeWriter()
            return try {
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                    }
                }
                bmp
            } catch (e: WriterException) {
                e.printStackTrace()
                null
            }
        }


        @Composable
        fun QRCodeScanner(navController: NavController) {
            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data: Intent? = result.data
                    val contents = data?.getStringExtra("SCAN_RESULT")
                    contents?.let {
                        navController.popBackStack()
                        navController.navigate("art_details/${it}")
                    }
                }
            }

            LaunchedEffect(Unit) {
                val integrator = IntentIntegrator(context as Activity)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setPrompt("Scan QR code")
                integrator.setOrientationLocked(false)
                val intent = integrator.createScanIntent()
                launcher.launch(intent)
            }
        }


        @Composable
        fun QRCodeScreen(artId: String, navController: NavController) {
            val qrCodeBitmap = remember(artId) {
                generateQRCode(artId)
            }

            Scaffold(
                topBar = { TopBar(routeName = "QRCode da Obra", navController = navController, null, { /**/ })}
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    qrCodeBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(300.dp)
                        )
                    }
                }
            }
        }




    }
}