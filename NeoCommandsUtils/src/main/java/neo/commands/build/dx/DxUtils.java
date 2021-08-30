package neo.commands.build.dx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

import neo.commands.fundation.CommandUtils;
import neo.commands.fundation.Utils;

/**
 * Utilitários de linha de comando para o dx
 * permite: comandos diretos no dx, criar bytecode dex
 *
 * @author Irineu A. Silva
 */
public class DxUtils {

    private final String TAG = getClass().getSimpleName();
    private final File dx;
    private final String main = "dx.dx.command.Main";

    public DxUtils(String path) throws Exception {
        this(new File(path));
    }

    public DxUtils(File dx) throws Exception {
        this.dx = dx;
        if (!dx.exists()) {
            throw new FileNotFoundException(TAG + ": O arquivo especificado não existe!");
        }
    }

    //add ecj command in line command String
    private String dx(String command) {
        return String.format("%s %s %s", dx.getPath(), main, command);
    }

    //add ecj command in line command varargs
    private String[] dx(String[] command) {

        String[] result = new String[command.length + 2];
        result[0] = dx.getPath();
        result[1] = main;

        System.arraycopy(
                command, 0, //a partir de 0 de command
                result, 2, //a partir de 2 de result
                command.length //até o final de ambos
        );

        return result;
    }

    //createDex
    //sync

    @NonNull
    public Utils.Result<String> syncCreateDex(@NonNull String input, @NonNull String output) {
        return syncCreateDex(new File(input), new File(output));
    }

    @Nullable
    public Process asyncCreateDex(@NonNull String input, @NonNull String output) {
        return asyncCreateDex(new File(input), new File(output));
    }

    //async

    @NonNull
    public Utils.Result<String> syncCreateDex(File input, File output) {
        /*
         * Criando o bytecode dex, usando dalvikvm e dx.jar de forma síncrona
         * @author Irineu A. Silva
         */
        return CommandUtils.DALVIK.CP.syncExec(
                dx.getPath(), //dx program
                "dx.dx.command.Main", //dx Main classe path
                "--dex", //gerar dex
                "--output=" + output.getPath(), //saída .dex
                input.getPath() //entrada .class ou .jar
        );
    }

    @Nullable
    public Process asyncCreateDex(File input, File output) {
        /*
         * Criando o bytecode dex, usando dalvikvm e dx.jar forma assíncrona
         * @author Irineu A. Silva
         */
        return CommandUtils.DALVIK.CP.asyncExec(
                dx.getPath(), //dx program
                "dx.dx.command.Main", //dx Main classe path
                "--dex", //gerar dex
                "--output=" + output.getPath(), //saída .dex
                input.getPath() //entrada .class ou .jar
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
        return CommandUtils.DALVIK.CP.syncExec(dx(command));
    }

    /**
     * Executa um comando direto no aapt de forma síncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    public Utils.Result<String> syncExec(@NonNull String command) {
        return CommandUtils.DALVIK.CP.syncExec(dx(command));
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
        return CommandUtils.DALVIK.CP.asyncExec(dx(command));
    }

    /**
     * Executa um comando direto no aapt de forma assíncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncExec(@NonNull String command) {
        return CommandUtils.DALVIK.CP.asyncExec(dx(command));
    }

    public Utils.Result<String> help() {
        return CommandUtils.DALVIK.CP.syncExec(
                dx.getPath(), //program
                "dx.dx.command.Main", //class Main
                "--help"
        );
    }

    public Utils.Result<String> version() {
        return syncExec("--version");
    }
}
