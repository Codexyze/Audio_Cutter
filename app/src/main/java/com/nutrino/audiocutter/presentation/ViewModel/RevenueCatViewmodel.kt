package com.nutrino.audiocutter.presentation.ViewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrino.audiocutter.domain.StateHandeling.BuyPremiumPackageState
import com.nutrino.audiocutter.domain.StateHandeling.GetAllPackageState
import com.nutrino.audiocutter.domain.StateHandeling.GetAppUserIdState
import com.nutrino.audiocutter.domain.StateHandeling.IsUserProState
import com.nutrino.audiocutter.domain.StateHandeling.ResultState
import com.nutrino.audiocutter.domain.UseCases.revenueCat.BuyPremiumPackageUseCase
import com.nutrino.audiocutter.domain.UseCases.revenueCat.GetAllPackagesUseCase
import com.nutrino.audiocutter.domain.UseCases.revenueCat.GetAppUserIdUseCase
import com.nutrino.audiocutter.domain.UseCases.revenueCat.IsUserProUseCase
import com.revenuecat.purchases.Package
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevenueCatViewmodel @Inject constructor(
    private val getAllPackagesUseCase: GetAllPackagesUseCase,
    private val isUserProUseCase: IsUserProUseCase,
    private val buyPremiumPackageUseCase: BuyPremiumPackageUseCase,
    private val getAppUserIdUseCase: GetAppUserIdUseCase
):
    ViewModel(){
    private  val _getAllPackageState = MutableStateFlow(GetAllPackageState())
    val getAllPackageState = _getAllPackageState.asStateFlow()

    private val _isUserProState = MutableStateFlow(IsUserProState())
    val isUserProState = _isUserProState.asStateFlow()

    private val _buyPremiumPackageState = MutableStateFlow(BuyPremiumPackageState())
    val buyPremiumPackageState = _buyPremiumPackageState.asStateFlow()

    private val _getAppUserIdState = MutableStateFlow(GetAppUserIdState())
    val getAppUserIdState = _getAppUserIdState.asStateFlow()

    fun getAllPackageRevenueCat(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllPackagesUseCase.invoke().collect { resultState ->
                when(resultState){
                    is ResultState.Loading -> {
                        _getAllPackageState.value = _getAllPackageState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Error->{
                        _getAllPackageState.value = _getAllPackageState.value.copy(
                            isLoading = false,
                            error = resultState.message
                        )
                    }
                    is ResultState.Success -> {
                        _getAllPackageState.value = GetAllPackageState(
                            isLoading = false,
                            data = resultState.data,
                            error = null
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
                        _isUserProState.value = _isUserProState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Error -> {
                        _isUserProState.value = _isUserProState.value.copy(
                            isLoading = false,
                            error = resultState.message
                        )
                    }
                    is ResultState.Success -> {
                        _isUserProState.value = IsUserProState(
                            isLoading = false,
                            data = resultState.data,
                            error = null
                        )
                    }
                }
            }
        }
    }

    fun buyPremiumPackage(activity: Activity, selectedPackage: Package) {
        viewModelScope.launch(Dispatchers.IO) {
            buyPremiumPackageUseCase.invoke(
                activity = activity,
                selectedPackage = selectedPackage
            ).collect { resultState ->
                when (resultState) {
                    is ResultState.Loading -> {
                        _buyPremiumPackageState.value = _buyPremiumPackageState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Error -> {
                        _buyPremiumPackageState.value = _buyPremiumPackageState.value.copy(
                            isLoading = false,
                            error = resultState.message
                        )
                        checkIsUserPro()
                    }

                    is ResultState.Success -> {
                        _buyPremiumPackageState.value = BuyPremiumPackageState(
                            isLoading = false,
                            data = resultState.data,
                            error = null
                        )
                        checkIsUserPro()
                    }
                }
            }
        }
    }

    fun getAppUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            getAppUserIdUseCase.invoke().collect { resultState ->
                when (resultState) {
                    is ResultState.Loading -> {
                        _getAppUserIdState.value = _getAppUserIdState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Error -> {
                        _getAppUserIdState.value = _getAppUserIdState.value.copy(
                            isLoading = false,
                            error = resultState.message
                        )
                    }
                    is ResultState.Success -> {
                        _getAppUserIdState.value = GetAppUserIdState(
                            isLoading = false,
                            data = resultState.data,
                            error = null
                        )
                    }
                }
            }
        }
    }


}
