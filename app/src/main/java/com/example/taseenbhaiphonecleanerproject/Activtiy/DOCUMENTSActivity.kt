package com.example.taseenbhaiphonecleanerproject.Activtiy

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taseenbhaiphonecleanerproject.Adapater.DocumentsAdapterClass
import com.example.taseenbhaiphonecleanerproject.Adapater.ImagesAdapterClass
import com.example.taseenbhaiphonecleanerproject.DataClass.DocumentItem
import com.example.taseenbhaiphonecleanerproject.DataClass.ImagesItem
import com.example.taseenbhaiphonecleanerproject.R
import com.example.taseenbhaiphonecleanerproject.databinding.ActivityDocumentsactivityBinding
import java.io.File
@RequiresApi(Build.VERSION_CODES.Q)
class DOCUMENTSActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentsactivityBinding
    private lateinit var documentList: ArrayList<DocumentItem>
    private lateinit var adapterClass: DocumentsAdapterClass
    private var selecteditemsdoc = ArrayList<DocumentItem>()
    val DOCUMENTS_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.deleBtn.setOnClickListener {

        if (checkPermission()) {
            loadPDFs()
        } else {
            requestPermission()
        }
    }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            IMAGEActivity.REQUEST_PERMISSION_CODE
        )
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == IMAGEActivity.REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPDFs()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadPDFs() {
        documentList = ArrayList()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATA,
//            MediaStore.Files.FileColumns.DATE_TAKEN
        )

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("application/pdf")

        val cursor = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
//            val dateColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val size = it.getLong(sizeColumn)
                val data = it.getString(dataColumn)
//                val date = it.getString(dateColumn)

                val contentUri: Uri = Uri.withAppendedPath(
                    MediaStore.Files.getContentUri("external"),
                    id.toString()
                )

                documentList.add(DocumentItem(name,contentUri, data, size,false))
            }

        }

        adapterClass = DocumentsAdapterClass(documentList, this)

        binding.deleRecycleView.adapter = adapterClass
        binding.deleRecycleView.layoutManager = LinearLayoutManager(this)

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
    fun ItemsSize(){
        selecteditemsdoc= documentList.filter { it.isSelected } as ArrayList<DocumentItem>
        if (selecteditemsdoc.isEmpty()){
            Toast.makeText(applicationContext,"Please Select Images",Toast.LENGTH_SHORT).show()
        }else{
            deleteImageaboveAPI29(this)
        }
    }
    fun deleteImageaboveAPI29(context: Context) {
        val resolver = context.contentResolver
        for (items in selecteditemsdoc) {
            try {
                resolver.delete(items.fileuri!!, null, null)
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = securityException as?
                            RecoverableSecurityException
                        ?: throw RuntimeException(securityException.message, securityException)

                    val intentSender =
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    intentSender?.let {
                        startIntentSenderForResult(
                            intentSender, DOCUMENTS_REQUEST_CODE,
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

        if (requestCode == DOCUMENTS_REQUEST_CODE) {
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
        for (item in selecteditemsdoc) {
            try {
                contentResolver.delete(item.fileuri!!, null, null)
                // Handle successful deletion if needed
                documentList.remove(item)
            } catch (e: Exception) {
                // Handle deletion failure
                Toast.makeText(this, "Failed to delete video.", Toast.LENGTH_SHORT).show()
            }
        }
        selecteditemsdoc.clear()
        adapterClass.notifyDataSetChanged()
    }
}
