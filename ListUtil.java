package com.gohnstudio.service.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List常量工具类
 * @author Gostoms
 * @date 2012-12-24 
 * 仿照Gava和apache-common-util类
 */
public class ListUtil {

	/**
	 * 列表转换接口
	 * @author 谢栋(Stone)
	 * @date 2013-3-8 
	 */
	public interface ListConvertExecute<T1, T2> {
		T2 convert(Integer pos, T1 data);
	}
	
	private static <T1, T2> List<T2> _add_convexec_rlt_to_list(List<T2> list, ListConvertExecute<T1, T2> execute, T1 data, Integer pos) {
		T2 dst_obj = execute.convert(pos, data);
		if (null != dst_obj) {
			list.add(dst_obj);
		}
		return list;
	}
	
	/**
	 * 判断列表是否为空(为null或元素数量为 0)
	 * @param l
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(List l) {
		return l == null  ||  l.size() == 0;
	}
	
	/**
	 * 判断是否不为空
	 * @param l
	 * @return
	 */
	public static boolean isNotEmpty(List<?> l) {
		return !isEmpty(l);
	}
	
	/**
	 * 转换到数组
	 * @param l
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> l, Class<T> cls) {
		if ( isEmpty(l) ) {
			return null;
		}
		l.toArray();
		Object a = Array.newInstance(cls, l.size());
		T[] ret = l.toArray((T[]) a);
		return ret;
	}
	
	/**
	 * 创建多元 List(嵌套MAP)
	 * @param datas
	 * @return 示例：List< Map< String, String>> lm = ListUtil.makeMultiDatasList(new Object[][] {{"lessonName", "boy", "lessonId", "1"},{"lessonName", "girl", "lessonId", "2"}});
	 */
	@SuppressWarnings("unchecked")
	public static <T1, T2> List<Map<T1, T2>> makeMultiDatasList(Object[][] datas) {
		int		pipe_count;
		T1		key;
		T2		val;
		Map<T1, T2> item;
		
		List<Map<T1, T2>> ret = new ArrayList<Map<T1, T2>>();
		for (int i=0; i<datas.length; i++) {
			
			pipe_count = Float.valueOf(datas[i].length / 2.0f).intValue();
			if (pipe_count == 0) {
				continue;
			}
			
			item = new HashMap<T1, T2>();
			for (int j=0; j<pipe_count; j++) {
				key = (T1)datas[i][j * 2];
				val = (T2)datas[i][j * 2 + 1];
				item.put(key, val);
			}
			ret.add(item);
		}
		
		return ret;
	}
	
	/**
	 * 创建列表通过对象组
	 * @param datas
	 * @return
	 */
	public static <T> List<T> makeList(T...datas) {
		if (null==datas  ||  datas.length==0) {
			return null;
		}
		List<T> ret = new ArrayList<T>();
		for (T obj : datas) {
			ret.add(obj);
		}
		return ret;
	}
	
	/**
	 * 创建列表通过对象组
	 * @param convertExecute 转换接口
	 * @param datas
	 * @return
	 */
	public static <T1, T2> List<T2> makeList(ListConvertExecute<T1, T2> convertExecute, T1...datas) {
		int i = 0;
		List<T2> ret = new ArrayList<T2>();
		for (T1 obj : datas) {
			_add_convexec_rlt_to_list(ret, convertExecute, obj, i);
			i++;
		}
		return ret;
	}

	/**
	 * 从对象过滤属性并生成 List>Map
	 * @param list	列表
	 * @param prop	属性名称数组
	 * @return
	 */
	public static <T> List<Map<String, Object>> filterFromObjectProp(List<T> list, String... prop) {
		List<Map<String, Object>> ret = null;
		if ( isNotEmpty(list) ) {
			ret = new ArrayList<Map<String,Object>>();
			Map<String, Object> data = null;
			for (T o : list) {
				data = MapUtil.filterFromObjectProp(o, prop);
				if (null != data) {
					ret.add(data);
				}
			}
		}
		return ret;
	}
	
	/**
	 * 从对象过滤属性并生成 List>Map
	 * @param list	列表
	 * @param prop	属性名称数组
	 * @return
	 */
	public static <T> List<Map<String, Object>> filterFromObjectPropWithNickname(List<T> list, String... prop) {
		List<Map<String, Object>> ret = null;
		if ( isNotEmpty(list) ) {
			ret = new ArrayList<Map<String,Object>>();
			Map<String, Object> data = null;
			for (T o : list) {
				data = MapUtil.filterFromObjectPropWithNickname(o, prop);
				if (null != data) {
					ret.add(data);
				}
			}
		}
		return ret;
	}
	
	/**
	 * 转换列表类型
	 * @param list 列表
	 * @param convertExecute 转换接口
	 * @return
	 */
	public static <T1, T2> List<T2> convertListType(List<T1> list, ListConvertExecute<T1, T2> convertExecute) {
		int i = 0;
		List<T2> ret = new ArrayList<T2>();
		for (T1 data : list) {
			_add_convexec_rlt_to_list(ret, convertExecute, data, i);
			i++;
		}
		return ret;
	}
	
	public static void main(String[] args) {
		List<Long> l = new ArrayList<Long>();
		l.add(1L);
		l.add(2L);
		l.add(8L);
		l.add(9L);
		Long[] ll = toArray(l, Long.class);
		
		List<String> ls = ListUtil.convertListType(l, new ListConvertExecute<Long, String>() {
			public String convert(Integer pos, Long data) {
				return data.toString();
			}
		});
		
		List<Float> lf = ListUtil.convertListType(ls, new ListConvertExecute<String, Float>() {
			public Float convert(Integer pos, String data) {
				return Float.valueOf(data);
			}
		});

		System.out.println( ls );
		System.out.println( lf );
		
		for (Long i : ll) {
			System.out.println( i );
		}
		
		List<Map<String, String>> lm = ListUtil.makeMultiDatasList(new Object[][] {{"lessonName", "boy", "lessonId", "1"},{"lessonName", "girl", "lessonId", "2"}});
		
		System.out.println(lm);
	}
}
