package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class OnSetWithoutPublicModifier {
	@Configurable(onSet = "privateOnSet")
	public static int field = 42;

	private static void privateOnSet(Integer value, boolean fromSync) { // should be public

	}
}