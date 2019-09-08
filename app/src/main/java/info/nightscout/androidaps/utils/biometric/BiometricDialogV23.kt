package info.nightscout.androidaps.utils.biometric

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import info.nightscout.androidaps.R
import kotlinx.android.synthetic.main.biometric_dialog.*

class BiometricDialogV23(ctx: Context, biometricHandler: BiometricHandler) : BottomSheetDialog(ctx, R.style.BottomSheetDialogTheme) {

    init {
        val bottomSheetView = layoutInflater.inflate(R.layout.biometric_dialog, null)
        setContentView(bottomSheetView)

        btn_cancel.setOnClickListener {
            dismiss()
            biometricHandler.onAuthenticationCancelled()
        }
    }

    fun updateStatus(status: String) {
        item_status?.text = status
    }
}
