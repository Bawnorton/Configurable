package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class ValidatorWithoutPublicModifier {
	@Configurable(validator = @Validator("privateValidator"))
	public static int field = 42;

	private static boolean privateValidator(int value) { // should be public
		return true;
	}
}