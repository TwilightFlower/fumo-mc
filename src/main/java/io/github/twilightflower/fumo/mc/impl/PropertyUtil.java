/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.mc.impl;

public class PropertyUtil {
	public static final String MAPPINGS_LOCATION = System.getProperty("fumo.minecraft.mappingsfile", "hashed/mappings.tiny");
	public static final String RUNTIME_MAPPINGS = System.getProperty("fumo.minecraft.runtimemappings", "hashed");
	public static final String PROVIDED_MAPPINGS = System.getProperty("fumo.minecraft.providedmappings", "official");
	public static final boolean IS_DEVELOPMENT = System.getProperty("fumo.minecraft.dev", "false").equals("true");
	
	private static final String JAR_LOC_PROP = "fumo.minecraft.jarloc";
	private static final String SIDE_PROP = "fumo.minecraft.side";
	
	public static void setJarLoc(String to) {
		System.setProperty(JAR_LOC_PROP, to);
	}
	
	public static String getJarLoc() {
		return System.getProperty(JAR_LOC_PROP);
	}
	
	public static String getSide() {
		return System.getProperty(SIDE_PROP, "server");
	}
	
	public static void setSide(String to) {
		if(System.getProperty(SIDE_PROP) != null) {
			System.setProperty(SIDE_PROP, to);
		}
	}
}
