package sources.field;

import com.bawnorton.configurable.Configurable;

public class GroupedFields {
    @Configurable(group = "abc")
    public static int field = 42;

    @Configurable(group = "abc.def")
    public static int field2 = 43;

    @Configurable(group = "abc.def.ghi")
    public static int field3 = 44;

    @Configurable(group = "abc")
    public static int field4 = 45;

    @Configurable(group = "abc.def.ghi.jkl.mno")
    public static int field5 = 46;
}