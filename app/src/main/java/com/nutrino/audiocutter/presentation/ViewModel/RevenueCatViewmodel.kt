package com.nutrino.audiocutter.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.GetAllPackageState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.revenueCat.GetAllPackagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevenueCatViewmodel @Inject constructor(private val getAllPackagesUseCase: GetAllPackagesUseCase):
    ViewModel(){
    private  val _getAllPackageState = MutableStateFlow(GetAllPackageState())
    val getAllPackageState = _getAllPackageState.asStateFlow()

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


}