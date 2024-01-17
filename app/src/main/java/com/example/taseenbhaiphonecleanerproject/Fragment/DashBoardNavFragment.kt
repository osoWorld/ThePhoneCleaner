package com.example.taseenbhaiphonecleanerproject.Fragment

import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.example.taseenbhaiphonecleanerproject.Activtiy.AUDIOActivity
import com.example.taseenbhaiphonecleanerproject.Activtiy.DOCUMENTSActivity
import com.example.taseenbhaiphonecleanerproject.Activtiy.IMAGEActivity
import com.example.taseenbhaiphonecleanerproject.Activtiy.VIDEOActivity
import com.example.taseenbhaiphonecleanerproject.R
import com.example.taseenbhaiphonecleanerproject.databinding.FragmentDashBoardNavBinding
import kotlin.random.Random


class DashBoardNavFragment : Fragment() {
    private var _binding: FragmentDashBoardNavBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashBoardNavBinding.inflate(inflater,container,false)
        getMemory()
        GetphoneRom()
        navigation()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            binding.btnboostnowID.visibility = View.GONE
        } else {
            binding.btnboostnowID.visibility = View.VISIBLE
        }
        binding.btnboostnowID.setOnClickListener {
//            val i= Intent(context, JunkCleanerAnimationS::class.java)
//            startActivity(i)
        }
        return binding.root
    }
    fun getMemory() {
        val activityManager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem
        val availableMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availableMemory
        val usedMemoryPercentage = (usedMemory * 100 / totalMemory).toInt()
        // set into textview
        binding.scanTextViewram.text = usedMemoryPercentage.toString()

        //set ram into progress bar and convert into int to float
        val myInt = usedMemoryPercentage
        val myFloat = myInt.toFloat()
        binding.rAmrogressID.apply {
            progressMax = 100f
            setProgressWithAnimation(myFloat, 1000)

        }
    }
    private fun GetphoneRom() {
        val randomNumber = Random.nextInt(50, 81)

        binding.scanTextViewrom.text = randomNumber.toString()
        binding.romrogressID.apply {
            progressMax = 100f
            setProgressWithAnimation(randomNumber.toFloat(), 1000)
        }
    }

    fun navigation() {
        binding.toggledrawerID.setOnClickListener {
            binding.drawerlayoutid.openDrawer(GravityCompat.START)
        }
        binding.cardjunkcleanerID.setOnClickListener {
            val intent = Intent(context, VIDEOActivity::class.java)
            startActivity(intent)
        }
        binding.viruscardID.setOnClickListener {
            val intent = Intent(context, IMAGEActivity::class.java)
            startActivity(intent)
        }
        binding.BatteryCardID.setOnClickListener {
            val intent = Intent(context, AUDIOActivity::class.java)
            startActivity(intent)
        }
//        binding.cpuCoolerID.setOnClickListener {
//            val intent = Intent(context, DOCUMENTSActivity::class.java)
//            startActivity(intent)
//        }

        binding.navViewid.setNavigationItemSelectedListener {
            when(it.itemId){


                R.id.privacyID ->
                {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val fragment = PrivacyMemoryFragment()
                    fragmentTransaction.replace(R.id.framelayoutMainID, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                    binding.drawerlayoutid.closeDrawer(GravityCompat.START)
                }

            }
            true
        }
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Exit()
            }
        }

    private fun Exit() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Phone Cleaner")
        builder.setMessage("Are you sure you want to exit the Phone Cleaner?")
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { dialogInterface, i -> requireActivity().finishAffinity() })
        builder.setNegativeButton("No", null)
        builder.show()
    }


    // Register the callback in the onCreate() method
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    // Unregister the callback in the onDestroy() method
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }



}


