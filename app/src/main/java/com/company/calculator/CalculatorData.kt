package com.company.calculator

import android.util.Log
import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.sqrt

data class CalculatorData(
    var exprsn: StringBuilder = StringBuilder().append(0),
    var firstNumber: BigDecimal? = null,
    var secondNumber: BigDecimal? = null,
    var result: BigDecimal? = null,
    var operation: Char? = null,
    var nextOperation: Char? = null,
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

    fun buildString(input: Char) = with(exprsn) {
//        Если входной знак - число, тогда добавляем его
        if (input in '0'..'9') {
//            if (cd.exprsn.length >= 3 && cd.operation != null) Log.i("SecondNumber", cd.exprsn.indexOf(cd.operation!!).toString())
            // Если длина строки равна 1 и первый символ в строке это 0, тогда очищаем строку
            if (length == 1 && first() == '0')
                clear()

            // Добавляем цифру, выводим на экран
            append(input)

        } // Иначе, если входной знак '.' и хотя бы в 1 числе нет точки,
        // тогда добавляем точку
        else if (input == '.' && (!pointFlag1 || !pointFlag2)) {
            pointProcessing()
        }
        // Иначе, если входной знак '=', тогда вычисляем выражение
        else if (input == '=') {
            operationsProcessing()
        }
        // Иначе, если входной знак - это одна из операций,
        // вычисляем выражение, если это уже 2-ая операция
        // а если первая, то просто добавляем знак и выводим
        else if (input in operations) {
            operation(input)
        } else {

        }
    }

    private fun pointProcessing() = with(exprsn) {

        // Если мы работаем с 1 числом и у нас нет точки в 1 числе, тогда
        // добавляем точку, вешаем флаг точки на 1 число
        if (!pointFlag1 && operation == null) {
            append('.')
            pointFlag1 = true
        }
        // Иначе, если мы работаем со 2 числом и у нас нет точки во 2 числе,
        // тогда...
        else if (!pointFlag2 && operation != null) {

            // Если последний знак - это операция, тогда добавляем 0
            if (last() in operations) {
                append(0)
            }
            // Добавляем точку и вешаем флаг точки на 2 число
            append('.')
            pointFlag2 = true
        }

    }

    private fun operation(usedOperation: Char) = with(exprsn) {


        // Если уже есть какая-то операция...
        if (operation != null) {
            when {

                // 1) .. и эта операция не является '%',
                // а следующая операция является '%',
                // тогда считаем % 2-го числа и выполняем операцию
                usedOperation == '%' && operation != '%'
                        && last() !in operations -> percentInTheEnd()

                // 2) .. и когда последним символом является любая из операций,
                // удаляем эту операцию, заменяя её на новую
                last() in operations ->
                    deleteCharAt(lastIndex)

                // 3) .. и когда последним символом является точка,
                // удаляем её, снимаем флаг точки со 2-го числа,
                // если вдруг он там был и вычисляем выражение
                last() == '.' -> {
                    deleteCharAt(lastIndex)
                    pointFlag2 = false
                    operationsProcessing()
                }

                // 4).. и когда последним символом не является
                // ни какая-то операция, ни точка, вычисляем выражение
                else -> {
                    operationsProcessing()
                }
            }
            // Если ещё нет никакой операции в строке..
        } else {
            if (last() == '.') {
                // 1) .. и последним символом в строке является точка,
                // удаляем флаг с 1 точки и удаляем точку
                pointFlag1 = false
                deleteCharAt(lastIndex)
            }

        }
//        Log.i("percentFlag", percentFlag.toString())
        // Если наша операция не процент, тогда добавляем операцию в конец
        if (!percentFlag) {
            append(usedOperation)
            operation = usedOperation
        }
    }

    private fun percentInTheEnd() = with(exprsn) {

        // Записываем выражение с процентом в конце,
        // чтобы потом вывести его в верхнюю строку
        percentExpression = "$this%="

        // Вытаскиваем 1 и 2 числа из выражения
        val firstNumberPart = substring(0, indexOf(operation!!)).toBigDecimal()
        var secondNumberPart = substring(indexOf(operation!!) + 1, length).toBigDecimal()

        // Высчитываем % 2-го числа
        secondNumberPart = firstNumberPart
            .divide(100.toBigDecimal(), MathContext.DECIMAL64)
            .multiply(secondNumberPart, MathContext.DECIMAL64)

        // Удаляем из выражения 2 число с % и добавляем вычисленное число
        delete(indexOf(operation!!) + 1, length).append(secondNumberPart)

        /*(exprsn.length >= 2 && exprsn.last() == '0' &&
            exprsn[exprsn.lastIndex - 1] == '.')
            exprsn.delete(exprsn.lastIndex - 1, exprsn.length)*/

        percentFlag = true
        operationsProcessing()
    }

    private fun operationsProcessing() = with(exprsn) {
        // Если есть какая-то операция и она не является
        // последним символом, тогда..
        if (operation != null && last() !in operations) {

            // Если последний символ - это точка, добавить 0
            if (last() == '.') append(0)

            // Вычисляем индекс операции
            val operationIndex: Int = indexOf(operation!!, 1)
            // Вытаскиваем из строки 1 и 2 числа
            firstNumber = BigDecimal(substring(0, operationIndex), MathContext.DECIMAL64)
            secondNumber = BigDecimal(substring(operationIndex + 1, length), MathContext.DECIMAL64)
            Log.i("Divide", "$firstNumber and $secondNumber")

            // Вычислеям выражение в зависимости от операции
            when (operation) {
                '+' -> {
                    result = firstNumber?.add(secondNumber, MathContext.DECIMAL64)
                }
                '-' -> {
                    result = firstNumber?.subtract(secondNumber, MathContext.DECIMAL64)
                }
                0x00D7.toChar() -> {
                    result = firstNumber?.multiply(secondNumber, MathContext.DECIMAL64)
                    Log.i("Divide", "$result")
                }
                0x00F7.toChar() -> {
                    result = if (secondNumber?.toDouble() != 0.0) {
                        firstNumber?.divide(secondNumber, MathContext.DECIMAL64)
                    } else {
                        dividedBy0Flag = true
                        null
                    }
                }
                0x221A.toChar() -> {
                    result = secondNumber?.toDouble()
                        ?.let {
                            sqrt(it).toBigDecimal().multiply(firstNumber, MathContext.DECIMAL64)
                        }

                }
                '%' -> {
                    result = firstNumber?.divide(100.toBigDecimal())
                        ?.multiply(secondNumber, MathContext.DECIMAL64)
                }
                '^' -> {
                    result = secondNumber?.toInt()
                        ?.let {
                            firstNumber?.pow(it)?.multiply(1.toBigDecimal(), MathContext.DECIMAL64)
                        }
                }
            }

        }
    }

    fun deleteOne() = with(exprsn) {

        // Если последний символ - это операция,
        // то обнуляем переменную, хранящую действующую операцию
        if (last() in operations) operation = null

        // Иначе, если последний символ - '.', удаляем её и снимаем флаг точки
        else if (last() == '.') {

            // Если это 2 число, удаляем флаг 2-ой точки
            if (operation != null) pointFlag2 = false
            // Иначе - это 1 число, а значит удаляем флаг 1-ой точки
            else pointFlag1 = false

        }

        // Удаляем последний символ из строки и выводим строку на экран,
        // если длина строки больше 1
        if (length > 1)
            deleteCharAt(lastIndex)
        // Иначе очищаем и добавляем 0
        else clear().append(0)

        // userView()

    }

    fun clearAll() {

        // Полное обнуление данных калькулятора
        exprsn.clear().append(0)
        firstNumber = BigDecimal(0.0)
        secondNumber = null
        result = null
        operation = null
        nextOperation = null
        pointFlag1 = false
        pointFlag2 = false
        dividedBy0Flag = false
        percentExpression = ""
        percentFlag = false

        // enteredNumber.text = ""
        // userView()
    }

}