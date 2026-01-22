package deus.btd.toml;

import deus.btd.annotations.DeserializeToml;

import deus.btd.annotations.TomlIgnoreClass;
import deus.btd.annotations.TomlIgnoreField;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtomTomlDeserializer {

	public static String toSnakeCase(String str) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) {
					result.append('_');
				}
				result.append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	private static Class<?> getClassFromType(Type type) {
		if (type instanceof Class<?>) return (Class<?>) type;
		if (type instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) type).getRawType();
			if (rawType instanceof Class<?>) return (Class<?>) rawType;
		}
		return null;
	}

	public static <T> T fromToml(TomlTable table, Class<T> clazz) throws Exception {
		if (!clazz.isAnnotationPresent(DeserializeToml.class)) {
			throw new IllegalArgumentException("The class" + clazz.getSimpleName() + " isn't marked with @DeserializeToml");
		}

		T obj = clazz.getDeclaredConstructor().newInstance();

		for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass()) {
			for (Field field : currentClass.getDeclaredFields()) {
				field.setAccessible(true);

				if (field.isAnnotationPresent(TomlIgnoreField.class)) continue;
				if (field.isAnnotationPresent(TomlIgnoreClass.class)) continue;

				String key = toSnakeCase(field.getName());
				if (table.contains(key)) {
					Object value = table.get(key);
					Class<?> type = field.getType();

					if (type == int.class || type == Integer.class) {
						field.set(obj, ((Number)value).intValue());
					} else if (type == long.class || type == Long.class) {
						field.set(obj, ((Number)value).longValue());
					} else if (type == double.class || type == Double.class) {
						field.set(obj, ((Number)value).doubleValue());
					} else if (type == boolean.class || type == Boolean.class) {
						field.set(obj, value);
					} else if (type == String.class) {
						field.set(obj, value.toString());
					} else if (value instanceof TomlTable && Map.class.isAssignableFrom(type)) {
						Type genericType = field.getGenericType();
						Type keyType = String.class;
						Type valueType = Object.class;

						if (genericType instanceof ParameterizedType) {
							ParameterizedType pt = (ParameterizedType) genericType;
							keyType = pt.getActualTypeArguments()[0];
							valueType = pt.getActualTypeArguments()[1];
						}

						Map<Object, Object> map = tomlTableToMap((TomlTable) value, keyType, valueType);
						field.set(obj, map);
					} else if (value instanceof TomlTable && type.isAnnotationPresent(DeserializeToml.class)) {
						TomlTable subTable = (TomlTable) value;
						Object nestedObj = fromToml(subTable, type);
						field.set(obj, nestedObj);
					} else if (value instanceof TomlArray) {
						TomlArray array = (TomlArray) value;

						if (List.class.isAssignableFrom(type)) {
							Type genericType = field.getGenericType();
							if (genericType instanceof ParameterizedType) {
								ParameterizedType pt = (ParameterizedType) genericType;
								Type actualType = pt.getActualTypeArguments()[0];

								List<Object> list = new ArrayList<>();

								if (actualType instanceof ParameterizedType) {
									ParameterizedType innerType = (ParameterizedType) actualType;
									Type innerRawType = innerType.getRawType();

									if (innerRawType instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) innerRawType)) {
										Type keyType = innerType.getActualTypeArguments()[0];
										Type valueType = innerType.getActualTypeArguments()[1];

										for (int i = 0; i < array.size(); i++) {
											Object elem = array.get(i);
											if (elem instanceof TomlTable) {
												Map<Object, Object> map = tomlTableToMap((TomlTable) elem, keyType, valueType);
												list.add(map);
											}
										}
									} else if (innerRawType instanceof Class<?> && List.class.isAssignableFrom((Class<?>) innerRawType)) {
										Type elementType = innerType.getActualTypeArguments()[0];

										for (int i = 0; i < array.size(); i++) {
											Object elem = array.get(i);
											if (elem instanceof TomlArray) {
												TomlArray innerArray = (TomlArray) elem;
												List<Object> innerList = new ArrayList<>();

												for (int j = 0; j < innerArray.size(); j++) {
													Object innerElem = innerArray.get(j);
													Object converted = convertValue(innerElem, elementType);
													if (converted != null) {
														innerList.add(converted);
													}
												}
												list.add(innerList);
											}
										}
									}
								} else if (actualType instanceof Class<?>) {
									Class<?> itemType = (Class<?>) actualType;

									for (int i = 0; i < array.size(); i++) {
										Object elem = array.get(i);

										if (itemType.isAnnotationPresent(DeserializeToml.class) && elem instanceof TomlTable) {
											list.add(fromToml((TomlTable) elem, itemType));
										} else if (itemType == String.class) {
											list.add(elem.toString());
										} else if (itemType == Integer.class || itemType == int.class) {
											list.add(((Number) elem).intValue());
										} else if (itemType == Double.class || itemType == double.class) {
											list.add(((Number) elem).doubleValue());
										} else if (itemType == Boolean.class || itemType == boolean.class) {
											list.add(elem);
										} else {
											System.out.println("Advertencia: tipo de lista no soportado: " + itemType.getTypeName());
										}
									}
								}

								field.set(obj, list);
							}
						}
					}
				}
			}
		}

		return obj;
	}

	private static Map<Object, Object> tomlTableToMap(TomlTable table, Type keyType, Type valueType) {
		Map<Object, Object> map = new HashMap<>();

		for (String key : table.keySet()) {
			Object value = table.get(key);

			Object convertedKey = convertValue(key, keyType);
			Object convertedValue = convertValue(value, valueType);

			if (convertedKey != null && convertedValue != null) {
				map.put(convertedKey, convertedValue);
			}
		}

		return map;
	}

	private static Object convertValue(Object value, Type targetType) {
		if (value instanceof TomlTable) {
			if (targetType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) targetType;
				Type rawType = pt.getRawType();
				if (rawType instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) rawType)) {
					Type keyType = pt.getActualTypeArguments()[0];
					Type valueType = pt.getActualTypeArguments()[1];
					return tomlTableToMap((TomlTable) value, keyType, valueType);
				}
			}

			if (targetType instanceof Class<?> && Map.class.isAssignableFrom((Class<?>) targetType)) {
				return tomlTableToMap((TomlTable) value, String.class, Object.class);
			}
			if (targetType instanceof Class<?> && ((Class<?>) targetType).isAnnotationPresent(DeserializeToml.class)) {
				try {
					return fromToml((TomlTable) value, (Class<?>) targetType);
				} catch (Exception e) {
					System.out.println("Error deserializando objeto: " + e.getMessage());
					return null;
				}
			}
			if (targetType instanceof Class<?> && ((Class<?>) targetType) == Object.class) {
				return tomlTableToMap((TomlTable) value, String.class, Object.class);
			}
		}

		if (targetType instanceof Class<?>) {
			Class<?> clazz = (Class<?>) targetType;

			if (clazz == String.class) {
				return value.toString();
			} else if (clazz == Integer.class || clazz == int.class) {
				if (value instanceof Number) {
					return ((Number) value).intValue();
				}
			} else if (clazz == Long.class || clazz == long.class) {
				if (value instanceof Number) {
					return ((Number) value).longValue();
				}
			} else if (clazz == Double.class || clazz == double.class) {
				if (value instanceof Number) {
					return ((Number) value).doubleValue();
				}
			} else if (clazz == Boolean.class || clazz == boolean.class) {
				return value;
			} else if (clazz == Object.class) {
				return value;
			}
		}

		return value;
	}
}
