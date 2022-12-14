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

    fun setpunchinLocation(punchinLocation: String?){
        pref.edit().putString("punchinLocation", punchinLocation).commit()
    }

    fun getpunchinLocation(): String?{
        return  pref.getString("punchinLocation", "")
    }

    fun setpunchoutLocation(punchoutLocation: String?){
        pref.edit().putString("punchoutLocation", punchoutLocation).commit()
    }

    fun getpunchoutLocation(): String?{
        return  pref.getString("punchoutLocation", "")
    }


    fun setpunchinId(punchinId: String?){
        pref.edit().putString("punchinId", punchinId).commit()
    }

    fun getpunchinId(): String?{
        return  pref.getString("punchinId", "")
    }


    fun setinputName(inputName: String?){
        pref.edit().putString("inputName", inputName).commit()
    }

    fun getinputName(): String?{
        return  pref.getString("inputName", "")
    }

    fun setpunchIntime(punchIntime: String?){
        pref.edit().putString("punchIntime", punchIntime).commit()
    }

    fun getpunchIntime(): String?{
        return  pref.getString("punchIntime", "")
    }


    fun setcurrentLat(currentLat: String?){
        pref.edit().putString("currentLat", currentLat).commit()
    }

    fun getcurrentLat(): String?{
        return  pref.getString("currentLat", "")
    }


    fun setcurrentLong(currentLong: String?){
        pref.edit().putString("currentLong", currentLong).commit()
    }

    fun getcurrentLong(): String?{
        return  pref.getString("currentLong", "")
    }



    fun setlogoutLat(logoutLat: String?){
        pref.edit().putString("logoutLat", logoutLat).commit()
    }

    fun getlogoutLat(): String?{
        return  pref.getString("logoutLat", "")
    }


    fun setlogoutLong(logoutLong: String?){
        pref.edit().putString("logoutLong", logoutLong).commit()
    }

    fun getlogoutLong(): String?{
        return  pref.getString("logoutLong", "")
    }



    fun setfencingLat(fencingLat: String?){
        pref.edit().putString("fencingLat", fencingLat).commit()
    }

    fun getfencingLat(): String?{
        return  pref.getString("fencingLat", "")
    }


    fun setfencingLong(fencingLong: String?){
        pref.edit().putString("fencingLong", fencingLong).commit()
    }

    fun getfencingLong(): String?{
        return  pref.getString("fencingLong", "")
    }

    fun setisHoliday(isHoliday: String?){
        pref.edit().putString("isHoliday", isHoliday).commit()
    }

    fun getisHoliday(): String?{
        return  pref.getString("isHoliday", "")
    }


    fun setisonLeave(isonLeave: String?){
        pref.edit().putString("isonLeave", isonLeave).commit()
    }

    fun getisonLeave(): String?{
        return  pref.getString("isonLeave", "")
    }


    fun setdefultShift(defultShift: String?){
        pref.edit().putString("defultShift", defultShift).commit()
    }

    fun getdefultShift(): String?{
        return  pref.getString("defultShift", "")
    }


    fun setsnapShot(snapShot: String?){
        pref.edit().putString("snapShot", snapShot).commit()
    }

    fun getsnapShot(): String?{
        return  pref.getString("snapShot", "")
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


    fun setUsertypename(Usertypename: String?){
        pref.edit().putString("Usertypename", Usertypename).commit()
    }

    fun getUsertypename(): String?{
        return  pref.getString("Usertypename", "")
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