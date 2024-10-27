package com.example.madk7_8

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var editTextUrl: EditText
    lateinit var buttonDownload: Button
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUrl = findViewById(R.id.editTextUrl)
        buttonDownload = findViewById(R.id.buttonDownload)
        imageView = findViewById(R.id.imageView)

        buttonDownload.setOnClickListener {
            val url = editTextUrl.text.toString()
            if (url.isNotBlank()) {
                downloadAndSaveImage(url)
            } else {
                Toast.makeText(this, "Enter the image URL", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun downloadAndSaveImage(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    Picasso.get().load(url).get()
                }
                imageView.setImageBitmap(bitmap)

                saveImageToStorage(bitmap)
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveImageToStorage(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(storageDir, fileName)
            try {
                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Image saved: $fileName", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error saving image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}