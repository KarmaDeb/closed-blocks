// noinspection JSUnusedGlobalSymbols

"use strict";

const Material = Java.type('org.bukkit.Material');
const Registry = Java.type('org.bukkit.Registry');

const RecipeRegistry = Java.type('dev.tagless.craft.closedblocks.engine.RecipeBuilder');
const ElevatorUtils = Java.type('dev.tagless.craft.closedblocks.spigot.elevator.integration.ElevatorUtils');

for (const material of Material.values()) {
	if (!material.isBlock() || !material.isSolid()) {
		continue;
	}

	const key = material.getKeyOrNull();
	/** @type {string} */ const name = key.getKey();

	if (!name.endsWith('_wool') || !name.endsWith('_concrete')) {
		continue;
	}

	const raw = key.toString();
	const ite = 'minecraft:ender_pearl';

	const shape = [
		[raw, raw, raw],
		[raw, ite, raw],
		[raw, raw, raw]
	];

	RecipeRegistry.register(shape, () => {
		return ElevatorUtils.create(material);
	});
}