package deus.atoms.toml.types;

import deus.atoms.annotations.DeserializeToml;

import java.util.List;

@DeserializeToml
public class CompiledItemTool extends CompiledItem {

	public Tool tool;

	@DeserializeToml
	public static class Tool {
		public boolean isWeapon;
		public int damageDealt;
		public String toolMaterial;
		public String material;
		public int weaponDamage;
	}
}
