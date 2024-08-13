package carpet.tick;

import carpet.commands.framework.StructuredCommandData;
import carpet.utils.Messenger;
import carpet.api.algebraic.Algebraic;
import carpet.api.algebraic.MatchWith;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.source.CommandSource;

@Algebraic({TickCommandData.NullaryData.class, TickCommandData.UnaryData.class})
public interface TickCommandData extends StructuredCommandData<AbstractCommand> {
    enum NullaryOption {
        FREEZE {
            @Override
            public boolean run(MinecraftServer server, CommandSource source) {
                TickContext.INSTANCE.flipFreezeState();
                Messenger.m(source, "w " + (TickContext.INSTANCE.frozen ?
                        "Froze" : "Unfroze") + " the server tick loop");
                return true;
            }
        };
        private final String name = name().toLowerCase();
        public abstract boolean run(MinecraftServer server, CommandSource source);
        @Override
        public String toString() {
            return name;
        }
    }

    enum UnaryOption {
        RATE {
            @Override
            public boolean run(MinecraftServer server, CommandSource source, double value) {
                TickContext.INSTANCE.setTps(value);
                Messenger.m(source, "w Tick rate is " + value);
                return true;
            }
        }, STEP {
            @Override
            public boolean run(MinecraftServer server, CommandSource source, double value) {
                TickContext context = TickContext.INSTANCE;
                if (context.frozen) {
                    if (context.remainingTicks > 0) {
                        Messenger.m(source, "w Already stepping ticks!");
                    } else if (value >= 0) {
                        context.remainingTicks = (int) value;
                        Messenger.m(source, "w Stepping " + ((int) value) + " ticks");
                    } else {
                        Messenger.m(source, "w Please enter a nonnegative number!");
                    }
                } else {
                    Messenger.m(source, " w You can only step ticks when frozen!");
                }
                return true;
            }
        };
        private final String name = name().toLowerCase();
        public abstract boolean run(MinecraftServer server, CommandSource source, double value);
        @Override
        public String toString() {
            return name;
        }
    }

    class NullaryData implements TickCommandData {
        @MatchWith
        private NullaryOption option;

        @Override
        public void run(AbstractCommand command, MinecraftServer server, CommandSource source) throws CommandException {
            option.run(server, source);
        }
    }

    class UnaryData implements TickCommandData {
        @MatchWith
        private UnaryOption option;
        @MatchWith
        private double value;

        @Override
        public void run(AbstractCommand command, MinecraftServer server, CommandSource source) throws CommandException {
            option.run(server, source, value);
        }
    }
}
