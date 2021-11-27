/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

import com.github.zafarkhaja.semver.Version;

import io.github.twilightflower.fumo.core.api.FumoIdentifier;
import io.github.twilightflower.fumo.core.api.FumoLoader;
import io.github.twilightflower.fumo.core.api.data.DataObject;
import io.github.twilightflower.fumo.core.api.mod.ModMetadata;
import io.github.twilightflower.fumo.core.api.plugin.FumoLoaderPlugin;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;

public class FumoMcPlugin implements FumoLoaderPlugin {
	private boolean discoveredMods = false;
	
	@Override
	public void init(FumoLoader loader) {
		loader.registerLaunchProvider(new FumoIdentifier("fumo-mc", "minecraft-main"), new McLaunchProvider());
	}
	
	@Override
	public Set<ModMetadata> discoverMods(Map<String, Set<ModMetadata>> allMods, Set<ModMetadata> newMods) {
		if(!discoveredMods) {
			discoveredMods = true;
			Path mcJar = Paths.get(System.getProperty("fumo.minecraft.jarloc"));
			try {
				FileSystem mcFs = FileSystems.newFileSystem(mcJar, getClass().getClassLoader());
				Path mcRoot = mcFs.getPath("/");
				return Collections.singleton(new ModMetadata(
						"minecraft", "Minecraft",
						Version.valueOf(System.getProperty("fumo.minecraft.version")),
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
	public void registerTransformers(TransformerRegistry transformerRegistry) {
		if(PropertyUtil.IS_DEVELOPMENT) {
			transformerRegistry.registerTransformer(
					new FumoIdentifier("fumo-mc", "devwidener"),
					new DevWidenerTransformer(),
					Collections.emptySet(),
					Collections.singleton(new FumoIdentifier("mixin", "mixin"))
				);
		}
	}
}
