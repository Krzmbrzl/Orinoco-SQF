package arma.orinocosqf.parsing.postfix;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 3/24/20
 */
public interface ProcessContextInspiration {
	@NotNull
	static ArrayInspiration array() {
		return ArrayInspiration.instance;
	}

	@NotNull
	static CodeInspiration code() {
		return CodeInspiration.instance;
	}

	@NotNull
	static RootInspiration root() {
		return RootInspiration.instance;
	}

	boolean inspirationEquals(@NotNull ProcessContextInspiration other);

	class RootInspiration implements ProcessContextInspiration {
		public static final RootInspiration instance = new RootInspiration();

		private RootInspiration() {
		}

		@Override
		public boolean inspirationEquals(@NotNull ProcessContextInspiration other) {
			return other == this;
		}
	}

	class ArrayInspiration implements ProcessContextInspiration {

		public static final ArrayInspiration instance = new ArrayInspiration();

		private ArrayInspiration() {
		}

		@Override
		public boolean inspirationEquals(@NotNull ProcessContextInspiration other) {
			return other == this;
		}
	}

	class CodeInspiration implements ProcessContextInspiration {

		public static final CodeInspiration instance = new CodeInspiration();

		private CodeInspiration() {
		}

		@Override
		public boolean inspirationEquals(@NotNull ProcessContextInspiration other) {
			return other == this;
		}
	}
}


