package com.example.taseenbhaiphonecleanerproject.Activtiy

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taseenbhaiphonecleanerproject.R

class ManageExternalStorage : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 456
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_external_storage)
        val readPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val writePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        val managePermission = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE

        val readGranted = ContextCompat.checkSelfPermission(this, readPermission) == PackageManager.PERMISSION_GRANTED
        val writeGranted = ContextCompat.checkSelfPermission(this, writePermission) == PackageManager.PERMISSION_GRANTED
        val manageGranted = ContextCompat.checkSelfPermission(this, managePermission) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if (!readGranted) {
            permissionsToRequest.add(readPermission)
        }

        if (!writeGranted) {
            permissionsToRequest.add(writePermission)
        }

        if (!manageGranted) {
            permissionsToRequest.add(managePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CODE_STORAGE_PERMISSION)
        } else {
            // Permissions are already granted, proceed with your operations
            Toast.makeText(applicationContext,"Permission is granted",Toast.LENGTH_SHORT).show()
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_STORAGE_PERMISSION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted, proceed with your operations
                    Toast.makeText(applicationContext,"Permission is granted",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext,"Permission is not granted",Toast.LENGTH_SHORT).show()
                    // Permissions denied, handle accordingly (e.g., show a message or disable functionality)
                }
            }
            // Handle other permission requests if needed
        }
    }



}