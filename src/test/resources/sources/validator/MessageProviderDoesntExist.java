package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MessageProviderDoesntExist {
	@Configurable(validator = @Validator(message = "nonExistentMessageProvider"))
	public static int field = 42;
}