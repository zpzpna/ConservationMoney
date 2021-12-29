package com.example.conservationmoney

class AccountList(var typename: String, var date:String, var data:String) {

    fun gettypename(): String {
        return typename
    }
    fun getdate(): String {
        return date
    }
    fun getdata(): String {
        return data
    }

    fun settypename(typename: String) {
        this.typename = typename
    }
    fun setdate(date: String) {
        this.date = date
    }
    fun setdata(data: String) {
        this.data = data
    }

    //上面这些set和get的接口函数，我在网上搜了很多recycler模板会写
    //但是kotlin存在语法糖，不需要用自己写这些get和set...写了练手
}