package com.company.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.calculator.databinding.ActivityMainBinding
import java.math.MathContext

class MainActivity : AppCompatActivity() {

    private lateinit var cd: CalculatorData

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?): Unit = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)
        cd = CalculatorData()
        val viewIds = arrayOf(
            number0, number1, number2, number3, number4,
            number5, number6, number7, number8, number9, plus, minus,
            multiply, divide, point, equals, clearAll, squareRoot, degree, percent
        )

        clearAll.setOnLongClickListener {
            clearAll()
            true
        }

        val numbersOnCl = idProcessing()

        for (item in viewIds) {
            item.setOnClickListener(numbersOnCl)
        }

    }

    private fun idProcessing(): View.OnClickListener = with(binding) {
        val numbersOnCl = View.OnClickListener {
            when (it.id) {
                number0.id -> buildString('0')
                number1.id -> buildString('1')
                number2.id -> buildString('2')
                number3.id -> buildString('3')
                number4.id -> buildString('4')
                number5.id -> buildString('5')
                number6.id -> buildString('6')
                number7.id -> buildString('7')
                number8.id -> buildString('8')
                number9.id -> buildString('9')
                clearAll.id -> deleteOne()
                plus.id -> buildString('+')
                minus.id -> buildString('-')
                multiply.id -> buildString(0x00D7.toChar())
                divide.id -> buildString(0x00F7.toChar())
                point.id -> buildString('.')
                equals.id -> buildString('=')
                squareRoot.id -> buildString(0x221A.toChar())
                percent.id -> buildString('%')
                degree.id -> buildString('^')
            }
        }
        return numbersOnCl
    }

    private fun buildString(input: Char) = with(binding) {
//        Если входной знак - число, тогда добавляем его
        if (input in '0'..'9') {
//            if (cd.exprsn.length >= 3 && cd.operation != null) Log.i("SecondNumber", cd.exprsn.indexOf(cd.operation!!).toString())
    // Если длина строки равна 1 и первый символ в строке это 0, тогда очищаем строку
    if (cd.exprsn.length == 1 && cd.exprsn[0] == '0')
                cd.exprsn.clear()

            // Иначе, если есть какая-то операция и есть '.' во 2 числе,
            // тогда
            /*else if (cd.operation != null && !cd.pointFlag2 &&
                (cd.exprsn.length - cd.exprsn.indexOf(cd.operation!!) >= 2) &&
                    cd.exprsn[cd.exprsn.indexOf(cd.operation!!) + 1] == '0')
                        cd.exprsn.deleteCharAt(cd.exprsn.indexOf(cd.operation!!) + 1)*/

            // Добавляем цифру, выводим на экран
            cd.exprsn.append(input)
            result.text = cd.exprsn

        } // Иначе, если входной знак '.' и хотя бы в 1 числе нет точки,
        // тогда добавляем точку
        else if (input == '.' && (!cd.pointFlag1 || !cd.pointFlag2)) {
            pointProcessing()
        }
        // Иначе, если входной знак '=', тогда вычисляем выражение
        else if (input == '=') {

            equalsProcessing()

        }
        // Иначе, если входной знак - это одна из операций,
        // вычисляем выражение, если это уже 2-ая операция
        // а если первая, то просто добавляем знак и выводим
        else if (input in cd.operations){
            operation(input)
        }

    }

    private fun pointProcessing() = with(binding) {

        // Если мы работаем с 1 числом и у нас нет точки в 1 числе, тогда
        // добавляем точку, вешаем флаг точки на 1 число
        if (!cd.pointFlag1 && cd.operation == null) {
            cd.exprsn.append('.')
            cd.pointFlag1 = true
        }
        // Иначе, если мы работаем со 2 числом и у нас нет точки во 2 числе,
        // тогда...
        else if (!cd.pointFlag2 && cd.operation != null) {

            // Если последний знак - это операция, тогда добавляем 0
            if (cd.exprsn.last() in cd.operations) {
                cd.exprsn.append(0)
            }
            // Добавляем точку и вешаем флаг точки на 2 число
            cd.exprsn.append('.')
            cd.pointFlag2 = true
            Log.i("Divide", "${cd.operation}")
        }

        // Выводим результат в независимости от того куда мы добавили точку
        result.text = cd.exprsn
    }

    private fun deleteOne() = with (binding){

        cd.deleteOne()
        result.text = cd.exprsn

        /*if (cd.exprsn.length > 2 && cd.exprsn[cd.exprsn.length - 2] == '.') {
            cd.exprsn.delete(cd.exprsn.length - 1, cd.exprsn.length)

        }

        if (cd.exprsn.length > 1) {
            val temp = cd.exprsn.substring(
                cd.exprsn.length - 2,
                cd.exprsn.length - 1
            ).toInt()
            cd.exprsn.delete(
                cd.exprsn.length - 2,
                cd.exprsn.length
            )
            buildString(temp.toChar())
        } else {
            cd.exprsn.delete(0, 1)
            buildString('0')
        }*/
    }

    private fun clearAll() = with(binding) {
        cd = CalculatorData()
        /*cd.operation = null
        cd.secondNumber = 0.0
        cd.firstNumber = 0.0
        cd.result = 0.0
        cd.lastOperation = null
        cd.pointFlag1 = false
        cd.pointFlag2 = false
        cd.dividedBy0Flag = false
        cd.exprsn.clear()*/
        enteredNumber.text = ""
        result.text = ""
        buildString('0')
    }

    private fun operation(usedOperation: Char) = with(binding) {


        // Если уже есть какая-то операция...
        if (cd.operation != null) {
            when {

                // .. и эта операция не является '%',
                // а следующая операция является '%',
                // тогда считаем % 2-го числа и выполняем операцию
                usedOperation == '%' && cd.operation != '%' -> percentInTheEnd()

                // .. и когда последним символом является любая из операций,
                // удаляем эту операцию, заменяя её на новую
                cd.exprsn.last() in cd.operations ->
                    cd.exprsn.deleteCharAt(cd.exprsn.lastIndex)
                    // .. и когда последним символом является точка,
                    // удаляем её, снимаем флаг точки со 2-го числа,
                    // если вдруг он там был и вычисляем выражение
                cd.exprsn.last() == '.' -> {
                    cd.pointFlag2 = false
                    cd.exprsn.deleteCharAt(cd.exprsn.lastIndex)
                    equalsProcessing()
                }

                // ..и последним символом не является ни какая-то операция ни точка,
                // вычисляем выражение
                else -> {
                    equalsProcessing()
                }
            }
        } else {
            if (cd.exprsn.last() == '.') {
                cd.pointFlag1 = false
                cd.exprsn.deleteCharAt(cd.exprsn.lastIndex)
            } else if (cd.exprsn.last() in cd.operations) {
                cd.exprsn.deleteCharAt(cd.exprsn.lastIndex)
            }

        }

        if (!cd.percentFlag) {
            cd.exprsn.append(usedOperation)
            cd.operation = usedOperation
            result.text = cd.exprsn
        }
        cd.percentFlag = false
    }

    private fun equalsProcessing() = with (binding){
        cd.equalsProcessing()  // Вычисляем выражение

        // Если вычисление дало какой-то результат
        // выводим результат и выражение на экран
        if (cd.result != null) {
            if (!cd.percentFlag) enteredNumber.text = cd.exprsn.append('=')
            else enteredNumber.text = cd.percentExpression

            cd.exprsn.clear().append(cd.result)


            /*if (cd.exprsn.length > 2 && cd.exprsn.last() == '0')
                cd.exprsn.delete(cd.exprsn.lastIndex - 1, cd.exprsn.length)
            else if (cd.exprsn.contains('.', true))
                cd.pointFlag1 = true*/
            result.text = cd.exprsn
            cd.operation = null
            cd.pointFlag2 = false
        }
        // Если деление на ноль, вывести тостер
        else if (cd.dividedBy0Flag) {
            Toast.makeText(applicationContext,
                "Делить на 0 нельзя!", Toast.LENGTH_SHORT)
                .show()
            cd.exprsn.delete(cd.exprsn.lastIndex - 1, cd.exprsn.length)
            cd.operation = null
            result.text = cd.exprsn
//            Log.i("SecondNumber", cd.result.toString())
        }
        if (cd.exprsn.contains('.')) cd.pointFlag1 = true
        cd.dividedBy0Flag = false
    }

    /*private fun equals() {

        // Если есть какая-то операция (operation != null),
        // тогда проверяем какая и вычисляем выражение
        cd.operation?.let {
            when (it) {
                '+' -> {
                    cd.result = cd.secondNumber + cd.firstNumber
                    binding.result.text = cd.result.toString()
                }
                '-' -> {
                    cd.result = cd.firstNumber - cd.secondNumber
                    binding.result.text = cd.result.toString()
                }
                0x00D7.toChar() -> {
                    cd.result = cd.secondNumber * cd.firstNumber
                    binding.result.text = cd.result.toString()
                }
                0x00F7.toChar() -> {
                    if (cd.secondNumber != 0.0) {
                        cd.result = cd.firstNumber / cd.secondNumber
                        binding.result.text = cd.result.toString()
                    } else {
                        cd.result = 0.0
                        binding.result.text = "На 0 делить нельзя!"
                    }
                }
                0x221A.toChar() -> {

                }
                '%' -> {

                }
                '^' -> {

                }
            }
            cd.exprsn.clear()
        }
    }*/

    private fun percentInTheEnd() {

        cd.percentExpression = cd.exprsn.toString() + "%="
        val firstNumberPart = cd.exprsn.substring(0,
            cd.exprsn.indexOf(cd.operation!!)).toBigDecimal()
        var secondNumberPart = cd.exprsn.substring(cd.exprsn.indexOf(cd.operation!!) + 1,
            cd.exprsn.length).toBigDecimal()
        secondNumberPart = firstNumberPart.divide(100.toBigDecimal(), MathContext.DECIMAL32).multiply(secondNumberPart, MathContext.DECIMAL32)
        Log.i("Divide", secondNumberPart.toString())
        cd.exprsn.delete(cd.exprsn.indexOf(cd.operation!!) + 1,
            cd.exprsn.length).append(secondNumberPart)

        if (cd.exprsn.length >= 2 && cd.exprsn[cd.exprsn.lastIndex] == '0' &&
            cd.exprsn[cd.exprsn.lastIndex - 1] == '.')
            cd.exprsn.delete(cd.exprsn.lastIndex - 1,
                cd.exprsn.length)

        Log.i("Divide", secondNumberPart.toString())
        cd.percentFlag = true
        equalsProcessing()
    }

}