package com.amightytank.vanillatweaks.debug.ai;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class AiGoalSelectorAccess {
    private static Field cachedWrappedGoalSetField;

    private AiGoalSelectorAccess() {
    }

    public static List<WrappedGoal> getWrappedGoals(GoalSelector selector) {
        if (selector == null) {
            return Collections.emptyList();
        }

        try {
            if (cachedWrappedGoalSetField != null) {
                List<WrappedGoal> cachedResult = tryReadWrappedGoalSet(selector, cachedWrappedGoalSetField);
                if (!cachedResult.isEmpty()) {
                    return cachedResult;
                }
            }

            for (Field field : GoalSelector.class.getDeclaredFields()) {
                if (!Set.class.isAssignableFrom(field.getType())) {
                    continue;
                }

                field.setAccessible(true);

                List<WrappedGoal> result = tryReadWrappedGoalSet(selector, field);
                if (!result.isEmpty()) {
                    cachedWrappedGoalSetField = field;
                    return result;
                }
            }
        } catch (Exception ignored) {
        }

        return Collections.emptyList();
    }

    private static List<WrappedGoal> tryReadWrappedGoalSet(GoalSelector selector, Field field) {
        try {
            Object value = field.get(selector);

            if (!(value instanceof Set<?> set)) {
                return Collections.emptyList();
            }

            List<WrappedGoal> goals = new ArrayList<>();

            for (Object object : set) {
                if (!(object instanceof WrappedGoal wrappedGoal)) {
                    return Collections.emptyList();
                }

                goals.add(wrappedGoal);
            }

            return goals;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}