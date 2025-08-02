package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorWithoutStaticModifier {
    @Configurable(validator = @Validator("wrongModifiersValidator"))
    public static int fieldWithWrongModifiers = 42;

    public boolean wrongModifiersValidator(Integer value) { // should be a static method
        return true;
    }
}