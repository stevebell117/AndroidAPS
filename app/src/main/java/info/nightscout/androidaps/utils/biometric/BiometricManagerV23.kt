package info.nightscout.androidaps.utils.biometric

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties

import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.util.UUID

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import info.nightscout.androidaps.R


@TargetApi(Build.VERSION_CODES.M)
open class BiometricManagerV23 (val ctx : Context){

    private var cipher: Cipher? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var cryptoObject: FingerprintManagerCompat.CryptoObject? = null


    private var biometricDialogV23: BiometricDialogV23? = null
    protected var mCancellationSignalV23 = CancellationSignal()


    fun displayBiometricPromptV23(biometricHandler: BiometricHandler) {
        generateKey()

        if (initCipher()) {

            cryptoObject = FingerprintManagerCompat.CryptoObject(cipher!!)
            val fingerprintManagerCompat = FingerprintManagerCompat.from(ctx)

            fingerprintManagerCompat.authenticate(cryptoObject, 0, mCancellationSignalV23,
                    object : FingerprintManagerCompat.AuthenticationCallback() {
                        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                            super.onAuthenticationError(errMsgId, errString)
                            updateStatus(errString.toString())
                            biometricHandler.onAuthenticationError(errMsgId, errString!!)
                        }

                        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                            super.onAuthenticationHelp(helpMsgId, helpString)
                            updateStatus(helpString.toString())
                            biometricHandler.onAuthenticationHelp(helpMsgId, helpString!!)
                        }

                        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
                            super.onAuthenticationSucceeded(result)
                            dismissDialog()
                            biometricHandler.onAuthenticationSuccessful()
                        }


                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            updateStatus(ctx.getString(R.string.biometric_failed))
                            biometricHandler.onAuthenticationFailed()
                        }
                    },
                    null)

            displayBiometricDialog(biometricHandler)
        }
    }


    private fun displayBiometricDialog(biometricHandler: BiometricHandler) {
        biometricDialogV23 = BiometricDialogV23(ctx, biometricHandler)
        biometricDialogV23?.show()
    }


    private fun dismissDialog() {
            biometricDialogV23?.dismiss()
    }

    private fun updateStatus(status: String) {
            biometricDialogV23?.updateStatus(status)
    }

    private fun generateKey() {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore!!.load(null)

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator!!.init(KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())

            keyGenerator!!.generateKey()

        } catch (exc: KeyStoreException) {
            exc.printStackTrace()
        } catch (exc: NoSuchAlgorithmException) {
            exc.printStackTrace()
        } catch (exc: NoSuchProviderException) {
            exc.printStackTrace()
        } catch (exc: InvalidAlgorithmParameterException) {
            exc.printStackTrace()
        } catch (exc: CertificateException) {
            exc.printStackTrace()
        } catch (exc: IOException) {
            exc.printStackTrace()
        }

    }


    private fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7)

        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore!!.load(null)
            val key = keyStore!!.getKey(KEY_NAME, null) as SecretKey
            cipher!!.init(Cipher.ENCRYPT_MODE, key)
            return true


        } catch (e: KeyPermanentlyInvalidatedException) {
            return false

        } catch (e: KeyStoreException) {

            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }

    }

    companion object {

        private val KEY_NAME = UUID.randomUUID().toString()
    }
}