package deus.btd.pipeline.io;

public class AtomTomlUtils {

	public static String formatAtomKey(String author, String atomName) {
		return (author + "_" + atomName).trim().replace(" ", "_");
	}
}
