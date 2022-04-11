package net.cancheta.ai.command;

import net.cancheta.ai.strategy.AIStrategy;
import net.cancheta.ai.strategy.StackStrategy;
import net.cancheta.ai.strategy.StrategyStack;

import java.util.ArrayList;

public class StackBuilder {
	private boolean collecting;
    private ArrayList<AIStrategy> collected = new ArrayList<>();
    private SafeStrategyRule hardestSafeRule = SafeStrategyRule.NONE;

    public void startCollecting() {
        this.collecting = true;
        collected.clear();
        hardestSafeRule = SafeStrategyRule.NONE;
    }

    public boolean collect(AIStrategy strategy, SafeStrategyRule rule) {
        if (collecting) {
            collected.add(strategy);
            if (rule.ordinal() > hardestSafeRule.ordinal()) {
                hardestSafeRule = rule;
            }
            return true;
        } else {
            return false;
        }
    }

    public AIStrategy getStrategy() {
        collecting = false;
        StrategyStack stack = new StrategyStack();
        collected.forEach(stack::addStrategy);

        collected.clear(); // < to save memory
        return new StackStrategy(stack);
    }

    public void abort() {
        collecting = false;
        collected.clear(); // < to save memory
    }

    public boolean hasCollectedAnyStrategies() {
        return !collected.isEmpty();
    }

    public boolean isCollecting() {
        return collecting;
    }

    public SafeStrategyRule getSafeRule() {
        return hardestSafeRule;
    }
}
