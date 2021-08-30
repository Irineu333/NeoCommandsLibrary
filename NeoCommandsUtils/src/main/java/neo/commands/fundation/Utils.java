package neo.commands.fundation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Fundação de utilitários
 * @author Irineu A. Silva
 */
public class Utils  {

    /**
     * Callback universal
     * @author Irineu A. Silva
     * @param <E> tipo do resultado
     */
    public interface Callback <E> {
       void success(@NonNull E result);
       void error(@NonNull String error);
    }

    /**
     * Resultado universal
     * @author Irineu A. Silva
     * @param <E> tipo do resultado
     */
    public static class Result <E> {

        @Nullable
        final private String error;
        final private Boolean success;
        @Nullable
        final private E result;

        public Result(Boolean success, @Nullable E result, @Nullable String error) {
            this.error = error;
            this.success = success;
            this.result = result;
        }

        public Boolean isSuccess() {
            return success;
        }

        public Boolean isError() {
            return !success;
        }

        @Nullable
        public String getError() {
            return error;
        }

        @Nullable
        public E getResult() {
            return result;
        }

        public void verify(@NonNull Callback<E> callback) {
            if (isSuccess()) {
                assert getResult() != null;
                callback.success(getResult());
            } else {
                assert getError() != null;
                callback.error(getError());
            }
        }
    }
}
