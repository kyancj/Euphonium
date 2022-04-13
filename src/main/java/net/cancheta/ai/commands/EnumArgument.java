package net.cancheta.ai.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
//import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

//import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> implements ArgumentType<T> {

    private final EnumSet<T> constants;
    private final Function<T, String> getName;

    public static  <T extends Enum<T>> EnumArgument<T> of(Class<T> allOf) {
        return of(EnumSet.allOf(allOf));
    }
    public static  <T extends Enum<T>> EnumArgument<T> of(EnumSet<T> set) {
        return of(set, Enum::name);
    }
    public static  <T extends Enum<T>> EnumArgument<T> of(EnumSet<T> set, Function<T, String> getName) {
        return new EnumArgument<>(set, getName);
    }

    @SafeVarargs
    public static <T extends Enum<T>> EnumArgument<T> of(T first, T... remaining) {
        return of(EnumSet.of(first, remaining));
    }

    private EnumArgument(EnumSet<T> allOf, Function<T, String> getName) {
        this.getName = getName;
        if (allOf.size() == 0) {
            throw new IllegalArgumentException("At least one enum constant needs to be provided");
        }
        this.constants = EnumSet.copyOf(allOf);
    }


    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        String value = reader.readUnquotedString();
        Optional<T> result = constants
                .stream()
                .filter(it -> getName.apply(it).equalsIgnoreCase(value))
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new DynamicCommandExceptionType((value2) -> new LiteralText("Value not allowed for parameter: " + value)).create(value);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
    	return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return constantNames()
                .limit(5)
                .collect(Collectors.toList());
    }

    private Stream<String> constantNames() {
        return constants
                .stream()
                .map(it -> getName.apply(it).toLowerCase(Locale.US));
    }

    @Override
    public String toString() {
        return "EnumArgument{" +
                "constants=" + constants +
                '}';
    }
}