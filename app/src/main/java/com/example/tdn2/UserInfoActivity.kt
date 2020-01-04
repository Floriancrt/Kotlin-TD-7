package com.example.tdn2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tdn2.network.Api
import kotlinx.android.synthetic.main.user_activity.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class UserInfoActivity : AppCompatActivity() {
    private val coroutineScope = MainScope()

    companion object {
        const val CAMERA_PERMISSION_CODE = 42
        const val CAMERA_REQUEST_CODE = 2001
        const val GALLERY_PERMISSION_CODE = 43
        const val GALLERY_REQUEST_CODE = 2002
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()
        if (id == R.id.tasks) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        take_picture_button.setOnClickListener {
            askCameraPermissionAndOpenCamera()
        }

        upload_image_button.setOnClickListener {
            askStoragePermission()
        }
    }

    override fun onResume() {
        super.onResume()

        var glide = Glide.with(this)
        this.coroutineScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            name_user.text = "${userInfo.firstname} ${userInfo.lastname}"
            email_user.text = "(${userInfo.email})"
            glide.load(userInfo.avatar).circleCrop().into(avatar_view)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            PackageManager.PERMISSION_GRANTED
            openCamera()
        } else {
            Toast.makeText(
                this,
                "En refusant, vous ne pourrez pas prendre de photo",
                Toast.LENGTH_LONG
            ).show()
        }
        if (requestCode == GALLERY_PERMISSION_CODE) {
            PackageManager.PERMISSION_GRANTED
            openGallery()
        } else {
            Toast.makeText(
                this,
                "En refusant, vous ne pourrez pas choisir de photo",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
            handlePhotoTaken(data)
        }

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            handlePictureTaken(data)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }


    private fun askCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                showDialogBeforeRequest()
            } else {
                requestCameraPermission()
            }
        } else {
            openCamera()
        }
    }

    private fun askStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showDialogBeforeExternalStorageRequest()
            } else {
                requestStoragePermission()
            }
        } else {
            openGallery()
        }
    }

    private fun showDialogBeforeRequest() {
        with(AlertDialog.Builder(this)) {
            setMessage("Nous avons besoin d'accéder à votre Caméra")
            setPositiveButton(android.R.string.ok) { _, _ -> requestCameraPermission() }
            setCancelable(true)
            show()
        }
    }

    private fun showDialogBeforeExternalStorageRequest() {
        with(AlertDialog.Builder(this)) {
            setMessage("Nous avons besoin d'accéder à votre Stockage interne")
            setPositiveButton(android.R.string.ok) { _, _ -> requestStoragePermission() }
            setCancelable(true)
            show()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_CODE
        )
    }



    private fun openGallery() {
        //Intent to pick image
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }


    private fun handlePhotoTaken(data: Intent?) {
        val image = data?.extras?.get("data") as? Bitmap
        val glide = Glide.with(this)

        lifecycleScope.launch {
            imageToBody(image)?.let { imageBody ->
                val response = Api.userService.updateAvatar(imageBody).body()
                if (response != null) {
                    glide.load(image).circleCrop().into(avatar_view)
                }
            }
        }
    }

    private fun handlePictureTaken(data: Intent?) {
        val image = data?.data
        val glide = Glide.with(this)
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image)

        lifecycleScope.launch {
            imageToBody(bitmap)?.let { imageBody ->
                val response = Api.userService.updateAvatar(imageBody).body()
                if (response != null) {
                    glide.load(image).circleCrop().into(avatar_view)
                }
            }
        }
    }

    private fun imageToBody(image: Bitmap?): MultipartBody.Part? {
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


}