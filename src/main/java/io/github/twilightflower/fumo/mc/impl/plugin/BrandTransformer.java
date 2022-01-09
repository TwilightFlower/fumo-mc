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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerContext;

public class BrandTransformer implements ClassTransformer {
	@Override
	public void acceptTransformerContext(TransformerContext context) { }

	@Override
	public boolean transforms(String className) {
		return className.equals("net.minecraft.client.ClientBrandRetreiver");
	}

	@Override
	public ClassNode transform(String className, ClassNode clazz) {
		for(MethodNode m : clazz.methods) {
			if(m.name.equals("getClientModName")) {
				m.instructions.clear();
				m.instructions.add(new LdcInsnNode("fumo"));
				m.instructions.add(new InsnNode(Opcodes.ARETURN));
				break;
			}
		}
		return clazz;
	}

}
