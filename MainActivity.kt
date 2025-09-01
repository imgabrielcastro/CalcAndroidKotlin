OBS: foi feito com IA pois infelizmente não consegui fazer pois meu notebook não rodou o programa :(

Porém, acompanhei um tutorial exibindo como criar a calculadora em Kotlin no Android Studio.

package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CalculatorScreen()
                }
            }
        }
    }
}

private data class CalcState(
    val display: String = "0",
    val operand: Double? = null,
    val pendingOp: Char? = null,
    val resetDisplay: Boolean = false
)

@Composable
private fun CalculatorScreen() {
    var state by remember { mutableStateOf(CalcState()) }

    fun format(n: Double): String {
        val s = n.toString()
        return if (s.endsWith(".0")) s.dropLast(2) else s
    }

    fun doPending(a: Double?, b: Double, op: Char?): Double? {
        if (a == null || op == null) return b
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '×' -> a * b
            '÷' -> if (b == 0.0) Double.NaN else a / b
            else -> b
        }
    }

    fun onDigit(d: Char) {
        state = if (state.resetDisplay || state.display == "0") {
            state.copy(display = d.toString(), resetDisplay = false)
        } else {
            state.copy(display = state.display + d)
        }
    }

    fun onDot() {
        if (state.resetDisplay) {
            state = state.copy(display = "0.", resetDisplay = false)
        } else if (!state.display.contains(".")) {
            state = state.copy(display = state.display + ".")
        }
    }

    fun onClear() {
        state = CalcState()
    }

    fun onDel() {
        if (state.resetDisplay) return
        val new = state.display.dropLast(1).ifEmpty { "0" }
        state = state.copy(display = new)
    }

    fun onPercent() {
        val v = state.display.toDoubleOrNull() ?: return
        state = state.copy(display = format(v / 100.0))
    }

    fun onOp(op: Char) {
        val current = state.display.toDoubleOrNull() ?: return
        val result = doPending(state.operand, current, state.pendingOp)
        state = state.copy(
            display = if (result == null) state.display else format(result),
            operand = result,
            pendingOp = op,
            resetDisplay = true
        )
    }

    fun onEquals() {
        val current = state.display.toDoubleOrNull() ?: return
        val result = doPending(state.operand, current, state.pendingOp) ?: current
        state = state.copy(
            display = format(result),
            operand = null,
            pendingOp = null,
            resetDisplay = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Display
        Text(
            text = state.display,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(20.dp),
            fontSize = 40.sp,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Teclas
        val rows = listOf(
            listOf("C", "DEL", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "=")
        )

        rows.forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { label ->
                    val isWideZero = label == "0" && row.size == 3
                    val weight = if (isWideZero) 2f else 1f
                    CalcButton(
                        text = label,
                        modifier = Modifier
                            .weight(weight)
                            .height(64.dp)
                    ) {
                        when (label) {
                            in "0".."9" -> onDigit(label.first())
                            "." -> onDot()
                            "C" -> onClear()
                            "DEL" -> onDel()
                            "%" -> onPercent()
                            "+" -> onOp('+')
                            "-" -> onOp('-')
                            "×" -> onOp('×')
                            "÷" -> onOp('÷')
                            "=" -> onEquals()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOp = text in listOf("+", "-", "×", "÷", "=")
    val colors = if (isOp) ButtonDefaults.buttonColors()
    else ButtonDefaults.elevatedButtonColors()

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = colors,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = if (isOp) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
