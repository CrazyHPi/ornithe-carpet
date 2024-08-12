package carpet.commands;

import carpet.CarpetSettings;
import carpet.api.settings.SettingsManager;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.RuleHelper;
import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import carpet.utils.TranslationKeys;
import com.google.common.collect.Iterables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static carpet.utils.Translations.tr;

//#if MC<=11202
public class CarpetCommand_old extends AbstractCommand {
	private final SettingsManager manager;

	public CarpetCommand_old(SettingsManager manager) {
		this.manager = manager;
	}

	public SettingsManager getManager() {
		return manager;
	}

	@Override
	public String getName() {
		return this.manager.identifier();
	}

	@Override
	public String getUsage(CommandSource commandSource) {
		return this.manager.identifier() + " <rule> <value>";
	}

	@Override
	//#if MC>10809
	public void run(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings) throws CommandException {
		//#else
		//$$ public void run(CommandSource commandSource, String[] strings) throws CommandException {
		//#endif
		if (strings.length == 0) {
			this.manager.listAllSettings(commandSource);
		}
		if (strings.length == 1) {
			if ("list".equalsIgnoreCase(strings[0])) {
				this.manager.listSettings(commandSource, String.format(tr(TranslationKeys.ALL_MOD_SETTINGS), this.manager.getFancyName()), this.manager.getRulesSorted());
			} else if (strings[0].equalsIgnoreCase("setDefault") || strings[0].equalsIgnoreCase("removeDefault")) {
				return;
			} else {
				CarpetRule<?> rule = this.manager.contextRule(strings[0]);
				if (rule != null) {
					this.manager.displayRuleMenu(commandSource, rule);
				} else {
					Messenger.c("rb " + tr(TranslationKeys.UNKNOWN_RULE) + ": " + strings[0]);
				}
			}
		}
		if (strings.length == 2) {
			if ("list".equalsIgnoreCase(strings[0]) && Iterables.contains(this.manager.getCategories(), strings[1])) {
				this.manager.listSettings(commandSource, String.format(tr(TranslationKeys.MOD_SETTINGS_MATCHING),
						this.manager.getFancyName(), RuleHelper.translatedCategory(this.manager.identifier(), strings[1])),
					this.manager.getRulesMatching(strings[1]));
			} else if (strings[0].equalsIgnoreCase("setDefault")) {
				return;
			} else if (strings[0].equalsIgnoreCase("removeDefault")) {
				CarpetRule<?> rule = this.manager.contextRule(strings[1]);
				if (rule != null) {
					this.manager.removeDefault(commandSource, rule);
				} else {
					Messenger.c("rb " + tr(TranslationKeys.UNKNOWN_RULE) + ": " + strings[1]);
				}
			} else {
				CarpetRule<?> rule = this.manager.contextRule(strings[0]);
				if (rule != null) {
					this.manager.setRule(commandSource, rule, strings[1]);
				} else {
					Messenger.c("rb " + tr(TranslationKeys.UNKNOWN_RULE) + ": " + strings[0]);
				}
			}
		}
		if (strings.length == 3) {
			if (strings[0].equalsIgnoreCase("setDefault")) {
				CarpetRule<?> rule = this.manager.contextRule(strings[1]);
				if (rule != null) {
					this.manager.setDefault(commandSource, rule, strings[2]);
				} else {
					Messenger.c("rb " + tr(TranslationKeys.UNKNOWN_RULE) + ": " + strings[1]);
				}
			}
		}
	}

	@Override
	//#if MC>10809
	public boolean canUse(MinecraftServer minecraftServer, CommandSource commandSource) {
		//#else
		//$$ public boolean canUse(CommandSource commandSource) {
		//#endif
		return CommandHelper.canUseCommand(commandSource, CarpetSettings.carpetCommandPermissionLevel) && !this.manager.locked();
	}

	private List<String> smartSuggestion(List<String> stream, String key) {
		List<String> regularSuggestionList = new ArrayList<>();
		List<String> smartSuggestionList = new ArrayList<>();
		stream.forEach((listItem) -> {
			List<String> words = Arrays.stream(listItem.split("(?<!^)(?=[A-Z])")).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
			List<String> prefixes = new ArrayList<>(words.size());
			for (int i = 0; i < words.size(); i++)
				prefixes.add(String.join("", words.subList(i, words.size())));
			if (prefixes.stream().anyMatch(s -> s.startsWith(key))) {
				smartSuggestionList.add(listItem);
			}
			if (this.manager.matchesSubStr(key, listItem.toLowerCase(Locale.ROOT))) {
				regularSuggestionList.add(listItem);
			}
		});
		return regularSuggestionList.isEmpty() ? smartSuggestionList : regularSuggestionList;
	}

	@Override
	//#if MC>10809
	public List<String> getSuggestions(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
		//#elseif MC>10710
		//$$ public List<String> getSuggestions(CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
		//#else
		//$$ public List<String> getSuggestions(CommandSource commandSource, String[] strings) {
		//#endif
		if (this.manager.locked()) {
			return Collections.emptyList();
		}
		if (strings.length == 1) {
			List<String> stream = this.manager.getRulesSorted().stream().map(CarpetRule::name).collect(Collectors.toList());
			stream.add("list");
			stream.add("removeDefault");
			stream.add("setDefault");
			return this.smartSuggestion(stream, strings[0]);
		}
		if (strings.length == 2) {
			if (strings[0].equalsIgnoreCase("list")) {
				List<String> categories = new ArrayList<>();
				this.manager.getCategories().forEach(categories::add);
				return this.smartSuggestion(categories, strings[1]);
			} else if (strings[0].equalsIgnoreCase("setDefault") || strings[0].equalsIgnoreCase("removeDefault")) {
				return this.smartSuggestion(this.manager.getRulesSorted().stream().map(CarpetRule::name).collect(Collectors.toList()), strings[1]);
			} else {
				CarpetRule<?> rule = this.manager.contextRule(strings[0]);
				if (rule != null) {
					return this.smartSuggestion(new ArrayList<>(rule.suggestions()), strings[1]);
				}
			}
		}
		if (strings.length == 3) {
			if (strings[0].equalsIgnoreCase("setDefault")) {
				CarpetRule<?> rule = this.manager.contextRule(strings[1]);
				if (rule != null) {
					return this.smartSuggestion(new ArrayList<>(rule.suggestions()), strings[2]);
				}
			}
		}
		return Collections.emptyList();
	}
	//#if MC<=10710
	//$$ @Override
	//$$ public int compareTo(@NotNull Object o) {
	//$$ 	return this.compareTo((Command) o);
	//$$ }
	//#endif
}
//#endif
