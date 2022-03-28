package com.company.calculator

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.calculator.databinding.ActivityOneExpressionBinding

class OneExpressionActivity: Activity() {

    private val binding: ActivityOneExpressionBinding by lazy {
        ActivityOneExpressionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) = with (binding){
        super.onCreate(savedInstanceState)
        setContentView(root)
    }

}