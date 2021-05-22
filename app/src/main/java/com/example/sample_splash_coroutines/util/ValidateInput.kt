package com.example.sample_splash_coroutines.util

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast

class ValidateInput() {

    fun emailValidate(email : EditText, context: Context) : Boolean {
        if (email.text.isNullOrEmpty()) {
            Toast.makeText( context, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
            Toast.makeText(context, "メールアドレスが不正です", Toast.LENGTH_SHORT).show()
            return false
        }  else {
            return true
        }
    }

    fun passwordValidate(password: EditText, context: Context) : Boolean {
        if (password.text.isNullOrEmpty()) {
            Toast.makeText(context, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.text.length < 8) {
            Toast.makeText(context, "パスワードが不正です", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

//    fun repeatPasswordValidate(password: EditText, repeatPassword : EditText, context: Context): Boolean {
//        if (repeatPassword.text.isNullOrEmpty()) {
//            Toast.makeText(context, "パスワードを入力してください", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (repeatPassword.text.length < 8) {
//            Toast.makeText(context, "パスワードが不正です", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (password.text.length != repeatPassword.text.length){
//            Toast.makeText(context, "パスワードが一致していません", Toast.LENGTH_SHORT).show()
//            return false
//        } else {
//            return true
//        }
//    }
//
//    fun newEmailValidate(email : EditText, newemail : EditText, context: Context) : Boolean {
//        println("${email.text} ${ newemail.text}")
//        if (newemail.text.isNullOrEmpty()) {
//            Toast.makeText( context, "メールアドレスを入力してください", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(newemail.text).matches()) {
//            Toast.makeText(context, "メールアドレスが不正です", Toast.LENGTH_SHORT).show()
//            return false
//        }  else if (email.text.toString() == newemail.text.toString())  {
//            Toast.makeText(context, "メールアドレスが変更されていません", Toast.LENGTH_SHORT).show()
//            return false
//        } else {
//            return true
//        }
//    }
//
    fun usernameValidater(username : EditText, context: Context) : Boolean {
        if (username.text.isNullOrEmpty()) {
            Toast.makeText( context, "ユーザーネームを入力してください", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}