package carpet.utils.algebraic;


import java.util.HashMap;
import java.util.Map;

public class EnumAdtMatcher<E> extends SingleAdtMatcher<E> {
	public Map<String, E> nameMap = new HashMap<>();

	public EnumAdtMatcher(Class<E> type) {
		if (!type.isEnum())
			throw new IllegalArgumentException("Creating EnumAdtMatcher with non-enum type");
		for (E elem : type.getEnumConstants()) {
			nameMap.put(elem.toString(), elem);
		}
	}

	@Override
	E parseDirectly(String raw) {
		return nameMap.get(raw);
	}
}
