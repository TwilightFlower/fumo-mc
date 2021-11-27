/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.mc.api;

import java.lang.invoke.MethodType;

import io.github.twilightflower.fumo.mc.impl.NameRemapperImpl;

/**
 * Utility for mapping names to current mappings.
 * All class names should be in the form they would be returned by {@link Class#getName()}
 */
public interface NameRemapper {
	static NameRemapper getInstance() {
		return NameRemapperImpl.INSTANCE;
	}
	
	/**
	 * 
	 * @param namespaceFrom Namespace the provided name is in.
	 * @param className Name to map to the current namespace, in the format {@code java.lang.String$Inner}
	 * @return Mapped class name, in the format {@code java.lang.String$Inner}
	 */
	String mapClassName(String namespaceFrom, String className);
	/**
	 * 
	 * @param namespaceFrom Namespace the provided field name is in.
	 * @param ownerClass Name of the field's owner class, in the current namespace.
	 * @param fieldType Type of the field, in the current namespace.
	 * @param name Name of the field to remap to the current namespace.
	 * @return Mapped field name
	 */
	String mapFieldName(String namespaceFrom, String ownerClass, String fieldType, String name);
	
	/**
	 * 
	 * @param namespaceFrom Namespace the provided method name is in.
	 * @param ownerClass Name of the method's owner class, in the current namespace.
	 * @param returnType Method's return type, in the current namespace.
	 * @param paramTypes Method's parameter types, in the current namespace.
	 * @param name Name of the method to remap to the current namespace.
	 * @return Mapped method name
	 */
	String mapMethodName(String namespaceFrom, String ownerClass, String returnType, String[] paramTypes, String name);
	
	/**
	 * @see NameRemapper#mapClassName(String, String)
	 * @param namespaceFrom namespace to remap from
	 * @param classNames names to remap
	 * @return remapped names
	 */
	default String[] mapClassNames(String namespaceFrom, String[] classNames) {
		String[] result = new String[classNames.length];
		for(int i = 0; i < classNames.length; i++) {
			result[i] = mapClassName(namespaceFrom, classNames[i]);
		}
		return result;
	}
	
	/**
	 * See {@link NameRemapper#mapFieldName(String, String, String, String)}
	 */
	default String mapFieldName(String namespaceFrom, Class<?> ownerClass, Class<?> fieldType, String name) {
		return mapFieldName(namespaceFrom, ownerClass.getName(), fieldType.getName(), name);
	}
	
	/**
	 * See {@link NameRemapper#mapFieldName(String, String, String, String)}
	 */
	default String mapFieldName(String namespaceFrom, Class<?> ownerClass, String fieldType, String name) {
		return mapFieldName(namespaceFrom, ownerClass.getName(), fieldType, name);
	}
	
	/**
	 * See {@link NameRemapper#mapFieldName(String, String, String, String)}
	 */
	default String mapFieldName(String namespaceFrom, String ownerClass, Class<?> fieldType, String name) {
		return mapFieldName(namespaceFrom, ownerClass, fieldType.getName(), name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, String ownerClass, String returnType, Class<?>[] paramTypes, String name) {
		String[] paramTypeStrs = new String[paramTypes.length];
		for(int i = 0; i < paramTypes.length; i++) {
			paramTypeStrs[i] = paramTypes[i].getName();
		}
		return mapMethodName(namespaceFrom, ownerClass, returnType, paramTypeStrs, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, Class<?> ownerClass, Class<?> returnType, Class<?>[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass.getName(), returnType.getName(), paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, Class<?> ownerClass, String returnType, Class<?>[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass.getName(), returnType, paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, String ownerClass, Class<?> returnType, Class<?>[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass, returnType.getName(), paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, Class<?> ownerClass, Class<?> returnType, String[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass.getName(), returnType.getName(), paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, Class<?> ownerClass, String returnType, String[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass.getName(), returnType, paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, String ownerClass, Class<?> returnType, String[] paramTypes, String name) {
		return mapMethodName(namespaceFrom, ownerClass, returnType.getName(), paramTypes, name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, String ownerClass, MethodType methodType, String name) {
		return mapMethodName(namespaceFrom, ownerClass, methodType.returnType(), methodType.parameterArray(), name);
	}
	
	/**
	 * See {@link NameRemapper#mapMethodName(String, String, String, String[], String)}
	 */
	default String mapMethodName(String namespaceFrom, Class<?> ownerClass, MethodType methodType, String name) {
		return mapMethodName(namespaceFrom, ownerClass.getName(), methodType, name);
	}
}
