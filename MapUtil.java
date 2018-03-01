package com.gohnstudio.service.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * Map 工具类
 * @author Gostoms
 * @date 2013-3-8 
 * 仿照Gava和apache-common-util类
 */
public class MapUtil {
	
	/**
	 * 列表转换接口
	 * @author 谢栋(Stone)
	 * @date 2013-3-8 
	 */	
	public abstract static class MapConvertExecute<T1, T2> {
		public abstract T2 convert(T1 data);
	}
	
	public static <T1, T2> MapConvertExecute<T1, T2> getExecSourceConvert() {
		return new MapConvertExecute<T1, T2>() {
			@SuppressWarnings("unchecked")
			@Override
			public T2 convert(T1 data) {
				return (T2)data;
			}
		};
	}
	
	/**
	 * 原始值传递为转换结果
	 */
	public final static MapConvertExecute<?, ?> ConvExecSource = new MapConvertExecute<Object, Object>() {
		@Override
		public Object convert(Object data) {
			return data;
		}
	};
	
	/**
	 * 判断 Map 是否为空
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?,?> map) {
		return map == null  ||  map.size() == 0;
	}
	
	/**
	 * 判断是否不为空
	 * @param map
	 * @return
	 */
	public static boolean isNotEmpty(Map<?,?> map) {
		return !isEmpty(map);
	}
	
	@SuppressWarnings("unchecked")
	public static <T1, T2> Map<T1, T2> add (Map<T1, T2> map, Object...datas) {
		int pipe_count = Float.valueOf(datas.length / 2.0f).intValue();
		if (pipe_count == 0) {
			return null;
		}
		for (int j=0; j<pipe_count; j++) {
			map.put((T1)datas[j * 2], (T2)datas[j * 2 + 1]);
		}
		
		return map;
	}
	
	/**
	 * 从数据创建 Map
	 * @param datas
	 * @return
	 * 示例：
		Map< String, Integer> map1 = MapUtil.makeMap("boy", 1, "girl", 2);
		或
		Map< String, Integer> map2 = MapUtil.makeMap(new Object[] {"boy", 1, "girl", 2});
	 */
	@SuppressWarnings("unchecked")
	public static <T1, T2> Map<T1, T2> makeMap(Object...datas) {
		int pipe_count = Float.valueOf(datas.length / 2.0f).intValue();
		if (pipe_count == 0) {
			return null;
		}
		
		T1 key;
		T2 val;
		
		Map<T1, T2> map = new HashMap<T1, T2>();
		for (int j=0; j<pipe_count; j++) {
			key = (T1)datas[j * 2];
			val = (T2)datas[j * 2 + 1];
			map.put(key, val);
		}
		
		return map;
	}
	/**
	 * 创建多元 Map(嵌套MAP)
	 * @param datas
	 * @return 示例：Map<Integer, Map< String, String>> mmap = MapUtil.makeMultiDatasMap(new Object[][] {{1, "lessonName", "boy", "lessonId", "1"},{2, "lessonName", "girl", "lessonId", "2"}});
	 *>>>> {1={lessonName=boy}, 2={lessonName=girl}}
	 */
	@SuppressWarnings("unchecked")
	public static <T1, T2, T3> Map<T1, Map<T3, T2>> makeMultiDatasMap(Object[][] datas) {
		int		pipe_count;
		T3	key;
		T2		val;
		Map<T3, T2> item;
		
		Map<T1, Map<T3, T2>> ret = new HashMap<T1, Map<T3, T2>>();
		for (int i=0; i<datas.length; i++) {
			
			pipe_count = Float.valueOf((datas[i].length+1) / 2.0f).intValue();
			if (pipe_count == 0) {
				continue;
			}
			
			item = new HashMap<T3, T2>();
			for (int j=1; j<pipe_count; j++) {
				key = (T3)datas[i][j * 2 - 1];
				val = (T2)datas[i][j * 2];
				item.put(key, val);
			}
			
			ret.put((T1)datas[i][0], item);
		}
		
		return ret;
	}
	
	/**
	 * 同时获取多项
	 * @param map Map对象
	 * @param names Key数组
	 * @return
	 */
	public static <T1,T2> Object[] multiGet(Map<T1, T2> map, T1...names) {
		if ( map.isEmpty() ) {
			return null;
		}
		List<Object> l = new ArrayList<Object>();
		for (T1 d : names) {
			l.add( map.get(d) );
		}
		return ListUtil.toArray(l, Object.class);
	}
	
