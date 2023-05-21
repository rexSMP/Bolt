package org.popcraft.bolt.matcher.block;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.popcraft.bolt.matcher.Match;
import org.popcraft.bolt.util.FoliaUtil;

import java.util.Set;

public class LeashHitchMatcher implements BlockMatcher {
    private boolean enabled;

    @Override
    public void initialize(Set<Material> protectableBlocks, Set<EntityType> protectableEntities) {
        enabled = protectableEntities.contains(EntityType.LEASH_HITCH);
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public boolean canMatch(Block block) {
        return enabled && Tag.FENCES.isTagged(block.getType());
    }

    @Override
    public Match findMatch(Block block) {
        return Match.ofEntities(FoliaUtil.getNearbyEntities(block, block.getBoundingBox(), LeashHitch.class::isInstance));
    }
}
