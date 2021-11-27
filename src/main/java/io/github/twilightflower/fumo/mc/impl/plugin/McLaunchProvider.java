/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.mc.impl.plugin;

import io.github.twilightflower.fumo.core.api.LaunchProvider;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;

public class McLaunchProvider implements LaunchProvider {

	@Override
	public boolean isActive(ClassLoader targetLoader, String[] programArgs) {
		if(PropertyUtil.getSide().equals("server")) {
			return hasClass("net.minecraft.server.Main", targetLoader);
		} else {
			return hasClass("net.minecraft.client.main.Main", targetLoader);
		}
	}

	@Override
	public Class<?> getMainClass(ClassLoader targetLoader) {
		try {
			if(PropertyUtil.getSide().equals("server")) {
				return targetLoader.loadClass("net.minecraft.server.Main");
			} else {
				return targetLoader.loadClass("net.minecraft.client.main.Main");
			}
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Could not find Minecraft class", e);
		}
	}

	private boolean hasClass(String clazz, ClassLoader loader) {
		try {
			loader.loadClass(clazz);
			return true;
		} catch(ClassNotFoundException e) {
			return false;
		}
	}
}
