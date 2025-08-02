package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class CustomValidator {
    @Configurable(validator = @Validator("simpleValidator"))
    public static int field = 42;

    @Configurable(validator = @Validator("sources.validator.CustomValidator#simpleValidator"))
    public static Integer fieldWithInteger = 42;

    @Configurable(validator = @Validator(min = 0))
    public static int fieldWithMin = 42;

    @Configurable(validator = @Validator(max = 100))
    public static int fieldWithMax = 42;

    public static boolean simpleValidator(Integer value) {
        return value != null && value >= 0 && value <= 100;
    }
}