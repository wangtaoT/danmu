package net.codeages.live.danmaku.util

import android.content.Context
import android.content.res.Resources

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by retamia on 2017/11/7.
 */

object TextResourceReader {
    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        val body = StringBuilder()

        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var nextLine = bufferedReader.readLine()

            while ((nextLine) != null) {
                body.append(nextLine)
                body.append('\n')
                nextLine = bufferedReader.readLine()
            }

        } catch (ex: IOException) {
            throw RuntimeException("cloud not open resource: $resourceId", ex)
        } catch (notFoundEx: Resources.NotFoundException) {
            throw RuntimeException("resource not found: $resourceId", notFoundEx)
        }

        return body.toString()
    }
}