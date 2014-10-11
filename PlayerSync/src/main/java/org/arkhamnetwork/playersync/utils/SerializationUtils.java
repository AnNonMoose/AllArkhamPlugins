/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.playersync.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.v1_7_R4.NBTBase;
import net.minecraft.server.v1_7_R4.NBTNumber;
import net.minecraft.server.v1_7_R4.NBTTagByteArray;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagDouble;
import net.minecraft.server.v1_7_R4.NBTTagInt;
import net.minecraft.server.v1_7_R4.NBTTagIntArray;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.minecraft.server.v1_7_R4.NBTTagLong;
import net.minecraft.server.v1_7_R4.NBTTagString;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author devan_000
 */
public class SerializationUtils {

      private static final ItemStack AIR = new ItemStack(36, 0);

      public static byte[] serializeItemStacks(ItemStack[] inv) throws SerializationException, IOException {
            NBTTagCompound baseTag = new NBTTagCompound();
            NBTTagList inventoryTag = new NBTTagList();

            int current = 0;

            for (ItemStack stack : inv) {
                  if (stack == null) {
                        stack = new ItemStack(Material.AIR, 1);
                  }
                  if (stack.getType() != Material.AIR) {
                        NBTTagCompound item = new NBTTagCompound();
                        item.setByte("InvSize", (byte) inv.length);
                        item.setByte("CurrentSlot", (byte) current);
                        inventoryTag.add(CraftItemStack.asNMSCopy(stack).save(item));
                  }
                  current++;
            }

            baseTag.set("Inventory", inventoryTag);

            Map<String, Object> map = toMap(baseTag);
            return JSONObject.toJSONString(map).getBytes();
      }

      public static ItemStack[] deserializeItemStacks(byte[] b) throws SerializationException, ParseException, IOException, ClassNotFoundException {
            if (b == null) {
                  return null;
            }
            NBTTagCompound baseTag = toTag(JSONValue.parseWithException(new String(b)).toString());
            NBTTagList inventoryTag = baseTag.getList("Inventory", 10);
            Map<Integer, ItemStack> itemMap = new ConcurrentHashMap<>();

            final int invSize = inventoryTag.get(0).getByte("InvSize");

            for (int i = 0; i < inventoryTag.size(); i++) {
                  NBTTagCompound item = inventoryTag.get(i);

                  int slot = item.getByte("CurrentSlot");
                  if (!itemMap.containsKey(slot)) {
                        itemMap.put(slot, CraftItemStack.asCraftMirror(net.minecraft.server.v1_7_R4.ItemStack.createStack(item)));
                  }
            }

            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < invSize; i++) {
                  if (i > invSize) {
                        break;
                  }

                  if (itemMap.get(i) == null) {
                        items.add(AIR.clone());
                        continue;
                  }

                  items.add(itemMap.get(i));
                  //IMPORTANT
                  itemMap.remove(i);
            }

