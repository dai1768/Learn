package com.fly.learn.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FindProp {

	
	public static List<String> readFile(String filename) {
		filename = "C:\\Users\\FlyWeight\\Documents\\mysql\\计算脚本2.csv";
		File f = new File(filename);
		BufferedReader br = null;
		
		List<String> list = new LinkedList<String>();
		try {
			br = new BufferedReader( new InputStreamReader(new FileInputStream(f),"utf-8"));
			String item = "";
			while ((item = br.readLine()) != null) {
				if(org.apache.commons.lang3.StringUtils.isNotBlank(item)){
				//	System.out.println(item);
					list.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null){
					br.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	static String classPattern  = "selectObjectType\\((.*)\\.class\\)";
	static String fieldPattern  = "\\.get([a-zA-Z]*\\S*)\\(+\\s*\\)";
	static  String match(String src,String regxStr,boolean isLowCase){
		Pattern pattern = Pattern.compile(regxStr);
		String str = "getInstance(\"\"24小时卖家累计交易资金\"\").contains(order.getColCustUserId());   obj).getColCustUserId()ada|a.getAdd();    elect(Object obj) {            return ((DSPayOrder) obj).getTransTime();        }    }).method(new Method() {        public Mergeable invoke(Object obj) {            DSPayOrder order = (DSPayOrder) obj;            return new SumNumber(order.getTransAmount());    ";
		Matcher matcher = pattern.matcher(src);
		StringBuffer buffer = new StringBuffer();
		Set<String> st = new HashSet<>();
		while (matcher.find()) {
			String e = matcher.group(1);
			String s = "";
			if(isLowCase){
				s= e.substring(0,1).toLowerCase() + e.substring(1);
			}else{
				s = e;
			}
			
			st.add(s);
		//	System.out.println(s);
		}
		
		
        return StringUtils.join(st, ",");
	}
	
	public static void main(String[] args) {
		List<String> lines = readFile("");
		String tmp = "";
		for (String line : lines) {
			String[] cols = line.split(",");
			
			String[] arr = new String[7];
			arr[6]= match(line,classPattern,false)+"(" + match(line,fieldPattern,true) + ")";
			System.arraycopy(cols, 0, arr, 0, 6);
			tmp = StringUtils.join(arr, ",");
			System.out.println(arr[6]);
		}

	}
}
