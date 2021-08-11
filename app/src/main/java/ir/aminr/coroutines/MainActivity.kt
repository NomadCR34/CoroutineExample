package ir.aminr.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import ir.aminr.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlin.coroutines.coroutineContext

private const val TAG = "MYLOG"

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private val result1 = "RESULT #1"
    private val result2 = "RESULT #2"
    private val jobTimeOut = 600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        CoroutineScope(IO).launch {
            fakeApiRequest()
        }
    }

    private suspend fun getResultFromApi(): String {
        logThread("getResultFromApi")
        delay(1000)
        return result1
    }

    private suspend fun getResultFromApi2(): String {
        logThread("getResultFromApi2")
        delay(1000)
        return result2
    }

    private fun logThread(methodName: String) {
        Log.i(TAG, "logThread: $methodName , ${Thread.currentThread().name}")
    }

    private suspend fun fakeApiRequest() {
        withContext(IO){
            val job = withTimeoutOrNull(jobTimeOut) {
                val result = getResultFromApi()
                setTextOnMainThread(result)
            }

            val job2 = launch {
                val result = getResultFromApi2()
                setTextOnMainThread(result)
            }

            if(job == null){
                Log.i(TAG, "fakeApiRequest: Job took longer that timeout")
            }

            if(job2 == null){
                Log.i(TAG, "fakeApiRequest2: Job took longer that timeout")
            }
        }


    }

    private suspend fun setTextOnMainThread(text:String){
        withContext(Dispatchers.Main){
            setNewText(text)
            logThread("logThread")
        }
    }

    private fun setNewText(text:String){
        binding.txtText.text = binding.txtText.text.toString() + "\n" + text
    }
}