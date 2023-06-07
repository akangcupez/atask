package com.akangcupez.atask.domain.usecase

import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.utils.Resource
import com.akangcupez.atask.utils.calculateMath
import com.akangcupez.atask.utils.sanitizeMathOperator
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProcessOcrUseCase {

    operator fun invoke(text: FirebaseVisionText): Flow<Resource<Calculation>> = flow {
        emit(Resource.Loading())
        var error = ""
        var input = ""
        var result = 0
        if (text.textBlocks.isNotEmpty()) {
            //Only process the first block
            val block = text.textBlocks[0]
            if (block.lines.isNotEmpty()) {
                //only process the first line
                val line = block.lines[0]
                //sanitize from unwanted characters. We also ignore multiplication using brackets
                val source = line.text.lowercase().replace("[^0-9\\+\\-x\\*\\/\\:\\-]".toRegex(), "").trim()

                val pattern = "[x\\*\\/\\:\\+\\-]"
                if (source.isEmpty()) {
                    error = "Unable to process image, please try again"
                } else {
                    val numberList = source.split(pattern.toRegex()).filter { it.isNotEmpty() }
                    val symbolList = source.split("").filter { it.isNotEmpty() && it.matches(pattern.toRegex()) }
                    if (numberList.size > 1 && symbolList.isNotEmpty()) {
                        val number1 = numberList[0].toInt()
                        val number2 = numberList[1].toInt()
                        val operator = sanitizeMathOperator(symbolList[0])
                        input = "$number1$operator$number2"
                        if (operator == "/" && number2 == 0) {
                            error = "Error: division by zero is detected"
                        } else {
                            result = calculateMath(number1, number2, operator)
                        }
                    } else {
                        error = "Unable to process image, please try again"
                    }
                }
            }
        }

        if (error.isNotEmpty()) {
            emit(Resource.Error(error))
        } else {
            emit(Resource.Completed(Calculation(System.currentTimeMillis(), input = input, result = result.toString())))
        }
    }
}
