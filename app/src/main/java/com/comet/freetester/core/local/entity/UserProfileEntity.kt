package com.comet.freetester.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserProfileEntity (
    @PrimaryKey val uid: String,
    val username: String?,
    val email: String?,
    val location: String?,
    val photoUri: String?,
    val firstName: String?,
    val lastName: String?,
    val country: String?,
    val countryCode: String?,
    val phoneNumber: String?,
    val gender: String?,
    val bio: String?,
    val weight: Double,
    val birthday: Long,
    val metricUnits: Boolean,
    val followerIds: String?,
    val pendingIds: String?,
)