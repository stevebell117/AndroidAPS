package info.nightscout.androidaps.utils.biometric

import android.widget.Toast
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.R
import info.nightscout.androidaps.data.PumpEnactResult
import info.nightscout.androidaps.queue.Callback


class BiometricCallback (private val onResult: Callback): BiometricHandler {

    var result : String = ""
    var success = false

    override fun onSdkVersionNotSupported() {
        success = false
        result = MainApp.gs(R.string.biometric_error_sdk_not_supported)
        call()
    }

    override fun onBiometricAuthenticationNotSupported() {
        success = false
        result = MainApp.gs(R.string.biometric_error_hardware_not_supported)
        call()
    }

    override fun onBiometricAuthenticationNotAvailable() {
        success = false
        result = MainApp.gs(R.string.biometric_error_fingerprint_not_available)
        call()
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        success = false
        result = MainApp.gs(R.string.biometric_error_permission_not_granted)
        call()
    }

    override fun onBiometricAuthenticationInternalError(error: String) {
        success = false
        result = error
        call()
    }

    override fun onAuthenticationFailed() {
        //        Toast.makeText(getApplicationContext(), MainApp.gs(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    override fun onAuthenticationCancelled() {
        // TODO        mBiometricManager.cancelAuthentication()
        success = false
        result = MainApp.gs(R.string.biometric_cancelled)
        call()
    }

    override fun onAuthenticationSuccessful() {
        success = true
        result = MainApp.gs(R.string.biometric_success)
        call()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        //        Toast.makeText(getApplicationContext(), helpString, Toast.LENGTH_LONG).show();
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        //        Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_LONG).show();
    }

    private fun call() {
        Toast.makeText(MainApp.instance().applicationContext, result, Toast.LENGTH_LONG).show()
        onResult.result(PumpEnactResult().success(success).comment(result)).run()
    }
}