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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.twilightflower.fumo.core.api.util.Pair;
import io.github.twilightflower.fumo.mc.api.NameRemapper;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.FieldDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;

public class NameRemapperImpl implements NameRemapper {
	public static final NameRemapperImpl INSTANCE = new NameRemapperImpl(loadMappings());
	
	// pair: <namespace, classname>
	private final Map<Pair<String, String>, String> classesMap = new HashMap<>();
	private final Map<MemberEntry, String> fieldsMap = new HashMap<>();
	private final Map<MemberEntry, String> methodsMap = new HashMap<>();
	
	private NameRemapperImpl(TinyTree mappings) {
		String mapTo = PropertyUtil.RUNTIME_MAPPINGS;
		for(ClassDef clazz : mappings.getClasses()) {
			String mappedClassName = clazz.getName(mapTo).replace('/', '.');
			for(String namespace : mappings.getMetadata().getNamespaces()) {
				if(!namespace.equals(mapTo)) {
					classesMap.put(Pair.of(namespace, clazz.getName(namespace).replace('/', '.')), mappedClassName);
				}
			}
			
			for(FieldDef field : clazz.getFields()) {
				String mappedFieldName = field.getName(mapTo);
				String mappedFieldDescriptor = field.getDescriptor(mapTo);
				for(String namespace : mappings.getMetadata().getNamespaces()) {
					if(!namespace.equals(mapTo)) {
						fieldsMap.put(new MemberEntry(namespace, mappedClassName, mappedFieldDescriptor, field.getName(namespace)), mappedFieldName);
					}
				}
			}
			
			for(MethodDef method : clazz.getMethods()) {
				String mappedMethodName = method.getName(mapTo);
				String mappedMethodDescriptor = method.getDescriptor(mapTo);
				for(String namespace : mappings.getMetadata().getNamespaces()) {
					if(!namespace.equals(mapTo)) {
						methodsMap.put(new MemberEntry(namespace, mappedClassName, mappedMethodDescriptor, method.getName(namespace)), mappedMethodName);
					}
				}
			}
		}
	}
	
	@Override
	public String mapClassName(String namespaceFrom, String className) {
		if(namespaceFrom.equals(PropertyUtil.RUNTIME_MAPPINGS)) {
			return className;
		}
		
		Pair<String, String> idx = Pair.of(namespaceFrom, className);
		return classesMap.getOrDefault(idx, className);
	}
	
	@Override
	public String mapFieldName(String namespaceFrom, String ownerClass, String fieldType, String name) {
		if(namespaceFrom.equals(PropertyUtil.RUNTIME_MAPPINGS)) {
			return name;
		}
		MemberEntry idx = new MemberEntry(namespaceFrom, ownerClass, descriptorize(fieldType), name);
		return fieldsMap.getOrDefault(idx, name);
	}
	
	@Override
	public String mapMethodName(String namespaceFrom, String ownerClass, String returnType, String[] paramTypes, String name) {
		if(namespaceFrom.equals(PropertyUtil.RUNTIME_MAPPINGS)) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder("(");
		for(String paramTy : paramTypes) {
			sb.append(descriptorize(paramTy));
		}
		sb.append(descriptorize(returnType));
		sb.append(')');
		String descriptor = sb.toString();
		
		MemberEntry idx = new MemberEntry(namespaceFrom, ownerClass, descriptor, name);
		return methodsMap.getOrDefault(idx, name);
	}
	
	private static String descriptorize(String type) {
		if(type.length() > 1 && !type.startsWith("[")) {
			return "L" + type.replace('.', '/') + ";";
		} else {
			return type;
		}
	}
	
	private static TinyTree loadMappings() {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(NameRemapperImpl.class.getClassLoader().getResourceAsStream(PropertyUtil.MAPPINGS_LOCATION)))) {
			return TinyMappingFactory.loadWithDetection(br, true);
		} catch (IOException e) {
			throw new RuntimeException("Error loading mappings", e);
		}
	}
	
	private static class MemberEntry {
		final String namespace;
		final String owner;
		final String descriptor;
		final String name;
		final int hashCode;
		
		MemberEntry(String namespace, String owner, String descriptor, String name) {
			this.name = name;
			this.namespace = namespace;
			this.owner = owner;
			this.descriptor = descriptor;
			
			hashCode = Objects.hash(namespace, owner, descriptor, name);
		}
		
		public boolean equals(Object other) {
			if(!(other instanceof MemberEntry)) return false;
			MemberEntry o = (MemberEntry) other;
			return o.hashCode == hashCode && o.name.equals(name) && o.namespace.equals(namespace) && o.descriptor.equals(descriptor) && o.owner.equals(owner);
		}
		
		public int hashCode() {
			return hashCode;
		}
	}
}
