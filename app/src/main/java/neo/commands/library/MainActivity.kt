package neo.commands.library

import android.os.Bundle
import android.os.Environment
import android.util.Log
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

        Log.d(commandDebug, File(native).listFiles()?.joinToString("\n", "files: \n") { it.name + " " + it.permissions() } ?: "Nenhum arquivo")

        val root = File(Environment.getExternalStorageDirectory(), "NeoIDE")
        val platform = File(root, "android-platforms/android-sdk-30.jar")

        //project
        val project = File(root, "Project")

        //main
        val res = File(project, "src/main/res")
        val java = File(project, "src/main/java")

        val manifest = File(project, "AndroidManifest.xml")

        //build
        val gen = File(project, "build/gen")
        val bin = File(project, "build/bin")
        val bytecode = File(bin, "class")
        val dex = File(project, "build/bin/dex")

        //output
        val output = File(project, "build/output")
        val unsignedApk = File(output, "unsigned.apk")
        val signedApk = File(output, "signed.apk")

        //utils
        val aapt = AaptUtils("$native/aapt-arm.so") //is a program
        val ecj = EcjUtils("$native/ecj.jar.so") //ia a lib
        val dx = DxUtils("$native/dx.jar.so") //is a lib

        //programs
        val aapt2 = File("$native/aapt2-arm.so")

        //libs
        val apkSigner = File("$native/apksigner.jar.so")
        val d8 = File("$native/d8.jar.so")

        //versions
        Log.d(commandDebug, "aapt: " + aapt.version().result!!)
        Log.d(commandDebug, "aapt2: " + CommandUtils.syncExec(aapt2.path, "version").result)
        Log.d(commandDebug, "dx: " + dx.version().result!!)
        Log.d(commandDebug, "ecj: " + ecj.version().result!!)

//        CommandUtils.DALVIK.CP.syncExec(d8.path, ).apply {
//            if (isSuccess) {
//                Log.d(commandDebug, result!!)
//            } else {
//                Log.e(commandDebug, error!!)
//            }
//        }
//

        //helps

        val aapt2Help = findViewById<Button>(R.id.aapt2Help)

        aapt2Help.setOnClickListener {
            CommandUtils.syncExec(
                aapt2.path,
                "--help"
            ).apply {
                if (isSuccess) {
                    NeoUtils.showMessage(this@MainActivity, result!!)
                } else {
                    NeoUtils.showDialogError(this@MainActivity, error!!)
                }
            }
        }

        //novas ferramentas


        val btnCompileAapt2 = findViewById<Button>(R.id.compileAapt2)

        btnCompileAapt2.setOnClickListener {

            val alert = NeoUtils.showProgressDialog(
                this,
                "Aapt2 : Compilando recursos..."
            )

            File(bin.path + "/compiled/res").mkdirs()

            val process = CommandUtils.asyncExec(
                aapt2.path,
                "compile",
                "--dir", res.path, //directory find resources (opcional)
                "-o", bin.path + "/compiled/res" //output compiled resources
            )

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

        val linkAapt2 = findViewById<Button>(R.id.linkAapt2)

        linkAapt2.setOnClickListener {
            val alert = NeoUtils.showProgressDialog(
                this,
                "Aapt2 : Linkando recursos..."
            )

            if (!output.exists()){
                output.mkdirs()
            }

            val args = mutableListOf(
                aapt2.path,
                "link",
                "-I", platform.path, //platform
                "--manifest", manifest.path, //manifest
                "-o", unsignedApk.path, //output apk
                "--java", gen.path, //directory R.java (opcional)
                "--min-sdk-version", "21", //opcional
                "--target-sdk-version", "30", //opcional
                "--version-code", "1", //opcional
                "--compile-sdk-version-name", "30", //opcional
            ).apply {
                addAll(File(bin.path + "/compiled/res").listFiles()!!.map { it.path })
            }
            val process = CommandUtils.asyncExec(args.joinToString(" ") { it })

            StreamUtils.asyncReadProcess(process!!, object : Utils.Callback<String> {
                override fun success(result: String) {
                    alert.dismiss()
                    Toast.makeText(this@MainActivity, "Linkado!", Toast.LENGTH_SHORT).show()
                }

                override fun error(error: String) {
                    alert.dismiss()
                    NeoUtils.showDialogError(this@MainActivity, error)
                }
            })
        }

        //processo legacy de criação de apps

        val btnCreateClasseR = findViewById<Button>(R.id.createR)

        btnCreateClasseR.setOnClickListener {
            createR(gen, aapt, res, manifest, platform)
        }

        val btnCompile = findViewById<Button>(R.id.compile)
        btnCompile.setOnClickListener {
            compileJava(ecj, java, gen, platform, bytecode)
        }

        val btnGenDex = findViewById<Button>(R.id.generateDex)
        btnGenDex.setOnClickListener {
            genDex(dex, dx, bytecode)
        }

        val btnBuildApk = findViewById<Button>(R.id.buildApk)
        btnBuildApk.setOnClickListener {
            buildApk(output, aapt, res, manifest, platform, dex, unsignedApk)
        }

        val btnSigner = findViewById<Button>(R.id.signer)
        btnSigner.setOnClickListener {
            signerApk(apkSigner, project, unsignedApk, signedApk)
        }

    }

    private fun createR(
        gen: File,
        aapt: AaptUtils,
        res: File,
        manifest: File,
        framework: File
    ) {
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

    private fun compileJava(
        ecj: EcjUtils,
        java: File,
        gen: File,
        framework: File,
        bytecode: File
    ) {
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

    private fun genDex(dex: File, dx: DxUtils, bytecode: File) {
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

    private fun signerApk(
        apkSigner: File,
        project: File,
        unsignedApk: File,
        signedApk: File
    ) {
        val alert = NeoUtils.showProgressDialog(
            this,
            "Apk Signer : Assinando apk..."
        )

        val process = CommandUtils.DALVIK.CP.asyncExec(
            apkSigner.path, //
            "net.fornwall.apksigner.Main", //
            "-p", "235711", //password
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

    private fun buildApk(
        output: File,
        aapt: AaptUtils,
        res: File,
        manifest: File,
        framework: File,
        dex: File,
        unsignedApk: File
    ) {
        val alert = NeoUtils.showProgressDialog(
            this,
            "Aapt : Construindo apk..."
        )

        if (!output.exists()) {
            output.mkdirs()
        }

        val process = aapt.asyncBuildApkUnsigned(
            1, "1.0",
            21, 30,
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


    companion object {
        private val TAG = this::class.simpleName
    }
}

private fun File.permissions(): String {
    val x = if (canExecute()) "x" else "-"
    val r = if (canRead()) "r" else "-"
    val w = if (canWrite()) "w" else "-"
    return x + r + w
}
