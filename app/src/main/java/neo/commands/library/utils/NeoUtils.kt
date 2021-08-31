package neo.commands.library.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import neo.commands.library.MainActivity
import neo.commands.library.R

class NeoUtils {

    companion object {
        fun showProgressDialog(context: Context, title: String): AlertDialog {
            val builder = AlertDialog.Builder(context)

            val progress = ProgressBar(
                context, null,
                android.R.attr.progressBarStyleHorizontal
            )

            progress.isIndeterminate = true

            progress.setPadding(16.dp(context))

            builder.setTitle(title)

            val container = LinearLayout(context)
            container.addView(progress, LinearLayout.LayoutParams(-1, -2))

            builder.setView(container)
            builder.setCancelable(false)

            return builder.show()
        }

        fun showDialogError(context: Context, error: String) {
            AlertDialog.Builder(context).apply {
                setTitle("Ocorreu um erro")
                setMessage(error)
            }.show()
        }

        fun showMessage(context: Context, result: String) {
            AlertDialog.Builder(context).apply {
                setTitle("Help")
                setMessage(result)
            }.show()
        }
    }
}


fun Int.dp(context: Context) : Int {
    return (context.resources.getDimension(R.dimen.dimen_1dp) * this).toInt()
}