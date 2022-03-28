package com.company.calculator

import android.util.Log
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.pow
import kotlin.math.sqrt

data class CalculatorData(
    var exprsn: StringBuilder = StringBuilder().append(0),
    var secondNumber: BigDecimal = BigDecimal(0.0),
    var firstNumber: BigDecimal = BigDecimal(0.0),
    var result: BigDecimal? = null,
    var operation: Char? = null,
    var lastOperation: String? = null,
    var pointFlag1: Boolean = false,
    var pointFlag2: Boolean = false,
    var dividedBy0Flag: Boolean = false,
    var percentExpression: String = "",
    var percentFlag: Boolean = false,
    val operations: Array<Char> = arrayOf(
        '+', '-', 0x00D7.toChar(),
        0x00F7.toChar(), 0x221A.toChar(), '%', '^'
    )
) {

    fun equalsProcessing() {
        // Если есть какая-то операция (operation != null),
        // тогда проверяем какая и вычисляем выражение
        if (operation != null && exprsn[exprsn.lastIndex] !in operations) {

            if (exprsn.last() == '.') exprsn.append(0)

            val operationIndex: Int = exprsn.indexOf(operation!!)
            firstNumber = BigDecimal(exprsn.substring(0, operationIndex))
            secondNumber = BigDecimal(
                exprsn.substring(operationIndex + 1, exprsn.length))
            Log.i("Divide", "$firstNumber and $secondNumber")
            when (operation) {
                '+' -> {
                    result = firstNumber.add(secondNumber, MathContext.DECIMAL32)
                }
                '-' -> {
                    result = firstNumber.subtract(secondNumber, MathContext.DECIMAL32)
                }
                0x00D7.toChar() -> {
                    result = firstNumber.multiply(secondNumber, MathContext.DECIMAL32)
                    Log.i("Divide", "$result")
                }
                0x00F7.toChar() -> {
                    result = if (secondNumber.toDouble() != 0.0) {
                        firstNumber.divide(secondNumber, MathContext.DECIMAL32)
                    } else {
                        dividedBy0Flag = true
                        null
                    }
                }
                0x221A.toChar() -> {
                    result = sqrt(secondNumber.toDouble()).toBigDecimal().multiply(firstNumber, MathContext.DECIMAL32)
                }
                '%' -> {
                    result = firstNumber.divide(100.toBigDecimal()).multiply(secondNumber, MathContext.DECIMAL32)
                }
                '^' -> {
                    result = firstNumber.pow(secondNumber.toInt()).multiply(1.toBigDecimal(), MathContext.DECIMAL32)
                }
            }
        }
    }

    fun deleteOne() {

        // Если последний символ - это операция,
        // то обнуляем переменную, хранящую действующую операцию
        if (exprsn.last() in operations) operation = null

        // Иначе, если последний символ - '.', удаляем её и снимаем флаг точки
        else if (exprsn.last() == '.') {

            // Если это 2 число, удаляем флаг 2-ой точки
            if (operation != null) pointFlag2 = false
            // Иначе - это 1 число, а значит удаляем флаг 1-ой точки
            else pointFlag1 = false

        }

        // Удаляем последний символ из строки и выводим строку на экран
        if (exprsn.length > 1)
            exprsn.deleteCharAt(exprsn.lastIndex)
        else exprsn.clear().append(0)

    }
}