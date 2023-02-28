package com.fini.pro.sportsvet.utils.video.videooperations

import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.CyclicBarrier

class CallBackOfQuery: CoroutineScope by MainScope() {

    fun callQuery(query: Array<String>, fFmpegCallBack: FFmpegCallBack){
        launch(Dispatchers.IO){
            val gate = CyclicBarrier(2)
            //gate.await()
            process(query, fFmpegCallBack)
            gate.await()
        }

    }

    fun cancelProcess(executionId: Long) {
        if (executionId == 0.toLong()) {
            FFmpeg.cancel(executionId)
        } else {
            FFmpeg.cancel()
        }
    }

    fun cancelProcess() {
        FFmpeg.cancel()
    }

    private fun process(query: Array<String>, ffmpegCallBack: FFmpegCallBack) {
        Config.enableLogCallback { logMessage ->
            val logs = LogMessage(logMessage.executionId, logMessage.level, logMessage.text)
            ffmpegCallBack.process(logs)
        }
        Config.enableStatisticsCallback { statistics ->
            val statisticsLog =
                Statistics(statistics.executionId, statistics.videoFrameNumber, statistics.videoFps, statistics.videoQuality, statistics.size, statistics.time, statistics.bitrate, statistics.speed)
            ffmpegCallBack.statisticsProcess(statisticsLog)
        }
        when (FFmpeg.execute(query)) {
            Config.RETURN_CODE_SUCCESS -> launch(Dispatchers.Main) {
                ffmpegCallBack.success()
            }

            Config.RETURN_CODE_CANCEL -> launch(Dispatchers.Main) {
                ffmpegCallBack.cancel()
                FFmpeg.cancel()
            }

            else -> launch(Dispatchers.Main) {
                ffmpegCallBack.failed()
                Config.printLastCommandOutput(Log.INFO)
            }
        }
    }
}