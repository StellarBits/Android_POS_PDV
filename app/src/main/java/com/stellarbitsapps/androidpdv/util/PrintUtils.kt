package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
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
            fragment: TokensFragment,
            printHelper: AP80PrintHelper
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

            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printHelper.printData(tokenSettings.header, 35, 0, false, 1, 80, 0)
            printHelper.printData("VALE $tokenValue", 80, 0, false, 1, 80, 0)
            printHelper.printBitmap(bitmap, 2, 80)
            printHelper.printData("$date - ${Utils.getDeviceName()} - $paymentMethod", 28, 0, false, 0, 80, 0)
            printHelper.printData(tokenSettings.footer, 40, 0, false, 0, 80, 0)
            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printSpace(2, printHelper)
            printHelper.printStart()
            printHelper.cutPaper(1)
            printHelper.clean()
        }

        @RequiresApi(Build.VERSION_CODES.N_MR1)
        @SuppressLint("SimpleDateFormat")
        fun printSangria(sangria: Float, printHelper: AP80PrintHelper) {
            val calendar = Calendar.getInstance()
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = format.format(calendar.time)

            for (i in 1..2) {
                printHelper.printData("Sangria: ${Utils.getDeviceName()}", 60, 1, false, 1, 80, 0)
                printSpace(1, printHelper)
                printHelper.printData("R$ ${String.format("%.2f", sangria)}", 50, 0, false, 0, 80, 0)
                printHelper.printData(date, 50, 0, false, 0, 80, 0)
                printSpace(4, printHelper)
                printHelper.printData("ASS ..................................", 30, 0, false, 0, 80, 0)
                printSpace(3, printHelper)
                printHelper.printStart()
                printHelper.cutPaper(1)
                printHelper.clean()
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
            printHelper: AP80PrintHelper,
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

            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printSpace(1, printHelper)
            printHelper.printData(Utils.getDeviceName(), 70, 1, false, 1, 80, 1)
            printSpace(1, printHelper)
            printHelper.printData("Abertura:\n$initialDate - R$ ${String.format("%.2f", initialValue)}", 30, 0, false, 0, 80, 0)
            printSpace(1, printHelper)
            printHelper.printData("Fechamento:\n$finalDate - R$ ${String.format("%.2f", finalValue)}", 30, 0, false, 0, 80, 0)
            printSpace(1, printHelper)
            printHelper.printData("Saldo (Abertura + Vendas - Sangrias):\nR$ ${String.format("%.2f", balance)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printSpace(1, printHelper)
            printHelper.printData("R$ 1,00  - Qtde x $formattedTokensOneSold - Total R$: ${String.format("%.2f", totalTokensOne)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 2,00  - Qtde x $formattedTokensTwoSold - Total R$: ${String.format("%.2f", totalTokensTwo)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 4,00  - Qtde x $formattedTokensFourSold - Total R$: ${String.format("%.2f", totalTokensFour)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 5,00  - Qtde x $formattedTokensFiveSold - Total R$: ${String.format("%.2f", totalTokensFive)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 6,00  - Qtde x $formattedTokensSixSold - Total R$: ${String.format("%.2f", totalTokensSix)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 8,00  - Qtde x $formattedTokensEightSold - Total R$: ${String.format("%.2f", totalTokensEight)}", 26, 0, false, 0, 80, 0)
            printHelper.printData("R$ 10,00 - Qtde x $formattedTokensTenSold - Total R$: ${String.format("%.2f", totalTokensTen)}", 26, 0, false, 0, 80, 0)
            printSpace(1, printHelper)
            printHelper.printData("Total de vendas R$: ${String.format("%.2f", tokensTotal)}", 30, 0, false, 0, 80, 1)
            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)
            printSpace(1, printHelper)
            printHelper.printData("Total Dinheiro ..... R$: ${String.format("%.2f", report.paymentCash)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("Total Pix .......... R$: ${String.format("%.2f", report.paymentPix)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("Total Débito ....... R$: ${String.format("%.2f", report.paymentDebit)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("Total Crédito ...... R$: ${String.format("%.2f", report.paymentCredit)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("Abertura do caixa .. R$: ${String.format("%.2f", report.initialCash)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("Total Geral ........ R$: ${String.format("%.2f", report.paymentCash + report.paymentPix + report.paymentDebit + report.paymentCredit + report.initialCash)}", 30, 0, false, 0, 80, 0)
            printHelper.printData("______________________________________", 30, 0, false, 1, 80, 1)

            // Sangria
            printSpace(1, printHelper)
            printHelper.printData("Sangria:", 30, 0, false, 0, 80, 0)

            sangrias.forEach {
                val date = format.format(it.date)
                val text = "$date - R$: ${String.format("%.2f", it.sangria)}"
                printHelper.printData(text, 30, 0, false, 0, 80, 0)
            }

            printSpace(1, printHelper)
            printHelper.printData("Total das sangrias R$: ${String.format("%.2f", sangriaSum)}", 30, 0, false, 0, 80, 0)

            // Errors
            printSpace(1, printHelper)
            printHelper.printData("Erros reportados:", 30, 0, false, 0, 80, 0)

            errors.forEach {
                val date = format.format(it.date)
                val text = "$date - R$: ${String.format("%.2f", it.error)}"
                printHelper.printData(text, 30, 0, false, 0, 80, 0)
            }

            printSpace(1, printHelper)
            printHelper.printData("Total dos erros reportados R$: ${String.format("%.2f", errorSum)}", 30, 0, false, 0, 80, 0)

            printSpace(3, printHelper)
            printHelper.printStart()
            printHelper.cutPaper(1)
            printHelper.clean()

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
    
        private fun printSpace(spaceSize: Int, printHelper: AP80PrintHelper) {
            if (spaceSize < 0) {
                return
            }
            val strSpace = StringBuilder()
            for (i in 0 until spaceSize) {
                strSpace.append("\n")
            }
            printHelper.printData(strSpace.toString(), 32, 0, false, 1, 80, 0)
        }
    }
}