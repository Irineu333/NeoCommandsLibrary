package neo.commands.build.aapt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

import neo.commands.fundation.CommandUtils;
import neo.commands.fundation.Utils;

/**
 * Utilitário de linha de comando para aapt
 * permite: comandos no aapt, comando package, criar classe R, criar apk
 *
 * @author Irineu A. Silva
 */
public class AaptUtils {

    private final String TAG = getClass().getSimpleName();

    //package command easy
    public final PACKAGE P = new PACKAGE();

    private final File aapt;

    public AaptUtils(String path) throws Exception {
        this(new File(path));
    }

    public AaptUtils(File aapt) throws Exception {
        this.aapt = aapt;
        if (!aapt.exists()) {
            throw new FileNotFoundException(TAG + ": O arquivo especificado não existe!");
        }
    }

    //add aapt command in line command String
    private String aapt(String command) {
        return String.format("%s %s", aapt.getPath(), command);
    }

    //add aapt command in line command varargs
    private String[] aapt(String[] command) {

        String[] result = new String[command.length + 1];
        result[0] = aapt.getPath();

        System.arraycopy(
                command, 0, //a partir de 0 de command
                result, 1, //a partir de 1 de result
                command.length //até o final de ambos
        );

        return result;
    }

    /**
     * Obtém informações sobre a versão do aapt especificado
     *
     * @return resultado do comando
     */
    public Utils.Result<String> version() {
        return CommandUtils.syncExec(
                aapt.getPath(), //program
                "v" //ou "version"
        );
    }

    //createR
    //sync

    /**
     * Criar a classe R, que contém as definições de todos os recursos do pacote
     *
     * @param gen       arquivo gerados, onde fica a classe R
     * @param res       arquivos de recursos
     * @param manifest  manifest do módulo
     * @param framework classes do framework android
     * @return resultado do processo
     */
    @NonNull
    public Utils.Result<String> syncCreateR(File gen, File res, File manifest, File framework) {
        return CommandUtils.syncExec(
                aapt.getPath(), //program
                "p", //ou "package", package the android resources
                "-m", //make package directories under location specified by -J
                "-J", gen.getPath(), //-j  specify where to output R.java resource constant definitions
                "-S", res.getPath(), //directory in which to find resources.
                "-M", manifest.getPath(), // specify full path to AndroidManifest.xml to include in zip
                "-I", framework.getPath() //add an existing package to base include (android framework)
        );
    }

    //async

    /**
     * Criar a classe R, que contém as definições de todos os recursos do pacote
     *
     * @param gen       arquivo gerados, onde fica a classe R
     * @param res       arquivos de recursos
     * @param manifest  manifest do módulo
     * @param framework classes do framework android
     * @return resultado do processo
     */
    @Nullable
    public Process asyncCreateR(File gen, File res, File manifest, File framework) {
        return CommandUtils.asyncExec(
                aapt.getPath(), //program
                "p", //ou "package", package the android resources
                "-m", //make package directories under location specified by -J
                "-J", gen.getPath(), //-j  specify where to output R.java resource constant definitions
                "-S", res.getPath(), //directory in which to find resources.
                "-M", manifest.getPath(), // specify full path to AndroidManifest.xml to include in zip
                "-I", framework.getPath() //add an existing package to base include (android framework)
        );
    }

    //exec methods
    //sync

    /**
     * Executa um comando direto no aapt de forma síncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    @NonNull
    public Utils.Result<String> syncExec(@NonNull String... command) {
        return CommandUtils.syncExec(aapt(command));
    }

    /**
     * Executa um comando direto no aapt de forma síncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    public Utils.Result<String> syncExec(@NonNull String command) {
        return CommandUtils.syncExec(aapt(command));
    }

    //async

    /**
     * Executa um comando direto no aapt de forma assíncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncExec(@NonNull String... command) {
        return CommandUtils.asyncExec(aapt(command));
    }

    /**
     * Executa um comando direto no aapt de forma assíncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncExec(@NonNull String command) {
        return CommandUtils.asyncExec(aapt(command));
    }

    //buildApkUnsigned methods
    //sync

    /**
     * Cria um apk não assinado de forma síncrona
     *
     * @param versionCode      código único da versão
     * @param versionName      nome da versão
     * @param minSdkVersion    versão mínima suportada
     * @param targetSdkVersion ultima versão otimizada
     * @param resourcesPath    recursos do aplicativo (layout, values, drawables etc.)
     * @param androidManifest  manifesto do aplicativo
     * @param androidClasses   classes do framework android (android.jar)
     * @param dexFiles         arquivos bytecode dex (.dex)
     * @param output           destino do apk não assinado
     * @return resultado da execução
     */
    @NonNull
    public Utils.Result<String> syncBuildApkUnsigned(
            int versionCode, @NonNull String versionName,
            int minSdkVersion, int targetSdkVersion,
            @NonNull String resourcesPath,
            @NonNull String androidManifest,
            @NonNull String androidClasses,
            @NonNull String dexFiles,
            @NonNull String output
    ) {
        return syncBuildApkUnsigned(
                versionCode, versionName,
                minSdkVersion, targetSdkVersion,
                new File(resourcesPath),
                new File(androidManifest),
                new File(androidClasses),
                new File(dexFiles),
                new File(output)
        );
    }

