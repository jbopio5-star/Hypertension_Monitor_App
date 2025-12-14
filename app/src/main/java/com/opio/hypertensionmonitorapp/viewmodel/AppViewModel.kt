package com.opio.hypertensionmonitorapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opio.hypertensionmonitorapp.data.BpReading
import com.opio.hypertensionmonitorapp.data.User
import com.opio.hypertensionmonitorapp.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _latestReading = MutableStateFlow<BpReading?>(null)
    val latestReading: StateFlow<BpReading?> = _latestReading.asStateFlow()

    private val _readings = MutableStateFlow<List<BpReading>>(emptyList())
    val readings: StateFlow<List<BpReading>> = _readings.asStateFlow()

    init {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _currentUser.value = user
                loadUserData(user.id)
            }
        }
    }

    suspend fun login(phone: String, pin: String): Boolean {
        val user = repository.getUserByPhoneAndPin(phone, pin) ?: return false
        _currentUser.value = user
        repository.setCurrentUser(user)
        loadUserData(user.id)
        return true
    }

    // THIS IS USED IN RegisterScreen — suppress false warning
    @Suppress("unused")
    suspend fun register(fullName: String, phone: String, patientId: String, pin: String): Boolean {
        if (repository.getUserByPhone(phone) != null) return false

        val allUsers = getAllUsers()
        if (allUsers.any { it.patientId.equals(patientId, ignoreCase = true) }) return false

        val newUser = User(fullName = fullName, phone = phone, patientId = patientId, pin = pin)
        val insertedId = repository.insertUser(newUser)

        return if (insertedId > 0L) {
            val loggedInUser = newUser.copy(id = insertedId)
            _currentUser.value = loggedInUser
            repository.setCurrentUser(loggedInUser)
            loadUserData(insertedId)
            true
        } else false
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearCurrentUser()
            _currentUser.value = null
            _readings.value = emptyList()
            _latestReading.value = null
        }
    }

    suspend fun insertReading(reading: BpReading) {
        repository.insertReading(reading)
        _currentUser.value?.let { loadUserData(it.id) }
    }

    private suspend fun loadUserData(userId: Long) {
        val userReadings = repository.getReadingsByUser(userId)
        _readings.value = userReadings
        _latestReading.value = userReadings.maxByOrNull { it.timestamp }
    }

    // THIS IS USED INSIDE register() — suppress false warning
    @Suppress("unused")
    private suspend fun getAllUsers(): List<User> = repository.getAllUsers()
}