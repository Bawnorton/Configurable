package com.bawnorton.configurable.api.serialisation;

@FunctionalInterface
public interface SerialisationBootstrap {
	/**
	 * Register additional handlers/resolvers.
	 */
	void bootstrap(SerialisationRegistrar registrar);

	/**
	 * Lower values run first. Default is 0.
	 */
	default int priority() {
		return 0;
	}
}

