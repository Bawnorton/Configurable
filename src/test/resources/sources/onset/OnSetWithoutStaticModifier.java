package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class OnSetWithoutStaticModifier {
	@Configurable(onSet = "wrongModifiersOnSet")
	public static int field = 42;

	public void wrongModifiersOnSet(Integer value, boolean fromSync) { // should be a static method
	}
}