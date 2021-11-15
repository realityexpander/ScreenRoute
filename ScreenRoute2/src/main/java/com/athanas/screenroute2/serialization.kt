package com.athanas.screenroute2

import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class Model( val a: Int, val b: String)

fun main() {
    val data = Model(12, "Hello world")

    // serialize an object -> JSON
//    val x = Json.encodeToString(Model.serializer(), data)
    val x = Json.encodeToString(data)
    println(x)

    val xList = Json.encodeToString(listOf(data))
    println(xList)

    val yList = Json.decodeFromString<List<Model>>(xList)
    println(yList)

    val yListList = Json.encodeToString(listOf(yList))
    println(yListList)
}