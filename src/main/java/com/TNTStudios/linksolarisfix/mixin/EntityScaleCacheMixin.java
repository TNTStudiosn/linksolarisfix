package com.TNTStudios.linksolarisfix.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.util.PehkuiEntityExtensions;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public abstract class EntityScaleCacheMixin implements PehkuiEntityExtensions {

    private final Map<ScaleType, ScaleData> pehkui_cachedScaleData = new HashMap<>();
    private int pehkui_cacheTick = -1;

    @Override
    public ScaleData pehkui_getScaleData(ScaleType type) {
        Entity entity = (Entity)(Object) this;

        int currentTick = entity.level() instanceof ServerLevelAccessor accessor
                ? accessor.getServer().getTickCount()
                : -1;

        if (currentTick != -1 && currentTick != pehkui_cacheTick) {
            pehkui_cachedScaleData.clear();
            pehkui_cacheTick = currentTick;
        }

        return pehkui_cachedScaleData.computeIfAbsent(type, t -> {
            return ((ScaleTypeBridge) (Object) t).getScaleData(entity);
        });
    }
}
