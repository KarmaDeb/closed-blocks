// noinspection JSUnusedGlobalSymbols

"use strict";

const Registry = Java.type('org.bukkit.Registry');

const RecipeRegistry = Java.type('dev.tagless.craft.closedblocks.engine.RecipeBuilder');
const ElevatorUtils = Java.type('dev.tagless.craft.closedblocks.spigot.elevator.integration.ElevatorUtils');


const usableBlocks = Registry.MATERIAL.stream().filter(material => {
	if (!material.isBlock() && !material.isSolid()) {
		return false;
	}

	const key = material.getKeyOrNull();
	/** @type {string} */ const name = key.getKey();

	return name.endsWith('_wool') || name.endsWith('_concrete');
}).toList();

for (const material of usableBlocks) {
	const key = material.getKeyOrNull();
	/** @type {string} */ const name = key.getKey();

	const raw = key.toString();
	const ite = 'minecraft:ender_pearl';

	const shape = [
		[raw, raw, raw],
		[raw, ite, raw],
		[raw, raw, raw]
	];

	RecipeRegistry.register(`elevator_${name}`, shape, ElevatorUtils.create(material));
}