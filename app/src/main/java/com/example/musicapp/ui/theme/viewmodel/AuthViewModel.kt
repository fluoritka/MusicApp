package com.example.musicapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.model.RealmUser
import com.example.musicapp.model.SavedTrack
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.TypedRealmObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class AuthViewModel : ViewModel() {
    private val _username        = MutableStateFlow("")
    val username: StateFlow<String> = _username
    private val _password        = MutableStateFlow("")
    val password: StateFlow<String> = _password
    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // текущий залогиненный userId
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    // Realm instance
    private val realm: Realm by lazy {
        val config = RealmConfiguration.Builder(
            schema = setOf<KClass<out TypedRealmObject>>(RealmUser::class, SavedTrack::class)
        )
            .name("musicapp.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.open(config)
    }

    fun onUsernameChange(v: String)         { _username.value = v }
    fun onPasswordChange(v: String)         { _password.value = v }
    fun onConfirmPasswordChange(v: String)  { _confirmPassword.value = v }

    fun canLogin() = _username.value.isNotBlank() && _password.value.isNotBlank()
    fun canRegister() =
        _username.value.isNotBlank()
                && _password.value.isNotBlank()
                && _password.value == _confirmPassword.value

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = realm.query<RealmUser>(
                "username == $0 AND password == $1",
                _username.value, _password.value
            ).first().find()
            if (user != null) {
                _error.value = null
                _currentUserId.value = user.id
                onSuccess()
            } else {
                _error.value = "Неверный логин или пароль"
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val exists = realm.query<RealmUser>("username == $0", _username.value)
                .first().find() != null
            if (exists) {
                _error.value = "Пользователь уже существует"
                return@launch
            }
            realm.write {
                copyToRealm(RealmUser().apply {
                    id = java.util.UUID.randomUUID().toString()
                    username = _username.value
                    password = _password.value
                })
            }
            _error.value = null
            onSuccess()
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}
