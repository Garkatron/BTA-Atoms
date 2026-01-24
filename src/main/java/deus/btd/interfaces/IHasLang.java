package deus.btd.interfaces;

import deus.btd.pipeline.compile.types.fields.Lang;

public interface IHasLang extends IHasNamespace{
	Lang getLang();
}