	private static Map<String, Map<String, Method>> CLASS_METHOD_GETTER_INDEX = new HashMap<String, Map<String, Method>>();
	private static Method CLASS_GET_METHOD_BY_PROP_NAME(Map<String, Method> index, String prop) {
		String key;
		String uprop = prop.substring(0, 1).toUpperCase();
		if (prop.length() > 0) {
			uprop += prop.substring(1, prop.length());
		}
		if ( index.containsKey(prop) ) {
			return index.get(prop);
		} else if ( index.containsKey(key = "get"+uprop) ) {
			return index.get(key);
		} else if ( index.containsKey(key = "is"+uprop) ) {
			return index.get(key);
		} else if ( index.containsKey(key = "getIs"+uprop) ) {
			return index.get(key);
		} else {
			return null;
		}
	}
	
	/**
	 * 从对象过滤属性并生成 Map
	 * @param obj	对象
	 * @param prop	属性名称数组
	 * @return
	 */
	public static <T> Map<String, Object> filterFromObjectPropWithNickname(T obj, String... prop) {
		// 确认参数个数为 2 的整数倍
		if (null==prop  ||  prop.length==0  ||  prop.length % 2!=0) {
			return null;
		}
		
		int i = 0;
		List<String> props = new ArrayList<String>();
		for (String p : prop) {
			if (i%2 == 1) {
				props.add(p);
			}
			i++;
		}
		
		Map<String, Object> datas_new = null;
		Map<String, Object> datas_src = MapUtil.filterFromObjectProp(obj, prop);
		if (null != datas_src) {
			datas_new = new HashMap<String, Object>();
			
			i = 0;
			for (String p : prop) {
				if (i%2 == 1) {
					datas_new.put(prop[i-1], datas_src.get(p));
				}
				i++;
			}
			
			datas_src.clear();
			datas_src = null;
		}
		props.clear();
		props = null;
		
		return datas_new;
	}
	
