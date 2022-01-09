/*
 * Fumo-MC
 * Copyright (C) 2021-2022  TwilightFlower
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.twilightflower.fumo.mc.impl.plugin;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.MixinEnvironment;

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.FumoIdentifier;
import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;
import io.github.twilightflower.fumo.mc.impl.NameRemapperImpl;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;

public class FumoMcPlugin implements FumoLoaderPlugin {
	private boolean discoveredMods = false;
	private FumoLoader loader;
	
	@Override
	public void init(FumoLoader loader) {
		loader.registerLaunchProvider(new FumoIdentifier("fumo-mc", "minecraft-main"), new McLaunchProvider());
		this.loader = loader;
	}
	
	@Override
	public Set<ModMetadata> discoverMods(Map<String, Set<ModMetadata>> allMods, Set<ModMetadata> newMods) {
		if(!discoveredMods) {
			discoveredMods = true;
			Path mcJar = Paths.get(PropertyUtil.getJarLoc());
			try {
				FileSystem mcFs = FileSystems.newFileSystem(mcJar, getClass().getClassLoader());
				Path mcRoot = mcFs.getPath("/");
				return Collections.singleton(new ModMetadata(
						"minecraft", "Minecraft",
						Version.valueOf(PropertyUtil.getMcVers()),
						DataObject.of(Collections.emptyMap()),
						mcRoot,
						Collections.emptySet(), 
						Collections.emptySet(),
						true));
			} catch(IOException e) {
				throw new RuntimeException("Exception loading minecraft container", e);
			}
		} else {
			return Collections.emptySet();
		}
	}
	
	@Override
	public void pluginsLoaded() {
		// mixin plugin is now at the very least on classpath
		if(PropertyUtil.IS_DEVELOPMENT) {
			MixinEnvironment.getDefaultEnvironment().getRemappers().add(NameRemapperImpl.INSTANCE);
		}
	}
	
	@Override
	public void registerTransformers(TransformerRegistry transformerRegistry) {
		if(PropertyUtil.IS_DEVELOPMENT) {
			transformerRegistry.registerTransformer(
					new FumoIdentifier("fumo-mc", "devwidener"),
					new DevWidenerTransformer(),
					Collections.emptySet(),
					Collections.singleton(new FumoIdentifier("mixin", "mixin"))
				);
		}
		transformerRegistry.registerTransformer(new FumoIdentifier("fumo-mc", "brand"), new BrandTransformer());
	}
}
