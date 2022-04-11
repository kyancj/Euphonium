package net.cancheta.ai.command;

import net.cancheta.ai.strategy.*;

public enum SafeStrategyRule {
	NONE {
		@Override
		public AIStrategy makeSafe(AIStrategy strategy) {
			return strategy;
		}
	},
	DEFEND {
        @Override
        public AIStrategy makeSafe(AIStrategy strategy) {
            final StrategyStack stack = new StrategyStack();
            stack.addStrategy(new AbortOnDeathStrategy());
            stack.addStrategy(new DamageTakenStrategy());
            stack.addStrategy(new PlayerComesActionStrategy());
            stack.addStrategy(new CreeperComesActionStrategy());
            stack.addStrategy(strategy);
            return new StackStrategy(stack);
        }
    },
    DEFEND_MINING {
        @Override
        public AIStrategy makeSafe(AIStrategy strategy) {
            final StrategyStack stack = new StrategyStack();
            stack.addStrategy(new AbortOnDeathStrategy());
//            stack.addStrategy(new DoNotSuffocateStrategy());
            stack.addStrategy(new DamageTakenStrategy());
            stack.addStrategy(new PlayerComesActionStrategy());
            stack.addStrategy(new CreeperComesActionStrategy());
//            stack.addStrategy(new PlaceTorchStrategy());
            stack.addStrategy(strategy);
            return new StackStrategy(stack);
        }
    };

    public abstract AIStrategy makeSafe(AIStrategy strategy);
}
