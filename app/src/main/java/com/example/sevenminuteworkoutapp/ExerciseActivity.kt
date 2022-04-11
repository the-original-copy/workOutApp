package com.example.sevenminuteworkoutapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sevenminuteworkoutapp.databinding.ActivityExeciseBinding
import com.example.sevenminuteworkoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var restTimeDuration : Long = 1
    private var exerciseTimeDuration : Long = 1
    private var exerciseAdapter: RecyclerStatusAdapter? = null
    private var player: MediaPlayer? = null
    private var tts : TextToSpeech? = null
    private var currentExercisePosition = -1
    private var exerciseList : ArrayList<Exercise>? = null
    private var binding : ActivityExeciseBinding ? = null
    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer : CountDownTimer? = null
    private var exerciseProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExeciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolBarExercise)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolBarExercise?.setNavigationOnClickListener{
            customDialogBackButton()
        }

        //Initialization of arrayList
        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this,this)

        setUpRestView()
        //Size the ArrayList has been forced on repeat, the method setting up the recycler view must be called after the arrayList has been initialized
        setUpRecyclerView()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        customDialogBackButton()
    }

    private fun customDialogBackButton() {
        //Pause TTS
        val customDialog = Dialog(this)
        //Inflating using xml file
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        //Dont use the normal setContentView....use the one from the class dialog through the val customDialog
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.yesBtn.setOnClickListener{
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.noBtn.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setUpRecyclerView(){
        //Layout Manager
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        //The adapter
        exerciseAdapter = RecyclerStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun setUpExerciseView(){

      binding?.flRestTimer?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.flExercise?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility= View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.visibility = View.INVISIBLE

        //Speak out the current exercise
        speakOut(exerciseList!![currentExercisePosition].getName())

        //Setup the image,exercise name
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        //Reset the timer
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        setExerciseProgress()

    }

    private fun setUpRestView(){

        try{
            val soundURI = Uri.parse("android.resource://com.example.sevenminuteworkoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch(e : Exception){
            e.printStackTrace()
        }
        binding?.flRestTimer?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.flExercise?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility= View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.text = exerciseList!![currentExercisePosition + 1].getName()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        speakOut("Rest for ten seconds")

        setRestProgress()
    }

    private fun setRestProgress(){
        binding?.restProgressBar?.progress = restProgress
        restTimer = object : CountDownTimer(restTimeDuration*1000,1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.restProgressBar?.progress = 10 - restProgress
                binding?.restTvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }
        }.start()
    }
    private fun setExerciseProgress(){
        binding?.progressBarExercise?.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimeDuration*1000,1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList!!.size - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                }else{
                    finish()
                    //More information has to be added since the this keyword returns the object CountDownTimer
                    val intent = Intent(this@ExerciseActivity,EndExercise::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }
        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        if(tts != null){
            tts?.stop()
            tts?.shutdown()
        }

        if(player != null){
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language stated is not supported")
            }else{
                Log.e("TTS","Initialization failed!! ")
            }
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null," ")
    }
}