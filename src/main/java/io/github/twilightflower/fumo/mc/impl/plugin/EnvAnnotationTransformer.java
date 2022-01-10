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

import java.util.Iterator;
import java.util.List;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerContext;
import io.github.twilightflower.fumo.mc.impl.PropertyUtil;
import net.fabricmc.api.EnvType;

public class EnvAnnotationTransformer implements ClassTransformer {
	@Override
	public void acceptTransformerContext(TransformerContext context) { }

	@Override
	public boolean transforms(String className) {
		return true;
	}

	@Override
	public ClassNode transform(String className, ClassNode clazz) {
		if(clazz.invisibleAnnotations != null) {
			for(AnnotationNode a : clazz.invisibleAnnotations) {
				if(a.desc.equals("Lnet/fabricmc/api/Environment;")) {
					EnvType env = getEnvType(a);
					if(!PropertyUtil.isCurrentSide(env)) {
						throw new NoClassDefFoundError(className + " does not exist on this side");
					}
				} else if(a.desc.equals("Lnet/fabricmc/api/EnvironmentInterface;")) {
					EnvType env = getEnvType(a);
					if(!PropertyUtil.isCurrentSide(env)) {
						Type itf = getItfType(a);
						clazz.interfaces.remove(itf.getClassName().replace('.', '/'));
					}
				} else if(a.desc.equals("Lnet/fabricmc/api/EnvironmentInterfaces;")) {
					@SuppressWarnings("unchecked")
					List<AnnotationNode> vals = (List<AnnotationNode>) getAnnotationProperty(a, "value");
					for(AnnotationNode envItf : vals) {
						EnvType env = getEnvType(envItf);
						if(!PropertyUtil.isCurrentSide(env)) {
							Type itf = getItfType(envItf);
							clazz.interfaces.remove(itf.getClassName().replace('.', '/'));
						}
					}
				}
			}
		}
		Iterator<MethodNode> mIter = clazz.methods.iterator();
		while(mIter.hasNext()) {
			MethodNode m = mIter.next();
			if(m.invisibleAnnotations != null) {
				for(AnnotationNode a : m.invisibleAnnotations) {
					if(a.desc.equals("Lnet/fabricmc/api/Environment;")) {
						EnvType env = getEnvType(a);
						if(!PropertyUtil.isCurrentSide(env)) {
							mIter.remove();
						}
						break;
					}
				}
			}
		}
		
		Iterator<FieldNode> fIter = clazz.fields.iterator();
		while(fIter.hasNext()) {
			FieldNode f = fIter.next();
			if(f.invisibleAnnotations != null) {
				for(AnnotationNode a : f.invisibleAnnotations) {
					if(a.desc.equals("Lnet/fabricmc/api/Environment;")) {
						EnvType env = getEnvType(a);
						if(!PropertyUtil.isCurrentSide(env)) {
							fIter.remove();
						}
						break;
					}
				}
			}
		}
		
		return clazz;
	}
	
	private EnvType getEnvType(AnnotationNode on) {
		return EnvType.valueOf(((String[]) getAnnotationProperty(on, "value"))[1]);
	}
	
	private Object getAnnotationProperty(AnnotationNode on, String propName) {
		for(int i = 0; i < on.values.size(); i += 2) {
			String s = (String) on.values.get(i);
			if(s.equals(propName)) {
				return on.values.get(i + 1);
			}
		}
		throw new IllegalArgumentException("Annotation doesn't have element " + propName);
	}
	
	private Type getItfType(AnnotationNode on) {
		return (Type) getAnnotationProperty(on, "itf");
	}
}
