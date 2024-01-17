package com.example.taseenbhaiphonecleanerproject.Activtiy


import android.annotation.SuppressLint
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taseenbhaiphonecleanerproject.Adapater.ImagesAdapterClass
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.databinding.ActivityImageactivityBinding


class IMAGEActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageactivityBinding
    private lateinit var adapterCLass: ImagesAdapterClass
    private var permissionImages: String? = null
    private lateinit var lisimage:ArrayList<ImagesItem>
    companion object {
        const val REQUEST_PERMISSION_CODE = 101
    }
    val IMAGE_REQUEST_CODE = 100
    private var selecteditemsimage=ArrayList<ImagesItem>()
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S){
            permissionImages = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S){
            permissionImages = android.Manifest.permission.READ_MEDIA_IMAGES
        }

       binding.deleBtn.setOnClickListener {
           ItemsSize()
       }
        binding.imageView3.setOnClickListener {
            startActivity(Intent(applicationContext,DashBoardActivity::class.java))
        }

        if (checkPermission()) {
            getlistofIMage()
        } else {
            requestPermission()
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(permissionImages),
            REQUEST_PERMISSION_CODE
        )
    }
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissionImages!!
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getlistofIMage()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getlistofIMage(){
        lisimage= ArrayList();
        val imageProjection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN,

            MediaStore.Images.Media._ID,
            )
//        Now we need to pass this projection on query with the help of ContentResolver.
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val imagedata = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getLong(sizeColumn)
                    val date = it.getString(dateColumn)
                    val img = it.getString(imagedata)
                    val sizeformat=formatFileSize(size)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    Log.d("ImageInfo", "ID: $id, Name: $name, Size: $size, Date: $date, Path: $img")
                    lisimage.add(ImagesItem(id,img,contentUri,sizeformat,name,date,false))
                }
                adapterCLass= ImagesAdapterClass(lisimage,applicationContext)

                binding.deleRecycleView.adapter = adapterCLass
                binding.deleRecycleView.layoutManager = LinearLayoutManager(this)
            }
        }


    }
    fun formatFileSize(sizeInBytes:Long) : String {
        val kilobyte = 1024
        val megabyte = kilobyte * 1024

        return when {
            sizeInBytes > megabyte -> String.format("%.2f MB", sizeInBytes.toFloat() / megabyte)
            sizeInBytes > kilobyte -> String.format("%.2f KB", sizeInBytes.toFloat() / kilobyte)
            else -> String.format("%d B", sizeInBytes)
        }
    }
    fun ItemsSize(){
        selecteditemsimage= lisimage.filter { it.isSelected } as ArrayList<ImagesItem>
        if (selecteditemsimage.isEmpty()){
            Toast.makeText(applicationContext,"Please Select Images",Toast.LENGTH_SHORT).show()
        }else{
            deleteImageaboveAPI29(this)
        }
    }
    fun deleteImageaboveAPI29(context: Context) {
        val resolver = context.contentResolver
        for (items in selecteditemsimage) {
            try {
                resolver.delete(items.imageuri!!, null, null)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as?
                            RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                    val intentSender =
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    intentSender?.let {
                        startIntentSenderForResult(
                            intentSender, IMAGE_REQUEST_CODE,
                            null, 0, 0, 0, null
                        )
                    }
                } else {
                    throw RuntimeException(securityException.message, securityException)
                }

            }
        }

    }
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)

                if (requestCode == IMAGE_REQUEST_CODE) {
                    if (resultCode == Activity.RESULT_OK) {
                        retryDeleteImageOperation()

                    } else {
                        // The user denied the necessary permissions, handle accordingly
                        Toast.makeText(this, "Permission denied. Unable to delete image.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    fun retryDeleteImageOperation() {
        // Reattempt the delete operation for each selected video
        for (item in selecteditemsimage) {
            try {
                contentResolver.delete(item.imageuri!!, null, null)
                // Handle successful deletion if needed
                lisimage.remove(item)
            } catch (e: Exception) {
                // Handle deletion failure
                Toast.makeText(this, "Failed to delete video.", Toast.LENGTH_SHORT).show()
            }
        }
        selecteditemsimage.clear()
        adapterCLass.notifyDataSetChanged()
    }



}


