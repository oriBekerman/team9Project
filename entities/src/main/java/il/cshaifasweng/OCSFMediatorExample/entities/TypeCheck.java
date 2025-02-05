package il.cshaifasweng.OCSFMediatorExample.entities;

import java.util.List;

public class TypeCheck {
	public static <T> boolean isListOfType(Object obj, Class<T> type) {
		if (obj instanceof List<?>) {
			List<?> list = (List<?>) obj;
			return !list.isEmpty() && type.isInstance(list.get(0));
		}
		return false;
	}
}
