package deus.btd.interfaces;

import deus.btd.toml.types.fields.Lang;

public interface IHasLang extends IHasNamespace{
	Lang getLang();
}
