package zone.skychase.chaosemeralds;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static zone.skychase.chaosemeralds.ChaosEmeraldsMod.*;

public class ChaosEmerald extends Item {
    public ChaosEmerald(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ISkychasePlayerEntity skychasePlayer = (ISkychasePlayerEntity) player;
        if(stack.getTranslationKey().equals("item.skychase.chaos_emerald")) {
            if(skychasePlayer.getInvulnerableTime() > 0) {
                // Deactivate
                skychasePlayer.setInvulnerableTime(0);
                player.world.playSound(null, player.x, player.y, player.z, KNUX_EVENT,
                        SoundCategory.MASTER, 1, 1);
            } else {
                // Activate
                int emeralds = stack.getCount();
                int rings = InventoryHelper.playerTotalItem(player, "item.skychase.power_ring");
                if (emeralds >= 7 && rings >= 1) {
                    skychasePlayer.setInvulnerableTime(20);
                    skychasePlayer.setKnuxVoiceTime(100);
                    player.world.playSound(null, player.x, player.y, player.z, HEREWEGO_EVENT,
                            SoundCategory.MASTER, 1, 1);
                } else {
                    player.world.playSound(null, player.x, player.y, player.z, ICANT_EVENT,
                            SoundCategory.MASTER, 1, 1);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}
