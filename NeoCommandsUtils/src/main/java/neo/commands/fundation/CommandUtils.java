package neo.commands.fundation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * Utilitários de linha de comando
 * @author Irineu A. Silva
 */
public class CommandUtils {

    private static String TAG = "CommandUtils";
    private static final String DALVIKVM = "dalvikvm";

    /**
     * Executa um comando em String de forma síncrona
     */
    public static Utils.Result<String> syncExec(String command) {
        try {
            Log.d(TAG, "syncExec: " + command);
            Process process = Runtime.getRuntime().exec(command);
            return StreamUtils.syncReadProcess(process);
        } catch (IOException e) {
            e.printStackTrace();
            return new Utils.Result<>(false, null, e.getMessage());
        }
    }

    /**
     * Executa um comando em Array de forma síncrona
     *
     * @param command comando
     * @return resultado
     */
    public static Utils.Result<String> syncExec(String... command) {
        try {
            Log.d(TAG, "syncExec:" + join(command));
            Process process = Runtime.getRuntime().exec(command);
            return StreamUtils.syncReadProcess(process);
        } catch (IOException e) {
            e.printStackTrace();
            return new Utils.Result<>(false, null, e.getMessage());
        }
    }

    /**
     * Executa um comando em Array de forma assíncrona
     *
     * @param command comando
     * @return processo
     */
    @Nullable
    public static Process asyncExec(String... command) {
        try {
            Log.d(TAG, "syncExec:" + join(command));
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Executa um comando em String de forma assíncrona
     *
     * @param command comando
     * @return processo
     */
    @Nullable
    public static Process asyncExec(String command) {
        try {
            Log.d(TAG, "syncExec: " + command);
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String join(String[] command) {
        StringBuilder result = new StringBuilder();

        for (String c : command) {
            result.append(" ").append(c);
        }

        return result.toString();
    }

    /**
     * Comando direcionados ao dalvik
     * @author Irineu A. Silva
     */
    public static class DALVIK {

        private static final String CLASSPATH = "-cp";

        /**
         * Comando -cp do dalvik, executa a clase Main especificada
         * @author Irineu A. Silva
         */
        public static class CP {
            private static String classpath(String command) {
                return String.format("%s %s", CLASSPATH, command);
            }

            private static String[] classpath(String[] command) {

                String[] result = new String[command.length + 1];
                result[0] = CLASSPATH;

                System.arraycopy(
                        command, 0, //a partir de 0 de command
                        result, 1, //a partir de 1 de result
                        command.length //até o final de ambos
                );

                return result;
            }

            @Nullable
            public static Process asyncExec(String command) {
                return CommandUtils.DALVIK.asyncExec(classpath(command));
            }

            @Nullable
            public static Process asyncExec(String... command) {
                return CommandUtils.DALVIK.asyncExec(classpath(command));
            }

            @NonNull
            public static Utils.Result<String> syncExec(String command) {
                return CommandUtils.DALVIK.syncExec(classpath(command));
            }

            @NonNull
            public static Utils.Result<String> syncExec(String... command) {
                return CommandUtils.DALVIK.syncExec(classpath(command));
            }
        }

        private static String dalvikvm(String command) {
            return String.format("%s %s", DALVIKVM, command);
        }

        private static String[] dalvikvm(String[] command) {

            String[] result = new String[command.length + 1];
            result[0] = DALVIKVM;

            System.arraycopy(
                    command, 0, //a partir de 0 de command
                    result, 1, //a partir de 1 de result
                    command.length //até o final de ambos
            );

            return result;
        }

        @Nullable
        public static Process asyncExec(String command) {
            return CommandUtils.asyncExec(dalvikvm(command));
        }

        @Nullable
        public static Process asyncExec(String... command) {
            return CommandUtils.asyncExec(dalvikvm(command));
        }

        @NonNull
        public static Utils.Result<String> syncExec(String command) {
            return CommandUtils.syncExec(dalvikvm(command));
        }

        @NonNull
        public static Utils.Result<String> syncExec(String... command) {
            return CommandUtils.syncExec(dalvikvm(command));
        }
    }
}
