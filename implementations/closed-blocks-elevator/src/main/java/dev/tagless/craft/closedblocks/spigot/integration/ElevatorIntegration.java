package dev.tagless.craft.closedblocks.spigot.integration;

import dev.tagless.craft.closedblocks.api.extension.ClosedBlocksIntegration;
import dev.tagless.craft.closedblocks.api.extension.IntegrationStateReason;
import dev.tagless.craft.closedblocks.spigot.integration.elevator.BlockElevator;
import dev.tagless.craft.closedblocks.spigot.integration.elevator.behaviors.ElevatorTitleBehavior;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ElevatorIntegration extends ClosedBlocksIntegration {

    /**
     * Enables the integration
     *
     * @param reason the reason of why the integration was enabled
     */
    @Override
    public void enable(final @NotNull IntegrationStateReason reason) {
        this.getFeatureManager().enableFor(ElevatorTitleBehavior.class, BlockElevator.class);
    }

    /**
     * Disables the integration
     *
     * @param reason the reason of why the integration was disabled
     */
    @Override
    public void disable(final @NotNull IntegrationStateReason reason) {
        this.getFeatureManager().disable(ElevatorTitleBehavior.class);
        this.getFeatureManager().disable(BlockElevator.class);
    }
}
