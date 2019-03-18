package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author K
 * @since 3/17/19
 */
public class PreProcessorMacro {
	private final String name;
	private final String[] params;
	private final BodySegment body;

	public PreProcessorMacro(@NotNull MacroSet macroSet, @NotNull String name, @NotNull List<String> params, @NotNull String body) {
		this.name = name;
		this.params = new String[params.size()];
		int i = 0;
		for (String s : params) {
			this.params[i++] = s;
		}
		this.body = parseBody(macroSet, body, params);
	}

	@NotNull
	public BodySegment getBody() {
		return body;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@NotNull
	public String[] getParams() {
		return params;
	}

	@NotNull
	public static BodySegment parseBody(@NotNull MacroSet currentMacros, @NotNull String body, @NotNull List<String> params) {
		return null; // todo
	}

	//todo move to outer class
	public abstract static class MacroSet implements Map<String, PreProcessorMacro> {

	}

	public static abstract class BodySegment {
		protected final PreProcessorMacro ownerMacro;

		public BodySegment(@NotNull PreProcessorMacro macro) {
			this.ownerMacro = macro;
		}

		@NotNull
		public abstract CharSequence applyArguments(@NotNull List<CharSequence> args);

		@NotNull
		public abstract CharSequence toStringNoPreProcessing();
	}

	public static class BodySegmentSequence extends BodySegment {

		private final List<BodySegment> segments;

		public BodySegmentSequence(@NotNull PreProcessorMacro macro, @NotNull List<BodySegment> segments) {
			super(macro);
			this.segments = segments;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			StringBuilder sb = new StringBuilder();
			for (BodySegment bs : segments) {
				sb.append(bs.applyArguments(args));
			}
			return sb;
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			StringBuilder sb = new StringBuilder();
			for (BodySegment bs : segments) {
				sb.append(bs.toStringNoPreProcessing());
			}
			return sb;
		}

		@Override
		public String toString() {
			return "BodySegmentSequence{" + segments + '}';
		}
	}

	public static class TextSegment extends BodySegment {

		private final String text;

		public TextSegment(@NotNull PreProcessorMacro macro, @NotNull String text) {
			super(macro);
			this.text = text;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			return text;
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			return text;
		}

		@Override
		public String toString() {
			return "TextSegment{" + text + '}';
		}
	}

	public static class MacroArgumentSegment extends BodySegment {

		private final String argumentName;
		private final BodySegment replacement;

		public MacroArgumentSegment(@NotNull PreProcessorMacro macro, @NotNull String argumentName, @NotNull BodySegment replacement) {
			super(macro);
			this.argumentName = argumentName;
			this.replacement = replacement;
		}

		@NotNull
		public String getArgumentName() {
			return argumentName;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			return replacement.applyArguments(args);
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			return argumentName;
		}

		@Override
		public String toString() {
			return "MacroArgumentSegment{" + argumentName + ", repl=" + replacement + '}';
		}
	}

	public static class WordSegment extends BodySegment {

		private final MacroSet macroSet;
		private final String word;

		public WordSegment(@NotNull PreProcessorMacro macro, @NotNull MacroSet macroSet, @NotNull String word) {
			super(macro);
			this.macroSet = macroSet;
			this.word = word;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			PreProcessorMacro macro = macroSet.get(word);
			if (macro != null) {
				return macro.getBody().applyArguments(args);
			}
			return word;
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			return word;
		}

		@Override
		public String toString() {
			return "WordSegment{" + word + '}';
		}
	}

	public static class ParameterizedWordSegment extends BodySegment {

		private final MacroSet macroSet;
		private final String word;
		private final List<BodySegment> args;

		public ParameterizedWordSegment(@NotNull PreProcessorMacro macro, @NotNull MacroSet macroSet, @NotNull String word, @NotNull List<BodySegment> args) {
			super(macro);
			this.macroSet = macroSet;
			this.word = word;
			this.args = args;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			PreProcessorMacro macro = macroSet.get(word);
			if (macro != null) {
				ArrayList<CharSequence> newArgs = new ArrayList<>(args.size());

				/*
				 * This loop is ensuring that parameters with identical names from the #define and the define body
				 * are matched and the correct argument is applied to the embodied macro.
				 *
				 * Example:
				 *
				 * #define ASSIGN(KEY,VAL) KEY=VAL
				 * #define SET_TO_ONE(NAME) ASSIGN(NAME, 1) //Embodied ASSIGN
				 *
				 * SET_TO_ONE(Key); //yields Key=1;
				 *
				 * In SET_TO_ONE, this loop is applied. NAME in "SET_TO_ONE(NAME)" is matched with NAME in "ASSIGN(NAME, 1)".
				 * The argument "Key" is passed to ASSIGN's first parameter "NAME". Then, since "1" isn't matched with a parameter in SET_TO_ONE,
				 * 1 is is just added to the new argument list to be passed through the embodied ASSIGN.
				 */
				for (BodySegment bs : this.args) {
					if (bs instanceof MacroArgumentSegment) {
						MacroArgumentSegment mas = (MacroArgumentSegment) bs;
						int i = 0;
						for (String p : this.ownerMacro.getParams()) {
							if (p.equals(mas.getArgumentName())) {
								newArgs.add(args.get(i));
							}
							i++;
						}
					} else {
						newArgs.add(bs.applyArguments(Collections.emptyList()));
					}
				}

				return macro.getBody().applyArguments(newArgs);
			}

			return toStringNoPreProcessing();
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			StringBuilder sb = new StringBuilder();
			sb.append(word);
			sb.append('(');
			for (int i = 0; i < args.size(); i++) {
				sb.append(args.get(i).toStringNoPreProcessing());
				if (i != args.size() - 1) {
					sb.append(',');
				}
			}
			sb.append(')');
			return sb;
		}

		@Override
		public String toString() {
			return "WordSegment{" + word + '}';
		}
	}

	public static class StringifySegment extends BodySegment {

		private final BodySegment segment;

		public StringifySegment(@NotNull PreProcessorMacro macro, @NotNull BodySegment segment) {
			super(macro);
			this.segment = segment;
		}

		@NotNull
		@Override
		public CharSequence applyArguments(@NotNull List<CharSequence> args) {
			return "\"" + segment.applyArguments(args) + "\"";
		}

		@Override
		@NotNull
		public CharSequence toStringNoPreProcessing() {
			return segment.toStringNoPreProcessing();
		}

		@Override
		public String toString() {
			return "StringifySegment{" + segment + '}';
		}
	}
}
