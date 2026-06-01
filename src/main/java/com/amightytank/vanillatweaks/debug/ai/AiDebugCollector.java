package com.amightytank.vanillatweaks.debug.ai;

import com.amightytank.vanillatweaks.network.packet.S2CAiDebugPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class AiDebugCollector {
    private AiDebugCollector() {
    }

    public static S2CAiDebugPacket collect(Mob mob) {
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());

        String title = mob.getDisplayName().getString();
        String entityType = typeId == null ? "unknown" : typeId.toString();

        List<String> statusLines = new ArrayList<>();
        statusLines.add("Entity ID: " + mob.getId());
        statusLines.add("Type: " + entityType);
        statusLines.add("Health: " + format(mob.getHealth()) + " / " + format(mob.getMaxHealth()));
        statusLines.add("Target: " + describeTarget(mob));
        statusLines.add("Navigation: " + (mob.getNavigation().isDone() ? "idle" : "moving"));
        statusLines.add("Vehicle: " + describeVehicle(mob));
        statusLines.add("Passengers: " + mob.getPassengers().size());

        List<String> goalLines = describeSelector(mob.goalSelector);
        List<String> targetGoalLines = describeSelector(mob.targetSelector);

        return new S2CAiDebugPacket(
                mob.getId(),
                title,
                entityType,
                mob.getHealth(),
                mob.getMaxHealth(),
                statusLines,
                goalLines,
                targetGoalLines
        );
    }

    private static List<String> describeSelector(GoalSelector selector) {
        List<WrappedGoal> wrappedGoals = AiGoalSelectorAccess.getWrappedGoals(selector);

        int totalGoals = wrappedGoals.size();

        List<String> running = wrappedGoals.stream()
                .filter(WrappedGoal::isRunning)
                .sorted(Comparator.comparingInt(WrappedGoal::getPriority))
                .map(AiDebugCollector::describeWrappedGoal)
                .collect(Collectors.toCollection(ArrayList::new));

        if (running.isEmpty()) {
            running.add("No running goals. Available goals: " + totalGoals);
        } else {
            running.add(0, "Running: " + running.size() + " / Available: " + totalGoals);
        }

        return running;
    }

    private static String describeWrappedGoal(WrappedGoal wrappedGoal) {
        Goal goal = wrappedGoal.getGoal();

        String goalName = goal.getClass().getSimpleName();
        if (goalName == null || goalName.isBlank()) {
            goalName = goal.getClass().getName();
        }

        String flags = goal.getFlags().isEmpty()
                ? "no flags"
                : goal.getFlags().stream()
                  .map(Enum::name)
                  .collect(Collectors.joining(", "));

        return "#" + wrappedGoal.getPriority() + " " + goalName + " [" + flags + "]";
    }

    private static String describeTarget(Mob mob) {
        LivingEntity target = mob.getTarget();

        if (target == null) {
            return "none";
        }

        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

        return target.getDisplayName().getString()
                + " | "
                + (typeId == null ? "unknown" : typeId.toString())
                + " | "
                + format(mob.distanceTo(target))
                + " blocks";
    }

    private static String describeVehicle(Mob mob) {
        Entity vehicle = mob.getVehicle();

        if (vehicle == null) {
            return "none";
        }

        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType());

        return vehicle.getDisplayName().getString()
                + " | "
                + (typeId == null ? "unknown" : typeId.toString());
    }

    private static String format(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}