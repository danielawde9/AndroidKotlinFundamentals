package com.daniel.androidkotlinfundamentals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.daniel.androidkotlinfundamentals.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val myName: MyName = MyName("Aleks Haecky")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.myName = myName

        binding.doneButton.setOnClickListener{
            addNickName(it)
        }
    }

    private fun addNickName(view: View) {
        myName?.nickname = binding.nicknameEdit.text.toString()
        binding.invalidateAll()
        binding.nicknameText.text = binding.nicknameEdit.text.toString()
        binding.nicknameEdit.visibility = View.GONE
        binding.doneButton.visibility = View.GONE
        binding.nicknameText.visibility = View.VISIBLE
    }
}