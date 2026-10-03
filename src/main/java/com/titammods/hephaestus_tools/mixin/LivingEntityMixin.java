package com.titammods.hephaestus_tools.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.titammods.hephaestus_tools.tools.helper.ToolCombat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow protected boolean dead;

    @WrapMethod(method = "hurtServer")
    private boolean hephaestusTools$captureWeapon(ServerLevel level, DamageSource source, float amount,
                                                Operation<Boolean> original) {
        return ToolCombat.hurt((LivingEntity) (Object) this, source, () -> original.call(level, source, amount));
    }

    @WrapMethod(method = "die")
    private void hephaestusTools$resolvedKill(DamageSource source, Operation<Void> original) {
        boolean wasDead = dead;
        original.call(source);
        if (!wasDead && dead) ToolCombat.killed((LivingEntity) (Object) this, source);
    }
}
