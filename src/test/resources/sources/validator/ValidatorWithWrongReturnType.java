package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorWithWrongReturnType {
    @Configurable(validator = @Validator("wrongReturnTypeValidator"))
    public static int fieldWithWrongReturnType = 42;

    public static String wrongReturnTypeValidator(Integer value) { // should return boolean
        return "";
    }
}