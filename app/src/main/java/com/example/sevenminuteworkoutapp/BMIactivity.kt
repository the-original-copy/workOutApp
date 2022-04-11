package com.example.sevenminuteworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sevenminuteworkoutapp.databinding.ActivityBmiActivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIactivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val IMPERIAL_UNITS_VIEW = "IMPERIAL_UNITS_VIEW"
    }

    private var binding: ActivityBmiActivityBinding? = null
    private var currentVisibleView : String = METRIC_UNITS_VIEW


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.bmiActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }
        binding?.bmiActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        binding?.btnCalculateBmi?.setOnClickListener{
            calculateBMI()
        }

        //By default the metric view will be visible
        makeMetricVisible()

        //This method takes {the radioGroup & the id of the radio button}
        //In this case since there is only one radio group the first param is left empty
        binding?.rgUnits?.setOnCheckedChangeListener{ _,checkedId : Int ->
            if(checkedId == R.id.rbMetricUnits){
                makeMetricVisible()
            }else{
                makeImperialVisible()
            }
        }
    }

    private fun calculateBMI(){
        if(currentVisibleView == METRIC_UNITS_VIEW){
            if(validateEntries()){
                val height : Float = binding?.metricHeight?.text.toString().toFloat() / 100
                val weight : Float = binding?.metricWeight?.text.toString().toFloat()
                val bmi : Float = weight / (height*height)
                displayBMI(bmi)
            }else{
                Toast.makeText(this,"Please input valid entries",Toast.LENGTH_SHORT).show()
            }
        }else{
            if(validateImperialEntries()){
                val impweight : Float = binding?.imperialWeight?.text.toString().toFloat()
                val impheightfeet : String = binding?.imperialHeightFeet?.text.toString()
                val impheightinch : String = binding?.imperialHeightInch?.text.toString()

                //The input value in in feet and inches
                val imperialheight = impheightinch.toFloat() + (impheightfeet.toFloat() * 12)
                val impbmi : Float = 703 * (impweight / (imperialheight * imperialheight))
                displayBMI(impbmi)
            }else{
                Toast.makeText(this,"Please input valid entries",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeMetricVisible(){
        currentVisibleView = METRIC_UNITS_VIEW
        //Metric views
        binding?.metricSystemWeight?.visibility = View.VISIBLE
        binding?.metricSystemHeight?.visibility = View.VISIBLE
        //Imperial views
        binding?.imperialSystemWeight?.visibility = View.INVISIBLE
        binding?.imperialSystemHeightFeet?.visibility = View.INVISIBLE
        binding?.imperialSystemHeightInch?.visibility = View.INVISIBLE

        //Clear the values
        binding?.metricWeight?.text!!.clear()
        binding?.metricHeight?.text!!.clear()

        //Making result layout invisible when the mode is changed
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE

    }

    private fun makeImperialVisible(){
        currentVisibleView = IMPERIAL_UNITS_VIEW
        //Metric views
        binding?.metricSystemWeight?.visibility = View.INVISIBLE
        binding?.metricSystemHeight?.visibility = View.INVISIBLE
        //Imperial views
        binding?.imperialSystemWeight?.visibility = View.VISIBLE
        binding?.imperialSystemHeightFeet?.visibility = View.VISIBLE
        binding?.imperialSystemHeightInch?.visibility = View.VISIBLE

        //Clear the values
        binding?.imperialWeight?.text!!.clear()
        binding?.imperialHeightFeet?.text!!.clear()
        binding?.imperialHeightInch?.text!!.clear()

        //Making result layout invisible when the mode is changed
        binding?.llDisplayBMIResult?.visibility = View.INVISIBLE
    }

    private fun displayBMI(bmi: Float) {
        val bmiLabel : String
        val bmiDescription : String

        if(bmi.compareTo(15f) <= 0){
            bmiLabel = "You are very severely underweight"
            bmiDescription = "You need to eat more!!"
        }
        else if(bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0){
            bmiLabel = "You are severely underweight"
            bmiDescription = "You need to eat more!!"
        }
        else if(bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0){
            bmiLabel = "You are underweight"
            bmiDescription = "You need to eat more!!"
        }
        else if(bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0){
            bmiLabel = "You are normal"
            bmiDescription = "Congratulations! You are in good shape!!"
        }
        else if(bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0){
            bmiLabel = "You are slightly overweight"
            bmiDescription = "You need to start exercising before it get worse"
        }
        else if(bmi.compareTo(30f) > 0 && bmi.compareTo(40f) <= 0){
            bmiLabel = "You are obese"
            bmiDescription = "You need to start exercising to live a healthy life"
        }else{
            bmiLabel = "Death"
            bmiDescription = "See you in the after life"
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
        binding?.llDisplayBMIResult?.visibility = View.VISIBLE
    }

    private fun validateEntries() : Boolean{
        var isValid = true
        if(binding?.metricWeight?.text.toString().isEmpty()){
                isValid = false
            }else if(binding?.metricHeight?.text.toString().isEmpty()){
                isValid = false
        }
        return isValid
    }

    private fun validateImperialEntries() : Boolean{
        var isValid = true
        when {
            binding?.imperialWeight?.text.toString().isEmpty() -> {
                isValid = false
            }
            binding?.imperialHeightFeet?.text.toString().isEmpty() -> {
                isValid = false
            }
            binding?.imperialHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }
        }
        return isValid
    }
}