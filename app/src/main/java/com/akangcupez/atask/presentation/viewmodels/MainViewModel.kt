package com.akangcupez.atask.presentation.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akangcupez.atask.domain.UseCases
import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.utils.DispatcherProvider
import com.akangcupez.atask.utils.Resource
import com.google.firebase.ml.vision.text.FirebaseVisionText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCases: UseCases,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _calculationList = MutableStateFlow<Resource<List<Calculation>>?>(null)
    val calculationList = _calculationList.asStateFlow()

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    fun getCalculations() = viewModelScope.launch(dispatcherProvider.io) {
        useCases.calculationList().collect {
            _calculationList.value = it
        }
    }

    fun setBitmap(bmp: Bitmap?) = viewModelScope.launch {
        _bitmap.value = bmp
    }

    fun setImageUri(uri: Uri?) = viewModelScope.launch {
        _imageUri.value = uri
    }

    fun processOcr(text: FirebaseVisionText): Flow<Resource<Calculation>> = useCases.processOcr(text)

    fun insertCalculation(calculation: Calculation): Flow<Resource<Unit>> = useCases.insertCalculation(calculation)
}
