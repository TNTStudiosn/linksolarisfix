
package com.TNTStudios.linksolarisfix.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;

@Mixin(ScaleType.class)
public abstract class ScaleTypeBridge {
    @Shadow
    public abstract ScaleData getScaleData(Entity entity);
}
