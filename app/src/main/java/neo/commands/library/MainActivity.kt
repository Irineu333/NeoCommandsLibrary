package neo.commands.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import neo.commands.build.aapt.AaptUtils
import neo.commands.build.dx.DxUtils
import neo.commands.build.ecj.EcjUtils
import neo.commands.library.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val commandDebug = "commandDebug"
        val native = applicationInfo.nativeLibraryDir

        //utils
        val aapt = AaptUtils("$native/aapt-arm.so")
        val ecj = EcjUtils("$native/ecj.jar.so")
        val dc = DxUtils("$native/dx.jar.so")

        val apkSigner = File("$native/apksigner.jar.so")


//        binding.createR.setOnClickListener {
//            aapt.syncCreateR()
//        }

    }


    companion object {
        private val TAG = this::class.simpleName
    }
}