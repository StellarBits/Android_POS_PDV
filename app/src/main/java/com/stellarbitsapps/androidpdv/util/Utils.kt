package com.stellarbitsapps.androidpdv.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.android.sublcdlibrary.SubLcdHelper
import com.elotouch.AP80.sdkhelper.AP80PrintHelper
import com.stellarbitsapps.androidpdv.R
import com.stellarbitsapps.androidpdv.database.entity.LayoutSettings
import com.stellarbitsapps.androidpdv.database.entity.Report
import com.stellarbitsapps.androidpdv.database.entity.Tokens
import com.stellarbitsapps.androidpdv.ui.MainActivity
import com.stellarbitsapps.androidpdv.ui.MainActivity.Companion.mainActivityContentResolver
import com.stellarbitsapps.androidpdv.ui.initialcash.InitialCashFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensFragment
import com.stellarbitsapps.androidpdv.ui.tokens.TokensViewModel
import java.text.NumberFormat


class Utils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        @SuppressLint("SetTextI18n")
        fun showCashDialog(
            fragment: Fragment,
            viewModel: TokensViewModel,
            isSangria: Boolean,
            tokenSum: Float,
            printHelper: AP80PrintHelper
        ) {
            val inflater = LayoutInflater.from(fragment.requireContext())
            val dialogLayout: View =
                inflater.inflate(
                    R.layout.cash_change_dialog_layout,
                    fragment.requireActivity().findViewById(R.id.token_layout) as ViewGroup?
                )
            val alertDialogBuilder = AlertDialog.Builder(fragment.requireContext())

            val amountReceivedEditText =
                dialogLayout.findViewById<EditText>(R.id.edt_amount_received)

            val calcCashChangeButton =
                dialogLayout.findViewById<Button>(R.id.bt_calc_cash_change)

            val totalCashChangeTextView =
                dialogLayout.findViewById<TextView>(R.id.tv_total_cash_change)

            amountReceivedEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    Handler().postDelayed({
                        amountReceivedEditText.setSelection(amountReceivedEditText.length())
                    }, 1)
                }
            }

            amountReceivedEditText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    formatCashTextMask(s, amountReceivedEditText, this)
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun afterTextChanged(s: Editable) {}
            })

            calcCashChangeButton.setOnClickListener {
                val valueEntered = if (amountReceivedEditText.text.toString().isEmpty()) 0f else {
                    amountReceivedEditText.text.toString()
                        .replace("R$", "")
                        .replace(".", "")
                        .replace(",", ".")
                        .trim()
                        .toFloat()
                }

                if (isSangria) {
                    PrintUtils.printSangria(valueEntered, printHelper)
                    viewModel.insertSangria(valueEntered, MainActivity.currentReportId)
                } else {
                    val cashChange = valueEntered - tokenSum
                    totalCashChangeTextView.text = "Troco: R$ " + String.format("%.2f", cashChange)
                }
            }

            amountReceivedEditText.requestFocus()

            if (isSangria) {
                totalCashChangeTextView.visibility = View.GONE
            } else {
                totalCashChangeTextView.visibility = View.VISIBLE
            }

            with(calcCashChangeButton) {
                backgroundTintList = if (isSangria)
                    resources.getColorStateList(android.R.color.holo_red_light, null)
                else
                    resources.getColorStateList(android.R.color.holo_green_dark, null)

                text = if (isSangria) "Sangria" else "Calcular"
            }

            // Create an custom TextView for dialog title
            val titleTextView = TextView(fragment.requireContext())
            titleTextView.text = "Digite o valor recebido (Total: R$ ${String.format("%.2f", tokenSum)})"
            titleTextView.textSize = 14f // Font size in SP
            titleTextView.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.black)) // Text color
            titleTextView.setTypeface(titleTextView.typeface, Typeface.BOLD) // Bold if needed
            titleTextView.gravity = Gravity.CENTER // Text alignment

            alertDialogBuilder.setView(dialogLayout)

            if (isSangria)
                alertDialogBuilder.setTitle("Digite o valor da sangria:")
            else
                alertDialogBuilder.setCustomTitle(titleTextView)

            if (!isSangria) {
                alertDialogBuilder.setPositiveButton("OK") { _, _ -> }
            }
            alertDialogBuilder.setCancelable(isSangria)
            alertDialogBuilder.show()
        }

        fun formatCashTextMask(s: CharSequence, editText: EditText, watcher: TextWatcher): String {
            var current = ""

            if (s.toString() != current) {
                editText.removeTextChangedListener(watcher)

                val cleanString: String = s.replace("""[R$,.]""".toRegex(), "").trim()

                val parsed = cleanString.toDouble()
                val formatted = NumberFormat.getCurrencyInstance().format((parsed / 100))

                current = formatted
                editText.setText(formatted)
                editText.setSelection(formatted.length)

                editText.addTextChangedListener(watcher)
            }

            return current
        }

        fun prepareAndPrintToken(
            viewModel: TokensViewModel,
            tokenSettings: LayoutSettings,
            paymentMethod: String,
            tokenValues: Array<Float>,
            selectedTokensList: ArrayList<Tokens>,
            fragment: TokensFragment,
            printHelper: AP80PrintHelper
        ) {
            var tokenPaymentValues = tokenValues

            selectedTokensList.forEach { token ->

                // Update report in database
                val reportToBeUpdated = Report(
                    cashOneTokensSold = token.cashOne,
                    cashTwoTokensSold = token.cashTwo,
                    cashFourTokensSold = token.cashFour,
                    cashFiveTokensSold = token.cashFive,
                    cashSixTokensSold = token.cashSix,
                    cashEightTokensSold = token.cashEight,
                    cashTenTokensSold = token.cashTen,
                    paymentCash = tokenPaymentValues[0],
                    paymentPix = tokenPaymentValues[1],
                    paymentDebit = tokenPaymentValues[2],
                    paymentCredit = tokenPaymentValues[3]
                )

                tokenPaymentValues = arrayOf(0f, 0f, 0f, 0f)

                viewModel.updateReportTokens(reportToBeUpdated)

                // Print tokens
                val auxTokensList = listOf(
                    Pair(token.cashOne, "R$ 1,00"),
                    Pair(token.cashTwo, "R$ 2,00"),
                    Pair(token.cashFour, "R$ 4,00"),
                    Pair(token.cashFive, "R$ 5,00"),
                    Pair(token.cashSix, "R$ 6,00"),
                    Pair(token.cashEight, "R$ 8,00"),
                    Pair(token.cashTen, "R$ 10,00")
                )

                auxTokensList.forEach { tokensPair ->
                    if (tokensPair.first > 0) {
                        for (i in 1..tokensPair.first) {
                            PrintUtils.printToken(tokensPair.second, paymentMethod, tokenSettings, fragment, printHelper)
                            Thread.sleep(1250)
                        }
                    }
                }
            }
        }

        fun showInSubDisplay(subLcdHelper: SubLcdHelper, fragment: InitialCashFragment, layoutSettings: LayoutSettings) {

            if (layoutSettings.header.isNotEmpty() && layoutSettings.image.isNotEmpty()) {

                val myBitmap = BitmapFactory.decodeStream(
                    fragment.requireActivity().contentResolver.openInputStream(layoutSettings.image.toUri())
                )

                val subDisplayLayout =
                    fragment.layoutInflater.inflate(R.layout.sub_display_layout, null)

                subDisplayLayout.findViewById<TextView>(R.id.tv_sub_display_header).text =
                    layoutSettings.header
                subDisplayLayout.findViewById<ImageView>(R.id.img_sub_display_image)
                    .setImageBitmap(myBitmap)

                val constraintLayout =
                    subDisplayLayout.findViewById<View>(R.id.sub_display_layout) as ConstraintLayout

                constraintLayout.isDrawingCacheEnabled = true

                constraintLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )

                constraintLayout.layout(0, 0, constraintLayout.measuredWidth, constraintLayout.measuredHeight)
                constraintLayout.buildDrawingCache(true)

                val bmp = subLcdHelper.doRotateBitmap(constraintLayout.drawingCache, 90f)
                subLcdHelper.sendBitmap(bmp)
            }
        }

        @RequiresApi(Build.VERSION_CODES.N_MR1)
        fun getDeviceName(): String {
            return Settings.Global.getString(mainActivityContentResolver, Settings.Global.DEVICE_NAME)
        }
    }
}