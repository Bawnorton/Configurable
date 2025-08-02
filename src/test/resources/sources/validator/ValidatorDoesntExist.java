package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorDoesntExist {
    @Configurable(validator = @Validator("nonExistentValidator"))
    public static int field = 42;
}