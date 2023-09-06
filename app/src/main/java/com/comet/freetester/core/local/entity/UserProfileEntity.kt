package com.comet.freetester.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserProfileEntity (
    @PrimaryKey() var uid: String,
    var username: String?,
    var email: String?,
    var location: String?,
    var photoUri: String?,
    var firstName: String?,
    var lastName: String?,
    var country: String?,
    var countryCode: String?,
    var phoneNumber: String?,
    var gender: String?,
    var bio: String?,
    var weight: Double,
    var birthday: Long,
    var metricUnits: Boolean,
    var followerIds: String?,
    var pendingIds: String?,
)