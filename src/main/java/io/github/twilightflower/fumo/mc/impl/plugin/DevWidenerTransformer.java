/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
