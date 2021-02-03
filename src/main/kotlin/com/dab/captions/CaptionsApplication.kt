package com.dab.captions

import com.example.kotlin.utils.TimeUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.ArrayList

@SpringBootApplication
class CaptionsApplication : CommandLineRunner {
    override fun run(vararg args: String?) {
        val path: String
        val rollTime: Int
        val scanner = Scanner(System.`in`)
        println("请输入字幕的路径:")
        var params = ""
        while (true) {
            params = scanner.nextLine()
            if (File(params).exists()) {
                path = params
                break
            } else println("路径无效, 请重新输入字幕的路径:")
        }
        println("请输入折越的时间(ms):")
        while (true) {
            params = scanner.nextLine()
            if (null != params.toIntOrNull()) {
                rollTime = params.toInt()
                break
            } else println("时间无效, 请重新输入折越的时间(ms):")
        }
        convert(path, rollTime)
        run(null)
    }
}

fun main(args: Array<String>) {
    runApplication<CaptionsApplication>(*args)
}

fun convert(path: String, rollTime: Int) {
    val savePath = "D:\\${TimeUtils.dateToString(Date(), TimeUtils.Format_Time_Ext)}.txt"
    val format = "H:mm:ss.SS"
    val startMark = "Dialogue:"
    val captions = loadCaptions(path).map {
        it.takeIf {
            it.contains(startMark) && it.split(",").count() >= 3
        }?.apply {
            val splits = this.split(",")
            val startDate = TimeUtils.stringToDate(splits[1], format)
            val startRoll = rollTime(startDate, rollTime)
            val startString = TimeUtils.dateToString(startRoll, format)
            val endDate = TimeUtils.stringToDate(splits[2], format)
            val endRoll = rollTime(endDate, rollTime)
            val endString = TimeUtils.dateToString(endRoll, format)
			it.replace(splits[1], startString).replace(splits[2], endString)
        } ?: ""
    }.filter {
        it.isNotBlank()
    }.toList()
    appendFile(captions, savePath)
    println("转换完成, 默认路径: $savePath")
    println()
}

fun loadCaptions(path: String): List<String> {
    val file = File(path)
    if (!file.exists()) {
        return arrayListOf()
    }
    val result: ArrayList<String> = ArrayList()
    val inputStream = File(path).inputStream()
    inputStream.bufferedReader(Charsets.UTF_16LE).forEachLine { line ->
        result.add(line)
    }
    return result
}

fun rollTime(date: Date?, millisecond: Int): Date {
    val calendar: Calendar = GregorianCalendar()
    calendar.time = date
    calendar.add(Calendar.MILLISECOND, millisecond)
    return calendar.time
}

fun createDirectory(outputPath: String): Boolean {
    return File(outputPath).takeIf {
        !it.exists()
    }?.mkdirs()?.also {
        println("createDirectory => $outputPath:$it")
    } ?: false
}

fun appendFile(content: List<String>, filePath: String) {
    createDirectory(File(filePath).parent)
    var fw: FileWriter? = null
    try {
        fw = FileWriter(File(filePath), true).let { fileWriter ->
            content.forEach {
                fileWriter.write("$it \r\n")
            }
            fileWriter
        }
    } catch (ignored: Exception) {
        println("appendFile => ${ignored.message}")
    } finally {
        try {
            fw?.close()
        } catch (ignored: Exception) {
            println("appendFile => ${ignored.message}")
        }
    }
}
