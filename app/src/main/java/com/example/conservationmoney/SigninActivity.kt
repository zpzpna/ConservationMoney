package com.example.conservationmoney

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class SigninActivity : AppCompatActivity() {
    companion object{
        var count = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        val intent = Intent(this,MainActivity::class.java)
        val username:EditText = findViewById(R.id.input_name)
        val keyword:EditText = findViewById(R.id.input_key)

        val signin_button:Button=findViewById(R.id.signin_button)
        signin_button.setOnClickListener {
            //一开始用equals，但是无法登录，后来发现是输入的数据没有自动转化为string，与我设置不匹配
            if (username.text.toString() == "wzp") {
                if (keyword.text.toString() == "123456") {
                    startActivity(intent)
                    Toast.makeText(this,"登录成功",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}