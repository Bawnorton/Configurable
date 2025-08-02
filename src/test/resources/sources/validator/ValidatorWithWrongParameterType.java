package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorWithWrongParameterType {
    @Configurable(validator = @Validator("wrongParameterValidator"))
    public static int field = 42;

    public static boolean wrongParameterValidator(int value) { // should be Integer
        return true;
    }
}