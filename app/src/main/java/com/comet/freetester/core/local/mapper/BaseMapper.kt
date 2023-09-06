package com.comet.freetester.core.local.mapper

interface BaseMapper<in T, out R> {
    fun map(value: T): R
}