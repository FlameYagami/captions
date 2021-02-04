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
        println("字幕时间轴偏移工具启动, 默认使用UTF_16LE编码")
        val scanner = Scanner(System.`in`)
        start(scanner)
    }

    private fun start(scanner: Scanner){
        val path: String
        val rollTime: Int
        println("请输入字幕路径:")
        var params = ""
        while (true) {
            params = scanner.nextLine()
            if (File(params).exists()) {
                path = params
                break
            } else println("请重新输入有效的字幕路径:")
        }
        println("请输入偏移时间(ms):")
        while (true) {
            params = scanner.nextLine()
            if (null != params.toIntOrNull()) {
                rollTime = params.toInt()
                break
            } else println("请重新输入有效的偏移时间(ms):")
        }
        convert(path, rollTime)
        start(scanner)
    }
}

fun main(args: Array<String>) {
    runApplication<CaptionsApplication>(*args)
}

fun convert(path: String, rollTime: Int) {
    val savePath = "D:\\${TimeUtils.dateToString(Date(), TimeUtils.Format_Time_Ext)}.txt"
    val format = "H:mm:ss.SSS"
    val startMark = "Dialogue:"
    val captions = loadCaptions(path).map {
        it.takeIf {
            it.contains(startMark) && it.split(",").count() >= 3
        }?.let { it ->
            val splits = it.split(",")
            val startDate = TimeUtils.stringToDate(splits[1] + "0", format)
            val startRoll = rollTime(startDate, rollTime)
            val startString = TimeUtils.dateToString(startRoll, format).substring(0, 10)
            val endDate = TimeUtils.stringToDate(splits[2] + "0", format)
            val endRoll = rollTime(endDate, rollTime)
            val endString = TimeUtils.dateToString(endRoll, format).substring(0, 10)
            it.replace(splits[1], startString).replace(splits[2], endString)
        } ?: ""
    }.filter {
        it.isNotBlank()
    }.toList()
    appendFile(captions, savePath)
    println("字幕时间轴偏移完成: $savePath")
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
