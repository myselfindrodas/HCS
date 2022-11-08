package com.example.wemu.session

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import com.app.hcsassist.Login

class SessionManager(  // Context
    var _context: Context
) {
    // Shared Preferences
    var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE = 0
    var TOKEN_PREFERENCES = "TokenPreferences"
    var USER_TOKEN = "UserToken"



    /**
     * Create login session
     */
    fun createLoginSession(username: String?, password: String?) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_username, username)
        editor.putString(KEY_password, password)
        editor.commit()
    }

    fun checkLogin() {
        // Check login status
        if (!isLoggedIn) {
            // user is not logged in redirect him to Login Activity
            val i = Intent(_context, Long::class.java)
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add new Flag to start new Activity
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // Staring Login Activity
            _context.startActivity(i)
        }
    }

    fun setToken(token: String?){
        pref.edit().putString("token", token).commit()
    }



    fun getToken(): String?{
        return  pref.getString("token", "")
    }

    fun setUsername(username: String?){
        pref.edit().putString("name", username).commit()
    }

    fun getUsername(): String?{
        return  pref.getString("name", "")
    }


    fun setemail(email: String?){
        pref.edit().putString("email", email).commit()
    }

    fun getemail(): String?{
        return  pref.getString("email", "")
    }

    fun setempname(empname: String?){
        pref.edit().putString("name", empname).commit()
    }

    fun getempname(): String?{
        return  pref.getString("name", "")
    }


    fun setempemail(empemail: String?){
        pref.edit().putString("empemail", empemail).commit()
    }

    fun getempemail(): String?{
        return  pref.getString("empemail", "")
    }


    fun setempcode(empcode: String?){
        pref.edit().putString("empcode", empcode).commit()
    }

    fun getempcode(): String?{
        return  pref.getString("empcode", "")
    }


    fun setmanager(manager: String?){
        pref.edit().putString("manager", manager).commit()
    }

    fun getmanager(): String?{
        return  pref.getString("manager", "")
    }

    fun setphnumber(phnumber: String?){
        pref.edit().putString("phnumber", phnumber).commit()
    }

    fun getphnumber(): String?{
        return  pref.getString("phnumber", "")
    }

    fun setempaddress(empaddress: String?){
        pref.edit().putString("empaddress", empaddress).commit()
    }

    fun getempaddress(): String?{
        return  pref.getString("empaddress", "")
    }

    fun setprofimage(profimage: String?){
        pref.edit().putString("profimage", profimage).commit()
    }

    fun getprofimage(): String?{
        return  pref.getString("profimage", "")
    }

    fun setuserid(userid: String?){
        pref.edit().putString("userid", userid).commit()
    }

    fun getuserid(): String?{
        return  pref.getString("userid", "")
    }


    fun setusercode(usercode: String?){
        pref.edit().putString("usercode", usercode).commit()
    }

    fun getusercode(): String?{
        return  pref.getString("usercode", "")
    }


    fun setaddress(address: String?){
        pref.edit().putString("address", address).commit()
    }

    fun getaddress(): String?{
        return  pref.getString("address", "")
    }


    fun setphone(phone: String?){
        pref.edit().putString("phone", phone).commit()
    }

    fun getphone(): String?{
        return  pref.getString("phone", "")
    }

    fun setid(id: String?){
        pref.edit().putString("id", id).commit()
    }

    fun getid(): String?{
        return  pref.getString("id", "")
    }

    fun setPunchin(punch: String?){
        pref.edit().putString("punch", punch).commit()
    }

    fun getPunchin(): String?{
        return  pref.getString("punch", "")
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()

        // After logout redirect user to Loing Activity
        val i = Intent(_context, Login::class.java)
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Add new Flag to start new Activity
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        _context.startActivity(i)
    }

    // Get Login State
    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    companion object {
        private const val PREF_NAME = "NWS_Pref"
        private const val IS_LOGIN = "IsLoggedIn"

        //username
        const val KEY_username = "username"

        //password
        const val KEY_password = "password"
        const val KEY_batchcount = "batchcount"
        const val KEY_VENDOR = "key_vandor"
        const val KEY_ADDRESS = "key_address"
    }

    // Constructor
    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}