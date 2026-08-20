package com.android.samsungparts

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider

class BatterySettings : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var fastCharging: MaterialSwitch
    private lateinit var batteryProtection: MaterialSwitch
    private lateinit var protectionSlider: Slider
    private lateinit var protectionSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)

        toolbar=findViewById(R.id.toolbar)
        collapsingToolbar=findViewById(R.id.collapsingToolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbar.title=getString(R.string.protect_battery_title)

        toolbar.setNavigationOnClickListener{
            finish()
        }

        fastCharging=findViewById(R.id.fastCharging)
        batteryProtection=findViewById(R.id.batteryProtectionSwitch)
        protectionSlider=findViewById(R.id.protectionSlider)
        protectionSummary=findViewById(R.id.protectionSummary)

        initializeDefaultProps()
        loadFastChargeStatus()
        loadBatteryStatus()

        fastCharging.setOnCheckedChangeListener{_,checked->
            setProp("persist.sec.fastcharge",if(checked)"1" else "0")
        }

        batteryProtection.setOnCheckedChangeListener{_,checked->
            if(checked){
                protectionSlider.isEnabled=true
                updateBatteryLimit(protectionSlider.value.toInt())
            }else{
                setProp("persist.sec.battery","5")
                protectionSlider.isEnabled=false
                protectionSummary.text=getString(R.string.protect_battery_disable)
            }
        }

        protectionSlider.addOnChangeListener{_,value,_->
            if(batteryProtection.isChecked){
                updateBatteryLimit(value.toInt())
            }
        }
    }

    private fun initializeDefaultProps(){
        val fast=getProp("persist.sec.fastcharge","")
        if(fast!="0"&&fast!="1"){
            setProp("persist.sec.fastcharge","1")
        }

        val battery=getProp("persist.sec.battery","")
        if(battery !in listOf("1","2","3","4","5")){
            setProp("persist.sec.battery","5")
        }
    }

    private fun updateBatteryLimit(percent:Int){
        val value=when(percent){
            80->"1"
            85->"2"
            90->"3"
            95->"4"
            100->"5"
            else->"5"
        }

        setProp("persist.sec.battery",value)
        protectionSummary.text=getString(R.string.charging_status,percent)
    }

    private fun loadFastChargeStatus(){
        fastCharging.isChecked=getProp("persist.sec.fastcharge","1")=="1"
    }

    private fun loadBatteryStatus(){
        when(getProp("persist.sec.battery","5")){
            "1"->setBatteryUI(80)
            "2"->setBatteryUI(85)
            "3"->setBatteryUI(90)
            "4"->setBatteryUI(95)
            "5"->{
                batteryProtection.isChecked=false
                protectionSlider.isEnabled=false
                protectionSlider.value=100f
                protectionSummary.text=getString(R.string.protect_battery_disable)
            }
            else->{
                setProp("persist.sec.battery","5")
                batteryProtection.isChecked=false
                protectionSlider.isEnabled=false
                protectionSummary.text=getString(R.string.protect_battery_disable)
            }
        }
    }

    private fun setBatteryUI(percent:Int){
        batteryProtection.isChecked=true
        protectionSlider.isEnabled=true
        protectionSlider.value=percent.toFloat()
        protectionSummary.text=getString(R.string.charging_status,percent)
    }

    private fun setProp(key:String,value:String){
        try{
            val clazz=Class.forName("android.os.SystemProperties")
            clazz.getMethod(
                "set",
                String::class.java,
                String::class.java
            ).invoke(null,key,value)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    private fun getProp(key:String,default:String):String{
        return try{
            val clazz=Class.forName("android.os.SystemProperties")
            clazz.getMethod(
                "get",
                String::class.java,
                String::class.java
            ).invoke(null,key,default) as String
        }catch(e:Exception){
            default
        }
    }

    override fun onSupportNavigateUp():Boolean{
        finish()
        return true
    }
}