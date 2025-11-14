import com.bawnorton.configurable.Configurable;
import com.bawnorton.configurable.Validator;

public class MinAndMaxConflict {
	@Configurable(validator = @Validator(min = 100, max = 0))
	public static int field = 42;
}