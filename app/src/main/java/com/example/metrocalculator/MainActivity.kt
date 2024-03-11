package com.example.metrocalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.metrocalculator.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var FIRST_LINE:Map<String,Int>
    private lateinit var SECOND_LINE:Map<String,Int>
    private lateinit var THIRD_LINE:Map<String,Int>
    lateinit var LINES:Array<Map<String,Int>>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FIRST_LINE = parseFileToMap("firstLine.txt")
        SECOND_LINE = parseFileToMap("secondLine.txt")
        THIRD_LINE = parseFileToMap("thirdLine.txt")
        LINES = arrayOf( FIRST_LINE,SECOND_LINE,THIRD_LINE)
        binding= ActivityMainBinding.inflate(layoutInflater)
        var adapter = ArrayAdapter(this, R.layout.spinner_list, FIRST_LINE.keys.toTypedArray())
        val adapter2 = ArrayAdapter(this, R.layout.spinner_list,arrayOf("First","Second","Third"))
        binding.start.adapter= adapter
        binding.end.adapter = adapter
        binding.startLines.adapter= adapter2
        binding.endLines.adapter = adapter2
        binding.startLines.onItemSelectedListener = object:OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_list, LINES[p2].keys.toTypedArray())
                binding.start.adapter=adapter
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.endLines.onItemSelectedListener = object:OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_list, LINES[p2].keys.toTypedArray())
                binding.end.adapter=adapter
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        object:OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_list, LINES[p2].keys.toTypedArray())
                binding.start.adapter=adapter
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.calculate.setOnClickListener {
            val startStation = binding.start.selectedItem.toString()
            val endStation = binding.end.selectedItem.toString()
            binding.cost.text= "The cost of your ticket is "+ getCost(getDistance(startStation,endStation)).toString()+"EGP"
        }
        setContentView(binding.root)
    }
    private fun getInterchangeDistance(endStation:String, startLine:Int, endLine:Int):Pair<Int,String>{
        val INTERCHANGE = mapOf("Anwar El Sadat" to arrayOf(1,2 ),"Al Shohadaa" to arrayOf(1,2 ),"Ataba" to arrayOf(2,3 ),"Gamal Abdel Nasser" to arrayOf(1,3) )
        var finalDistance =99
        var interchangeStation =""
        INTERCHANGE.forEach{ (station, lines) ->
            if(startLine in lines && endLine in lines) {
                Log.d("Test","INTERCHANGE "+LINES[endLine-1][station]!!.toString())
                val distance = abs(LINES[endLine-1][station]!!- LINES[endLine-1][endStation]!!)
                if(distance<finalDistance){
                    finalDistance = distance
                    interchangeStation= station
                }
            }


        }
        return Pair(finalDistance,interchangeStation)
    }
    private fun getLine(station:String): List<Int> {
        val result = mutableListOf<Int>()
        if(station in FIRST_LINE)
          result.add(1)
        if(station in SECOND_LINE)
            result.add(2)
        if(station in THIRD_LINE)
            result.add(3)
        return result
    }
    private fun getCost(distance:Int):Int{
        return if(distance==1){
            0
        } else if(distance<=9){
            6
        } else if(distance<=16){
            8
        } else if(distance<=23){
            12
        } else 15
    }
    private fun getDistance(start:String, end:String): Int {
        val startLine = getLine(start)
        val endLine = getLine(end)
        val sharedLines = startLine.intersect(endLine)
        val targetLine:Map<String,Int>

        return if(sharedLines.isNotEmpty()){
            targetLine=  LINES[sharedLines.elementAt(0)-1]
            abs(targetLine.getValue(start)- targetLine.getValue(end)) +1
        } else{
            val (distance,name) = getInterchangeDistance(end,startLine[0],endLine[0])
            abs(LINES[startLine[0] - 1][start]!! - LINES[startLine[0] - 1][name]!!) +1 + distance

        }
    }
    private fun parseFileToMap(fileName: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        assets.open(fileName).bufferedReader().forEachLine  { line ->
            val parts = line.split(": ")
            if (parts.size == 2) {
                val key = parts[0]
                val value = parts[1].toIntOrNull()
                if (value != null) {
                    map[key] = value
                } else {
                    Log.d("Metro Calculator","Invalid value for key '$key': '${parts[1]}'")
                }
            } else {
                Log.d("Metro Calculator","Invalid line: $line")
            }
        }
        return map
    }

    }