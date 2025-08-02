package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderWithWrongReturnType {
    @Configurable(validator = @Validator(message = "wrongReturnTypeMessageProvider"))
    public static int fieldWithWrongReturnType = 42;

    public static boolean wrongReturnTypeMessageProvider(Integer value) { // should return String
        return true;
    }
}