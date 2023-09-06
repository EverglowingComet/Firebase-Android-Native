package com.comet.freetester.core.local.mapper

import com.comet.freetester.core.local.entity.UserProfileEntity
import com.comet.freetester.core.remote.data.UserProfile
import com.comet.freetester.util.FirebaseUtils

class ProfileToEntityMapper : BaseMapper<UserProfile, UserProfileEntity> {
    override fun map(value: UserProfile): UserProfileEntity {
        value.run {
            return UserProfileEntity(
                uid = value.uid,
                username = value.username,
                email = value.email,
                location = value.location,
                photoUri = value.photoUri,
                firstName = value.firstName,
                lastName = value.lastName,
                country = value.country,
                countryCode = value.countryCode,
                phoneNumber = value.phoneNumber,
                gender = value.gender,
                bio = value.bio,
                weight = value.weight,
                birthday = value.birthday,
                metricUnits = value.metricUnits,
                followerIds = FirebaseUtils.getStringFromStringArray(value.followerIds),
                pendingIds = FirebaseUtils.getStringFromStringArray(value.pendingIds),
            )
        }
    }
}

class EntityToProfileMapper : BaseMapper<UserProfileEntity, UserProfile> {
    override fun map(value: UserProfileEntity): UserProfile {
        value.run {
            val result =
                UserProfile(value.uid)


            result.uid = value.uid
            result.username = value.username
            result.email = value.email
            result.location = value.location
            result.photoUri = value.photoUri
            result.firstName = value.firstName
            result.lastName = value.lastName
            result.country = value.country
            result.countryCode = value.countryCode
            result.phoneNumber = value.phoneNumber
            result.gender = value.gender
            result.bio = value.bio
            result.weight = value.weight
            result.birthday = value.birthday
            result.metricUnits = value.metricUnits
            result.followerIds = FirebaseUtils.getStringArrayFromString(value.followerIds)
            result.pendingIds = FirebaseUtils.getStringArrayFromString(value.pendingIds)

            return result
        }
    }
}