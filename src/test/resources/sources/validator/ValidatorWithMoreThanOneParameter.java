package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorWithMoreThanOneParameter {
	@Configurable(validator = @Validator("wrongNumberOfParameters"))
	public static int field = 42;

	public static boolean wrongNumberOfParameters(int value, String message) {
		return value > 0;
	}
}