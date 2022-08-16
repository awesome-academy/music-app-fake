package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.BuildConfig
import com.example.zingmp3phake.utils.API_HOST
import com.example.zingmp3phake.utils.API_KEY
import com.example.zingmp3phake.utils.METHOD_GET
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GetJson {

    fun getJsonFromZINGAPI(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = METHOD_GET
        return readConnection(connection)
    }

    fun getJonFromSpotifyAPI(url: URL): String {
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = METHOD_GET
            addRequestProperty(API_KEY, BuildConfig.API_KEY_VALUE)
            addRequestProperty(API_HOST, BuildConfig.API_HOST_VALUE)
        }
        return readConnection(connection)
    }

    private fun readConnection(connection: HttpURLConnection): String {
        val stringBuilder = StringBuilder()
        val httpResult: Int = connection.responseCode
        if (httpResult == HttpURLConnection.HTTP_OK) {
            val br =
                BufferedReader(InputStreamReader(connection.getInputStream(), "utf-8"))
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            br.close()
            connection.disconnect()
        }
        return stringBuilder.toString()
    }
}
