package com.comet.freetester.util


fun Double.toFormattedStr() : String {
    return String.format("%.1f", this)
}

fun Double.toValueStr() : String {
    if (this == 0.0) return "_"
    return String.format("%.1f", this)
}

fun Double.toKg() : Double {
    return this * Utils.ONE_KG_IN_LB
}

fun Double.toLb() : Double {
    return this / Utils.ONE_KG_IN_LB
}

fun Double.toCurrentMetrics(isMetrics: Boolean) : Double {
    return if (isMetrics) this else toLb()
}
