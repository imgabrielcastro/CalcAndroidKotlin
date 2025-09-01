OBS: foi feito com IA pois infelizmente não consegui fazer pois meu notebook não rodou o programa :(

Porém, acompanhei um tutorial exibindo como criar a calculadora em Kotlin no Android Studio.

package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf<Int?>(null) }
    var operation by remember { mutableStateOf<Char?>(null) }
    var shouldReset by remember { mutableStateOf(false) }

    fun onDigitClick(digit: String) {
        if (shouldReset || display == "0") {
            display = digit
            shouldReset = false
        } else {
            display += digit
        }
    }

    fun onOperationClick(op: Char) {
        firstNumber = display.toIntOrNull()
        operation = op
        shouldReset = true
    }

    fun onEqualsClick() {
        val secondNumber = display.toIntOrNull() ?: return
        val result = when (operation) {
            '+' -> firstNumber?.plus(secondNumber)
            '-' -> firstNumber?.minus(secondNumber)
            '×' -> firstNumber?.times(secondNumber)
            '÷' -> if (secondNumber != 0) firstNumber?.div(secondNumber) else null
            else -> null
        }
        display = result?.toString() ?: "Erro"
        firstNumber = null
        operation = null
        shouldReset = true
    }

    fun onClear() {
        display = "0"
        firstNumber = null
        operation = null
        shouldReset = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Display
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            fontSize = 48.sp,
            textAlign = TextAlign.End
        )

        // Buttons
        val buttons = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { label ->
                    Button(
                        onClick = {
                            when (label) {
                                in "0".."9" -> onDigitClick(label)
                                "+" -> onOperationClick('+')
                                "-" -> onOperationClick('-')
                                "×" -> onOperationClick('×')
                                "÷" -> onOperationClick('÷')
                                "=" -> onEqualsClick()
                                "C" -> onClear()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Text(text = label, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

