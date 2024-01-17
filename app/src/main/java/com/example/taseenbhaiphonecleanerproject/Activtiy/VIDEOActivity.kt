package com.example.taseenbhaiphonecleanerproject.Activtiy


import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taseenbhaiphonecleanerproject.Adapater.VideosAdapterClass
import com.example.taseenbhaiphonecleanerproject.DataClass.VideoItem
import com.example.taseenbhaiphonecleanerproject.R
import com.example.taseenbhaiphonecleanerproject.databinding.ActivityVideoactivityBinding


class VIDEOActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoactivityBinding
    private val videosList = ArrayList<VideoItem>()
    private lateinit var adapter: VideosAdapterClass
    private var permissionVideos: String? = null
    private val REQUEST_CODE_MEDIA_VIDEO_PERMISSION = 101
    val VIDEO_REQUEST_CODE = 103
    private var selecteditemsvideo = ArrayList<VideoItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            permissionVideos = android.Manifest.permission.READ_EXTERNAL_STORAGE
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
            permissionVideos = android.Manifest.permission.READ_MEDIA_VIDEO
        }
        binding.deleBtn.setOnClickListener {
            ItemsSize()
        }
        if (checkPermissionForMediaVideo()) {
            loadVideos()
        } else {
            requestPermissionForMediaVideo()
        }
    }
    private fun checkPermissionForMediaVideo(): Boolean {
        // Check for the permission to read media videos
        return ContextCompat.checkSelfPermission(
            this,
            permissionVideos!!
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermissionForMediaVideo() {
        // Request permission to read media videos
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permissionVideos),
            REQUEST_CODE_MEDIA_VIDEO_PERMISSION
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == IMAGEActivity.REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadVideos() {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Thumbnails.DATA
        )
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val cursor = contentResolver.query(
            collection,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_TAKEN} DESC"  //For Storing the List in RecyclerView
        )
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val thum = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val thumtitle = it.getString(thum)
                val duration = it.getLong(durationColumn)
                val size = it.getLong(sizeColumn)
                val totalsize = formatFileSize(size)
                val contentUri: Uri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                videosList.add(VideoItem(thumtitle, contentUri, name, duration, totalsize, false))
            }
            adapter = VideosAdapterClass(videosList, this)
            binding.deleRecycleView.layoutManager = LinearLayoutManager(this)
            binding.deleRecycleView.adapter = adapter
            adapter.notifyDataSetChanged()

        }
//        binding.deleRecycleView.layoutManager = LinearLayoutManager(this)

    }
    fun ItemsSize() {
        selecteditemsvideo = videosList.filter { it.isSelected } as ArrayList<VideoItem>
        if (selecteditemsvideo.isEmpty()) {
            Toast.makeText(applicationContext, "Please Select videos", Toast.LENGTH_SHORT).show()
        } else {
            deleteImageAPI29(this)
        }
    }
    fun deleteImageAPI29(context: Context) {
        val resolver = context.contentResolver
        for (items in selecteditemsvideo) {
            try {
                resolver.delete(items.videouri!!, null, null)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as?
                            RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                    val intentSender =
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    intentSender?.let {
                        startIntentSenderForResult(
                            intentSender, VIDEO_REQUEST_CODE,
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

        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                retryDeleteVideoOperation()
            } else {
                // The user denied the necessary permissions, handle accordingly
                Toast.makeText(
                    this,
                    "Permission denied. Unable to delete image.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    fun retryDeleteVideoOperation() {
        for (item in selecteditemsvideo) {
            try {
                contentResolver.delete(item.videouri!!, null, null)
                videosList.remove(item)
            } catch (e: Exception) {
                // Handle deletion failure
                Toast.makeText(this, "Failed to delete video.", Toast.LENGTH_SHORT).show()
            }
        }
        selecteditemsvideo.clear()
        adapter.notifyDataSetChanged()
    }
    fun formatFileSize(sizeInBytes: Long): String {
        val kilobyte = 1024
        val megabyte = kilobyte * 1024

        return when {
            sizeInBytes > megabyte -> String.format("%.2f MB", sizeInBytes.toFloat() / megabyte)
            sizeInBytes > kilobyte -> String.format("%.2f KB", sizeInBytes.toFloat() / kilobyte)
            else -> String.format("%d B", sizeInBytes)
        }
    }
}