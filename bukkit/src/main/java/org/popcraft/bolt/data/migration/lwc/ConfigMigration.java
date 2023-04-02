package org.popcraft.bolt.data.migration.lwc;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.access.Access;
import org.popcraft.bolt.access.AccessRegistry;
import org.popcraft.bolt.access.DefaultAccess;

import java.util.EnumMap;
import java.util.Map;

public class ConfigMigration {
    private final BoltPlugin plugin;
    private final String defaultProtectionPrivate;
    private final String defaultProtectionDisplay;
    private final String defaultProtectionDeposit;
    private final String defaultProtectionWithdrawal;
    private final String defaultProtectionPublic;

    public ConfigMigration(final BoltPlugin plugin) {
        this.plugin = plugin;
        final AccessRegistry accessRegistry = plugin.getBolt().getAccessRegistry();
        defaultProtectionPrivate = accessRegistry.findProtectionTypeWithExactPermissions(DefaultAccess.PRIVATE).orElse("private");
        defaultProtectionDisplay = accessRegistry.findProtectionTypeWithExactPermissions(DefaultAccess.DISPLAY).orElse("display");
        defaultProtectionDeposit = accessRegistry.findProtectionTypeWithExactPermissions(DefaultAccess.DEPOSIT).orElse("deposit");
        defaultProtectionWithdrawal = accessRegistry.findProtectionTypeWithExactPermissions(DefaultAccess.WITHDRAWAL).orElse("withdrawal");
        defaultProtectionPublic = accessRegistry.findProtectionTypeWithExactPermissions(DefaultAccess.PUBLIC).orElse("public");
    }

    public void convert(final Map<Material, Access> protectableBlocks) {
        if (!plugin.getBolt().getStore().loadBlockProtections().join().isEmpty()) {
            return;
        }
        final FileConfiguration lwcCoreConfig = YamlConfiguration.loadConfiguration(plugin.getPluginsPath().resolve("LWC/core.yml").toFile());
        final ConfigurationSection blocks = lwcCoreConfig.getConfigurationSection("protections.blocks");
        if (blocks == null) {
            return;
        }
        final Map<Material, String> migrateToBoltConfig = new EnumMap<>(Material.class);
        for (final String block : blocks.getKeys(false)) {
            final boolean enabled = blocks.getBoolean("%s.enabled".formatted(block), false);
            final Material material = Material.getMaterial(block.toUpperCase());
            if (material != null && material.isBlock() && !protectableBlocks.containsKey(material)) {
                migrateToBoltConfig.put(material, enabled ? blocks.getString("%s.autoRegister".formatted(block), "false") : "false");
            }
        }
        if (!migrateToBoltConfig.isEmpty()) {
            migrateToBoltConfig.forEach((material, protectionType) -> {
                final String boltProtectionType;
                if ("private".equals(protectionType)) {
                    boltProtectionType = defaultProtectionPrivate;
                } else if ("display".equals(protectionType)) {
                    boltProtectionType = defaultProtectionDisplay;
                } else if ("donation".equals(protectionType)) {
                    boltProtectionType = defaultProtectionDeposit;
                } else if ("supply".equals(protectionType)) {
                    boltProtectionType = defaultProtectionWithdrawal;
                } else if ("public".equals(protectionType)) {
                    boltProtectionType = defaultProtectionPublic;
                } else {
                    boltProtectionType = protectionType;
                }
                plugin.getConfig().set("blocks.%s.autoProtect".formatted(material.name().toLowerCase()), boltProtectionType);
            });
            plugin.saveConfig();
            plugin.reload();
        }
    }
}
