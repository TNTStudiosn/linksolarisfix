package com.TNTStudios.linksolarisfix.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityTickThrottleMixin {
    private static final double FREEZE_RADIUS = 128.0D;
    private static final int  TICK_INTERVAL = 10;

    @Shadow public int tickCount;
    @Shadow public net.minecraft.world.level.Level level;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void throttleTick(CallbackInfo ci) {
        if (!(level instanceof ServerLevel)) return;

        Entity self = (Entity)(Object)this;

        // Nunca tocamos a los jugadores
        if (self instanceof Player) return;

        // Solo estrangulamos pasivos/ambientales, no mobs hostiles ni NPCs críticos
        boolean passive = self instanceof Animal
                || self instanceof WaterAnimal
                || self instanceof Axolotl
                || self instanceof Frog
                || self instanceof Allay
                || self instanceof Cod
                || self instanceof Salmon
                || self instanceof Pufferfish
                || self instanceof TropicalFish
                || self instanceof Squid
                || self instanceof GlowSquid;
        if (!passive) return;

        ServerLevel server = (ServerLevel) level;
        Player nearest = server.getNearestPlayer(self, FREEZE_RADIUS);

        boolean far = (nearest == null)
                || (self.distanceToSqr(nearest) > FREEZE_RADIUS * FREEZE_RADIUS);

        // Si está fuera de rango o no es uno de cada TICK_INTERVAL ticks => cancelamos
        if (far || (tickCount % TICK_INTERVAL != 0)) {
            ci.cancel();
        }
    }
}
