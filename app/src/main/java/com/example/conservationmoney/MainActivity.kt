package com.example.conservationmoney


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    val AccountList = ArrayList<AccountList>()
    //构造一个空的记账列表用于下面初始化时塞入数据库数据以及返回给我们写的适配器来作为数据源
    val dbHelper = MyDatabaseHelper(this, "AccountTable.db", 1)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //启动时自动调入登陆界面
        //设置登陆界面发现他会无限循环登录，因为登录活动挑来主活动，运行主活动跳转又跳回登陆界面
        //因此我在登录活动设置了一个count，默认为0，启动时主活动跳过去+1，登陆后由于count=1，不再跳转，解决问题
        if(SigninActivity.count == 0) {
            SigninActivity.count+=1
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
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

        //支出按钮，只需要复用收入按钮的逻辑稍微修改，并且公用一个布局即可，因此大部分内容不需要修改
        //由于直接使用增加收入的逻辑，注释也一并复制，这里不做删除
        val consume_button: Button = findViewById(R.id.consume_record)
        consume_button.setOnClickListener {
            val consume_viewdialogue: View =LayoutInflater.from(this).inflate(R.layout.account_add, null)
            val typename: TextView = consume_viewdialogue.findViewById(R.id.write_typename)
            val date: DatePicker = consume_viewdialogue.findViewById(R.id.choose_date)
            val data: TextView = consume_viewdialogue.findViewById(R.id.write_data)
            AlertDialog.Builder(this).apply {
                setView(consume_viewdialogue)
                setTitle("增加支出记录")
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
                        "支出：" + data.text.toString()
                    )
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

        //这里实现删除全部记录按钮，这个比较好做，不需要指定记录只需要全部删掉
        val delete_all_button:Button = findViewById(R.id.delete_all_record)
        delete_all_button.setOnClickListener {
            //删除数据库中记录
            val db = dbHelper.writableDatabase
            db.delete("List",null,null)
            //及时删除此时列表记录，不然只有下一次启动可以消失，而不能点击按钮立即消失
            AccountList.clear()
            Account_adapter.notifyDataSetChanged()
            Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show()
        }

        //跳转到统计界面
        val intent_count = Intent(this,ClassifyActivity::class.java)
        val transfer_button:Button = findViewById(R.id.transer_count)
        transfer_button.setOnClickListener {
            startActivity(intent_count)
            Toast.makeText(this,"进入统计界面",Toast.LENGTH_SHORT).show()
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
        //和上面测试适配器手动加入的不同，这个从数据库我已经保存的数据进行读取
        //不然即使这一次添加数据，下次启动程序还是不显示已有的数据

        val db = dbHelper.writableDatabase
        val cursor: Cursor? =db.query("list", null, null, null, null, null, "data ASC")
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val AccountList_dbsorce = AccountList(cursor.getString(cursor.getColumnIndexOrThrow("typename")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("data")))

                    AccountList.add(AccountList_dbsorce)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }
}



