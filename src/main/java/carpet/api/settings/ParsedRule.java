package carpet.api.settings;

import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import carpet.utils.Translations;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParsedRule<T> implements CarpetRule<T>, Comparable<ParsedRule<?>> {

    private final Field field;
    private final String name;
    private final String desc;
    private final List<String> extraInfo;
    private final List<String> categories;
    private final List<String> options;
    private boolean isStrict;
    private boolean isClient;
    private final Class<T> type;
    private final T defaultValue;
    private final SettingsManager settingsManager;
    private final List<Validator<T>> validators;
    private final FromStringConverter<T> converter;
//	private final String scarpetApp;

    private static final Map<Class<?>, FromStringConverter<?>> CONVERTER_MAP = initConverterMap();

    private static Map<Class<?>, FromStringConverter<?>> initConverterMap() {
        Map<Class<?>, FromStringConverter<?>> converterMap = new HashMap<>();
        converterMap.put(String.class, str -> str);
        converterMap.put(Boolean.class, str -> {
            if (str.equalsIgnoreCase("true")) return true;
            if (str.equalsIgnoreCase("false")) return false;
            throw new InvalidRuleValueException("Invalid boolean value");
        });
        Map.Entry<Class<Integer>, FromStringConverter<Integer>> intConverter = numericalConverter(Integer.class, Integer::parseInt);
        converterMap.put(intConverter.getKey(), intConverter.getValue());
        Map.Entry<Class<Double>, FromStringConverter<Double>> doubleConverter = numericalConverter(Double.class, Double::parseDouble);
        converterMap.put(doubleConverter.getKey(), doubleConverter.getValue());
        Map.Entry<Class<Long>, FromStringConverter<Long>> longConverter = numericalConverter(Long.class, Long::parseLong);
        converterMap.put(longConverter.getKey(), longConverter.getValue());
        Map.Entry<Class<Float>, FromStringConverter<Float>> floatConverter = numericalConverter(Float.class, Float::parseFloat);
        converterMap.put(floatConverter.getKey(), floatConverter.getValue());

        return converterMap;
    }

    private static <T> Map.Entry<Class<T>, FromStringConverter<T>> numericalConverter(Class<T> outputClass, Function<String, T> converter) {
        return new AbstractMap.SimpleEntry<>(outputClass, str -> {
            try {
                return converter.apply(str);
            } catch (NumberFormatException e) {
                throw new InvalidRuleValueException("Invalid number for rule");
            }
        });
    }

    interface FromStringConverter<T> {

        T convert(String value) throws InvalidRuleValueException;
    }

    public ParsedRule(Field field, Rule rule, SettingsManager settingsManager) {
        this.field = field;
        this.name = field.getName();
        this.desc = rule.desc();
        this.settingsManager = settingsManager;
        String extraPrefix = String.format(TranslationKeys.RULE_EXTRA_PREFIX_PATTERN, settingsManager().identifier(), name());
        this.extraInfo = getTranslationArray(extraPrefix);
        this.categories = Arrays.asList(rule.categories());
        this.isStrict = rule.strict();
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) ClassUtils.primitiveToWrapper(field.getType());
        this.type = type;
        this.defaultValue = value();
        this.validators = Stream.of(rule.validators()).map(this::instantiateValidator).collect(Collectors.toList());
        FromStringConverter<T> converter0 = null;
        if (rule.options().length > 0) {
            this.options = Arrays.asList(rule.options());
        } else if (this.type == Boolean.class) {
            this.isStrict = true;
            this.options = Arrays.asList("true", "false");
        } else if (this.type == String.class && categories.contains(RuleCategory.COMMAND)) {
            this.options = Validators.CommandLevel.OPTIONS;
        } else if (this.type.isEnum()) {
            this.isStrict = true;
            this.options = Arrays.stream(this.type.getEnumConstants()).map(e -> ((Enum<?>) e).name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
            converter0 = str -> {
                try {
                    @SuppressWarnings({"unchecked", "rawtypes"}) // Raw necessary because of signature. Unchecked because compiler doesn't know T extends Enum
                    T ret = (T) Enum.valueOf((Class<? extends Enum>) type, str.toUpperCase(Locale.ROOT));
                    return ret;
                } catch (IllegalArgumentException e) {
                    throw new InvalidRuleValueException("Valid values for this rule are: " + this.options);
                }
            };
        } else {
            this.options = Collections.emptyList();
        }
        if (this.isStrict)
            this.validators.add(0, new Validators.StrictValidator<>());
        if (converter0 == null) {
            @SuppressWarnings("unchecked")
            FromStringConverter<T> converterFromMap = (FromStringConverter<T>) CONVERTER_MAP.get(type);
            if (converterFromMap == null)
                throw new UnsupportedOperationException("Unsupported type for ParsedRule" + type);
            converter0 = converterFromMap;
        }
        this.converter = converter0;

    }

    @SuppressWarnings({"unchecked", "rawtypes"}) // Needed because of the annotation
    private carpet.api.settings.Validator<T> instantiateValidator(Class<? extends carpet.api.settings.Validator> cls) {
        try {
            Constructor<? extends Validator> constr = cls.getDeclaredConstructor();
            constr.setAccessible(true);
            return constr.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public List<Text> extraInfo() {
        return getTranslationArray(String.format(TranslationKeys.RULE_EXTRA_PREFIX_PATTERN, settingsManager().identifier(), name()))
                .stream()
                .map(str -> Messenger.c("g " + str))
                .collect(Collectors.toList());
    }

    private List<String> getTranslationArray(String prefix) {
        List<String> ret = new ArrayList<>();
        for (int i = 0; Translations.hasTranslation(prefix + i); i++) {
            ret.add(Translations.tr(prefix + i));
        }
        return ret;
    }

    @Override
    public Collection<String> categories() {
        return categories;
    }

    @Override
    public Collection<String> suggestions() {
        return options;
    }

    @Override
    public SettingsManager settingsManager() {
        return settingsManager;
    }

    public List<Validator<T>> validators() {
        return validators;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T value() {
        try {
            return (T) field.get(null);
        } catch (IllegalAccessException e) {
            // Can't happen at regular runtime because we'd have thrown it on construction
            throw new IllegalArgumentException("Couldn't access field for rule: " + name, e);
        }
    }

    @Override
    public boolean canBeToggledClientSide() {
        return isClient;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T defaultValue() {
        return defaultValue;
    }

    @Override
    public boolean strict() {
        return isStrict;
    }

    @Override
    public void set(CommandSource source, String value) throws InvalidRuleValueException {
        set(source, converter.convert(value), value);
    }

    @Override
    public void set(CommandSource source, T value) throws InvalidRuleValueException {
        set(source, value, RuleHelper.toRuleString(value));
    }

    private void set(CommandSource source, T value, String userInput) throws InvalidRuleValueException {
        for (Validator<T> validator : this.validators) {
            value = validator.validate(source, this, value, userInput); // should this recalculate the string? Another validator may have changed value
            if (value == null) {
                if (source != null) validator.notifyFailure(source, this, userInput);
                throw new InvalidRuleValueException();
            }
        }
        if (!value.equals(value()) || source == null) {
            try {
                this.field.set(null, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Couldn't access field for rule: " + name, e);
            }
            if (source != null) settingsManager().notifyRuleChanged(source, this, userInput);
        }
    }

    @Override
    public int compareTo(@NotNull ParsedRule<?> o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ParsedRule && ((ParsedRule<?>) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name + ": " + RuleHelper.toRuleString(value());
    }
}
