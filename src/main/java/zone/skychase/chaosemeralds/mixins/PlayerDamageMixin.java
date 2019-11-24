package zone.skychase.chaosemeralds.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zone.skychase.chaosemeralds.ISkychasePlayerEntity;
import zone.skychase.chaosemeralds.PlayerDamageCallback;

@Mixin(PlayerEntity.class)
@Implements(@Interface(iface = ISkychasePlayerEntity.class, prefix = "skychase$"))
public class PlayerDamageMixin {
    private int skychase_invulnerableTime;
    private int skychase_knuxVoiceTime;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCreate(World world, GameProfile profile, CallbackInfo info) {
        skychase_invulnerableTime = 0;
        skychase_knuxVoiceTime = 0;
    }

    public int skychase$getInvulnerableTime() {
        return skychase_invulnerableTime;
    }

    public void skychase$setInvulnerableTime(int value) {
        skychase_invulnerableTime = value;
    }

    public int skychase$getKnuxVoiceTime() {
        return skychase_knuxVoiceTime;
    }

    public void skychase$setKnuxVoiceTime(int value) {
        skychase_knuxVoiceTime = value;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        ActionResult result = PlayerDamageCallback.EVENT.invoker().interact(source, amount, (PlayerEntity) (Object) this);
        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
