package com.example.taseenbhaiphonecleanerproject.Activtiy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.taseenbhaiphonecleanerproject.Fragment.DashBoardNavFragment
import com.example.taseenbhaiphonecleanerproject.R
import com.example.taseenbhaiphonecleanerproject.databinding.ActivityDashBoardBinding

class DashBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        val fragment = DashBoardNavFragment()
        supportFragmentManager.beginTransaction().replace(R.id.framelayoutMainID, fragment).commit()
    }
    }
