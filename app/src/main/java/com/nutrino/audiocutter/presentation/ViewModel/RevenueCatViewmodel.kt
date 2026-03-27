package com.nutrino.audiocutter.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.GetAllPackageState
import com.nutrino.audiocutter.domain.StateHandeling.IsUserProState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.revenueCat.GetAllPackagesUseCase
import com.nutrino.audiocutter.domain.UseCases.revenueCat.IsUserProUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevenueCatViewmodel @Inject constructor(
    private val getAllPackagesUseCase: GetAllPackagesUseCase,
    private val isUserProUseCase: IsUserProUseCase
):
    ViewModel(){
    private  val _getAllPackageState = MutableStateFlow(GetAllPackageState())
    val getAllPackageState = _getAllPackageState.asStateFlow()

    private val _isUserProState = MutableStateFlow(IsUserProState())
    val isUserProState = _isUserProState.asStateFlow()

    fun getAllPackageRevenueCat(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllPackagesUseCase.invoke().collect { resultState ->
                when(resultState){
                    is ResultState.Loading -> {
                        _getAllPackageState.value = GetAllPackageState(
                            isLoading = true
                        )
                    }
                    is ResultState.Error->{
                        _getAllPackageState.value = GetAllPackageState(
                            isLoading = false ,
                            error = resultState.message
                        )
                    }
                    is ResultState.Success -> {
                        _getAllPackageState.value = GetAllPackageState(
                            isLoading = false,
                            data =  resultState.data
                        )
                    }
                }

            }
        }
    }

    fun checkIsUserPro() {
        viewModelScope.launch(Dispatchers.IO) {
            isUserProUseCase.invoke().collect { resultState ->
                when (resultState) {
                    is ResultState.Loading -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = true
                        )
                    }
                    is ResultState.Error -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            error = resultState.message
                        )
                    }
                    is ResultState.Success -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            data = resultState.data
                        )
                    }
                }
            }
        }
    }


}