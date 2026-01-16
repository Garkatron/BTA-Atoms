package deus.atoms.annotations;

import deus.atoms.toml.types.CompiledAtom;

public interface IHasLang {
	CompiledAtom.Lang getLang();
}
