package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.utils.METHOD_GET
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GetJson {

    fun getJsonFromZINGAPI(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = METHOD_GET
        val stringBuilder = StringBuilder()
        val httpResult: Int = connection.responseCode
        if (httpResult == HttpURLConnection.HTTP_OK) {
            val br =
                BufferedReader(InputStreamReader(connection.getInputStream(), "utf-8"))
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        }
        return stringBuilder.toString()
    }
}
