package net.cancheta.settings.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.cancheta.ai.path.world.BlockSet;

import java.lang.reflect.Type;

public class BlockSetAdapter implements JsonSerializer<BlockSet>,
JsonDeserializer<BlockSet> {

@Override
public JsonElement serialize(BlockSet src, Type typeOfSrc,
	JsonSerializationContext context) {
JsonArray obj = new JsonArray();
// TODO
for (int blockId = 0; blockId < 0; blockId++) {
	if (src.contains(blockId)) {
		obj.add(getName(blockId));
	} else if (src.contains(blockId)) {
		for (int i = 0; i < 16; i++) {
			if (src.contains(blockId * 16 + i)) {
				JsonObject object = new JsonObject();
				object.add("block", getName(blockId));
				object.addProperty("meta", i);
				obj.add(object);
			}
		}
	}
}
return obj;
}

public static JsonElement getName(int blockId) {
	return new JsonPrimitive(blockId);
}

@Override
public BlockSet deserialize(JsonElement json, Type typeOfT,
	JsonDeserializationContext context) throws JsonParseException {
if (!json.isJsonArray()) {
	throw new JsonParseException("need an array.");
}

return BlockSet.builder().build();
}
}
