package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;
import org.jetbrains.annotations.Nullable;

public class CustomMessageProvider {
    @Configurable(validator = @Validator(message = "simpleMessageProvider", min = 1, max = 99))
    public static int field = 42;

    @Configurable(validator = @Validator(message = "sources.validator.CustomMessageProvider#simpleMessageProvider", min = 1, max = 99))
    public static Integer fieldWithInteger = 42;

    @Configurable(validator = @Validator(message = "a literal message", min = 1, max = 99))
    public static int fieldWithLiteralMessage = 42;

    public static String simpleMessageProvider(@Nullable Integer value) {
        if (value == null) {
            return "Value cannot be null";
        } else if (value < 0 || value > 100) {
            return "Value must be between 0 and 100";
        }
        return "Valid value: " + value;
    }
}