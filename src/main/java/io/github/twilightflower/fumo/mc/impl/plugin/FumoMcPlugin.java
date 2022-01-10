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
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it with other software, containing parts covered by the terms of the other software's license, the licensors of this Program grant you additional permission to convey the resulting work.
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
import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerRegistry;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;

public class FumoMcPlugin implements FumoLoaderPlugin {
	private static final FumoIdentifier TRANSFORMS_END = new FumoIdentifier("fumo-mc", "loader_transforms_end");
	private static final FumoIdentifier TRANSFORMS_BEGIN = new FumoIdentifier("fumo-mc", "loader_transforms_begin");
	private static final FumoIdentifier MIXIN = new FumoIdentifier("mixin", "mixin");
	
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
	public void registerTransformers(TransformerRegistry transformerRegistry) {
		transformerRegistry.registerTransformer(
				TRANSFORMS_END,
				new NoopTransformer(),
				Collections.singleton(MIXIN)
		);
		transformerRegistry.registerTransformer(
				TRANSFORMS_BEGIN,
				new NoopTransformer(),
				Collections.singleton(TRANSFORMS_END)
		);
		
		
		if(PropertyUtil.IS_DEVELOPMENT) {
			transformer(
					transformerRegistry,
					new FumoIdentifier("fumo-mc", "dev_widener"),
					new DevWidenerTransformer()
			);
		}
		transformer(
				transformerRegistry,
				new FumoIdentifier("fumo-mc", "brand"),
				new BrandTransformer()
		);
		transformer(
				transformerRegistry,
				new FumoIdentifier("fumo-mc", "env_annotations"),
				new EnvAnnotationTransformer()
		);
	}
	
	private void transformer(TransformerRegistry to, FumoIdentifier id, ClassTransformer transformer) {
		to.registerTransformer(
				id,
				transformer,
				Collections.singleton(TRANSFORMS_END),
				Collections.singleton(TRANSFORMS_BEGIN)
		);
	}
}
