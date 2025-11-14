package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class DefaultValueGreaterThanBounds {
	@Configurable(validator = @Validator(min = 0, max = 10))
	public static int field = 42;
}