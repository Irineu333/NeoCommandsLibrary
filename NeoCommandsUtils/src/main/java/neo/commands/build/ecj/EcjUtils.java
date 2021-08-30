package neo.commands.build.ecj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

import neo.commands.fundation.CommandUtils;
import neo.commands.fundation.Utils;

/**
 * Utilitários de linha de comando para o compilador ecj
 * permite: comandos diretos no ecj, compilar código java
 *
 * @author Irineu A. Silva
 */
public class EcjUtils {

    private final String TAG = getClass().getSimpleName();
    private final File ecj;
    private final String main = "org.eclipse.jdt.internal.compiler.batch.Main";

    //options
    private boolean showWarning = false;

    public EcjUtils(String path) throws FileNotFoundException {
        this(new File((path)));
    }

    public EcjUtils(File ecj) throws FileNotFoundException {
        this.ecj = ecj;
        if (!ecj.exists()) {
            throw new FileNotFoundException(TAG + ": O arquivo especificado não existe!");
        }
    }

    //add ecj command in line command String
    private String ecj(String command) {
        return String.format("%s %s %s", ecj.getPath(), main, command);
    }

    //add ecj command in line command varargs
    private String[] ecj(String[] command) {

        String[] result = new String[command.length + 2];
        result[0] = ecj.getPath();
        result[1] = main;

        System.arraycopy(
                command, 0, //a partir de 0 de command
                result, 2, //a partir de 2 de result
                command.length //até o final de ambos
        );

        return result;
    }

    //compile methods
    //sync

    /**
     * Cria as classes em bytecodes .class de forma síncrona
     *
     * @param source arquivos java source
     * @param gen builder gen folder
     * @param androidClasses classes do sistema
     * @param dir pasta destino das classes
     * @return resultado da operação
     */
    @NonNull
    public Utils.Result<String> syncCompile(String source, String gen, String androidClasses, String dir) {
        return syncCompile(new File(source), new File(gen), new File(androidClasses), new File(dir));
    }

    /**
     * Cria as classes em bytecodes .class de forma síncrona
     *
     * @param source    arquivos java source
     * @param gen       builder gen folder
     * @param bootclass classes do sistema
     * @param dir       pasta destino das classes
     * @return resultado da operação
     */
    @NonNull
    public Utils.Result<String> syncCompile(File source, File gen, File bootclass, File dir) {

        return CommandUtils.DALVIK.CP.syncExec(
                ecj.getPath(), //program
                "org.eclipse.jdt.internal.compiler.batch.Main", //class Main
                "-warn:" + getWarningCommand(), //warning
                "-proc:none", //annotation processor
                "-1.7", //java version,
                "-d", dir.getPath(), //destination directory
                source.getPath(), //java source code
                gen.getPath(), //gen
                "-bootclasspath", bootclass.getPath() //system classes
        );
    }

    //async

    /**
     * Cria as classes em bytecodes .class de forma síncrona
     *
     * @param source         arquivos java source
     * @param gen            builder gen folder
     * @param androidClasses classes do sistema
     * @param dir            pasta destino das classes
     * @return resultado da operação
     */
    @Nullable
    public Process asyncCompile(String source, String gen, String androidClasses, String dir) {
        return asyncCompile(new File(source), new File(gen), new File(androidClasses), new File(dir));
    }

    /**
     * Cria as classes em bytecodes .class de forma síncrona
     *
     * @param source    arquivos java source
     * @param gen       builder gen folder
     * @param bootclass classes do sistema
     * @param dir       pasta destino das classes
     * @return resultado da operação
     */
    @Nullable
    public Process asyncCompile(File source, File gen, File bootclass, File dir) {

        //comandos para compilar arquivos .java em .class utilizando o ecj executado no dalvik atraves do comando cp
        return CommandUtils.DALVIK.CP.asyncExec(
                ecj.getPath(), //program
                "org.eclipse.jdt.internal.compiler.batch.Main", //class Main
                "-warn:" + getWarningCommand(), //warning
                "-proc:none", //annotation processor
                "-1.7", //java version,
                "-d", dir.getPath(), //destination directory
                source.getPath(), //java source code
                gen.getPath(), //gen
                "-bootclasspath", bootclass.getPath() //system classes
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
        return CommandUtils.DALVIK.CP.syncExec(ecj(command));
    }

    /**
     * Executa um comando direto no aapt de forma síncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    public Utils.Result<String> syncExec(@NonNull String command) {
        return CommandUtils.DALVIK.CP.syncExec(ecj(command));
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
        return CommandUtils.DALVIK.CP.asyncExec(ecj(command));
    }

    /**
     * Executa um comando direto no aapt de forma assíncrona
     *
     * @param command comando a ser executado
     * @return resultado da execução
     */
    @Nullable
    public Process asyncExec(@NonNull String command) {
        return CommandUtils.DALVIK.CP.asyncExec(ecj(command));
    }

    private String getWarningCommand() {

        if (showWarning) {
            return "default";
        } else {
            return "none";
        }
    }

    public void setShowWarning(boolean showWarning) {
        this.showWarning = showWarning;
    }

    public boolean isShowWarning() {
        return showWarning;
    }

    /**
     * Manual do ecj
     *
     * @return instruções de como usar
     */
    public Utils.Result<String> help() {
        return CommandUtils.DALVIK.CP.syncExec(
                ecj.getPath(), //program
                "org.eclipse.jdt.internal.compiler.batch.Main", //class Main
                "-help"
        );
    }

    /**
     * Informações sobre a versão do ecj
     *
     * @return instruções da versão
     */
    public Utils.Result<String> version() {
        return syncExec("-v");
    }
}
