package zone.skychase.chaosemeralds;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.loot.v1.LootJsonParser;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.entry.LootEntry;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChaosEmeraldsMod implements ModInitializer {
    // Items and sounds to register
    public static final ChaosEmerald CHAOS_EMERALD_ITEM = new ChaosEmerald(new Item.Settings().group(ItemGroup.MISC));
    public static final PowerRing POWER_RING_ITEM = new PowerRing(new Item.Settings().group(ItemGroup.MISC));

    public static final Identifier OHNO_ID = new Identifier("skychase", "ohno");
    public static SoundEvent OHNO_EVENT = new SoundEvent(OHNO_ID);
    public static final Identifier ICANT_ID = new Identifier("skychase", "icant");
    public static SoundEvent ICANT_EVENT = new SoundEvent(ICANT_ID);
    public static final Identifier HEREWEGO_ID = new Identifier("skychase", "herewego");
    public static SoundEvent HEREWEGO_EVENT = new SoundEvent(HEREWEGO_ID);
    public static final Identifier RATS_ID = new Identifier("skychase", "rats");
    public static SoundEvent RATS_EVENT = new SoundEvent(RATS_ID);
    public static final Identifier ITSOVER_ID = new Identifier("skychase", "itsover");
    public static SoundEvent ITSOVER_EVENT = new SoundEvent(ITSOVER_ID);
    public static final Identifier KNUX_ID = new Identifier("skychase", "knux");
    public static SoundEvent KNUX_EVENT = new SoundEvent(KNUX_ID);
    public static final Identifier RING_GET_ID = new Identifier("skychase", "ring_get");
    public static SoundEvent RING_GET_EVENT = new SoundEvent(RING_GET_ID);
    public static final Identifier RING_DROP_ID = new Identifier("skychase", "ring_drop");
    public static SoundEvent RING_DROP_EVENT = new SoundEvent(RING_DROP_ID);

    private static final String LOOT_ENTRY_JSON = "{\"type\":\"minecraft:item\",\"name\":\"skychase:power_ring\"}";

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("skychase", "chaos_emerald"), CHAOS_EMERALD_ITEM);
        Registry.register(Registry.ITEM, new Identifier("skychase", "power_ring"), POWER_RING_ITEM);

        // All hostile mobs drop 1 ring when killed
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            final List<String> strings = Arrays.asList(
                    "guardian", "skeleton", "stray", "husk",
                    "zombie", "_illager", "creeper", "vex",
                    "spider", "giant", "slime", "ghast",
                    "enderman", "blaze", "magma_cube", "witch",
                    "endermite", "shulker", "silverfish", "pillager"
            );
            if (strings.stream().anyMatch(s -> id.toString().contains(s))) {
                LootEntry entryFromString = LootJsonParser.read(LOOT_ENTRY_JSON, LootEntry.class);
                LootPool pool = FabricLootPoolBuilder.builder()
                        .withEntry(entryFromString)
                        .withRolls(ConstantLootTableRange.create(1)) // Always
                        .withCondition(SurvivesExplosionLootCondition.builder())
                        .build();

                supplier.withPool(pool);
            }
        });

        Registry.register(Registry.SOUND_EVENT, OHNO_ID, OHNO_EVENT);
        Registry.register(Registry.SOUND_EVENT, ICANT_ID, ICANT_EVENT);
        Registry.register(Registry.SOUND_EVENT, RING_DROP_ID, RING_DROP_EVENT);

        // Hook for player taking damage (Logical Server method)
        PlayerDamageCallback.EVENT.register((source, amount, player) -> {
            // When invuln is active reflect damage to mobs, cancel damage otherwise
            ISkychasePlayerEntity skychasePlayer = (ISkychasePlayerEntity) player;
            if(skychasePlayer.getInvulnerableTime() > 0) {
                Entity entity = source.getSource();
                if(entity instanceof MobEntity) {
                    MobEntity mob = (MobEntity) entity;
                    mob.damage(source, amount);
                }
                amount = 0;
            }
            if(amount <= 0) return ActionResult.FAIL;

            // Play sound when hit
            if (InventoryHelper.playerHasItem(player, "item.skychase.chaos_emerald")) {
                if (InventoryHelper.playerHasItem(player, "item.skychase.power_ring")) {
                    player.world.playSound(null, player.x, player.y, player.z, OHNO_EVENT,
                            SoundCategory.MASTER, 1, 1);
                }
            } else if (InventoryHelper.playerHasItem(player, "item.skychase.power_ring")) {
                player.world.playSound(null, player.x, player.y, player.z, RING_DROP_EVENT,
                        SoundCategory.MASTER, 1, 1);
            }

            // Drop all your rings
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            for(;;) {
                int index = InventoryHelper.playerItemIndex(player, "item.skychase.power_ring");
                if(index == -1) break;
                ItemStack stack = player.inventory.takeInvStack(index, 1);
                ItemEntity ringEntity = new ItemEntity(player.world, player.x, player.y, player.z, stack);
                ringEntity.setPickupDelay(20);
                double x = rand.nextDouble(-0.5, 0.5);
                double y = rand.nextDouble(0.25, 0.5);
                double z = rand.nextDouble(-0.5, 0.5);
                // Minimum speed for X and Z
                if(x < 0 && x > -0.25) x = -0.25;
                if(x > 0 && x < 0.25) x = 0.25;
                if(z < 0 && z > -0.25) z = -0.25;
                if(z > 0 && z < 0.25) z = 0.25;
                ringEntity.setVelocity(x, y, z);
                player.world.spawnEntity(ringEntity);
            }

            return ActionResult.PASS;
        });

        // Count down invuln on world tick
        WorldTickCallback.EVENT.register((world) -> {
            ThreadLocalRandom rand = ThreadLocalRandom.current();
            for(PlayerEntity player : world.getPlayers()) {
                ISkychasePlayerEntity skychasePlayer = (ISkychasePlayerEntity) player;
                int invulnTime = skychasePlayer.getInvulnerableTime();
                int knuxTime = skychasePlayer.getKnuxVoiceTime();
                if(invulnTime > 0) {
                    // Quick and dirty portal particle
                    double x = player.x + rand.nextDouble(-0.5, 0.5);
                    double y = player.y + rand.nextDouble(0.5, 1.0);
                    double z = player.z + rand.nextDouble(-0.5, 0.5);
                    world.addParticle(ParticleTypes.PORTAL, x, y, z, 0, 0, 0);
                    // Annoy the player with a random Knuckles quote every 10 seconds
                    if(knuxTime > 0) {
                        knuxTime--;
                        if(knuxTime == 0) {
                            world.playSound(null, player.x, player.y, player.z, KNUX_EVENT,
                                    SoundCategory.MASTER, 1, 1);
                            knuxTime = 200;
                        }
                        skychasePlayer.setKnuxVoiceTime(knuxTime);
                    }
                    invulnTime--;
                    if(invulnTime == 0) {
                        // Reached 0 ticks, consume ring or deactivate
                        int index = InventoryHelper.playerItemIndex(player, "item.skychase.power_ring");
                        if(index != -1) {
                            ItemStack stack = player.inventory.getInvStack(index);
                            stack.decrement(1);
                            //player.inventory.removeOne(stack);
                            invulnTime = 20;
                            world.playSound(null, player.x, player.y, player.z, RING_GET_EVENT,
                                    SoundCategory.MASTER, 0.5f, 1);
                        } else {
                            world.playSound(null, player.x, player.y, player.z, RATS_EVENT,
                                    SoundCategory.MASTER, 1, 1);
                        }
                    }
                    skychasePlayer.setInvulnerableTime(invulnTime);
                }
            }
        });
    }

    public static void Main(String[] args) {
        System.out.println("Bruv what are you doing");
    }
}
