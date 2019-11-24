package zone.skychase.chaosemeralds;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

// I don't know what I'm doing lol
public interface PlayerDamageCallback {
    Event<PlayerDamageCallback> EVENT = EventFactory.createArrayBacked(PlayerDamageCallback.class,
        (listeners) -> (source, amount, player) -> {
        for(PlayerDamageCallback event : listeners) {
            ActionResult result = event.interact(source, amount, player);
            if(result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
    });
    ActionResult interact(DamageSource source, float amount, PlayerEntity player);
}
