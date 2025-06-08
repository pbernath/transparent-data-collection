package com.thesisproject.thesis_vt25.common.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.UserStore

fun Fragment.promptOnboarding(navController: NavController) {
    Log.e("Onboarding", "Onboarding finished = ${UserStore.getOnboardingFinished()}")
    if (!UserStore.getOnboardingFinished()) {
        navController.popBackStack()
        Snackbar.make(requireView(), "Complete tutorial to unlock more features", 5000).show()
    }
}

fun String.removeBrackets() : String{
    return this.replace("[", "").replace("]", "")
}
fun List<String>.toReadable() : List<String>{
    return this.map {
        it.replace("android.permission.", "").replace("ACCESS_", "")
    }
}
fun confirmationAlertBuilder(context: Context,
                             message: String,
                             positiveText:String,
                             negativeText:String,
                             neutralText:String?,
                             acceptAction: (() -> Unit)?,
                             denyAction: (() -> Unit)?){
    val builder = AlertDialog.Builder(context)

    if (neutralText != null){
        builder.setMessage(message)
            .setPositiveButton(neutralText) { dialog, id ->
                acceptAction?.invoke()
            }
    }else{
        builder.setMessage(message)
            .setPositiveButton(positiveText) { dialog, id ->
                acceptAction?.invoke()
            }
            .setNegativeButton(negativeText) { dialog, id ->
                denyAction?.invoke()
            }
    }
    builder.create().show()
}
class UiUtils {
    fun contractAlertBuilder(context: Context,
                             action: String,
                             acceptAction: (() -> Unit)?,
                             denyAction: (() -> Unit)?,
                             permissions:List<String>?){
        val builder = AlertDialog.Builder(context)
        val perms = if (action=="ACCEPT") "\n\nThe following permissions are required, and will" +
                " be requested on acceptance of this contract:\n\n $permissions" else ""
        builder.setMessage("Are you sure you want to $action this contract? " +
                "Make sure to thoroughly read the contract details before deciding." + perms)
            .setCancelable(false)
            .setPositiveButton("I ACCEPT") { dialog, id ->
                acceptAction?.invoke()
            }
            .setNegativeButton("I DO NOT ACCEPT") { dialog, id ->
                denyAction?.invoke()
            }
        builder.create().show()
    }

    fun confirmationAlertBuilder(context: Context,
                             message: String,
                                 positiveText:String,
                                 negativeText:String,
                             acceptAction: (() -> Unit)?,
                             denyAction: (() -> Unit)?){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setPositiveButton(positiveText) { dialog, id ->
                acceptAction?.invoke()
            }
            .setNegativeButton(negativeText) { dialog, id ->
                denyAction?.invoke()
            }
        builder.create().show()
    }

    fun convertToContractListItems(contractList: List<Contract>) : ArrayList<Contract>{
        val convertedList = ArrayList<Contract>()
        for(contract in contractList){
            convertedList.add(contract.toListItem())
        }
        return convertedList
    }

    fun Contract.toListItem(): Contract =
        Contract(id = id, title = title, overview = overview, reward = reward,
            permissions = permissions, start = start, end = end,
            created = created, status = status, organization = organization)


}

