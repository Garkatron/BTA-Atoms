package deus.btd.blocks;

import com.mojang.nbt.tags.CompoundTag;
import deus.btd.pipeline.compile.types.CompiledBlock;
import deus.btd.pipeline.registry.AtomBlockRegistry;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.NotNull;

public class DynTileEntity extends TileEntity {
	protected NamespaceID namespace = null;

	public DynTileEntity() {
	}

	public CompiledBlock getCompiled() {
		return AtomBlockRegistry.get(namespace);
	}


	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);

		if (tag.containsKey("dynblocknamespace")) {
			try {
				this.namespace = NamespaceID.getPermanent(
					tag.getString("dynblocknamespace")
				);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		super.writeToNBT(tag);

		if (namespace != null) {
			tag.putString("dynblocknamespace", namespace.toString());
		}
	}


	public void setNamespace(NamespaceID id) {
		this.namespace = id;
	}

	public NamespaceID getNamespace() {
		return namespace;
	}

	private @NotNull NamespaceID setupNamespaceId(String namespaceId) {
		if (namespaceId == null) {
			throw new NullPointerException("NamespaceId must not be null!");
		} else {
			try {
				return NamespaceID.getPermanent(namespaceId);
			} catch (HardIllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
