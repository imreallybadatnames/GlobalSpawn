package de.dafuqs.globalspawn.command;

import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import de.dafuqs.globalspawn.*;
import net.minecraft.server.command.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

import java.util.*;
import java.util.function.*;

public class SpawnCriterionArgumentType implements ArgumentType<GlobalSpawnPoint.SpawnCriterion> {
	private static final Collection<String> EXAMPLES = Arrays.stream(GlobalSpawnPoint.SpawnCriterion.values()).map(new Function<GlobalSpawnPoint.SpawnCriterion, String>() {
		@Override
		public String apply(GlobalSpawnPoint.SpawnCriterion criterion) {
			return criterion.toString().toLowerCase(Locale.ROOT);
		}
	}).toList();
	public static final SimpleCommandExceptionType INVALID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.spawn_criterion.invalid"));

	public SpawnCriterionArgumentType() {
	}

	public static SpawnCriterionArgumentType criterion() {
		return new SpawnCriterionArgumentType();
	}

	public static GlobalSpawnPoint.SpawnCriterion getCriterion(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, GlobalSpawnPoint.SpawnCriterion.class);
	}

	public GlobalSpawnPoint.SpawnCriterion parse(StringReader reader) throws CommandSyntaxException {
		String string = reader.readUnquotedString();

		try {
			return GlobalSpawnPoint.SpawnCriterion.valueOf(string.toUpperCase(Locale.ROOT));
		} catch (InvalidIdentifierException var4) {
			throw INVALID_EXCEPTION.createWithContext(reader);
		}
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}

}
