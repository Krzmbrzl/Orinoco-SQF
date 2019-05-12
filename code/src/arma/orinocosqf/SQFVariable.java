package arma.orinocosqf;

import org.jetbrains.annotations.NotNull;

/**
 * @author K
 * @since 5/12/19
 */
public class SQFVariable implements CaseInsentiveKey {
	private final String name;
	private final boolean isLocal;
	private int id = -1;

	public SQFVariable(@NotNull String name) {
		this.name = name;
		isLocal = name.charAt(0) == '_';
	}

	public final boolean isLocal() {
		return isLocal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
