/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.twilightflower.fumo.mc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.ClientBrandRetriever;

@Mixin(ClientBrandRetriever.class)
public class BrandMixin {
	@Overwrite
	public static String getClientModName() {
		return "fumo";
	}
}
