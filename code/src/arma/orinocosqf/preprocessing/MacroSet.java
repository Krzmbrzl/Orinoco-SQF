package arma.orinocosqf.preprocessing;

import arma.orinocosqf.HashableCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author K
 * @since 3/20/19
 */
public class MacroSet implements Map<String, PreProcessorMacro> {

	private static final long serialVersionUID = 6102312990139496236L;

	private final HashMap<HashableCharSequence, PreProcessorMacro> map = new HashMap<>();

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(HashableCharSequence cs) {
		return map.containsKey(cs);
	}

	@Override
	public boolean containsKey(Object o) {
		return map.containsKey(HashableCharSequence.fromString(o.toString()));
	}

	@Override
	public boolean containsValue(Object o) {
		return map.containsValue(o);
	}

	public PreProcessorMacro get(HashableCharSequence cs) {
		return map.get(cs);
	}

	@Override
	public PreProcessorMacro get(Object o) {
		return map.get(HashableCharSequence.fromString(o.toString()));
	}

	public PreProcessorMacro put(HashableCharSequence cs, PreProcessorMacro value) {
		return map.put(cs.deepCopy(), value);
	}

	@Override
	public PreProcessorMacro put(String key, PreProcessorMacro value) {
		return map.put(HashableCharSequence.fromString(key), value);
	}

	@Override
	public PreProcessorMacro remove(Object o) {
		return map.remove(o);
	}

	@Override
	public void putAll(@NotNull Map<? extends String, ? extends PreProcessorMacro> map) {
		map.forEach((o, o2) -> {
			this.map.put(HashableCharSequence.fromString(o), o2);
		});
	}

	@Override
	public void clear() {
		map.clear();
	}

	@NotNull
	@Override
	public Set<String> keySet() {
		return map.keySet().stream().map(HashableCharSequence::asString).collect(Collectors.toSet());
	}

	@NotNull
	@Override
	public Collection<PreProcessorMacro> values() {
		return map.values();
	}

	@NotNull
	@Override
	public Set<Entry<String, PreProcessorMacro>> entrySet() {
		return map.entrySet().stream().map(e -> Map.entry(e.getKey().asString(), e.getValue())).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
