package cc.flawcra.resolutioncontrol.mixin;

import cc.flawcra.resolutioncontrol.ResolutionControlMod;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void onRenderWorldBegin(CallbackInfo callbackInfo) {
		ResolutionControlMod.getInstance().setRcShouldScale(true);
	}
	
	@Inject(at = @At("RETURN"), method = "renderWorld")
	private void onRenderWorldEnd(CallbackInfo callbackInfo) {
		ResolutionControlMod.getInstance().setRcShouldScale(false);
	}
}
