package org.popcraft.bolt.util.matcher.block;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.popcraft.bolt.util.matcher.Match;

import java.util.Collections;
import java.util.Optional;

public class SaplingMatcher implements BlockMatcher {
    @Override
    public boolean canMatch(Block block) {
        return true;
    }

    @Override
    public Optional<Match> findMatch(Block block) {
        final Block above = block.getRelative(BlockFace.UP);
        if (Tag.SAPLINGS.isTagged(above.getType()) || Material.BAMBOO_SAPLING.equals(above.getType())) {
            return Optional.of(Match.ofBlocks(Collections.singleton(above)));
        }
        return Optional.empty();
    }
}