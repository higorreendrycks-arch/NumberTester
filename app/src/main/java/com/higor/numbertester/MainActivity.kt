package com.higor.numbertester

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private var atual = 0L
    private var finalNumero = 0L
    private var tentativas = 0L
    private var executando = false
    private var intervalo = 500L

    private lateinit var inicio: EditText
    private lateinit var fim: EditText
    private lateinit var numero: TextView
    private lateinit var contador: TextView
    private lateinit var status: TextView

    private val tarefa = object : Runnable {
        override fun run() {
            if (!executando) return

            numero.text = atual.toString()
            tentativas++
            contador.text = "Total de números exibidos: $tentativas"

            if (atual >= finalNumero) {
                executando = false
                status.text = "Sequência concluída!"
                return
            }

            atual++
            handler.postDelayed(this, intervalo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(25, 25, 25, 25)
            setBackgroundColor(Color.rgb(20, 20, 28))
        }

        fun texto(valor: String, tamanho: Float): TextView {
            return TextView(this).apply {
                text = valor
                textSize = tamanho
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setPadding(8, 12, 8, 12)
                layout.addView(this)
            }
        }

        fun campo(dica: String): EditText {
            return EditText(this).apply {
                hint = dica
                inputType = 2
                setSingleLine(true)
                setTextColor(Color.WHITE)
                setHintTextColor(Color.GRAY)
                layout.addView(this)
            }
        }

        fun botao(nome: String, acao: () -> Unit) {
            Button(this).apply {
                text = nome
                setOnClickListener { acao() }
                layout.addView(this)
            }
        }

        texto("TESTADOR DE NÚMEROS", 24f)

        texto("Número inicial", 16f)
        inicio = campo("Digite o primeiro número")

        texto("Número final", 16f)
        fim = campo("Digite o último número")

        numero = texto("000000", 34f)
        contador = texto("Total de números exibidos: 0", 18f)
        status = texto("Aguardando início", 16f)

        botao("INICIAR") {
            val primeiro = inicio.text.toString().toLongOrNull()
            val ultimo = fim.text.toString().toLongOrNull()

            if (primeiro == null || ultimo == null ||
                primeiro < 0 || ultimo < primeiro) {
                status.text = "Confira os números digitados"
            } else {
                handler.removeCallbacks(tarefa)
                atual = primeiro
                finalNumero = ultimo
                tentativas = 0
                executando = true
                status.text = "Exibindo números..."
                tarefa.run()
            }
        }

        botao("PAUSAR") {
            executando = false
            handler.removeCallbacks(tarefa)
            status.text = "Pausado"
        }

        botao("CONTINUAR") {
            if (!executando && atual <= finalNumero) {
                executando = true
                status.text = "Exibindo números..."
                tarefa.run()
            }
        }

        botao("ZERAR") {
            executando = false
            handler.removeCallbacks(tarefa)
            atual = 0
            tentativas = 0
            numero.text = "000000"
            contador.text = "Total de números exibidos: 0"
            status.text = "Aguardando início"
        }

        texto("Velocidade de exibição", 16f)

        val velocidade = SeekBar(this).apply {
            max = 9
            progress = 4

            setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        intervalo = (10 - progress).coerceAtLeast(1) * 100L
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                }
            )
        }

        layout.addView(velocidade)
        setContentView(layout)
    }

    override fun onDestroy() {
        executando = false
        handler.removeCallbacks(tarefa)
        super.onDestroy()
    }
}