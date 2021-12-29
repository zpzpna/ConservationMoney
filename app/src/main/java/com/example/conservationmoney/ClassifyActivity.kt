package com.example.conservationmoney

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Insets.add
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ClassifyActivity : AppCompatActivity() {
    //和主活动一样的初始化，不再赘述，这里创建的是放统计的对象的列表
    val CountList = ArrayList<AccountList>()
    val dbHelper = MyDatabaseHelper(this, "AccountTable.db", 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify)
        //启动数据库
        dbHelper.writableDatabase

        //找到新写的分类统计布局，并且配置适配器显示数据
        val Recyclerview: RecyclerView = findViewById(R.id.classify_body_layout)
        val layoutManager = LinearLayoutManager(this)
        Recyclerview.layoutManager = layoutManager
        //设置适配器
        val Count_adapter = AccountListAdapter(CountList)

        //查询对应时间的记账记录
        val choose_count_view:View =LayoutInflater.from(this).inflate(R.layout.classify_or_time_count,null)
        val choose_count_datapicker:DatePicker = choose_count_view.findViewById(R.id.time_count_choose)

        AlertDialog.Builder(this).apply {
            setView(choose_count_view)
            setTitle("选择查询时间")
            setCancelable(false)
            setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                init_count_data(choose_count_datapicker)
                Count_adapter.notifyDataSetChanged()
            })
            setNegativeButton("取消", DialogInterface.OnClickListener { _, _ ->})
            show()
        }


        Recyclerview.adapter = Count_adapter

        //设置按钮可以改变时间,但是注意，如果再一次使用上面的对话框的布局，首先show会报错，就算使用新的变量名
        //索引对应的布局，之后对话框选择的日期并不会替换一开始选择的日期，因此重新制作一个对话框布局，重新索引，这样就不会重复了
        val transfer_button:Button = findViewById(R.id.choose_date_button)
        transfer_button.setOnClickListener {
            //索引新的布局
            val rechoose_count_view:View =LayoutInflater.from(this).inflate(R.layout.recount_time,null)
            val rechoose_count_datapicker:DatePicker = rechoose_count_view.findViewById(R.id.time_count_rechoose)
            AlertDialog.Builder(this).apply {
                setView(rechoose_count_view)
                setTitle("选择查询时间")
                setCancelable(false)
                setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                    CountList.clear()
                    init_count_data(rechoose_count_datapicker)
                    Count_adapter.notifyDataSetChanged()
                })
                setNegativeButton("取消", DialogInterface.OnClickListener { _, _ ->})
                //再一次使用show（）会报错，因为使用的是上一次的对话框的view需要释放
                show()
            }
        }
    }
    fun init_count_data(date:DatePicker){
        val db = dbHelper.writableDatabase
        //由于query直接搜索年月日三个变量不太会用，使用SQL语句搜索
        val cursor: Cursor? =db.rawQuery("select * from List where date=? order by id desc"
            ,arrayOf(date.year.toString() +"-"+ date.month.toString() +"-"+ date.dayOfMonth.toString()))
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val CountList_dbsorce = AccountList(cursor.getString(cursor.getColumnIndexOrThrow("typename")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data")))

                    CountList.add(CountList_dbsorce)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }
}
