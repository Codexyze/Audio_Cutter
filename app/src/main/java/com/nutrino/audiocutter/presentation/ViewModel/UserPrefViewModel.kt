package com.nutrino.audiocutter.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.Constants.Colors
import com.nutrino.audiocutter.domain.UseCases.userPref.UserPrefUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPrefViewModel @Inject constructor(
	private val userPrefUseCase: UserPrefUseCase
) : ViewModel() {

	private val _themeSelection = MutableStateFlow(Colors.ORANGETHEME)
	val themeSelection = _themeSelection.asStateFlow()

	private val _usageCount = MutableStateFlow(0)
	val usageCount = _usageCount.asStateFlow()

	private val _lastUsageDate = MutableStateFlow("")
	val lastUsageDate = _lastUsageDate.asStateFlow()

	init {
		observeThemeSelection()
		observeUsageInfo()
	}

	private fun observeThemeSelection() {
		viewModelScope.launch(Dispatchers.IO) {
			userPrefUseCase.getThemeSelection().collect { currentTheme ->
				_themeSelection.value = currentTheme
			}
		}
	}

	private fun observeUsageInfo() {
		viewModelScope.launch(Dispatchers.IO) {
			userPrefUseCase.getUsageCount().collect { count ->
				_usageCount.value = count
			}
		}
		viewModelScope.launch(Dispatchers.IO) {
			userPrefUseCase.getLastUsageDate().collect { date ->
				_lastUsageDate.value = date
			}
		}
	}

	fun updateThemeSelection(theme: String) {
		_themeSelection.value = theme
		viewModelScope.launch(Dispatchers.IO) {
			userPrefUseCase.updateThemeSelection(theme = theme)
		}
	}
}