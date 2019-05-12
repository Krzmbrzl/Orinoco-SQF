package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 5/12/19
 */
public class SQFVariable implements CaseInsentiveKey {
	private final String name;
	private final boolean isLocal;
	private final int id;

	public SQFVariable(@NotNull String name, int id) {
		this.name = name;
		isLocal = name.charAt(0) == '_';
		this.id = id;
	}

	public final boolean isLocal() {
		return isLocal;
	}

	public int getId() {
		return id;
	}

	@NotNull
	public String getName() {
		return name;
	}

	@Override
	@NotNull
	public CharSequence getKey() {
		return name;
	}
}
