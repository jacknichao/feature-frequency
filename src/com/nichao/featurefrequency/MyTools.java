package com.nichao.featurefrequency;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class MyTools {
	private static HashMap<String,String> hashMap=null;

	static {
		if(hashMap==null) hashMap=new HashMap<>();
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("config.properties"));

			for(String pName: prop.stringPropertyNames()){
				hashMap.put(pName,prop.get(pName).toString());
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据指定的属性名称来找到对应的属性值
	 * @param key 属性名
	 * @return
	 */
	public static String getBaseInfo(String key){
		if(hashMap.containsKey(key)){
			return hashMap.get(key);
		}else
			return null;

	}


	/**
	 * 根据给定的项目名称，找到改项目下的所有工程
	 * @param datasetName
	 * @return
	 */
	public static ArrayList<File> getProjects(String datasetName){
		ArrayList<File> projects=new ArrayList<>();
		String url=hashMap.get("basepath")+"/"+datasetName+"/";
		File dir=new File(url);

		for(File file: dir.listFiles()){
			if(file.isFile())
				projects.add(file);

		}

		return projects;


	}

	private static void outputAll(){
		for(Map.Entry<String,String> entry:hashMap.entrySet()){
			System.out.println(entry.getKey()+"-->"+entry.getValue());
		}

	}


	public static void main(String[] args){
		MyTools.outputAll();

//		ArrayList<File> projects=MyTools.getProjects("AEEEM");
//		for(File f:projects){
//			System.out.println(f.toString());
//		}

		ArrayList<Integer> arrayList=new ArrayList<>();
		arrayList.add(1);
		arrayList.add(90);
		arrayList.add(15);

		System.out.println(Arrays.toString(arrayList2Arr(arrayList)));


	}


	public static int[] arrayList2Arr(ArrayList<Integer> arrayList){
		if(arrayList==null|| arrayList.size()==0){
			return null;
		}

		int[] arr=new int[arrayList.size()];
		for(int i=0;i<arrayList.size();i++){
			arr[i]=arrayList.get(i);
		}
		return arr;
	}

	/**
	 * 根据VAR文件的特征选择标记 1 0来找到被选择出来的特征
	 * @return
	 */
	public static int[] findToDeletedFeature(String line){

		ArrayList<Integer> arrayList = new ArrayList<>();
		String[] infos = line.split(" ");
		for (int j = 0; j < infos.length; j++) {
			if (infos[j].trim().equals("0")) {
				arrayList.add(j);
			}
		}

		if(arrayList.size()==0) return null;
		else{
			return arrayList2Arr(arrayList);
		}

	}
}
