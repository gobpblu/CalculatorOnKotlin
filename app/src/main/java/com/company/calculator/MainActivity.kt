package com.company.calculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.calculator.databinding.ActivityMainBinding
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.MathContext
import java.math.MathContext.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

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

        // Listener на длительное нажатие для полной очистки
        clearAll.setOnLongClickListener {
            cd.clearAll()
            userView()
            enteredNumber.text = ""
            true
        }

        val numbersOnCl = idProcessing()

        for (item in viewIds) {
            item.setOnClickListener(numbersOnCl)
        }

    }

    private fun idProcessing(): View.OnClickListener = with(binding) {
        // Вешаем лисенеры на каждую кнопку
        // и в конце выводим результат после каждого нажатия
        val numbersOnCl = View.OnClickListener {
            when (it.id) {
                number0.id -> cd.buildString('0')
                number1.id -> cd.buildString('1')
                number2.id -> cd.buildString('2')
                number3.id -> cd.buildString('3')
                number4.id -> cd.buildString('4')
                number5.id -> cd.buildString('5')
                number6.id -> cd.buildString('6')
                number7.id -> cd.buildString('7')
                number8.id -> cd.buildString('8')
                number9.id -> cd.buildString('9')
                clearAll.id -> cd.deleteOne()
                plus.id -> equalsProcessing('+')
                minus.id -> equalsProcessing('-')
                multiply.id -> equalsProcessing(0x00D7.toChar())
                divide.id -> equalsProcessing(0x00F7.toChar())
                point.id -> cd.buildString('.')
                equals.id -> equalsProcessing('=')
                squareRoot.id -> equalsProcessing(0x221A.toChar())
                percent.id -> equalsProcessing('%')
                degree.id -> equalsProcessing('^')
            }
            userView()

        }
        return numbersOnCl
    }

    private fun userView() = with(binding){

        /*val number1: BigDecimal
        val number2: BigDecimal
        val operation: Char
        val df = DecimalFormat()
        // df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
         // df.maximumFractionDigits = 5

        val strBuilder = StringBuilder()

        if (cd.operation == null) {
            number1 = BigDecimal(cd.exprsn.toString(), DECIMAL64)
        } else {
            number1 = BigDecimal(cd.exprsn
                .substring(0 until cd.exprsn.indexOf(cd.operation!!) ),
                DECIMAL64
            )
        }
        strBuilder.append(number1)

        cd.operation?.let {
            strBuilder.append(it)
            if (cd.exprsn.last() !in cd.operations) {
                number2 = BigDecimal(cd.exprsn.substring(cd.exprsn.
                    indexOf(cd.operation!!) + 1 until cd.exprsn.length), DECIMAL64)
            } else {
                number2 = BigDecimal(0)
            }
            strBuilder.append(number2)
        }*/
/*if (cd.operation == null) {
            val number: BigDecimal = BigDecimal(cd.exprsn.toString())
//            str = String.format("%,f", number)
            str = DecimalFormat.getInstance().format(number)
        }*/

        /*val str = StringBuilder()
        cd.firstNumber?.let {
            str.append(it)
        }

        cd.operation?.let {
            str.append(it)
        }

        cd.secondNumber?.let {
            str.append(it)
        }


        Log.i("percentFlag", cd.exprsn.toString() + "eto ya")*/


        result.text = cd.exprsn
    }

    private fun equalsProcessing(usedOperation: Char) = with (binding) {

        // Если уже есть какая-тол операция..
        cd.operation?.let {
            // и если следующая операция не = или %, тогда
            // присваиваем её переменной, чтобы потом вывести в строку
            if (usedOperation != '=' && usedOperation != '%')
                cd.nextOperation = usedOperation
        }

        cd.buildString(usedOperation)  // Вычисляем выражение




        // Если вычисление дало какой-то результат
        // выводим результат и выражение на экран
        if (cd.result != null) {
            // Если последний символ это одна из операций, удаляем её,
                // чтобы сделать правильный вывод в верхней строке
            if (cd.exprsn.last() in cd.operations)
                cd.exprsn.deleteCharAt(cd.exprsn.lastIndex)

            // Если операция % не находится в конце, добавляем знак =
            if (!cd.percentFlag) enteredNumber.text = cd.exprsn.append('=')
            // Иначе выводим изначальное выражение с % в конце
            else enteredNumber.text = cd.percentExpression

            // Очищаем выражение, закидывая в него результат
            cd.exprsn.clear().append(cd.result.toString())

            // Если есть следующая операция, добавляем её в конце строки
            cd.nextOperation?.let {
                cd.exprsn.append(it)
            }

            // Если результат дробный, вешаем флаг на 1 число
            if (cd.exprsn.contains('.')) cd.pointFlag1 = true

            // Следующая операция становится текущей, всё остальное обнуляется
            cd.operation = cd.nextOperation
            cd.nextOperation = null
            cd.pointFlag2 = false
            cd.result = null
            cd.percentFlag = false
        }
        // Если деление на ноль, вывести тостер и
        // удалить деление с нолём из строки
        else if (cd.dividedBy0Flag) {
            Toast.makeText(
                applicationContext,
                "Делить на 0 нельзя!", Toast.LENGTH_SHORT
            )
                .show()

            // Если при делении на ноль есть вероятность, что нолей много,
            // удаляем все
            cd.exprsn.delete(cd.exprsn.indexOf(cd.operation!!), cd.exprsn.length)
            cd.operation = null
            cd.pointFlag2 = false
        }
        userView()
        cd.dividedBy0Flag = false
    }

}
