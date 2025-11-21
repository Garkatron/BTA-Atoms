package deus.utils.item;

import net.minecraft.core.item.ItemFood;

public class ItemFoodFactory<T extends ItemFood> {

	private final String name;
	private final String namespaceId;
	private final int id;
	private final Class<T> foodClass;

	private int healAmount = 1;
	private int ticksPerHeal = 32;
	private int maxStackSize = 32;
	private boolean favouriteDog = false;

	private T itemFood;

	public ItemFoodFactory(String name, String namespaceId, int id) {
		this((Class<T>) ItemFood.class, name, namespaceId, id);
	}

	public ItemFoodFactory(Class<T> foodClass, String name, String namespaceId, int id) {
		this.foodClass = foodClass;
		this.name = name;
		this.namespaceId = namespaceId;
		this.id = id;
	}

	public ItemFoodFactory<T> withHealAmount(int amount) {
		this.healAmount = amount;
		return this;
	}

	public ItemFoodFactory<T> withTicksPerHeal(int ticks) {
		this.ticksPerHeal = ticks;
		return this;
	}

	public ItemFoodFactory<T> withMaxStackSize(int size) {
		this.maxStackSize = size;
		return this;
	}

	public ItemFoodFactory<T> setFavouriteDog() {
		this.favouriteDog = true;
		return this;
	}

	public T build() {
		try {
			if (foodClass == ItemFood.class) {
				itemFood = (T) new ItemFood(name, namespaceId, id, healAmount, ticksPerHeal, favouriteDog, maxStackSize);
			} else {
				itemFood = foodClass
					.getConstructor(String.class, String.class, int.class, int.class, int.class, boolean.class, int.class)
					.newInstance(name, namespaceId, id, healAmount, ticksPerHeal, favouriteDog, maxStackSize);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error al crear el ItemFood: " + foodClass.getSimpleName(), e);
		}
		return itemFood;
	}

	public T get() {
		return itemFood;
	}
}
