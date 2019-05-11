package arma.orinocosqf.preprocessing;

import org.jetbrains.annotations.NotNull;

/**
 * Here is the format for a preprocessor command: <b>#commandName body?</b>. The
 * ? means optional.
 *
 * @author K
 * @since 02/20/2019
 */
public enum PreProcessorCommand {
	/** #ifdef MACRO */
	IfDef("ifdef"),
	/** #ifndef MACRO */
	IfNDef("ifndef"),
	/** #else */
	Else("else"),
	/** #endif */
	EndIf("endif"),
	/**
	 * <ul>
	 * <li>#define MACRO body</li>
	 * <li>#define MACRO(PARAM1) PARAM1</li>
	 * <li>#define MACRO(PARAM1,PARAM2) PARAM1 PARAM2</li>
	 * </ul>
	 */
	Define("define"),
	/** #undef MACRO */
	Undef("undef"),
	/**
	 * <ul>
	 * <li>#include "file"</li>
	 * <li>#include &lt;file&gt;</li>
	 * </ul>
	 */
	Include("include");

	private String s;

	PreProcessorCommand(@NotNull String s) {
		this.s = s;
	}

	@NotNull
	public String commandName() {
		return s;
	}
}
