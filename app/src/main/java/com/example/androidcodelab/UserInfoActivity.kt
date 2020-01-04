package com.example.androidcodelab

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.androidcodelab.network.Api
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        take_picture_button.setOnClickListener{
            askCameraPermissionAndOpenCamera()
        }


    }

    private fun askCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // l'OS dit d'expliquer pourquoi on a besoin de cette permission:
                showDialogBeforeRequest()
            } else {
                // l'OS ne demande pas d'explication, on demande directement:
                requestCameraPermission()
            }
        } else {
            openCamera()
        }
    }

    private fun showDialogBeforeRequest() {
        // Affiche une popup (Dialog) d'explications:
        AlertDialog.Builder(this).apply {
            setMessage("On a besoin de la caméra sivouplé ! 🥺")
            setPositiveButton(android.R.string.ok) { _, _ -> requestCameraPermission() }
            setCancelable(true)
            show()
        }
    }

    private fun requestCameraPermission() {
        // CAMERA_PERMISSION_CODE est défini par nous et sera récupéré dans onRequestPermissionsResult
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Si vous refusez, on peux pas prendre de photo ! 😢",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openCamera() {
        // On va utiliser un Intent implicite
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                super.onActivityResult(requestCode, resultCode, data)
                handlePhotoTaken(data)
            }else{
                Toast.makeText(
                    this,
                    "Vous n'avez pas le droit d'etre la !",
                    Toast.LENGTH_LONG
                ).show()
            }
        }else{
            Toast.makeText(
                this,
                "Vouuus ne passerez paaaaas ! Je suis gandalf",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handlePhotoTaken(data: Intent?) {
        val image = data?.extras?.get("data") as? Bitmap
        // Afficher l'image ici
        val imageBody = imageToBody(image)
        val glide = Glide.with(this)
        Glide
            .with(this)
            .load(image)
            .into(image_view)
        // Plus tard, on l'enverra au serveur
        lifecycleScope.launch{
            val upload = Api.userService.updateAvatar(imageBody)
            glide.load(image).into(image_view)
        }
    }

    // Celle ci n'est pas très intéressante à lire
// En gros, elle lit le fichier et le prépare pour l'envoi HTTP
    private fun imageToBody(image: Bitmap?): MultipartBody.Part {
        val f = File(cacheDir, "tmpfile.jpg")
        f.createNewFile()
        try {
            val fos = FileOutputStream(f)
            image?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val body = RequestBody.create(MediaType.parse("image/png"), f)
        return MultipartBody.Part.createFormData("avatar", f.path, body)
    }

    companion object {
        const val CAMERA_PERMISSION_CODE = 42
        const val CAMERA_REQUEST_CODE = 2001

    }

}
