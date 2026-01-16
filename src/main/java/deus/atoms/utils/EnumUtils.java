package deus.atoms.utils;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EnumUtils {

	public static final Map<String, Material> MATERIALS = new HashMap<>();
	public static final Map<String, ToolMaterial> TOOL_MATERIALS = new HashMap<>();
	public static final Map<String, BlockSound> BLOCK_SOUNDS = new HashMap<>();
	public static final Map<String, Tag<Block<?>>> BLOCK_TAGS = new HashMap<>();


	static {
		for (Field f : Material.class.getFields()) {
			if (Material.class.isAssignableFrom(f.getType())) {
				try {
					MATERIALS.put(f.getName().toUpperCase(), (Material) f.get(null));
				} catch (IllegalAccessException ignored) {}
			}
		}

		for (Field f : BlockSounds.class.getFields()) {
			if (BlockSound.class.isAssignableFrom(f.getType())) {
				try {
					BLOCK_SOUNDS.put(f.getName().toUpperCase(), (BlockSound) f.get(null));
				} catch (IllegalAccessException ignored) {}
			}
		}

		for (Field f : BlockTags.class.getFields()) {
			if (Tag.class.isAssignableFrom(f.getType())) {
				try {
					@SuppressWarnings("unchecked")
					Tag<Block<?>> tag = (Tag<Block<?>>) f.get(null);
					BLOCK_TAGS.put(f.getName().toUpperCase(), tag);
				} catch (IllegalAccessException ignored) {}
			}
		}

		for (Field f : ToolMaterial.class.getFields()) {
			if (ToolMaterial.class.isAssignableFrom(f.getType())) {
				try {
					TOOL_MATERIALS.put(f.getName().toUpperCase(), (ToolMaterial) f.get(null));
				} catch (IllegalAccessException ignored) {}
			}
		}

	}

}
