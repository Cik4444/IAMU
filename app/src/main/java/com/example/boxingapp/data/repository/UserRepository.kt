package com.example.boxingapp.data.repository

import com.example.boxingapp.data.dao.UserDao
import com.example.boxingapp.data.entity.UserEntity

class UserRepository(private val userDao: UserDao) {
    suspend fun register(username: String, password: String): Boolean {
        val existing = userDao.getByUsername(username)
        return if (existing == null) {
            userDao.insert(UserEntity(username, password))
            true
        } else {
            false
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val user = userDao.getByUsername(username)
        return user?.password == password
    }
}
