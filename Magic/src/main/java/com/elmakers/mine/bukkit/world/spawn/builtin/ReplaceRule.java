package com.elmakers.mine.bukkit.world.spawn.builtin;

import javax.annotation.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.world.spawn.SpawnRule;

public class ReplaceRule extends SpawnRule {
    protected EntityData replaceWith;

    @Override
    public void finalizeLoad(String worldName) {
        // Legacy support
        if (!parameters.contains("type")) {
            parameters.set("type", parameters.get("replace_type"));
        }
        if (!parameters.contains("sub_type")) {
            parameters.set("sub_type", parameters.get("replace_sub_type"));
        }

        replaceWith = controller.getMob(parameters.getString("type"));
        if (replaceWith == null) {
            replaceWith = controller.loadMob(parameters);
        }

        if (replaceWith == null || replaceWith.getType() == null) {
            controller.getLogger().warning("Error reading in configuration for custom mob in " + worldName + " for rule " + key);
            return;
        }
        String message = " Replacing: " + getTargetEntityTypeName() + " in " + worldName + " at y > " + minY
                + " with " + replaceWith.describe() + " at a " + (percentChance * 100) + "% chance";

        if (tags != null) {
            message = message + " in regions tagged with any of " + tags.toString();
        }
        if (biomes != null) {
            message = message + " in biomes " + biomes.toString();
        }
        if (notBiomes != null) {
            message = message + " not in biomes " + notBiomes.toString();
        }
        controller.info(message);
    }

    @Override
    @Nullable
    public LivingEntity onProcess(Plugin plugin, LivingEntity entity) {
        if (replaceWith == null) return null;

        // This makes replacing the same type of mob have better balance,
        // particularly with mob spawners
        if (entity.getType() == replaceWith.getType()) {
            replaceWith.modify(entity);
            return entity;
        }

        Entity spawned = replaceWith.spawn(entity.getLocation());
        return spawned instanceof LivingEntity ? (LivingEntity)spawned : null;
    }
}
