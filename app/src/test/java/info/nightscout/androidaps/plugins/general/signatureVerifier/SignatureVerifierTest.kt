package info.nightscout.androidaps.plugins.general.signatureVerifier

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import com.squareup.otto.Bus
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.logging.L
import info.nightscout.androidaps.utils.SP
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.mockito.ArgumentMatchers.anyObject
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.security.MessageDigest


@RunWith(PowerMockRunner::class)
@PrepareForTest(MainApp::class, L::class, SP::class, PackageManager::class, MessageDigest::class)
class SignatureVerifierTest {

    val time = 10000L;
    var mainApp : MainApp? = null

    var revokedCertString = "51:6D:12:67:4C:27:F4:9B:9F:E5:42:9B:01:B3:98:E4:66:2B:85:B7:A8:DD:70:32:B7:6A:D7:97:9A:0D:97:10"
    var nonRevokedCertString = "22:22:22:67:4C:27:F4:9B:9F:E5:42:9B:01:B3:98:E4:66:2B:85:B7:A8:DD:70:32:B7:6A:D7:97:9A:0D:97:10"

    @Test
    fun hasIllegalSignature() {
        var plugin = SignatureVerifier.getPlugin()

        plugin.revokedCerts = plugin.parseRevokedCertsFile(revokedCertString)

        prepareBus()

        val revokedSignature = Signature(revokedCertString)
        val nonRevokedSignature = Signature(nonRevokedCertString)

        val packageManager = Mockito.mock(PackageManager::class.java)
        `when`(mainApp!!.getPackageManager()).thenReturn(packageManager)

        `when`(mainApp!!.getPackageName()).thenReturn("a")

        val packageInfo = Mockito.mock(PackageInfo::class.java)
        packageInfo.signatures = arrayOf(revokedSignature)
        `when`(packageManager.getPackageInfo(Mockito.anyString(), Mockito.anyInt())).thenReturn(packageInfo)

        // this mock is not working
        val messageDigestMock : MessageDigest = mock(MessageDigest::class.java);
        PowerMockito.mockStatic(MessageDigest::class.java)
        `when`(MessageDigest.getInstance("SHA256")).thenReturn(messageDigestMock)
        `when`(messageDigestMock.digest(any())).thenReturn(packageInfo.signatures[0].toByteArray())

        Assert.assertEquals(revokedSignature.toChars() ,MainApp.instance().packageManager.getPackageInfo("a", 1).signatures[0].toChars())
        Assert.assertTrue(plugin.hasIllegalSignature())

        packageInfo.signatures = arrayOf(nonRevokedSignature)
        Assert.assertFalse(plugin.hasIllegalSignature())

    }

    @Test
    @PrepareForTest(System::class)
    fun `set time`() {
        PowerMockito.spy(System::class.java)
        PowerMockito.`when`(System.currentTimeMillis()).thenReturn(time)

        Assert.assertEquals(time, System.currentTimeMillis())
    }

    private fun prepareBus(): Bus {
        PowerMockito.mockStatic(MainApp::class.java)
        mainApp = Mockito.mock<MainApp>(MainApp::class.java)
        Mockito.`when`(MainApp.instance()).thenReturn(mainApp)
        val bus = Mockito.mock(Bus::class.java)
        Mockito.`when`(MainApp.bus()).thenReturn(bus)
        Mockito.`when`(MainApp.gs(ArgumentMatchers.anyInt())).thenReturn("some dummy string")
        prepareSP()
        return bus
    }

    private fun prepareSP() {
        PowerMockito.mockStatic(SP::class.java)
        Mockito.`when`(SP.getLong(Mockito.anyInt(), Mockito.anyLong())).thenReturn(time - 1)
    }

    private fun prepareLogging() {
        PowerMockito.mockStatic(L::class.java)
        Mockito.`when`(L.isEnabled(Mockito.any())).thenReturn(true)
    }


}