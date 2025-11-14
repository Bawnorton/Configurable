package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class DefaultValueSmallerThanBounds {
	@Configurable(validator = @Validator(min = 50, max = 100))
	public static int field = 42;
}