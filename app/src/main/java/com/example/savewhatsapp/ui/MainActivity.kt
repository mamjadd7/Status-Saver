package com.example.savewhatsapp.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.savewhatsapp.`interface`.ClickCallBack
import com.example.savewhatsapp.ui.whatsapp.WhatsappFragment
import com.example.savewhatsapp.utils.ActivityResult
import com.example.statussaverapplication.utils.ConstantVariables
import com.example.statussaverapplication.utils.SharedPrefObj
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.savewhatsapp.R
import com.savewhatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ClickCallBack{
    lateinit var binding: ActivityMainBinding
    private var appUpdateManager: AppUpdateManager? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkWhatsappSelectionClick()

    }

    override fun onBackPressed() {
        showExitConfirmationDialog()
    }
    private fun checkWhatsappSelectionClick(){
        if (SharedPrefObj.getToken(this)== ConstantVariables.business_Whatsapp_Click){
            binding.whatsAppBottomBar.visibility= View.GONE
            binding.businessWhatsappBottomBar.visibility= View.VISIBLE

            businessWhatsappBottomBarSetup()
        }else{
            whatsappBottomBarSetup()
            binding.whatsAppBottomBar.visibility= View.VISIBLE
            binding.businessWhatsappBottomBar.visibility= View.GONE
        }
    }
    fun whatsappBottomBarSetup(){
        val navController: NavController = Navigation.findNavController(this,R.id.whatsapp_Bottom_Nav_Fragment)
        setupWithNavController(binding.whatsappBottomNavigation,navController)
    }
    fun businessWhatsappBottomBarSetup(){
        val navController: NavController = Navigation.findNavController(this,R.id.businessWhats_Botton_Nav_Fragment)
        setupWithNavController(binding.businessWhatsappBottomNavigation,navController)
    }

    // Method to show the PopupMenu

    private fun checkForAppUpdate() {
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_CODE_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_APP_UPDATE) {
            when (resultCode) {
                RESULT_OK -> {
                    finish()
                    //finishAffinity()
                    // The in-app update is complete, but you may need to restart the app
                }
                RESULT_CANCELED, ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast.makeText(this,"App Is Not Updated Please Check Your Internet Connection:",
                        Toast.LENGTH_SHORT).show()
                    // Handle the user's decision to cancel or if the update failed
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_APP_UPDATE = 9001
    }








    private fun showExitConfirmationDialog() {

        val dialogBuilder = MaterialAlertDialogBuilder(this)
        dialogBuilder.setTitle("Are you sure?")
        dialogBuilder.setMessage("Do you want to exit the app?")
        dialogBuilder.setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
            // Perform the action when "Yes" button is clicked
            exitApp()
        }
        dialogBuilder.setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
            // Perform the action when "No" button is clicked
            dialogInterface.dismiss() // Dismiss the dialog
        }

        dialogBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Respond to positive button press
            dialog.cancel()
        }

        dialogBuilder.setCancelable(false) // Prevent the dialog from being canceled by outside touch
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun exitApp() {
        // Perform the action to exit the app
        finishAffinity() // Close all activities and exit the app
    }

    override fun onButtonClicked() {
        recreate()
    }

    override fun whatsappBussiness() {
        recreate()
    }


}