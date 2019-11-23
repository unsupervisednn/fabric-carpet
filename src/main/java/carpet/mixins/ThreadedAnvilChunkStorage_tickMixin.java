package carpet.mixins;

import carpet.network.channels.StructureChannel;
import carpet.utils.CarpetProfiler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorage_tickMixin
{
    @Shadow @Final private ServerWorld world;

//    @Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders;

    CarpetProfiler.ProfilerToken currentSection;

    @Inject(method = "tick", at = @At("HEAD"))
    private void startProfilerSection(BooleanSupplier booleanSupplier_1, CallbackInfo ci)
    {
        currentSection = CarpetProfiler.start_section(world, "Unloading", CarpetProfiler.TYPE.GENERAL);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void stopProfilerSecion(BooleanSupplier booleanSupplier_1, CallbackInfo ci)
    {
        if (currentSection != null)
        {
            CarpetProfiler.end_current_section(currentSection);
        }
    }

    @Inject(method = "sendWatchPackets", at = @At("HEAD"))
    private void recordChunkSent(ServerPlayerEntity player, ChunkPos pos, Packet<?>[] packets, boolean b1, boolean b2, CallbackInfo ci) {
        StructureChannel.instance.recordChunkSent(player, pos);
    }

//    @Inject(method = "setLevel", at = @At("RETURN"))
//    private void setLevelTrack(long long_1, int int_1, ChunkHolder chunkHolder_1, int int_2, CallbackInfoReturnable<ChunkHolder> ci) {
//        if (ci.isCancelled()) {
//            return;
//        }
//
//        LoadedChunksTracker.instance.setCurrentChunkHolders(this.currentChunkHolders);
//    }
}