    /**
     * Cria um apk não assinado de forma síncrona
     *
     * @param versionCode      código único da versão
     * @param versionName      nome da versão
     * @param minSdkVersion    versão mínima suportada
     * @param targetSdkVersion ultima versão otimizada
     * @param resourcesFile    recursos do aplicativo (layout, values, drawables etc.)
     * @param androidManifest  manifesto do aplicativo
     * @param androidClasses   classes do framework android (android.jar)
     * @param dexFiles         arquivos bytecode dex (.dex)
     * @param output           destino do apk não assinado
     * @return resultado da execução
     */
    @NonNull
    public Utils.Result<String> syncBuildApkUnsigned(
            int versionCode, @NonNull String versionName,
            int minSdkVersion, int targetSdkVersion,
            @NonNull File resourcesFile,
            @NonNull File androidManifest,
            @NonNull File androidClasses,
            @NonNull File dexFiles,
            @NonNull File output
    ) {
        //argumentos para criar um apk não assinado utilizando o aapt
        return P.syncExec(
                "--version-code", "1", //versão code do app [arg]
                "--version-name", "1.0", //versão name do app [arg]
                "--min-sdk-version", "14", //versão mínima suportada [arg]
                "--target-sdk-version", "21", //ultima versão otimizada [arg]
                "-S", resourcesFile.getPath(), //resources [arg]
                "-M", androidManifest.getPath(), //manifest [arg]
                "-I", androidClasses.getPath(), //android classes [arg]
                "-F", output.getPath(), //saída [arg]
                dexFiles.getPath() //dex files [arg]
        );
    }

    //async

    /**
     * Cria um apk não assinado de forma síncrona
     *
     * @param versionCode      código único da versão
     * @param versionName      nome da versão
     * @param minSdkVersion    versão mínima suportada
     * @param targetSdkVersion ultima versão otimizada
     * @param resourcesPath    recursos do aplicativo (layout, values, drawables etc.)
     * @param androidManifest  manifesto do aplicativo
     * @param androidClasses   classes do framework android (android.jar)
     * @param dexFiles         arquivos bytecode dex (.dex)
     * @param output           destino do apk não assinado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncBuildApkUnsigned(
            int versionCode, @NonNull String versionName,
            int minSdkVersion, int targetSdkVersion,
            @NonNull String resourcesPath,
            @NonNull String androidManifest,
            @NonNull String androidClasses,
            @NonNull String dexFiles,
            @NonNull String output
    ) {
        return asyncBuildApkUnsigned(
                versionCode, versionName,
                minSdkVersion, targetSdkVersion,
                new File(resourcesPath),
                new File(androidManifest),
                new File(androidClasses),
                new File(dexFiles),
                new File(output)
        );
    }

    /**
     * Cria um apk não assinado de forma síncrona
     *
     * @param versionCode      código único da versão
     * @param versionName      nome da versão
     * @param minSdkVersion    versão mínima suportada
     * @param targetSdkVersion ultima versão otimizada
     * @param resourcesFile    recursos do aplicativo (layout, values, drawables etc.)
     * @param androidManifest  manifesto do aplicativo
     * @param androidClasses   classes do framework android (android.jar)
     * @param dexFiles         arquivos bytecode dex (.dex)
     * @param output           destino do apk não assinado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncBuildApkUnsigned(
            int versionCode, @NonNull String versionName,
            int minSdkVersion, int targetSdkVersion,
            @NonNull File resourcesFile,
            @NonNull File androidManifest,
            @NonNull File androidClasses,
            @NonNull File dexFiles,
            @NonNull File output
    ) {
        //comandos para criar um apk não assinado utilizando o aapt
        return asyncExec(
                "p", //empacota recursos do aplicativo [command]
                "--version-code", "1", //versão code do app [arg]
                "--version-name", "1.0", //versão name do app [arg]
                "--min-sdk-version", "14", //versão mínima suportada [arg]
                "--target-sdk-version", "21", //ultima versão otimizada [arg]
                "-S", resourcesFile.getPath(), //resources [arg]
                "-M", androidManifest.getPath(), //manifest [arg]
                "-I", androidClasses.getPath(), //android classes [arg]
                "-F", output.getPath(), //saída [arg]
                dexFiles.getPath() //dex files [arg]
        );
    }

    //classes

    /**
     * Comando p ou package empacota os arquivos do aplicativo
     *
     * @author Irineu A. Silva
     */
    public class PACKAGE {

        //comando package
        private final String PACKAGE_COMMAND = "p";

        private PACKAGE() {
        }

        //add package command in line command String
        private String _package(String command) {
            return String.format("%s %s", PACKAGE_COMMAND, command);
        }

        //add package command in line command varargs
        private String[] _package(String[] command) {

            String[] result = new String[command.length + 1];
            result[0] = PACKAGE_COMMAND;

            System.arraycopy(
                    command, 0, //a partir de 0 de command
                    result, 1, //a partir de 1 de result
                    command.length //até o final de ambos
            );

            return result;
        }


        //exec methods
        //sync

        /**
         * Executa o comando package direto no aapt de forma síncrona
         *
         * @param command comando a ser executado
         * @return resultado da execução
         */
        @NonNull
        public Utils.Result<String> syncExec(@NonNull String... command) {
            return AaptUtils.this.syncExec(_package(command));
        }

        /**
         * Executa o comando package direto no aapt de forma síncrona
         *
         * @param command comando a ser executado
         * @return resultado da execução
         */
        public Utils.Result<String> syncExec(@NonNull String command) {
            return AaptUtils.this.syncExec(_package(command));
        }

        //async

        /**
         * Executa o comando package direto no aapt de forma assíncrona
         *
         * @param command comando a ser executado
         * @return resultado da execução
         */
        @Nullable
        public Process asyncExec(@NonNull String... command) {
            return AaptUtils.this.asyncExec(_package(command));
        }

        /**
         * Executa o comando package direto no aapt de forma assíncrona
         *
         * @param command comando a ser executado
         * @return resultado da execução
         */
        @Nullable
        public Process asyncExec(@NonNull String command) {
            return AaptUtils.this.asyncExec(_package(command));
        }

    }
}


