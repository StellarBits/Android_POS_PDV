package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.pos.device.printer.PrintCanvas
import com.pos.device.printer.PrintTask
import com.pos.device.printer.Printer
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.ReportError
import com.stellarbitsapps.androidpdv.database.entity.Sangria
import com.stellarbitsapps.androidpdv.ui.custom.dialog.ProgressHUD
import com.stellarbitsapps.androidpdv.ui.tokens.TokensFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

class PrintUtils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        @SuppressLint("SimpleDateFormat", "InflateParams")
        fun printToken(
            tokenValue: String,
            paymentMethod: String,
            tokenSettings: LayoutSettings,
            fragment: TokensFragment
        ) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = format.format(calendar.time)
    
            val tokenLayout = fragment.layoutInflater.inflate(R.layout.token_layout, null)
    
            tokenLayout.findViewById<TextView>(R.id.tv_token_value).text = tokenValue
    
            if (tokenSettings.image.isNotEmpty()) {
                val myBitmap = BitmapFactory.decodeStream(
                    fragment.requireActivity().contentResolver.openInputStream(tokenSettings.image.toUri())
                )
    
                tokenLayout.findViewById<ImageView>(R.id.img_token_image).setImageBitmap(myBitmap)
            }
    
            val bitmap = createBitmapFromConstraintLayout(tokenLayout)

            Printer.getInstance().reset()

            val printTask = PrintTask()
            printTask.gray = 200
            val printCanvas = PrintCanvas()
            val paint = Paint()
            paint.color = Color.BLACK

            paint.textSize = 20f
            printCanvas.drawText("_________________________________________", paint)

            paint.textSize = 22f
            printCanvas.drawText(tokenSettings.header, paint)

            paint.textSize = 28f
            printCanvas.drawText("VALE $tokenValue", paint)

            printCanvas.drawBitmap(bitmap, paint)

            paint.textSize = 20f
            printCanvas.drawText("$date - ${Utils.getDeviceName()} - $paymentMethod", paint)

            paint.textSize = 22f
            printCanvas.drawText(tokenSettings.footer, paint)

            paint.textSize = 20f
            printCanvas.drawText("_________________________________________", paint)

            printTask.setPrintCanvas(printCanvas)

            Printer.getInstance().startPrint(printTask) {
                    p0, _ -> Log.i("TAG", "result $p0")
            }

            Printer.getInstance().reset()
        }

        @RequiresApi(Build.VERSION_CODES.N_MR1)
        @SuppressLint("SimpleDateFormat")
        fun printSangria(sangria: Float) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = format.format(calendar.time)

            val printTask = PrintTask()
            printTask.gray = 200
            val printCanvas = PrintCanvas()
            val paint = Paint()
            paint.color = Color.BLACK

            for (i in 1..2) {
                Printer.getInstance().reset()

                paint.textSize = 20f
                printCanvas.drawText("_________________________________________", paint)

                paint.textSize = 26f
                printCanvas.drawText("             Sangria: ${Utils.getDeviceName()}", paint)

                paint.textSize = 6f
                printSpace(1, printCanvas, paint)

                paint.textSize = 64f
                printCanvas.drawText("R$ ${String.format("%.2f", sangria)}", paint)

                paint.textSize = 24f
                printCanvas.drawText(date, paint)

                printSpace(2, printCanvas, paint)

                printCanvas.drawText("ASS .....................................................", paint)

                printTask.setPrintCanvas(printCanvas)

                Printer.getInstance().startPrint(printTask) {
                        p0, _ -> Log.i("TAG", "result $p0")
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.N_MR1)
        @SuppressLint("SimpleDateFormat")
        suspend fun printReport(
            report: Report,
            sangrias: List<Sangria>,
            errors: List<ReportError>,
            finalDate: String,
            finalValue: Float,
            progressHUD: ProgressHUD,
        ) {
            // ------------------------- DATE ------------------------- //

            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val initialDate = format.format(report.initialDate)

            // ------------------------- DATE ------------------------- //

            // ------------------------- CALC ------------------------- //

            // Cash Register
            val initialValue = report.initialCash

            // Tokens
            val formattedTokensOneSold = report.cashOneTokensSold.toString().padEnd(6 - (report.cashOneTokensSold.toString().length + 1), ' ')
            val formattedTokensTwoSold = report.cashTwoTokensSold.toString().padEnd(6 - (report.cashTwoTokensSold.toString().length + 1), ' ')
            val formattedTokensFourSold = report.cashFourTokensSold.toString().padEnd(6 - (report.cashFourTokensSold.toString().length + 1), ' ')
            val formattedTokensFiveSold = report.cashFiveTokensSold.toString().padEnd(6 - (report.cashFiveTokensSold.toString().length + 1), ' ')
            val formattedTokensSixSold = report.cashSixTokensSold.toString().padEnd(6 - (report.cashSixTokensSold.toString().length + 1), ' ')
            val formattedTokensEightSold = report.cashEightTokensSold.toString().padEnd(6 - (report.cashEightTokensSold.toString().length + 1), ' ')
            val formattedTokensTenSold = report.cashTenTokensSold.toString().padEnd(6 - (report.cashTenTokensSold.toString().length + 1), ' ')

            val tokensOneSold = report.cashOneTokensSold
            val tokensTwoSold = report.cashTwoTokensSold
            val tokensFourSold = report.cashFourTokensSold
            val tokensFiveSold = report.cashFiveTokensSold
            val tokensSixSold = report.cashSixTokensSold
            val tokensEightSold = report.cashEightTokensSold
            val tokensTenSold = report.cashTenTokensSold

            val totalTokensOne = tokensOneSold.toFloat()
            val totalTokensTwo = 2 * tokensTwoSold.toFloat()
            val totalTokensFour = 4 * tokensFourSold.toFloat()
            val totalTokensFive = 5 * tokensFiveSold.toFloat()
            val totalTokensSix = 6 * tokensSixSold.toFloat()
            val totalTokensEight = 8 * tokensEightSold.toFloat()
            val totalTokensTen = 10 * tokensTenSold.toFloat()

            val tokensTotal = totalTokensOne + totalTokensTwo + totalTokensFour + totalTokensFive + totalTokensSix + totalTokensEight + totalTokensTen

            // Sangria
            val sangriaSum = sangrias.sumOf { it.sangria.toDouble() }.toFloat()

            // Errors
            val errorSum = errors.sumOf { it.error.toDouble() }.toFloat()

            // Balance
            val balance = initialValue + tokensTotal - sangriaSum // Abertura + Vendas - Sangrias

            // ------------------------- CALC ------------------------- //

            Printer.getInstance().reset()
            val printTask = PrintTask()
            printTask.gray = 200
            val printCanvas = PrintCanvas()
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 22f

            printCanvas.drawText("______________________________________", paint)
            printCanvas.drawText(Utils.getDeviceName(), paint)
            printSpace(1, printCanvas, paint)
            printCanvas.drawText("Abertura:\n$initialDate - R$ ${String.format("%.2f", initialValue)}", paint)
            printCanvas.drawText("Fechamento:\n$finalDate - R$ ${String.format("%.2f", finalValue)}", paint)
            printCanvas.drawText("Saldo (Abertura + Vendas - Sangrias):\nR$ ${String.format("%.2f", balance)}", paint)
            printCanvas.drawText("______________________________________", paint)
            printCanvas.drawText("R$ 1,00   - Qtde x $formattedTokensOneSold - Total R$: ${String.format("%.2f", totalTokensOne)}", paint)
            printCanvas.drawText("R$ 2,00   - Qtde x $formattedTokensTwoSold - Total R$: ${String.format("%.2f", totalTokensTwo)}", paint)
            printCanvas.drawText("R$ 4,00   - Qtde x $formattedTokensFourSold - Total R$: ${String.format("%.2f", totalTokensFour)}", paint)
            printCanvas.drawText("R$ 5,00   - Qtde x $formattedTokensFiveSold - Total R$: ${String.format("%.2f", totalTokensFive)}", paint)
            printCanvas.drawText("R$ 6,00   - Qtde x $formattedTokensSixSold - Total R$: ${String.format("%.2f", totalTokensSix)}", paint)
            printCanvas.drawText("R$ 8,00   - Qtde x $formattedTokensEightSold - Total R$: ${String.format("%.2f", totalTokensEight)}", paint)
            printCanvas.drawText("R$ 10,00 - Qtde x $formattedTokensTenSold - Total R$: ${String.format("%.2f", totalTokensTen)}", paint)

            paint.typeface = Typeface.DEFAULT_BOLD
            printCanvas.drawText("Total de vendas R$: ${String.format("%.2f", tokensTotal)}", paint)

            paint.typeface = Typeface.DEFAULT
            printCanvas.drawText("______________________________________", paint)
            printCanvas.drawText("Total Dinheiro ..... R$: ${String.format("%.2f", report.paymentCash)}", paint)
            printCanvas.drawText("Total Pix .......... R$: ${String.format("%.2f", report.paymentPix)}", paint)
            printCanvas.drawText("Total Débito ....... R$: ${String.format("%.2f", report.paymentDebit)}", paint)
            printCanvas.drawText("Total Crédito ...... R$: ${String.format("%.2f", report.paymentCredit)}", paint)
            printCanvas.drawText("Abertura do caixa .. R$: ${String.format("%.2f", report.initialCash)}", paint)

            paint.typeface = Typeface.DEFAULT_BOLD
            printCanvas.drawText("Total Geral ........ R$: ${String.format("%.2f", report.paymentCash + report.paymentPix + report.paymentDebit + report.paymentCredit + report.initialCash)}", paint)

            paint.typeface = Typeface.DEFAULT
            printCanvas.drawText("______________________________________", paint)

            // Sangria
            printCanvas.drawText("Sangria:", paint)

            sangrias.forEach {
                val date = format.format(it.date)
                val text = "$date - R$: ${String.format("%.2f", it.sangria)}"
                printCanvas.drawText(text, paint)
            }

            paint.typeface = Typeface.DEFAULT_BOLD
            printCanvas.drawText("Total das sangrias R$: ${String.format("%.2f", sangriaSum)}", paint)

            // Errors
            paint.typeface = Typeface.DEFAULT
            printCanvas.drawText("Erros reportados:", paint)

            errors.forEach {
                val date = format.format(it.date)
                val text = "$date - R$: ${String.format("%.2f", it.error)}"
                printCanvas.drawText(text, paint)
            }

            paint.typeface = Typeface.DEFAULT_BOLD
            printCanvas.drawText("Total dos erros reportados R$: ${String.format("%.2f", errorSum)}", paint)

            printTask.setPrintCanvas(printCanvas)

            Printer.getInstance().startPrint(printTask) {
                    p0, _ -> Log.i("TAG", "result $p0")
            }

            withContext(Dispatchers.IO) {
                Thread.sleep(2500)
            }

            progressHUD.dismiss()
        }
    
        private fun createBitmapFromConstraintLayout(inflatedLayout: View): Bitmap {
            val constraintLayout = inflatedLayout.findViewById<View>(R.id.token_layout) as ConstraintLayout
    
            constraintLayout.isDrawingCacheEnabled = true
    
            constraintLayout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
    
            constraintLayout.layout(0, 0, constraintLayout.measuredWidth, constraintLayout.measuredHeight)
            constraintLayout.buildDrawingCache(true)
    
            return constraintLayout.drawingCache
        }
    
        private fun printSpace(spaceSize: Int, printCanvas: PrintCanvas, paint: Paint) {
            if (spaceSize < 0) {
                return
            }
            val strSpace = StringBuilder()
            for (i in 0 until spaceSize) {
                strSpace.append("\n")
            }
            printCanvas.drawText(strSpace.toString(), paint)
        }
    }
}