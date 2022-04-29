package com.example.triki3.ui

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.triki3.R
import com.example.triki3.databinding.DialogMediaBinding
import com.example.triki3.databinding.FragmentPlayersBinding
import com.google.android.material.snackbar.Snackbar


class PlayersFragment : Fragment(R.layout.fragment_players) {
    private var currentPlayer = 0
    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data = it.data?.extras?.get("data") as Bitmap
             if (currentPlayer == 1){
                 binding.imgPlayerOne.setImageBitmap(data)
             }else{
                 binding.imgPlayerTwo.setImageBitmap(data)
             }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val data  = it.data?.data
            val source = ImageDecoder.createSource(requireContext().contentResolver,data!!)
            if (currentPlayer == 1){
                binding.imgPlayerOne.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }else{
                binding.imgPlayerTwo.setImageBitmap(ImageDecoder.decodeBitmap(source))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            pickFromGallery()
        }else{
            Snackbar.make(binding.root,"You need to enable the permission",Snackbar.LENGTH_SHORT).show()
        }
    }

    private lateinit var binding: FragmentPlayersBinding
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlayersBinding.bind(view)

        clicks()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun clicks() {
        binding.btnPlay.setOnClickListener { goGame() }
        binding.imgPlayerOne.setOnClickListener {
            currentPlayer = 1
            dialogMedia() }
        binding.imgPlayerTwo.setOnClickListener {
            currentPlayer = 2
            dialogMedia() }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun dialogMedia() {
        val dialogBinding = DialogMediaBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog =
            AlertDialog.Builder(requireContext()).apply { setView(dialogBinding.root) }.create()

        dialogBinding.imgClose.setOnClickListener { alertDialog.dismiss() }
        dialogBinding.btnCamera.setOnClickListener {
            pickFromCamera()
            alertDialog.dismiss() }
        dialogBinding.btnGallery.setOnClickListener {
            requestPermission()
            alertDialog.dismiss() }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED ->{pickFromGallery()}
                else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            pickFromGallery()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "image/*"
        galleryResult.launch(i)
    }

    private fun pickFromCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),1000)
        }else{
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                cameraResult.launch(i)
            }catch (e:Exception){
                Snackbar.make(binding.root,"You don't have a app to open the camera",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun goGame() {
        val results = arrayOf(validatePlayerOne(), validatePlayerTwo())
        if (false in results) {
            return
        }
        val action = PlayersFragmentDirections.actionPlayersFragmentToGameFragment(
            binding.playerOneEdt.text.toString(),
            binding.playerTwoEdt.text.toString()
        )
        findNavController().navigate(action)
    }

    private fun validatePlayerOne(): Boolean {
        return if (binding.playerOneEdt.text.toString().isNullOrEmpty()) {
            binding.playerOneTIL.error = "This Field is obligatory"
            false
        } else {
            binding.playerOneTIL.error = null
            true
        }
    }

    private fun validatePlayerTwo(): Boolean {
        return if (binding.playerTwoEdt.text.toString().isNullOrEmpty()) {
            binding.playerTwoTIL.error = "This Field is obligatory"
            false
        } else {
            binding.playerTwoTIL.error = null
            true
        }
    }
}