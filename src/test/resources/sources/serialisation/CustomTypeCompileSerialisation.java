package sources.serialisation;

import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;
import com.bawnorton.configurable.bootstrap.ServiceLoadedValue;

public class CustomTypeCompileSerialisation {
	@Configurable(validator = @Validator(fallback = false))
	public static ServiceLoadedValue customValue = new ServiceLoadedValue("seed-value");
}

