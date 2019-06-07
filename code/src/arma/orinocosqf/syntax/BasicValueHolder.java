package arma.orinocosqf.syntax;

import arma.orinocosqf.type.PolymorphicWrapperValueType;
import arma.orinocosqf.type.ValueType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kayler
 * @since 06/11/2016
 */
public class BasicValueHolder implements ValueHolder {

	private boolean optional;

	private final ValueType type;
	private final String description;
	private final List<String> literals;

	public BasicValueHolder(@NotNull ValueType type, @NotNull String description, boolean optional) {
		this(type, description, optional, new ArrayList<>());
	}

	public BasicValueHolder(@NotNull ValueType type, @NotNull String description, boolean optional, @NotNull List<String> literals) {
		this.type = new PolymorphicWrapperValueType(type);
		this.description = description.trim();
		this.optional = optional;
		this.literals = literals;
	}

	@Override
	@NotNull
	public ValueType getType() {
		return type;
	}

	@Override
	@NotNull
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@NotNull
	public List<String> getLiterals() {
		return literals;
	}

	@Override
	public void memCompact() {
		if (literals instanceof ArrayList) {
			((ArrayList<String>) literals).trimToSize();
		}
		type.memCompact();
	}
}
