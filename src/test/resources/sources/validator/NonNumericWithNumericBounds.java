package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class NonNumericWithNumericBounds {
    @Configurable(validator = @Validator(min = 0, max = 100))
    public static String field = "42";
}