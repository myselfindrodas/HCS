package com.app.hcsassist.model

class ShiftlistModel(var id: String, var shift_title: String) {
    var company_id: String? = null
    var location_id: String? = null
    var start_time: String? = null
    var end_time: String? = null
    var duration: String? = null
    var status: String? = null

}