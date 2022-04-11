package net.cancheta.build.commands;

//import com.mojang.brigadier.builder.ArgumentBuilder;
//import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//import com.mojang.brigadier.builder.RequiredArgumentBuilder;
//import com.mojang.brigadier.context.CommandContext;
//import net.cancheta.ai.AIHelper;
//import net.cancheta.ai.command.CommandEvaluationException;
//import net.cancheta.ai.commands.Commands;
//import net.cancheta.ai.commands.EnumArgument;
//import net.cancheta.ai.commands.IAIControllable;
//import net.cancheta.ai.commands.SafeStrategyRule;
//import net.cancheta.ai.path.ClearAreaPathfinder;
//import net.cancheta.ai.path.ClearAreaPathfinder.ClearMode;
//import net.cancheta.ai.path.world.BlockSet;
//import net.cancheta.ai.path.world.BlockSets;
//import net.cancheta.ai.path.world.WorldData;
//import net.cancheta.ai.strategy.PathFinderStrategy;
//import net.cancheta.ai.utils.BlockCuboid;
//import net.minecraft.command.argument.BlockPosArgumentType;
//import net.minecraft.command.argument.BlockStateArgument;
//import net.minecraft.command.argument.BlockStateArgumentType;
//import net.minecraft.command.argument.PosArgument;
//import net.minecraft.util.math.BlockPos;
//
//import java.util.function.Function;

@Deprecated
public class CommandClearArea {
//
//    private static final class ClearAreaStrategy extends PathFinderStrategy {
//        private String progress = "?";
//        private boolean done = false;
//        private final ClearAreaPathfinder pathFinder;
//
//        private ClearAreaStrategy(ClearAreaPathfinder pathFinder) {
//            super(pathFinder, "");
//            this.pathFinder = pathFinder;
//        }
//
//        @Override
//        public void searchTasks(AIHelper helper) {
//            final int max = pathFinder.getAreaSize();
//            if (max <= 100000) {
//                float toClearCount = pathFinder.getToClearCount(helper);
//                progress = 100 - Math.round(100f * toClearCount / max) + "%";
//                done = toClearCount == 0;
//            }
//            if (!done) {
//                System.out.println("Searching tasks, Desync:" +  isDesync());
//                super.searchTasks(helper);
//            }
//        }
//
//        @Override
//        public String getDescription(AIHelper helper) {
//            return "Clear area: " + progress;
//        }
//
//        @Override
//        public boolean hasFailed() {
//            return !done;
//        }
//    }
//
//    public static void register(LiteralArgumentBuilder<IAIControllable> dispatcher, LiteralArgumentBuilder<IAIControllable> minebuild) {
//        // /minebuild clear uses the area set by minebuild
//        LiteralArgumentBuilder<IAIControllable> normalClearCommand = Commands.literal("clear");
//        generateCommand(normalClearCommand, context -> getArea(context.getSource().getAiHelper()));
//        minebuild.then(normalClearCommand);
//
//        // Minecraft syntax: /minebot clear [from] [to]
//        RequiredArgumentBuilder<IAIControllable, ILocationArgument> clearWithRange = Commands.argument("to", BlockPosArgument.blockPos());
//        generateCommand(clearWithRange, context -> new BlockCuboid<>(
//                Commands.getBlockPos(context, "from"),
//                Commands.getBlockPos(context, "to")
//        ));
//        dispatcher.then(
//                Commands.literal("clear")
//                        .then(
//                                Commands.argument("from", BlockPosArgument.blockPos()).then(clearWithRange)));
//    }
//
//    /**
//     * Add mode and block type parameters
//     * @param base base command
//     */
//    private static void generateCommand(ArgumentBuilder<IAIControllable, ?> base,
//                                        Function<CommandContext<IAIControllable>, BlockCuboid<WorldData>> areaGetter) {
//        Commands.optional(base,
//                __ -> BlockSets.EMPTY.invert(),
//                "block",
//                BlockStateArgument.blockState(),
//                BlockStateInput.class,
//                (blockStateInput, context) -> BlockSet.builder().add(blockStateInput.getState()).build(),
//                (withBlock, block) -> Commands.optional(
//                        withBlock,
//                        __ -> ClearMode.VISIT_EVERY_POS,
//                        "mode",
//                        EnumArgument.of(ClearMode.class),
//                        ClearMode.class,
//                        (builder, mode) -> builder.executes(
//                                context -> requestUseStrategy(context, areaGetter.apply(context), block, mode)
//                        )
//                )
//        );
//    }
//
//    private static int requestUseStrategy(CommandContext<IAIControllable> context, BlockCuboid<WorldData> area,
//                                          Commands.ArgExecutorSupplier<BlockSet> toClear,
//                                          Commands.ArgExecutorSupplier<ClearMode> mode) {
//        return context.getSource().requestUseStrategy(
//                new ClearAreaStrategy(new ClearAreaPathfinder(area, toClear.get(context), mode.get(context))),
//                SafeStrategyRule.DEFEND_MINING);
//    }
//
//    public static <W extends WorldData> BlockCuboid<W> getArea(AIHelper helper) {
//        final BlockPos pos1 = helper.getPos1();
//        final BlockPos pos2 = helper.getPos2();
//        if (pos1 == null || pos2 == null) {
//            throw new CommandEvaluationException("No area has been set yet. Set an area to fill using /minebuild posN");
//        } else {
//            return new BlockCuboid<>(pos1, pos2);
//        }
//    }
}
