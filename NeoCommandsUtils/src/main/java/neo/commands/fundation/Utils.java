package neo.commands.fundation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Utils  {

    public interface Callback <E> {
       void success(@NonNull E result);
       void error(@NonNull String error);
    }

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
