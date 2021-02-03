package com.example.kotlin.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.util.*

/**
 * Created by FlameYagami on 2016/7/12.
 */
object JsonUtils {

    /**
     * 序列化
     *
     * @param obj 对象
     * @return Json
     */
    fun serializer(obj: Any): String {
        return GsonBuilder().create().toJson(obj)
    }

    /**
     * 反序列化对象
     *
     * @param jsonData Json
     * @return 对象
     */
    inline fun <reified T> deserializer(jsonData: String): T? {
        return try {
            Gson().fromJson(jsonData, T::class.java)
        } catch (e: Exception) {
            println("Deserializer Error => ${e.message}")
            null
        }
    }

    /**
     * 反序列化数组对象
     *
     * @param jsonData Json
     * @return 数组集合
     */
    inline fun <reified T> deserializerArray(jsonData: String): List<T> {
        return try {
            val array = Gson().fromJson(jsonData, Array<T>::class.java)
            listOf(*array)
        } catch (e: Exception) {
            println("Deserializer Error => ${e.message}")
            arrayListOf()
        }
    }

    /**
     * 反序列化集合对象
     *
     * @param jsonData Json
     * @return 对象集合
     */
    inline fun <reified T> deserializerList(jsonData: String): List<T> {
        val type = object : TypeToken<ArrayList<JsonObject>>() {}.type
        val gson = Gson()
        return try {
            gson.fromJson<List<JsonObject>>(jsonData, type)
                    .map { gson.fromJson(it, T::class.java) }
                    .toList()
        } catch (e: Exception) {
            println("Deserializer Error => ${e.message}")
            arrayListOf()
        }
    }

    /**
     * 反序列化集合对象
     *
     * @param jsonReader Json
     * @return 对象集合
     */
    inline fun <reified T> deserializerList(jsonReader: Reader): List<T> {
        val type = object : TypeToken<ArrayList<JsonObject>>() {}.type
        val gson = Gson()
        return try {
            gson.fromJson<List<JsonObject>>(jsonReader, type)
                    .map { gson.fromJson(it, T::class.java) }
                    .toList()
        } catch (e: Exception) {
            println("Deserializer Error => ${e.message}")
            arrayListOf()
        }

    }
}