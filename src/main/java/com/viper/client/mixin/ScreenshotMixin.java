package com.viper.client.mixin;

import com.viper.client.ViperClient;
import com.viper.client.gui.ScreenshotEditorScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotMixin {

    @Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/client/gl/Framebuffer;Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private static void viper_afterScreenshot(File gameDirectory, String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver, CallbackInfo ci) {
        try {
            File screenshotsDir = new File(gameDirectory, "screenshots");
            File screenshotFile = new File(screenshotsDir, fileName);

            // editor öffnen auf hauptthread
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && screenshotFile.exists()) {
                mc.execute(() -> mc.setScreen(new ScreenshotEditorScreen(screenshotFile)));
            }
        } catch (Throwable e) {
            ViperClient.LOGGER.error("[Screenshot] failed to open editor", e);
        }
    }
}