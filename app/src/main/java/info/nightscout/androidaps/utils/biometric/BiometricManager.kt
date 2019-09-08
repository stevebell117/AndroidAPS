package info.nightscout.androidaps.utils.biometric

import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.R
import info.nightscout.androidaps.queue.Callback

class BiometricManager(ctx: Context) : BiometricManagerV23(ctx) {


    protected var mCancellationSignal = CancellationSignal()

    fun authenticate(callback: Callback) {
        val biometricHandler = BiometricCallback(callback)

        if (!BiometricUtils.isSdkVersionSupported) {
            biometricHandler.onSdkVersionNotSupported()
            return
        }

        if (!BiometricUtils.isPermissionGranted(ctx)) {
            biometricHandler.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        if (!BiometricUtils.isHardwareSupported(ctx)) {
            biometricHandler.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isFingerprintAvailable(ctx)) {
            biometricHandler.onBiometricAuthenticationNotAvailable()
            return
        }

        displayBiometricDialog(biometricHandler)
    }

    fun biometricAvailable(): Boolean {
        if (!BiometricUtils.isSdkVersionSupported) return false
        if (!BiometricUtils.isPermissionGranted(ctx)) return false
        if (!BiometricUtils.isHardwareSupported(ctx)) return false
        if (!BiometricUtils.isFingerprintAvailable(ctx)) return false
        return true;
    }


    fun cancelAuthentication() {
        if (BiometricUtils.isBiometricPromptEnabled) {
            if (!mCancellationSignal.isCanceled)
                mCancellationSignal.cancel()
        } else {
            if (!mCancellationSignalV23.isCanceled())
                mCancellationSignalV23.cancel()
        }
    }


    private fun displayBiometricDialog(biometricHandler: BiometricHandler) {
        if (BiometricUtils.isBiometricPromptEnabled) {
            displayBiometricPrompt(biometricHandler)
        } else {
            displayBiometricPromptV23(biometricHandler)
        }
    }


    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricHandler: BiometricHandler) {
        BiometricPrompt.Builder(ctx)
                .setTitle(MainApp.gs(R.string.biometric_title))
                .setDescription(MainApp.gs(R.string.biometric_description))
                .setNegativeButton(MainApp.gs(R.string.cancel), ctx.getMainExecutor(), DialogInterface.OnClickListener { _, _ -> biometricHandler.onAuthenticationCancelled() })
                .build()
                .authenticate(mCancellationSignal, ctx.getMainExecutor(),
                        BiometricCallbackV28(biometricHandler))
    }
}