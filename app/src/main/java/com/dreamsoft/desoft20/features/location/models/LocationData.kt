package com.dreamsoft.desoft20.features.location.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val resultCode:Int,
    val message:String,
    val altitude:Double =0.0,
    val accuracy: Float =0.toFloat(),
    val latitude:Double =0.0,
    val longitude:Double =0.0,
){
    companion object{
        fun success(altitude:Double, accuracy: Float, latitude:Double, longitude:Double):LocationData{
            return LocationData(1,"Ubicacion obtenida",altitude,accuracy,latitude,longitude)
        }
        fun error(message:String):LocationData{
            return LocationData(3,message)
        }
        fun loading(message:String):LocationData{
            return LocationData(2,message)
        }
    }
}