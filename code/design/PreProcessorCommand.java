/**
 * Here is the format for a preprocessor command: <b>#commandName body?</b>. The ? means optional.
 * @author K
 * @since 02/20/2019
 */
public enum PreProcessorCommand {
	/** #ifdef MACRO */
	IfDef,
	/** #ifndef MACRO */
	IfNDef,
	/** #else */
	Else,
	/** #endif */
	EndIf,
	/**
	 * <ul>
	 * <li>#define MACRO body </li>
	 * <li>#define MACRO(PARAM1) PARAM1 </li>
	 * <li>#define MACRO(PARAM1,PARAM2) PARAM1 PARAM2 </li>
	 * </ul>
	 */
	Define,
	/** #undef MACRO */
	Undef,
	/** <ul>
	 * <li>#include "file"</li>
	 * <li>#include &lt;file&gt;</li>
	 * </ul> */
	Include
}