            return items.toArray(new ItemStack[items.size()]);
      }

      public static byte[] serializePotionEffects(PotionEffect[] ef) throws SerializationException {
            List<Map<String, Object>> effects = new ArrayList<>(ef.length);

            for (PotionEffect effect : ef) {
                  effects.add(effect.serialize());
            }

            return JSONValue.toJSONString(effects).getBytes();
      }

      public static PotionEffect[] deserializePotionEffects(byte[] b) throws SerializationException, ParseException {
            if (b == null) {
                  return null;
            }
            Object o = JSONValue.parseWithException(new String(b));
            try {
                  if (o instanceof List) {
                        final List<?> data = (List) o;
                        ArrayList<PotionEffect> items = new ArrayList<>(data.size());
                        for (Object t : data) {
                              if (t instanceof Map) {
                                    final Map<?, ?> mdata = (Map) t;
                                    final Map<String, Object> conv = new HashMap<>(mdata.size());
                                    for (Map.Entry<?, ?> e : mdata.entrySet()) {
                                          conv.put(String.valueOf(e.getKey()), convert(e.getValue()));
                                    }
                                    items.add(new PotionEffect(conv));
                              } else {
                                    throw new IllegalArgumentException("Not a Map");
                              }
                        }
                        return items.toArray(new PotionEffect[items.size()]);
                  }
                  throw new IllegalArgumentException("Not a List");
            } catch (IllegalArgumentException ex) {
                  ex.printStackTrace();
            }
            return null;
      }

      private static Object convert(Object o) {
            if (o instanceof Number) {
                  Long v = (Long) o;
                  if (Integer.MAX_VALUE > v) {
                        return v.intValue();
                  }
            }
            return o;
      }

      private static final NBTTagCompound toTag(String jsonString) throws IOException {
            try {
                  return (NBTTagCompound) javaTypeToNBTTag(new JSONParser().parse(jsonString));
            } catch (ParseException e) {
                  throw new IOException(e);
            }
      }

      private static final NBTBase javaTypeToNBTTag(Object object) throws IOException {
            // Handle compounds
            if (object instanceof Map) {
                  @SuppressWarnings("unchecked")
                  Map<String, ?> map = (Map<String, ?>) object;
                  NBTTagCompound tag = new NBTTagCompound();
                  for (Entry<String, ?> entry : map.entrySet()) {
                        tag.set(entry.getKey(), javaTypeToNBTTag(entry.getValue()));
                  }
                  return tag;
            }
            // Handle numbers
            if (object instanceof Number) {
                  Number number = (Number) object;
                  if (number.longValue() == number.doubleValue()) {
                        // Whole number
                        if (number.intValue() == number.longValue()) {
                              // Fits in integer
                              return new NBTTagInt(number.intValue());
                        }
                        return new NBTTagLong(number.longValue());
                  } else {
                        return new NBTTagDouble(number.doubleValue());
                  }
            }
            // Handle strings
            if (object instanceof String) {
                  return new NBTTagString((String) object);
            }
            // Handle lists
            if (object instanceof List) {
                  List<?> list = (List<?>) object;
                  NBTTagList listTag = new NBTTagList();

                  // Handle int arrays
                  if (list.size() > 0) {
                        Object firstElement = list.get(0);
                        if (firstElement instanceof Number) {
                              @SuppressWarnings("unchecked")
                              List<Number> intList = (List<Number>) list;
                              return new NBTTagIntArray(unboxIntegers(intList));
                        }
                  }

                  for (Object entry : list) {
                        listTag.add(javaTypeToNBTTag(entry));
                  }
                  return listTag;
            }
            throw new IOException("Unknown object: (" + object.getClass() + ") " + object + "");
      }

      private static final int[] unboxIntegers(List<Number> boxed) {
            int[] ints = new int[boxed.size()];
            for (int i = 0; i < ints.length; i++) {
                  ints[i] = boxed.get(i).intValue();
            }
            return ints;
      }

      private static final Map<String, Object> toMap(NBTTagCompound tagCompound) throws IOException {
            @SuppressWarnings("unchecked")
            Collection<String> tagNames = tagCompound.c();

            // Add all children
            Map<String, Object> jsonObject = new HashMap<>(tagNames.size());
            for (String subTagName : tagNames) {
                  NBTBase subTag = tagCompound.get(subTagName);
                  jsonObject.put(subTagName, nbtTagToJavaType(subTag));
            }
            return jsonObject;
      }

      private static final Object nbtTagToJavaType(NBTBase tag) throws IOException {
            if (tag instanceof NBTTagCompound) {
                  return toMap((NBTTagCompound) tag);
            } else if (tag instanceof NBTTagList) {
                  // Add all children
                  NBTTagList listTag = (NBTTagList) tag;
                  List<Object> objects = new ArrayList<>();
                  for (int i = 0; i < listTag.size(); i++) {
                        objects.add(tagInNBTListToJavaType(listTag, i));
                  }
                  return objects;
            } else if (tag instanceof NBTNumber) {
                  // Check if double or long
                  NBTNumber nbtNumber = (NBTNumber) tag;
                  if (nbtNumber.c() == nbtNumber.g()) {
                        // Long, as double value == long value
                        return nbtNumber.c();
                  } else {
                        // Double
                        return nbtNumber.g();
                  }
            } else if (tag instanceof NBTTagString) {
                  String value = ((NBTTagString) tag).a_();
                  return value;
            } else if (tag instanceof NBTTagByteArray) {
                  return boxBytes(((NBTTagByteArray) tag).c());
            } else if (tag instanceof NBTTagIntArray) {
                  return boxIntegers(((NBTTagIntArray) tag).c());
            }

            throw new IOException("Unknown tag: " + tag);
      }

      private static final List<Byte> boxBytes(byte[] byteArray) {
            List<Byte> byteList = new ArrayList<>(byteArray.length);
            for (byte aByte : byteArray) {
                  byteList.add(aByte); // Wraps
            }
            return byteList;
      }

      private static final List<Integer> boxIntegers(int[] intArray) {
            List<Integer> integerList = new ArrayList<>(intArray.length);
            for (int anInt : intArray) {
                  integerList.add(anInt); // Wraps
            }
            return integerList;
      }

      private static final Object tagInNBTListToJavaType(NBTTagList tagList, int position) throws IOException {
            switch (tagList.d()) {
                  case 10:
                        NBTTagCompound compoundValue = tagList.get(position);
                        return nbtTagToJavaType(compoundValue);
                  case 11:
                        return boxIntegers(tagList.c(position));
                  case 6:
                        double doubleValue = tagList.d(position);
                        return doubleValue;
                  case 5:
                        float floatValue = tagList.e(position);
                        return floatValue;
                  case 8:
                        String stringValue = tagList.getString(position);
                        return stringValue;
            }
            throw new IOException("Unknown list: " + tagList);
      }

}
