package neo.commands.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import neo.commands.build.dx.DxUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val commandDebug = "commandDebug"
        val native = applicationInfo.nativeLibraryDir

        DxUtils("$native/dx.jar.so").help().apply {
            if (isSuccess) {
                Log.d(commandDebug, result!!)
            } else {
                Log.e(commandDebug, error!!)
            }
        }
    }


    companion object {
        private val TAG = this::class.simpleName
    }
}