package neo.commands.library

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import neo.commands.build.aapt.AaptUtils
import neo.commands.build.dx.DxUtils
import neo.commands.build.ecj.EcjUtils
import neo.commands.fundation.CommandUtils
import neo.commands.fundation.StreamUtils
import neo.commands.fundation.Utils
import neo.commands.library.utils.NeoUtils
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val commandDebug = "commandDebug"
        val native = applicationInfo.nativeLibraryDir

        val root = File(Environment.getExternalStorageDirectory(), "NeoIDE")
        val framework = File(root, "framework/android.jar")

        //project
        val project = File(root, "Project")

        //main
        val res = File(project, "src/main/res")
        val java = File(project, "src/main/java")

        val manifest = File(project, "AndroidManifest.xml")

        //build
        val gen = File(project, "build/gen")
        val bytecode = File(project, "build/bin/class")
        val dex = File(project, "build/bin/dex")

        //output
        val output = File(project, "build/output")
        val unsignedApk = File(output, "unsigned.apk")
        val signedApk = File(output, "signed.apk")

        //utils
        val aapt = AaptUtils("$native/aapt-arm.so")
        val ecj = EcjUtils("$native/ecj.jar.so")
        val dx = DxUtils("$native/dx.jar.so")

        //programs
        val apkSigner = File("$native/apksigner.jar.so")

        val btnCreateClasseR = findViewById<Button>(R.id.createR)

        btnCreateClasseR.setOnClickListener {

            val alert = NeoUtils.showProgressDialog(
                this,
                "Aapt : Indexando recursos..."
            )

            if (!gen.exists()) {
                gen.mkdirs()
            }

            val process = aapt.asyncCreateR(
                gen, res,
                manifest,
                framework
            )

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Criado!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

        val btnCompile = findViewById<Button>(R.id.compile)
        btnCompile.setOnClickListener {
            val alert = NeoUtils.showProgressDialog(
                this,
                "Ecj : Compilando código java..."
            )

            val process = ecj.asyncCompile(java, gen, framework, bytecode)

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Compilado!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

        val btnGenDex = findViewById<Button>(R.id.generateDex)
        btnGenDex.setOnClickListener {
            val alert = NeoUtils.showProgressDialog(
                this,
                "Dx : Gerando bytecode dex..."
            )

            if (!dex.exists()) {
                dex.mkdirs()
            }

            val process = dx.asyncCreateDex(bytecode, dex)

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Gerado!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

        val btnBuildApk = findViewById<Button>(R.id.buildApk)
        btnBuildApk.setOnClickListener {
            val alert = NeoUtils.showProgressDialog(
                this,
                "Aapt : Construindo apk..."
            )

            if (!output.exists()) {
                output.mkdirs()
            }

            val process = aapt.asyncBuildApkUnsigned(
                1, "1.0",
                14, 21,
                res,
                manifest,
                framework,
                dex,
                unsignedApk
            )

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Criando!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

        val btnSigner = findViewById<Button>(R.id.signer)
        btnSigner.setOnClickListener {
            val alert = NeoUtils.showProgressDialog(
                this,
                "Apk Signer : Assinando apk..."
            )

            val process = CommandUtils.DALVIK.CP.asyncExec(
                apkSigner.path, //
                "net.fornwall.apksigner.Main", //
                "-p", //
                "235711", //
                project.path + "/keys/keystore.jks", //
                unsignedApk.path, //
                signedApk.path //
            )

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Assinado!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

    }


    companion object {
        private val TAG = this::class.simpleName
    }
}