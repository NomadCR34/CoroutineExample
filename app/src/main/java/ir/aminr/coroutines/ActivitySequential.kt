package ir.aminr.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import ir.aminr.coroutines.databinding.ActivityParallelBinding
import ir.aminr.coroutines.databinding.ActivitySequentialBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ActivitySequential : AppCompatActivity() {

    /**
     * Sequential task
     *
     * */

    private lateinit var binding: ActivitySequentialBinding
    private val result1 = "RESULT #1"
    private val result2 = "RESULT #2"
    private val jobTimeOut = 600L

    private val handler:CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.i(TAG, "Handle Coroutine exception with message: $throwable")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sequential)

        CoroutineScope(Dispatchers.IO).launch {
            fakeApiRequest()
        }
    }

    private fun logThread(methodName: String) {
        Log.i(TAG, "logThread: $methodName , ${Thread.currentThread().name}")
    }

    private suspend fun fakeApiRequest() {
        val parentJob = CoroutineScope(Dispatchers.IO).launch(handler) {
            val time = measureTimeMillis {
                val result1 = async {
                    Log.i(TAG, "fakeApiRequest: Launching job 1")
                    getResultFromApi()
                }.await()

                val result2 = async {
                    Log.i(TAG, "fakeApiRequest: Launching job 2")
                    getResultFromApi2(result1)
                }.await()
            }

            Log.i(TAG, "fakeApiRequest: Time = $time ms.")
        }
    }

    private suspend fun getResultFromApi(): String {
        logThread("getResultFromApi")
        delay(1000)
        return result1
    }

    private suspend fun getResultFromApi2(result1: String): String {
        logThread("getResultFromApi2")
        delay(1500)
        //any condition for result1
        return "$result2 - $result1"
    }


    private suspend fun setTextOnMainThread(text: String) {
        withContext(Dispatchers.Main) {
            setNewText(text)
            logThread("logThread")
        }
    }

    private fun setNewText(text: String) {
        binding.txtText.text = binding.txtText.text.toString() + "\n" + text
    }
}