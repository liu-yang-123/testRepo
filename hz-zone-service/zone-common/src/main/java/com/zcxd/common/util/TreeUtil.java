package com.zcxd.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName TreeUtil
 * @Description 树结构数据处理工具类
 * @author 秦江南
 * @Date 2021年5月6日下午3:42:13
 */
public class TreeUtil {
	
	/**
	 * 
	 * @Title getFiledInfo
	 * @Description 将对象转为Map
	 * @param o
	 * @return 返回类型 Map<String,Object>
	 */
	public static Map<String,Object> getFiledInfo(Object o) {
        Map<String,Object> parameters = new HashMap<String, Object>();
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            parameters.put(field.getName(), getFieldValueByName(field.getName(), o));
        }
        return parameters;
    }
 
	/**
	 * 
	 * @Title getFieldValueByName
	 * @Description 根据属性名获取属性值
	 * @param fieldName
	 * @param o
	 * @return 返回类型 Object
	 */
	public static Object getFieldValueByName(String fieldName,Object o) {
	        try {
	            String firstLetter = fieldName.substring(0, 1).toUpperCase();
	            String getter = "get" + firstLetter + fieldName.substring(1);
	            Method method = o.getClass().getMethod(getter, new Class[] {});
	            Object value = method.invoke(o, new Object[] {});
	            return value;
	        } catch (Exception e) {
	            return null;
	        }
	}
	
	/**
	 * 
	 * @Title listToTree
	 * @Description List结构转变为树结构
	 * @param list
	 * @return 返回类型 List<Map<String,Object>>
	 */
	public static List<Map<String, Object>> listToTree(List<Map<String, Object>> list) {
		String pk = "id"; 
		String pid = "pid"; 
		String child = "children";
		String root = "0";
		List<Map<String, Object>> tree = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> refer = new HashMap<String,Map<String, Object>>();
		for (Map<String, Object> map : list) {
			refer.put(map.get(pk).toString(),map);
		}
		
		for (Map<String, Object> map : list) {
			if (map.get(pid).toString().equals(root)) {
				tree.add(map);
			}else{
				Map<String, Object> parent = refer.get(map.get(pid).toString());
				if (null != parent) {
					List<Map<String, Object>> children = ((List<Map<String, Object>>)parent.get(child));
					if (children == null) {
						children = new ArrayList<Map<String, Object>>();
						refer.get(map.get(pid).toString()).put(child, children);
					}
					children.add(map);
				}

			}
		}
		
		return tree;
	}
	
//	
//	public static List<Map<String, Object>> formatTree(List<Map<String, Object>> list,int lv,String title){
//		List<Map<String, Object>> formatTree = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object>  map : list) {
//			String title_prefix = "";
//			for (int i = 0; i < lv; i++) {
//				title_prefix += "|---";
//			}
//			map.put("lv", lv);
//			map.put("namePrefix", lv == 0 ? "" : title_prefix);
//			map.put("showName",  lv == 0 ? map.get(title) : title_prefix + map.get(title));
//			if(!map.containsKey("_child")){
//				formatTree.add(map);
//			}else{
//				List<Map<String, Object>> child = (List<Map<String, Object>>)map.get("_child");
//			    map.remove("_child");
//		        formatTree.add(map);
//		        List<Map<String, Object>> middle = formatTree(child, lv+1, title);
//		        formatTree = mergeTwoList(formatTree, middle);
//			}
//		}
//	    return formatTree;
//	}
//	
//	public static List<Map<String, Object>> mergeTwoList(List<Map<String, Object>> listOne, List<Map<String, Object>> listTwo) {
//	    for(Map<String, Object> num : listTwo) {
//	        if(!listOne.contains(num)) {
//	            listOne.add(num);
//	        }
//	    }
//	    return listOne;
//	}
}
