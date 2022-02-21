package com.main

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class getAll {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun get(): Double {
        val currentTimestamp = System.currentTimeMillis() / 1000 // Время на by bit в секундах, млс не подходят

        val request = Request.Builder()
            .url("https://api2.bybit.com/public/linear/market/kline?symbol=BTCUSDT&resolution=15&from=" + (currentTimestamp - 31500) + "&to=$currentTimestamp")
            .build()
        println(currentTimestamp)
        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body()?.string()).get("result") as JSONObject
            val list = JSONArray(json.get("list").toString())
            println(list)

            var sum5 = 0.0
            var sum34 = 0.0

            for ((i, j) in list.reversed().withIndex()) { // Перебор массива с конца
                println(j)
                if (i != 0) {
                    if (i <= 5) { // среднее арифметическое за 5 свечей
                        sum5 += (JSONObject(j.toString()).get("high").toString()
                            .toDouble() + JSONObject(j.toString()).get("low").toString()
                            .toDouble()) / 2
                    }
                    // среднее арифметическое за 35 свечей
                    sum34 += (JSONObject(j.toString()).get("high").toString().toDouble() + JSONObject(j.toString()).get(
                        "low"
                    ).toString()
                        .toDouble()) / 2
                    if (i == 35) { // Если свейчей больше чем 35
                        break
                    }
                }
            }

            sum5 /= 5
            sum34 /= 34
            println(sum5)
            println(sum34)
            println(sum5 - sum34)
            return sum5 - sum34

        }
    }

    fun sendMessage(message: String, userID: String) {
        Unirest.setTimeouts(0, 0)
        val response: HttpResponse<String> =
            Unirest.post("https://api.telegram.org/bot5056719530:AAHbB8J5p2Bd1lIjavUTfKM02pN2Ej-BjPw/sendMessage")
                .field("chat_id", userID)
                .field("text", message)
                .asString()


    }


}