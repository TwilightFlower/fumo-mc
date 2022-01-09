package io.github.twilightflower.fumo.mc.impl;

import org.spongepowered.asm.mixin.extensibility.IRemapper;

import io.github.twilightflower.fumo.mixin.spi.MixinMappingProvider;

public class McMixinRemapperProvider implements MixinMappingProvider {
	@Override
	public IRemapper getRemapper() {
		return PropertyUtil.IS_DEVELOPMENT ? NameRemapperImpl.INSTANCE : null;
	}
}
