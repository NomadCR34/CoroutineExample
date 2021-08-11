package ir.aminr.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import ir.aminr.coroutines.databinding.ActivityExample3Binding
import ir.aminr.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class ActivityParallelExample : AppCompatActivity() {
    /**
     * PARALLEL background task
     *
     * */

    private lateinit var binding: ActivityExample3Binding
    private val result1 = "RESULT #1"
    private val result2 = "RESULT #2"
    private val jobTimeOut = 600L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_example3)

        CoroutineScope(Dispatchers.IO).launch {
            fakeApiRequest2()
        }
    }

    private suspend fun getResultFromApi(): String {
        logThread("getResultFromApi")
        delay(1000)
        return result1
    }

    private suspend fun getResultFromApi2(): String {
        logThread("getResultFromApi2")
        delay(1500)
        return result2
    }

    private fun logThread(methodName: String) {
        Log.i(TAG, "logThread: $methodName , ${Thread.currentThread().name}")
    }

    private suspend fun fakeApiRequest() {
        withContext(Dispatchers.IO){
            val job1 = launch {
                val time1 = measureTimeMillis {
                    Log.i(TAG, "fakeApiRequest: launching job 1")
                    val result1 = getResultFromApi()
                    setTextOnMainThread(result1)
                }
                Log.i(TAG, "fakeApiRequest: $time1 ms")
            }

//            job1.join() // wait for job1 to finish

            val job2 = launch {
                val time2 = measureTimeMillis {
                    Log.i(TAG, "fakeApiRequest: launching job 2")
                    val result2 = getResultFromApi2()
                    setTextOnMainThread(result2)
                }
                Log.i(TAG, "fakeApiRequest: $time2 ms")
            }
        }


    }

    private suspend fun fakeApiRequest2() {
        CoroutineScope(Dispatchers.IO).launch{
            val time2 = measureTimeMillis {
                val result1 : Deferred<String> = async {
                    getResultFromApi()
                }

                val result2 : Deferred<String> = async {
                    getResultFromApi2()
                }
                setTextOnMainThread(result1.await())
                setTextOnMainThread(result2.await())

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