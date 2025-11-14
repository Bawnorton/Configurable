package sources.validator;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class OnSetWithWrongReturnType {
	@Configurable(onSet = "wrongReturnTypeOnSet")
	public static int fieldWithWrongReturnType = 42;

	public static boolean wrongReturnTypeOnSet(Integer value, boolean fromSync) { // should return String
		return true;
	}
}