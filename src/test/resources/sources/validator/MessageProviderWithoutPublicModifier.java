package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderWithoutPublicModifier {
    @Configurable(validator = @Validator(message = "privateMessageProvider"))
    public static int field = 42;

    private static String privateMessageProvider(Integer value) { // should be public
        return "";
    }
}