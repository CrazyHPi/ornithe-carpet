package carpet.utils.algebraic;

import com.google.common.collect.AbstractIterator;

import java.lang.reflect.Field;
import java.util.*;


public class StructuredAdtMatcher<T> extends AbstractAdtMatcher<T> {
	private final Class<T> type;
	private final int branches;
	private final Class<?>[] constructors;
	private final Object[] constructions;
	private final Field[][] componentFields;
	private final String[][] fieldPrefixes;
	private final Map<Class<?>, AdtMatcher<?>>[] componentMatchers;
	private final int[] stringProgresses;
	private final int[] fieldProgresses;

	private int structuralFailures;
	private int firstNonFailure;
	private int firstSuccess;
	private Object successfulConstruction;

	public StructuredAdtMatcher(Class<T> type) {
		this.type = type;
		this.constructors = this.getConstructors();
		this.branches = constructors.length;
		this.componentFields = new Field[branches][];
		this.fieldPrefixes = new String[branches][];
		this.initComponentFields();
		this.constructions = new Object[branches];
		this.stringProgresses = new int[branches];
		this.fieldProgresses = new int[branches];
		this.componentMatchers = new Map[branches];
		for (int i = 0; i < branches; i ++) componentMatchers[i] = new HashMap<>();
		this.clear();
	}

	@Override
	public StructuredAdtMatcher<T> clear() {
		Arrays.fill(stringProgresses, 0);
		Arrays.fill(fieldProgresses, 0);
		for (int i = 0; i < branches; i ++) {
			try {
				constructions[i] = constructors[i].newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new AssertionError(e);
			}
		}
		data.clear();
		this.firstNonFailure = 0;
		this.firstSuccess = Integer.MAX_VALUE;
		this.structuralFailures = 0;
		this.successfulConstruction = null;
		return this;
	}

	@Override
	public StructuredAdtMatcher<T> append(Iterable<String> strings) {
		super.append(strings);
		for (int i = 0; i < branches; i ++) tryProgress(i);
		return this;
	}

	@Override
	public int maxMatchCount() {
		return -1;
	}

	@Override
	public int matchedCount() {
		if (structuralFailures == branches) return -1;
		if (successfulConstruction != null) {
			return stringProgresses[firstSuccess];
		}
		return 0;
	}

	@Override
	public T matchedValue() {
		return (T) successfulConstruction;
	}

	public int nonFailureCount() {
		if (structuralFailures == branches) return -1;
		return stringProgresses[firstNonFailure];
	}

	public T nonFailureValue() {
		if (structuralFailures == branches) return null;
		return (T) constructions[firstNonFailure];
	}

	private void tryProgress(int index) {
		final Object construction = this.constructions[index];
		final Field[] components = this.componentFields[index];
		final String[] prefixes = this.fieldPrefixes[index];
		final Map<Class<?>, AdtMatcher<?>> componentMatchers = this.componentMatchers[index];
		// Progress of -1 means a past structural failure
		boolean modified = false;
		int stringProgress = stringProgresses[index];
		int fieldProgress = fieldProgresses[index];
		while (stringProgress != -1) {
			// Successful construction, parsing finished
			if (fieldProgress == components.length) {
				if (index < firstSuccess) {
					firstSuccess = index;
					successfulConstruction = construction;
				}
				break;
			}
			// Parse another field
			Field field = components[fieldProgress];
			AdtMatcher<?> fieldMatcher = componentMatchers.computeIfAbsent(field.getType(), AdtMatcher::create);
			int maxMatchCount = fieldMatcher.maxMatchCount();
			int upperBound = maxMatchCount >= 0 ? Math.min(stringProgress + maxMatchCount, data.size()) : data.size();
			// Prepare sublist data based on the maximal possible matched items
			List<String> subData = data.subList(stringProgress, upperBound);
			int subDataSize = subData.size();
			Iterable<String> dataToMatch = null;
			String prefix = prefixes[fieldProgress];
			// Strip prefix off the sublist data
			int prefixLength = prefix.length();
			if (!"".equals(prefix)) {
				if (subData.stream().allMatch(s -> s.startsWith(prefix))) {
					dataToMatch = () -> new AbstractIterator<String>() {
						private int index = 0;
						@Override
						protected String computeNext() {
							if (index == subDataSize) return endOfData();
							String s = subData.get(index ++);
							return s.substring(prefixLength);
						}
					};
				}
			} else dataToMatch = subData;
			// Construct object based on another matcher
			int consumedStrings = 0;
			Object constructedObject = null;
			if (dataToMatch != null) {
				constructedObject = fieldMatcher.clear().append(dataToMatch).matchedValue();
				consumedStrings = fieldMatcher.matchedCount();

			}
			if (constructedObject != null) {
				// Successful sub-object matching
				try {
					field.setAccessible(true);
					field.set(construction, constructedObject);
				} catch (IllegalAccessException e) {
					throw new AssertionError(e);
				}
				fieldProgress ++;
				stringProgress += consumedStrings;
				modified = true;
				continue;
			} else if (consumedStrings == -1) {
				// Structural failure
				fieldProgress = -1;
				stringProgress = -1;
				modified = true;
			}
			break;
		}
		if (modified) {
			stringProgresses[index] = stringProgress;
			fieldProgresses[index] = fieldProgress;
			if (stringProgress == -1) {
				structuralFailures ++;
				if (index == firstNonFailure && structuralFailures != branches)
					while (stringProgresses[firstNonFailure] == -1) firstNonFailure ++;
			}
		}
	}

	private Class<?>[] getConstructors() {
		Algebraic algebraic = type.getAnnotation(Algebraic.class);
		if (algebraic == null)
			throw new IllegalArgumentException("Creating parser for a non-ADT type " + type.getName());
		Class<?>[] constructors = algebraic.value();
		for (Class<?> clazz : constructors) {
			try {
				clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("The nullary constructor " +
					clazz.getName() + " for ADT type " + type.getName() + " is missing");
			}
		}
		return constructors;
	}

	private void initComponentFields() {
		List<Field> currentComponents = new ArrayList<>();
		List<String> currentPrefixes = new ArrayList<>();
		for (int i = 0; i < branches; i ++) {
			Field[] declared = constructors[i].getDeclaredFields();
			for (Field field : declared) {
				MatchWith matchWith = field.getAnnotation(MatchWith.class);
				if (matchWith != null) {
					currentComponents.add(field);
					currentPrefixes.add(matchWith.prefix());
				}
			}
			componentFields[i] = currentComponents.toArray(new Field[0]);
			fieldPrefixes[i] = currentPrefixes.toArray(new String[0]);
			currentComponents.clear();
			currentPrefixes.clear();
		}
	}
}
