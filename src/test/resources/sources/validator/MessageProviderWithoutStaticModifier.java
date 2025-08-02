package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderWithoutStaticModifier {
    @Configurable(validator = @Validator(message = "wrongModifiersMessageProvider"))
    public static int field = 42;

    public String wrongModifiersMessageProvider(Integer value) { // should be a static method
        return "";
    }
}