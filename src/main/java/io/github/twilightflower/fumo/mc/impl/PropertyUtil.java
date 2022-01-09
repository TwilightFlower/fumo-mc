/*
 *
 *
 *     Copyright (C) 2021 TwilightFlower
 *
 *     This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along with this program; if not, see <https://www.gnu.org/licenses>.
 *
 *     Additional permission under GNU GPL version 3 section 7
 *
 *     If you modify this Program, or any covered work, by linking or combining it with another work, the licensors of this Program grant you additional permission to convey the resulting work.
 */

package io.github.twilightflower.fumo.mc.impl;

public class PropertyUtil {
	public static final String MAPPINGS_LOCATION = System.getProperty("fumo.minecraft.mappingsfile", "hashed/mappings.tiny");
	public static final String RUNTIME_MAPPINGS = System.getProperty("fumo.minecraft.runtimemappings", "hashed");
	public static final String PROVIDED_MAPPINGS = System.getProperty("fumo.minecraft.providedmappings", "official");
	public static final boolean IS_DEVELOPMENT = System.getProperty("fumo.minecraft.dev", "false").equals("true");
	
	private static final String JAR_LOC_PROP = "fumo.minecraft.jarloc";
	private static final String SIDE_PROP = "fumo.minecraft.side";
	private static final String MC_VERS_PROP = "fumo.minecraft.version";
	
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
	
	public static String getMcVers() {
		return System.getProperty(MC_VERS_PROP);
	}
	
	public static void setMcVers(String to) {
		System.setProperty(MC_VERS_PROP, to);
	}
}
