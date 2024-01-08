package com.oceantech.tracking.utils

import android.content.Context
import android.location.Location
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.oceantech.tracking.R
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun Date.format(format: String? = null): String {
    val ld = toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return ld.format(DateTimeFormatter.ofPattern(format ?: "dd/MM/yyyy"))
}
fun AppCompatActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    allowStateLoss: Boolean = false
) {
    supportFragmentManager.commitTransaction(allowStateLoss) { add(frameId, fragment) }
}
inline fun androidx.fragment.app.FragmentManager.commitTransaction(allowStateLoss: Boolean = false, func: FragmentTransaction.() -> FragmentTransaction) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}
fun <T : Fragment> AppCompatActivity.addFragmentToBackstack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    allowStateLoss: Boolean = false,
    option: ((FragmentTransaction) -> Unit)? = null) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass,null, tag).addToBackStack(tag)
    }
}

fun EditText.checkWhileListening(operation: () -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            operation()
        }
    })
}

fun setupSpinner(spinner: Spinner, operation: (position: Int) -> Unit, itemList: List<Any?>){
    val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, itemList)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = adapter
    spinner.onItemSelectedListener = object : OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            operation(position)
        }
    }
}

fun checkPages(maxPages: Int, pageIndex: Int, prevPage: ImageView, nextPage: ImageView) {
    if(maxPages == 1){
        prevPage.visibility = View.GONE
        nextPage.visibility = View.GONE
    }else{
        when (pageIndex) {
            1 -> {
                prevPage.visibility = View.GONE
                nextPage.visibility = View.VISIBLE
            }

            maxPages -> {
                nextPage.visibility = View.GONE
                prevPage.visibility = View.VISIBLE
            }

            else -> {
                nextPage.visibility = View.VISIBLE
                prevPage.visibility = View.VISIBLE
            }
        }
    }
}

fun toMonthString(month: Int, context: Context): String {
    return when (month) {
        0 -> context.getString(R.string.jan)
        1 -> context.getString(R.string.feb)
        2 -> context.getString(R.string.mar)
        3 -> context.getString(R.string.apr)
        4 -> context.getString(R.string.may)
        5 -> context.getString(R.string.jun)
        6 -> context.getString(R.string.jul)
        7 -> context.getString(R.string.aug)
        8 -> context.getString(R.string.sep)
        9 -> context.getString(R.string.oct)
        10 -> context.getString(R.string.nov)
        else -> context.getString(R.string.dec)
    }
}