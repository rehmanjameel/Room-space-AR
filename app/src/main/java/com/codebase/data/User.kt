package com.codebase.data

data class User(
    val firstName:String ,
    val lastName:String ,
    val email : String ,
    val userRole: String,
    val imagePath:String =""
) {
    constructor():this("","","", "","")
}