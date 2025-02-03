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

        val inputData: String? = inputData.getString("nsig_sc")
        val resultFuture = jsIsolate.evaluateJavaScriptAsync(inputData ?: "")

        val outputData = workDataOf("nsig_sc_output" to resultFuture.await())
        return Result.success(outputData)
    }


}
