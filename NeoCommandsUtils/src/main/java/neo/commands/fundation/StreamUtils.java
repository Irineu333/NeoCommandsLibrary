package neo.commands.fundation;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Utilitários de Stream de dados
 * @author Irineu A. Silva
 */
public class StreamUtils {

    static int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * Ler um processo e retorna o resultado tratado de forma síncrona
     * @param process processo a ser lido
     * @return resultado
     */
    @NonNull
    static public Utils.Result<String> syncReadProcess(@NonNull Process process) {

        try {
            if (process.waitFor() == 0) {
                String result = StreamUtils.readInputStream(process.getInputStream());

                if (result.isEmpty()) { //tratando caso do --version do dx
                    result = StreamUtils.readInputStream(process.getErrorStream());
                }

                return new Utils.Result<String>(true, result, null);
            } else {
                String error = StreamUtils.readInputStream(process.getErrorStream());

                if (error.isEmpty()) { //tratando caso do --version do dx
                    error = StreamUtils.readInputStream(process.getInputStream());
                }

                return new Utils.Result<String>(false, null, error);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Utils.Result<String>(false, null, e.getMessage());
        }
    }

    /**
     * Ler um processo e chama o callback passando o resultado tratado de forma síncrona
     * @param process processo a ser lido
     */
    static public void syncReadProcess(@NonNull Process process, @NonNull Utils.Callback<String> callback) {

        Utils.Result<String> result = syncReadProcess(process);

        if (result.isSuccess()) {
            assert result.getResult() != null;
            callback.success(result.getResult());
        } else {
            assert result.getError() != null;
            callback.error(result.getError());
        }
    }

    /**
     * Ler um processo e chama o callback passando o resultado tratado de forma assíncrona
     * @param process processo a ser lido
     */
    static public void asyncReadProcess(@NonNull final Process process, @NonNull final Utils.Callback<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Utils.Result<String> result = syncReadProcess(process);

                if (result.isSuccess()) {
                    assert result.getResult() != null;
                    callback.success(result.getResult());
                } else {
                    assert result.getError() != null;
                    callback.error(result.getError());
                }
            }
        }).start();
    }

    @NonNull
    static public String readInputStream(@NonNull InputStream steam) {
        return getMessage(new InputStreamReader(steam));
    }

    @NonNull
    private static String getMessage(InputStreamReader inputStreamReader) {
        StringWriter buffer = new StringWriter();
        try {
            long charsCopied = copyTo(inputStreamReader, buffer);
            Log.d("getMessage", "charsCopied: " + charsCopied);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return buffer.toString();
    }

    private static long copyTo(InputStreamReader inputStreamReader, Writer out) throws IOException {
        long charsCopied = 0;
        int bufferSize = DEFAULT_BUFFER_SIZE;

        char[] buffer = new char[bufferSize];
        int chars = inputStreamReader.read(buffer);

        while (chars >= 0) {
            out.write(buffer, 0, chars);
            charsCopied += chars;
            chars = inputStreamReader.read(buffer);
        }

        return charsCopied;
    }
}
