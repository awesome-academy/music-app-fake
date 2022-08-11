package com.example.zingmp3phake.data.repo.resource.remote.fetchjson

import com.example.zingmp3phake.utils.ZING_DATA
import com.example.zingmp3phake.utils.ZING_SONG
import org.json.JSONException
import org.json.JSONObject

class ParseDataWithJson {
    fun parseJsonToData(jsonObject: JSONObject?, keyEntity: String): Any {
        val data = mutableListOf<Any>()
        try {
            val jsonArray = jsonObject?.getJSONArray(keyEntity)
            for (i in 0 until (jsonArray?.length() ?: 0)) {
                val item = parseJsonToObject(jsonArray?.getJSONObject(i), keyEntity)
                if (item != null) {
                    data.add(item)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return data
    }

    private fun parseJsonToObject(jsonObject: JSONObject?, keyEntity: String): Any? {
        try {
            jsonObject?.let {
                when (keyEntity) {
                    ZING_SONG -> return ParseData().SongInfoInZingParseJson(it)
                    ZING_DATA -> return ParseData().SongDataInZingParseJson(it)
                    else -> return null
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}
