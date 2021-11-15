package com.athanas.screenroute2

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// This is an Android Module and can contain Android platform stuff

// To run from command line: kotlin -cp ScreenRoute.jar com.athanas.screenroute.Main

open class ScreenRoute2(val route: String) {
    // route format: screen_name/{arg1}/{arg2}/{arg3}
    // using strings
    fun withRequiredArgs(vararg value: String): String {
        return buildString {
            append(route)
            value.forEach { arg->
                append("/$arg")
            }
        }
    }

    // route format: screen_name/{arg1}/{arg2}/{arg3}
    fun withRequiredArgs(vararg value: NavArg.Required): String {
        return buildString {
            append(route)
            value.forEach { (arg)->
                append("/$arg")
            }
        }
    }

    // route format: screen_name?arg1={arg1}/arg2={arg2}/arg3={arg3}
    fun withOptionalArgs(vararg args: NavArg.Optional): String {
        var firstArg: Boolean = true

        return buildString {
            append(route)
            args.forEach { (k,v) ->
                append(if (firstArg) "?" else "/")
                firstArg = false
                append("$k=$v")
            }
        }
    }

    // route format: screen_name?arg1={arg1}/arg2/arg3={arg3}/arg4
    fun withArgs(vararg args: NavArg): String {
        var firstArg: Boolean = true

        return buildString {
            append(route)
            args.forEach { arg ->
                when(arg) {
                    is NavArg.Required ->
                        append("/${arg.arg}")
                    is NavArg.Optional -> {
                        append(if (firstArg) "?" else "/")
                        firstArg = false
                        append("${arg.key}=${arg.value}")
                    }
                }
            }
        }
    }

    // route format: screen_name/http%3A%2F%2Falphaone.me%2F
    fun withUrlArg(urlArg: String): String {
        return buildString {
            append(route)
            append("/")
            append(
                URLEncoder.encode(
                    urlArg, // http://alphaone.me/
                    StandardCharsets.UTF_8.toString()
                ))
        }
    }

    sealed class NavArg {
        // for Required arguments
        lateinit var arg: String

        // for Optional arguments
        lateinit var key: String
        lateinit var value: String

        operator fun component1(): String { // allows for destructuring (k,v) or (arg)
            if (::key.isInitialized) return key

            return arg
        }
        operator fun component2(): String { // allows for destructuring
            return value
        }

        class Required(arg: String): NavArg() {
            init {
                this.arg = arg
            }
        }

        class Optional(key: String = "", value: String): NavArg() {
            init {
                this.key = key
                this.value = value
            }
        }
    }

}



// Test class
sealed class ___ScreenTest2(route:String): ScreenRoute2(route) {
    object MainScreen : ___ScreenTest("main_screen")
    object DetailScreen : ___ScreenTest("detail_screen")
}

// Test ScreenRoute class (make sure VM has -ea turned on to see assertions)
fun main(args: Array<String> =  arrayOf()) {
    var out = ""

    out = ___ScreenTest2.MainScreen.withRequiredArgs("abc", "123", "xyz")
    println("withRequiredArgs=$out")
    assert(out == "main_screen/abc/123/xyz")

    out = ___ScreenTest2.MainScreen.withRequiredArgs(
        ScreenRoute2.NavArg.Required("abc" ),
        ScreenRoute2.NavArg.Required("123" )
    )
    println("withRequiredArgs usingArgs=$out")
    assert(out == "main_screen/abc/123")

    out = ___ScreenTest2.DetailScreen.withOptionalArgs(
        ScreenRoute2.NavArg.Optional("abc", "hey"),
        ScreenRoute2.NavArg.Optional("123", "xyz")
    )
    println("withOptionalArgs=$out")
    assert(out == "detail_screen?abc=hey/123=xyz")

    out = ___ScreenTest2.MainScreen.withArgs(
        ScreenRoute2.NavArg.Optional("userName", "abc"),
        ScreenRoute2.NavArg.Required("my Name"),
        ScreenRoute2.NavArg.Optional("screen", "xyz")
    )
    println("withMixedArgs=$out")
    assert(out == "main_screen?userName=abc/my Name/screen=xyz" )

    out = ___ScreenTest2.DetailScreen.withUrlArg(
        "http://alphaone.me/"
    )
    println("withUrlArg=$out")
    println("withUrlArg decoded=${URLDecoder.decode(out, StandardCharsets.UTF_8.toString())}")
    assert(out == "detail_screen/http%3A%2F%2Falphaone.me%2F")
    assert(URLDecoder.decode(out, StandardCharsets.UTF_8.toString()) == "detail_screen/http://alphaone.me/")

}

class Main { // Must have a Main class to run from kotlin command line
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            println("Testing ScreenRoute2 library....")
            main()
        }
    }
}