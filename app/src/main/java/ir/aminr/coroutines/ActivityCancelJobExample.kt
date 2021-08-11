package ir.aminr.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import ir.aminr.coroutines.databinding.ActivityExample2Binding
import kotlinx.coroutines.*

class ActivityCancelJobExample : AppCompatActivity() {

    /**
     * handle job cancellation
     * */

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 //ms

    private lateinit var job:CompletableJob

    private lateinit var binding:ActivityExample2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_example2)


        binding.jobButton.setOnClickListener {
            if(!::job.isInitialized){
                initJob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
    }


    private fun initJob(){
        binding.jobButton.text = "Start job #1"
        binding.jobCompleteText.text = ""
        job = Job()
        job.invokeOnCompletion {
            //job cancelled with error or completed
            it?.message.let {
                var message = it
                if(message.isNullOrBlank()){
                    message = "Unknown Error!"
                }
                Log.i(TAG, "$job cancelled with message : $message")
            }

        }
        binding.jobProgressBar.max = PROGRESS_MAX
        binding.jobProgressBar.progress = PROGRESS_START


    }

    fun ProgressBar.startJobOrCancel(job:Job){
        if(this.progress > 0){
            //job already started
            Log.i(TAG, "startJobOrCancel: Cancelling!")
            resetJob()
        }else{
            binding.jobButton.text = "Cancel job #1"
            CoroutineScope(Dispatchers.IO + job).launch {
                Log.i(TAG, "startJobOrCancel: Activated!")

                for (i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME/PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }

                updateText("Job completed!")
            }
        }
    }

    private fun resetJob() {
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Reset job"))
        }
        initJob()
    }

    private suspend fun updateText(text:String){
        withContext(Dispatchers.Main){
            binding.jobCompleteText.text = text
        }
    }
}