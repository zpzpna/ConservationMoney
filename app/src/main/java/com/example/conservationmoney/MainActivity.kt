package com.example.conservationmoney


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.Insets.add
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    val AccountList = ArrayList<AccountList>()
    //构造一个空的记账列表用于下面初始化时塞入数据库数据以及返回给我们写的适配器来作为数据源
    val dbHelper = MyDatabaseHelper(this, "AccountTable.db", 1)
    //全局声明，下面从数据库取数据要用
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //数据库部分
        //每次启动时都会自动检索是否有存记账表的库，没有就创
        //创的同时会执行我们写的SQL建表语句
        dbHelper.writableDatabase

        //适配器列表显示数据部分：
        //向创建的记账空集合列表放数据

        //测试数据，用来判断适配器有没有写好
        //initrowtest()
        initrowData()

        val Recyclerview: RecyclerView = findViewById(R.id.main_body_layout)
        //设置RecyclerView是线性布局，不然不好输出显示。。。
        val layoutManager = LinearLayoutManager(this)
        Recyclerview.layoutManager = layoutManager
        //设置适配器
        val Account_adapter = AccountListAdapter(AccountList)
        Recyclerview.adapter = Account_adapter

        //点击按钮记账增加记录部分 对话框创建数据+存入数据库+改变RecyclerView显示
        //设置输入数据的对话框格局
        val add_button: Button = findViewById(R.id.add_record)
        add_button.setOnClickListener {
            val add_viewdialogue: View =LayoutInflater.from(this).inflate(R.layout.account_add, null)
            val typename: TextView = add_viewdialogue.findViewById(R.id.write_typename)
            val date: DatePicker = add_viewdialogue.findViewById(R.id.choose_date)
            val data: TextView = add_viewdialogue.findViewById(R.id.write_data)
            AlertDialog.Builder(this).apply {
                setView(add_viewdialogue)
                setTitle("增加收入记录")
                setCancelable(false)
                setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                    //根据输入数据创建记账列表一个子行对象
                    val AccountList_one= AccountList(
                        //使用 as sring会报错误，
                        //androidx.emoji2.text.SpannableBuilder cannot be cast to java.lang.String
                        typename.text.toString(),
                        date.year.toString()+ "-" +
                                date.month.toString()+ "-" +
                                date.dayOfMonth.toString(),
                        "收入：" + data.text.toString()
                    )
                    //测试错误log，最后发现就是as string的原因
                    Log.d("运行","运行")
                    //插入数据库
                    //将才创建并得到数据的对象数据存到表里
                    val db = dbHelper.writableDatabase
                        val values = ContentValues().apply {
                            put("typename", AccountList_one.typename)
                            put("date", AccountList_one.date)
                            put("data", AccountList_one.data)
                        }
                        db.insert("List",null,values)

                    //然后放到RecyclerView的数据源列表,更新列表
                    AccountList.add(AccountList_one)
                    Account_adapter.notifyDataSetChanged()
                })
                setNegativeButton("取消",DialogInterface.OnClickListener { _, _ ->})
                show()
            }
        }
    }
    fun initrowtest(){
        //测试自己写的适配器，随便手动加入数据，以我吃的南财食堂为例子
        AccountList.add(AccountList("南财食堂瓦香鸡","2021-12-10", "11"))
        AccountList.add(AccountList("南财食堂麻辣香锅","2021-12-10", "15"))
        AccountList.add(AccountList("南财食堂炒饭","2021-12-11", "10"))
        AccountList.add(AccountList("南财食堂炒面","2021-12-11", "10"))
    }
    fun initrowData() {
        val cur: Cursor? = dbHelper.getInitData()
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    val AccountList_dbsorce = AccountList(cur.getString(cur.getColumnIndexOrThrow("typename")),
                        cur.getString(cur.getColumnIndexOrThrow("date")),
                        cur.getString(cur.getColumnIndexOrThrow("data")))

                    AccountList.add(AccountList_dbsorce)
                } while (cur.moveToNext())
            }
            cur.close()
        }
    }
}



