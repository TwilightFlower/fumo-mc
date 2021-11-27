/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.mc.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import io.github.twilightflower.fumo.bootstrap.api.Bootstrapper;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

public class MinecraftBootstrapper implements Bootstrapper {
	private static final String[] LOADER_PATH_FILES = {
		"org/objectweb/asm/ClassVisitor.class",
		"org/objectweb/asm/tree/analysis/Analyzer.class",
		"org/objectweb/asm/commons/Remapper.class",
		"org/objectweb/asm/tree/ClassNode.class",
		"org/objectweb/asm/util/ASMifier.class",
		
		"com/github/zafarkhaja/semver/Version.class",
		
		"com/google/common/base/MoreObjects.class",
		"com/google/gson/Gson.class",
		"com/google/common/jimfs/Jimfs.class",
		
		"org/apache/logging/log4j/Logger.class",
		"org/apache/logging/log4j/core/Logger.class",
		
		"org/spongepowered/asm/mixin/Mixin.class",
		
		"io/github/twilightflower/fumo/core/Main.class",
		"io/github/twilightflower/fumo/mixin/FumoMixinService.class",
		
		".fumoloaderpathmarker"
	};
	private static final String[] JAR_TEST_FILES = {
		"META-INF/MANIFEST.MF",
		"pack.png"
	};
	private final Set<Path> targetPaths = new HashSet<>();
	private final Collection<FileSystem> fileSystems = new ArrayList<>();
	private String[] args;
	private String mcVersion;
	private Path mcJar;
	
	@Override
	public void close() throws IOException {
		for(FileSystem fs : fileSystems) {
			fs.close();
		}
	}

	@Override
	public void accept(String[] args) {
		this.args = args;
		int i = 0;
		for(; i < args.length; i++) {
			if(args[i].equals("--version")) {
				mcVersion = args[i + 1];
				System.setProperty("fumo.minecraft.version", mcVersion);
				break;
			}
		}
	}

	@Override
	public String[] args() {
		return args;
	}

	@Override
	public Set<URL> loaderURLs() {
		Set<URL> loaderPath = new HashSet<>();
		// grab classpath via resources
		try {
			for(String test : JAR_TEST_FILES) {
				for(Path p : getJarsWithFile(test)) {
					Path root = toRoot(p);
					if(goesOnLoader(root)) {
						loaderPath.add(p.toUri().toURL());
						if(isntDefaultFs(root)) {
							root.getFileSystem().close();
						}
					} else {
						if(isntDefaultFs(root)) {
							fileSystems.add(root.getFileSystem());
						}
						if(!findMcJar(root, p)) {
							targetPaths.add(root);
						}
					}
				}
			}
			
			remapMc();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return loaderPath;
	}

	@Override
	public Set<Path> targetPaths() {
		return targetPaths;
	}
	
	private boolean goesOnLoader(Path root) {
		for(String f : LOADER_PATH_FILES) {
			if(Files.exists(root.resolve(f))) {
				return true;
			}
		}
		return false;
	}
	
	private Path toRoot(Path dest) throws IOException {
		if(Files.isDirectory(dest)) {
			return dest;
		} else {
			return FileSystems.newFileSystem(dest, getClass().getClassLoader()).getPath("/");
		}
	}
	
	private Iterable<Path> getJarsWithFile(String resLoc) throws IOException {
		Collection<Path> paths = new ArrayList<>();
		Enumeration<URL> metaRes = getClass().getClassLoader().getResources(resLoc);
		
		int levels = 1;
		for(char c : resLoc.toCharArray()) {
			if(c == '/') {
				levels++;
			}
		}
		
		int pathEnd = resLoc.length() + 1;
		while(metaRes.hasMoreElements()) {
			URL manifest = metaRes.nextElement();
			String spath = manifest.getFile();
			Path path;
			if(manifest.getProtocol().equals("jar")) {
				path = Paths.get(URI.create(spath.substring(0, spath.length() - (pathEnd + 1)))); // account for !/
			} else {
				path = Paths.get(urlToURI(manifest));
				for(int i = 0; i < levels; i++) {
					path = path.getParent();
				}
			}
			paths.add(path);
		}
		
		return paths;
	}
	
	private boolean isntDefaultFs(Path p) {
		return !p.getFileSystem().equals(FileSystems.getDefault());
	}
	
	private boolean findMcJar(Path root, Path jarPath) {
		if(Files.exists(root.resolve("META-INF").resolve("MOJANGCS.RSA"))) {
			mcJar = jarPath;
			PropertyUtil.setJarLoc(mcJar.toString());
			Path brandRet = root.resolve("net").resolve("minecraft").resolve("client").resolve("ClientBrandRetriever.class");
			if(Files.exists(brandRet)) {
				PropertyUtil.setSide("client");
			}
			return true;
		}
		return false;
	}
	
	private void remapMc() {
		if(!PropertyUtil.IS_DEVELOPMENT && mcJar != null) {
			String remapFrom = PropertyUtil.PROVIDED_MAPPINGS;
			String remapTo = PropertyUtil.RUNTIME_MAPPINGS;
			String mappingsLoc = PropertyUtil.MAPPINGS_LOCATION;
			
			Path mappingCache = Paths.get(".fumoremapcache", mcVersion, remapTo + ".jar");
			if(!Files.exists(mappingCache)) {
				IMappingProvider mappings = TinyUtils.createTinyMappingProvider(new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(mappingsLoc))), remapFrom, remapTo);
				try(FileSystem mappedFs = FileSystems.newFileSystem(mcJar, Collections.singletonMap("create", true), getClass().getClassLoader())) {
					TinyRemapper remapper = TinyRemapper.newRemapper().withMappings(mappings).build();
					remapper.readInputs(mcJar);
					remapper.apply((String fname, byte[] content) -> {
						try {
							Files.write(mappedFs.getPath(fname), content, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			mcJar = mappingCache;
			PropertyUtil.setJarLoc(mcJar.toString());
		}
	}
	
	private static URI urlToURI(URL url) {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
