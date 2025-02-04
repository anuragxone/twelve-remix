package org.lineageos.twelve.workers

import android.content.Context
import androidx.javascriptengine.JavaScriptSandbox
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.guava.await


class JavascriptWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val jsSandbox = JavaScriptSandbox
            .createConnectedInstanceAsync(applicationContext)
            .await()
        val jsIsolate = jsSandbox.createIsolate()
        val inputData: String? = inputData.getString("sc")
        val resultFuture = jsIsolate.evaluateJavaScriptAsync(inputData ?: "")

        val outputData = workDataOf("sc_output" to resultFuture.await())
        jsSandbox.close()
        return Result.success(outputData)
    }


}
