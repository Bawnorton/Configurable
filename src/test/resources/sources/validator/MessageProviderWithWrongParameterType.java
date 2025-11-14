package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderWithWrongParameterType {
	@Configurable(validator = @Validator(message = "wrongParameterMessageProvider"))
	public static int field = 42;

	public static String wrongParameterMessageProvider(int value) { // should be Integer
		return "";
	}
}