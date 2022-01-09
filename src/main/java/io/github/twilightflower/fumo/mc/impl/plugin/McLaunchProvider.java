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
