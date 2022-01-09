package io.github.twilightflower.fumo.mc.impl.plugin;

import org.objectweb.asm.tree.ClassNode;

import io.github.twilightflower.fumo.core.api.transformer.ClassTransformer;
import io.github.twilightflower.fumo.core.api.transformer.TransformerContext;

public class NoopTransformer implements ClassTransformer {
	@Override
	public void acceptTransformerContext(TransformerContext context) { }

	@Override
	public boolean transforms(String className) {
		return false;
	}

	@Override
	public ClassNode transform(String className, ClassNode clazz) {
		return clazz;
	}

}
