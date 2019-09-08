package info.nightscout.androidaps.utils.biometric

interface BiometricHandler {

    fun onSdkVersionNotSupported()

    fun onBiometricAuthenticationNotSupported()

    fun onBiometricAuthenticationNotAvailable()

    fun onBiometricAuthenticationPermissionNotGranted()

    fun onBiometricAuthenticationInternalError(error: String)

    fun onAuthenticationFailed()

    fun onAuthenticationCancelled()

    fun onAuthenticationSuccessful()

    fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence)

    fun onAuthenticationError(errorCode: Int, errString: CharSequence)
}