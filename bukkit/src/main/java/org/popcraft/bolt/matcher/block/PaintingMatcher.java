package org.popcraft.bolt.matcher.block;

import org.bukkit.Art;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.util.BoundingBox;
import org.popcraft.bolt.matcher.Match;
import org.popcraft.bolt.util.FoliaUtil;

import java.util.HashSet;
import java.util.Set;

public class PaintingMatcher implements BlockMatcher {
    private boolean enabled;

    @Override
    public void initialize(Set<Material> protectableBlocks, Set<EntityType> protectableEntities) {
        enabled = protectableEntities.contains(EntityType.PAINTING);
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public boolean canMatch(Block block) {
        return enabled;
    }

    @Override
    public Match findMatch(Block block) {
        final Set<Entity> entities = new HashSet<>();
        FoliaUtil.getNearbyEntities(block, block.getBoundingBox().expand(0.5, 0, 0.5, 0.5, 0, 0.5), Painting.class::isInstance).forEach(entity -> {
            if (entity instanceof final Painting painting) {
                final Art art = painting.getArt();
                final int width = art.getBlockWidth();
                final int height = art.getBlockHeight();
                if (BlockFace.NORTH.equals(painting.getFacing()) || BlockFace.SOUTH.equals(painting.getFacing())) {
                    final Block min = painting.getLocation().subtract(width / 2d, height / 2d, 0).getBlock().getRelative(painting.getAttachedFace());
                    final Block max = min.getRelative(width - 1, height - 1, 0);
                    if (BoundingBox.of(min, max).overlaps(block.getBoundingBox())) {
                        entities.add(painting);
                    }
                } else if (BlockFace.WEST.equals(painting.getFacing()) || BlockFace.EAST.equals(painting.getFacing())) {
                    final Block min = painting.getLocation().subtract(0, height / 2d, width / 2d).getBlock().getRelative(painting.getAttachedFace());
                    final Block max = min.getRelative(0, height - 1, width - 1);
                    if (BoundingBox.of(min, max).overlaps(block.getBoundingBox())) {
                        entities.add(painting);
                    }
                }
            }
        });
        return Match.ofEntities(entities);
    }
}
