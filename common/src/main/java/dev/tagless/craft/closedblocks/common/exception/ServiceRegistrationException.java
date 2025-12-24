package dev.tagless.craft.closedblocks.common.exception;

public class ServiceRegistrationException extends RuntimeException {

    public ServiceRegistrationException(final Class<?> serviceType) {
        super(String.format("Error loading ClosedBlocks. Because another plugin has already registered the %s service",
                serviceType.getSimpleName()));
    }
}
