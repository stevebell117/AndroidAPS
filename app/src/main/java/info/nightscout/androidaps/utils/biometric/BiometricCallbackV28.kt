package info.nightscout.androidaps.utils.biometric

import android.hardware.biometrics.BiometricPrompt
import androidx.annotation.RequiresApi
import android.os.Build


@RequiresApi(api = Build.VERSION_CODES.P)
class BiometricCallbackV28(private val biometricHandler: BiometricHandler) : BiometricPrompt.AuthenticationCallback() {


    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        biometricHandler.onAuthenticationSuccessful()
    }


    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        super.onAuthenticationHelp(helpCode, helpString)
        biometricHandler.onAuthenticationHelp(helpCode, helpString)
    }


    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        biometricHandler.onAuthenticationError(errorCode, errString)
    }


    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        biometricHandler.onAuthenticationFailed()
    }
}