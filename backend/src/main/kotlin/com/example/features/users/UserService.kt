package com.example.features.users

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserService {

    fun registerUser(request: CreateUserRequest): UUID?{
        return transaction {
            try {
                val newUserID = Users.insertAndGetId {
                    it[email]=request.email
                    it[passwordHash]=request.passwordHash
                    it[authProvider]=request.authProvider
                    it[fullName]=request.fullName
                    it[role]=request.role
                }.value

                if(request.role=="Mentor"){
                    ExpertProfiles.insert {
                        it[userId] = newUserID
                        it[education]=request.education
                        it[experienceDescription]=request.experienceDescription?: ""
                        it[hourlyRate] = request.hourlyRate?.toBigDecimal() ?: 0.0.toBigDecimal()
                    }
                }

                newUserID
            } catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }
}
