import android.app.Application
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.Timer
import java.util.TimerTask

class ResetService : Service() {

    var shouldResend = false
        private set

    private val handler = Handler(Looper.getMainLooper())
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        startTimer()
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    shouldResend = true
                    timer?.cancel()
                }
            }

        }, 6000, 6000)
    }

    fun resetTimer() {
        shouldResend = false
        timer?.cancel()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        instance = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        @Volatile
        private var instance: ResetService? = null

        val shouldResend: Boolean
            get() = instance?.shouldResend ?: false

        fun resetTimer() {
            instance?.resetTimer()
        }

        fun startService(application: Application) {
            if (instance == null) {
                val intent = Intent(application, ResetService::class.java)
                application.startService(intent)
            }
        }

        fun stopService(application: Application) {
            instance?.let {
                val intent = Intent(application, ResetService::class.java)
                application.stopService(intent)
            }
        }
    }
}
