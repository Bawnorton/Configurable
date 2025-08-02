package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderWithMoreThanOneParameter {
    @Configurable(validator = @Validator(message = "wrongNumberOfParameters"))
    public static int field = 42;

    public static String wrongNumberOfParameters(int value, String message) {
        return "";
    }
}