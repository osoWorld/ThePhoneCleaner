package com.example.taseenbhaiphonecleanerproject.Activtiy

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taseenbhaiphonecleanerproject.Activtiy.IMAGEActivity.Companion.REQUEST_PERMISSION_CODE
import com.example.taseenbhaiphonecleanerproject.Adapater.AudioAdapterClass
import com.example.taseenbhaiphonecleanerproject.Adapater.DocumentsAdapterClass
import com.example.taseenbhaiphonecleanerproject.Adapater.ImagesAdapterClass
import com.example.taseenbhaiphonecleanerproject.DataClass.AudioDataClass
import com.example.taseenbhaiphonecleanerproject.DataClass.DocumentItem
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.R
import com.example.taseenbhaiphonecleanerproject.databinding.ActivityAudioactivityBinding
import java.io.File

class AUDIOActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioactivityBinding
    private lateinit var audiolist :ArrayList<AudioDataClass>
    private lateinit var adapterCLass:AudioAdapterClass
    private var permissionAudio: String? = null
    private var selecteditemsAudio=ArrayList<AudioDataClass>()
    val AUDIO_REQUEST_CODE = 100
    val REQUEST_PERMISSION_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            permissionAudio = android.Manifest.permission.READ_EXTERNAL_STORAGE
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            permissionAudio = android.Manifest.permission.READ_MEDIA_AUDIO
        }

        binding.deleBtn.setOnClickListener {
            ItemsSize()
        }
        if (checkPermission()) {
            getlistofAudio()
        } else {
            requestPermission()
        }
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(permissionAudio),
            REQUEST_PERMISSION_CODE
        )
    }
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permissionAudio!!
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
                getlistofAudio()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getlistofAudio(){
        audiolist= ArrayList();
        val imageProjection = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATE_TAKEN,
        )
//        Now we need to pass this projection on query with the help of ContentResolver.
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            "${MediaStore.Audio.Media.DATE_TAKEN} DESC"
        )
        cursor.use {
            it?.let {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_TAKEN)
                val audiodata = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val size = it.getLong(sizeColumn)
                    val date = it.getString(dateColumn)
                    val audio = it.getString(audiodata)
                    val sizeformat=formatFileSize(size)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    Log.d("ImageInfo", "ID: $id, Name: $name, Size: $size, Date: $date, Path: $audio")
                    audiolist.add(AudioDataClass(audio,name,contentUri,sizeformat,false,id))
                }
                adapterCLass= AudioAdapterClass(audiolist,applicationContext)

                binding.deleRecycleView.adapter = adapterCLass
                binding.deleRecycleView.layoutManager = LinearLayoutManager(this)
            }
        }


    }
    fun ItemsSize(){
        selecteditemsAudio= audiolist.filter { it.isSelected } as ArrayList<AudioDataClass>
        if (selecteditemsAudio.isEmpty()){
            Toast.makeText(applicationContext,"Please Select Images",Toast.LENGTH_SHORT).show()
        }else{
            deleteImageaboveAPI29(this)
        }
    }
    fun deleteImageaboveAPI29(context: Context) {
        val resolver = context.contentResolver
        for (items in selecteditemsAudio) {
            try {
                resolver.delete(items.audiofileuri!!, null, null)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as?
                            RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                    val intentSender =
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    intentSender?.let {
                        startIntentSenderForResult(
                            intentSender, AUDIO_REQUEST_CODE,
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

        if (requestCode == AUDIO_REQUEST_CODE) {
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
        for (item in selecteditemsAudio) {
            try {
                contentResolver.delete(item.audiofileuri!!, null, null)
                // Handle successful deletion if needed
                audiolist.remove(item)
            } catch (e: Exception) {
                // Handle deletion failure
                Toast.makeText(this, "Failed to delete video.", Toast.LENGTH_SHORT).show()
            }
        }
        selecteditemsAudio.clear()
        adapterCLass.notifyDataSetChanged()
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

}