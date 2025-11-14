package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class OnSetWithWrongParameterType {
	@Configurable(onSet = "wrongParameterOnSet")
	public static int field = 42;

	public static void wrongParameterOnSet(int value, boolean fromSync) { // should be Integer
	}
}