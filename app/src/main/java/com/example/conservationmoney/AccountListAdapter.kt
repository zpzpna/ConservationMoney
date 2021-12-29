package com.example.conservationmoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//模仿书上Fruit来写一个记账的适配器
class AccountListAdapter(val AccountList: List<AccountList>) :
    RecyclerView.Adapter<AccountListAdapter.ViewHolder>() {

    //先写一个内部类设置属性为适配器对应记账栏子布局的各种控件
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typename: TextView = view.findViewById(R.id.Recyclerview_count_typename)
        val date: TextView = view.findViewById(R.id.Recyclerview_count_date)
        val data: TextView = view.findViewById(R.id.Recyclerview_count_data)
    }
    //找到子布局，传给我们写好的内部类来初始化内部类
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_every_row, parent, false)
        return ViewHolder(view)
    }
    //最后使用position定位此时AccountList中某一个实例
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Account = AccountList[position]
        holder.typename.text = Account.gettypename()
        holder.data.text = Account.getdata()
        holder.date.text = Account.getdate()
    }
    //计数AccountList列表个数
    override fun getItemCount() = AccountList.size
}