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

package io.github.twilightflower.fumo.mc.impl.plugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerContext;

public class DevWidenerTransformer implements ClassTransformer {

	@Override
	public boolean transforms(String className) {
		return className.startsWith("net.minecraft");
	}

	@Override
	public ClassNode transform(String className, ClassNode clazz) {
		clazz.access = publicize(clazz.access);
		for(FieldNode field : clazz.fields) {
			field.access = publicize(field.access);
		}
		for(MethodNode method : clazz.methods) {
			method.access = publicize(method.access);
		}
		return clazz;
	}
	
	private static int publicize(int access) {
		if((access & Opcodes.ACC_PRIVATE) == 0) {
			return (access & ~Opcodes.ACC_PROTECTED) | Opcodes.ACC_PUBLIC;
		} else {
			return access;
		}
	}

	@Override
	public void acceptTransformerContext(TransformerContext context) { }
}
