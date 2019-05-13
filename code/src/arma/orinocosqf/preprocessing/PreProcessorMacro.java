package arma.orinocosqf.preprocessing;

import org.jetbrains.annotations.NotNull;

import arma.orinocosqf.preprocessing.bodySegments.BodySegment;

import java.util.List;

/**
 * A {@link PreProcessorMacro} is a macro defined with a #define. It allows for params which are set at #define, i.e. #define MACRO(PARAM)
 * body. A no parameterized macro looks like the following: #define MACRO body. The macro name ({@link #getName()}) is the first word after
 * #define, i.e. #define MACRO results in MACRO being the name. The macro body is the text following the name and optional parameters. The
 * body is parsed into {@link BodySegment} instances. The root segment will be of type {@link BodySegmentSequence} if the body has multiple
 * segments.
 *
 * In order for the {@link BodySegment} instances to function properly, it needs access to the current set of defined macros. This is
 * provided via {@link MacroSet}.
 *
 * @author K
 * @see #parseBody(MacroSet, char[], int, int, List)
 * @see BodySegment
 * @since 3/17/19
 */
public class PreProcessorMacro {
	/**
	 * Name of the macro
	 */
	private final String name;
	/**
	 * Array of parameters as their names
	 */
	private final String[] params;
	private static final String[] EMPTY = {};
	/**
	 * The root body segment
	 */
	private final BodySegment body;

	/** The {@link MacroSet} instance which contains this macro */
	protected final MacroSet macroSet;

	public PreProcessorMacro(@NotNull MacroSet macroSet, @NotNull String name, @NotNull List<String> params, @NotNull BodySegment body) {
		this.macroSet = macroSet;
		this.name = name;
		int i = 0;
		if (params.isEmpty()) {
			this.params = EMPTY;
		} else {
			this.params = new String[params.size()];
			for (String s : params) {
				this.params[i++] = s;
			}
		}
		this.body = body;
		
		body.setOwner(this);
	}

	/**
	 * @return the root {@link BodySegment} instance for this macro
	 */
	@NotNull
	public BodySegment getBody() {
		return body;
	}

	/**
	 * @return the name of the macro
	 */
	@NotNull
	public String getName() {
		return name;
	}

	/** @return an array of the params, or empty array if there are none */
	@NotNull
	public String[] getParams() {
		return params;
	}

	/**
	 * @return Whether this macro needs any arguments to be expanded
	 */
	public boolean takesArguments() {
		return params.length > 0;
	}

	/**
	 * @return The {@link MacroSet} this maco is part of
	 */
	public MacroSet getMacroSet() {
		return macroSet;
	}



	// /**
	// * Similar to a {@link WordSegment}, except this segment allows for parameters.
	// * <pre>
	// * #define MACRO word(param1) //word(param1) is {@link ParameterizedWordSegment}
	// * #define MAC2 wordd(p,2) //word(p,2) is {@link ParameterizedWordSegment}
	// * #define MAC3 wordd() //not valid {@link ParameterizedWordSegment}
	// * #define MAC4(PARAM) word //no {@link ParameterizedWordSegment} present
	// * #define MAC5 MAC4(hello) //MAC4(hello) is {@link ParameterizedWordSegment}, and is mapped to existing macro
	// * </pre>
	// */
	// public static class ParameterizedWordSegment extends BodySegment {
	//
	// private final String word;
	// private final List<BodySegment> args;
	//
	// /**
	// * @param macro the macro which owns this segment
	// * @param word the word that comes before the parameters (#define MACRO word(arg1))
	// * @param args the arguments that come after the word (#define MACRO word(arg1,arg2))
	// */
	// public ParameterizedWordSegment(@NotNull PreProcessorMacro macro, @NotNull String word, @NotNull List<BodySegment> args) {
	// super(macro);
	// this.word = word;
	// this.args = args;
	// }
	//
	// @NotNull
	// @Override
	// public CharSequence applyArguments(@NotNull List<CharSequence> args) {
	// PreProcessorMacro macro = ownerMacro.macroSet.get(word);
	// if (macro != null) {
	// ArrayList<CharSequence> newArgs = new ArrayList<>(args.size());
	//
	// /*
	// * This loop is ensuring that parameters with identical names from the #define and the define body
	// * are matched and the correct argument is applied to the embodied macro.
	// *
	// * Example:
	// *
	// * #define ASSIGN(KEY,VAL) KEY=VAL
	// * #define SET_TO_ONE(NAME) ASSIGN(NAME, 1) //Embodied ASSIGN
	// *
	// * SET_TO_ONE(Key); //yields Key=1;
	// *
	// * In SET_TO_ONE, this loop is applied. NAME in "SET_TO_ONE(NAME)" is matched with NAME in "ASSIGN(NAME, 1)".
	// * The argument "Key" is passed to ASSIGN's first parameter "NAME". Then, since "1" isn't matched with a parameter in
	// * SET_TO_ONE, 1 is is just added to the new argument list to be passed through the embodied ASSIGN.
	// */
	// for (BodySegment bs : this.args) {
	// if (bs instanceof MacroArgumentSegment) {
	// MacroArgumentSegment mas = (MacroArgumentSegment) bs;
	// int i = 0;
	// for (String p : this.ownerMacro.getParams()) {
	// if (p.equals(mas.getArgumentName())) {
	// newArgs.add(args.get(i));
	// }
	// i++;
	// }
	// } else {
	// newArgs.add(bs.applyArguments(Collections.emptyList()));
	// }
	// }
	//
	// return macro.getBody().applyArguments(newArgs);
	// }
	//
	// return toStringNoPreProcessing();
	// }
	//
	// @Override
	// @NotNull
	// public CharSequence toStringNoPreProcessing() {
	// StringBuilder sb = new StringBuilder();
	// sb.append(word);
	// sb.append('(');
	// for (int i = 0; i < args.size(); i++) {
	// sb.append(args.get(i).toStringNoPreProcessing());
	// if (i != args.size() - 1) {
	// sb.append(',');
	// }
	// }
	// sb.append(')');
	// return sb;
	// }
	//
	// @Override
	// public String toString() {
	// return "WordSegment{" + word + '}';
	// }
	// }



}