	/**
	 * 从对象过滤属性并生成 Map
	 * @param obj	对象
	 * @param prop	属性名称数组
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Map<String, Object> filterFromObjectProp(T obj, String... prop) {
		if (null==obj   ||   null==prop) {
			return null;
		}
		Map<String, Object> m = new HashMap<String, Object>();
		if (
				obj instanceof Map
		) {
			Map om = (Map) obj;
			for (String p : prop) {
				if ( om.containsKey(p) ) {
					m.put(p, om.get(p));
				}
			}
		} else if (
			obj instanceof Integer
			|| obj instanceof Long
			|| obj instanceof Byte
			|| obj instanceof Boolean
			|| obj instanceof Float
			|| obj instanceof Double
			|| obj instanceof String
			|| obj instanceof List
			|| obj instanceof Date
		) {
			m.put(prop[0], obj);
		} else {
			Class cls = obj.getClass();
			Map<String, Method> method_index = CLASS_METHOD_GETTER_INDEX.get(cls.getName());
			if (null == method_index) {
				method_index = new HashMap<String, Method>();
				Method[] methods = cls.getMethods();
				if (null!=methods   &&   methods.length>0) {
					for (Method md : methods) {
						if( md.getName().matches("(get|is)[A-Z][a-zA-Z0-9_]*") ) {
							if (!md.getName().equals("getClass")  &&  md.getParameterTypes().length==0) {
								method_index.put(md.getName(), md);
							}
						}
					}
				}
				CLASS_METHOD_GETTER_INDEX.put(cls.getName(), method_index);
			}
			Method md = null;
			try {
				int pos;
				String key, val;
				Map<String, List<String>> extProps = null;
				for (String p : prop) {
					if ((pos = p.indexOf('.')) != -1) {
						if (extProps == null) {
							extProps = new HashMap<String, List<String>>();
						}
						key = p.substring(0, pos);
						val = p.substring(pos + 1, p.length());
						if (!extProps.containsKey(key)) {
							extProps.put(key, new ArrayList<String>());
						}
						extProps.get(key).add(val);
					} else {
						md = CLASS_GET_METHOD_BY_PROP_NAME(method_index, p);
						if (null != md) {
							m.put(p, md.invoke(obj));
						}
					}
				}
				if (null != extProps) {
					 Map<String, Object> mExt;
					Set<Entry<String, List<String>>> set = extProps.entrySet();
					Set<Entry<String,Object>> setExt; 
					for (Entry<String, List<String>> entry : set) {
						md = CLASS_GET_METHOD_BY_PROP_NAME(method_index, entry.getKey());
						if (null != md) {
							mExt = filterFromObjectProp(md.invoke(obj), ListUtil.toArray(entry.getValue(), String.class));
							if (null != mExt) {
								setExt = mExt.entrySet();
								for (Entry<String, Object> entryExt : setExt) {
									m.put(entry.getKey() + "." + entryExt.getKey(), entryExt.getValue());
								}
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
	
	/**
	 * 从对象过滤属性并生成 Map
	 * @param obj	对象 prop	属性名称数组
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> Map<String, Object> filterFromObject(T obj) {
		if (null==obj) {
			return null;
		}
		Map<String, Object> m = new HashMap<String, Object>();
		if (
				obj instanceof Map
		) {
			m.putAll((Map) obj);
		} else if (
			obj instanceof Integer
			|| obj instanceof Long
			|| obj instanceof Byte
			|| obj instanceof Boolean
			|| obj instanceof Float
			|| obj instanceof Double
			|| obj instanceof String
			|| obj instanceof List
			|| obj instanceof Date
		) {
			m.put("value", obj);
		} else {
			Class cls = obj.getClass();
			Map<String, Method> method_index = CLASS_METHOD_GETTER_INDEX.get(cls.getName());
			if (null == method_index) {
				method_index = new HashMap<String, Method>();
				Method[] methods = cls.getMethods();
				if (null!=methods   &&   methods.length>0) {
					for (Method md : methods) {
						if( md.getName().matches("(get|is)[A-Z][a-zA-Z0-9_]*") ) {
							if (!md.getName().equals("getClass")  &&  md.getParameterTypes().length==0) {
								method_index.put(md.getName(), md);
							}
						}
					}
				}
				CLASS_METHOD_GETTER_INDEX.put(cls.getName(), method_index);
			}
			Method md = null;
			try {
				String p;
				Set<Entry<String, Method>> method_collections = method_index.entrySet();
				for (Entry<String, Method> en : method_collections) {
					p = en.getKey();
					if (p.matches("^get(.*?)$")) {
						p = p.substring(3, 4).toLowerCase() + p.substring(4, p.length());
					} else if (p.matches("^is(.*?)$")) {
						p = p.substring(2, 3).toLowerCase() + p.substring(3, p.length());
					}
					md = en.getValue();
					if (null != md) {
						m.put(p, md.invoke(obj));
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return m;
	}
	
	/**
	 * 转换Map内部类型
	 * @param map
	 * @param keyConvExec
	 * @param valConvExec
	 * @return
	 */
	public static <T1, T2, T3, T4> Map<T3, T4> convertMapType(Map<T1, T2> map, MapConvertExecute<T1, T3> keyConvExec, MapConvertExecute<T2, T4> valConvExec) {
		Map<T3, T4> ret = new HashMap<T3, T4>();
		for (Entry<T1, T2> en : map.entrySet()) {
			ret.put(keyConvExec.convert(en.getKey()), valConvExec.convert(en.getValue()));
		}
		return ret;
	}
	
	static class A {
		int v = 1;
		public int getV() {
			return v;
		}
		void setV(int v) {
			this.v = v;
		}
	}
	
	static class B {
		A a;
		public A getA() {
			return a;
		}
		void setA(A a) {
			this.a = a;
		}
	}

	/**
	 * 从大到小给hasMap根据Value排序
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				//return (o1.getValue()).compareTo(o2.getValue());
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	public static void main(String[] args) {
		Map< String, Integer> map1 = MapUtil.makeMap("boy", 1, "girl", 2);
		Map< String, Integer> map2 = MapUtil.makeMap(new Object[] {"boy", 1, "girl", 2});
		System.out.println(map1);
		System.out.println(map2);
		
		Map<Integer, Map< String, String>> mmap = MapUtil.makeMultiDatasMap(new Object[][] {{1, "lessonName", "boy", "lessonId", "1"},{2, "lessonName", "girl", "lessonId", "2"}});
		System.out.println(mmap);
		
		A a = new A();
		B b = new B();
		b.setA(a);
		a.setV(100);
		
		System.out.println(MapUtil.filterFromObjectPropWithNickname(b,
			"ahaha", "a.v"
		));
	}
}
