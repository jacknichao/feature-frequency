package com.nichao.featurefrequency;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.*;
import java.util.*;

public class CalcFeatureFrequency {

	public static HashMap<String,String> featureIndex2Name=new HashMap<>();


	static {
		System.out.println("Init： featureIndex-->featureName");
		String enableDataset = MyTools.getBaseInfo("enableDataset").trim();
		String basepath = MyTools.getBaseInfo("basepath").trim();

		String fullPath=basepath+"/"+enableDataset;

		File oneProject=new File(fullPath).listFiles()[0];

		try {
			Instances instances= ConverterUtils.DataSource.read(oneProject.toString());
			for(int i =0;i<instances.numAttributes()-1;i++){
				featureIndex2Name.put(String.valueOf(i),instances.attribute(i).name());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Init： done");


	}
	public static void main(String[] args) {

		System.out.println("Starting to calculating:");
		String experimentBaseDirectory = MyTools.getBaseInfo("experimentBaseDirectory");

		String enableDataset = MyTools.getBaseInfo("enableDataset").trim();
		String experimentName = null;

		if (enableDataset.equals("Relink")) {
			experimentName = MyTools.getBaseInfo("experimentNameRelink");
		} else if (enableDataset.equals("PROMISE")) {
			experimentName = MyTools.getBaseInfo("experimentNamePROMISE");
		}

		//在randomIndex上计算出特征选择的频率
		int randomIndex = Integer.parseInt(MyTools.getBaseInfo("randomIndex"));

		int independentRuns = Integer.parseInt(MyTools.getBaseInfo("independentRuns"));

		String rootPath = null;
		String subDir = experimentName + "/data/NSGAII/";

		Scanner funIn = null;
		Scanner varIn = null;

		rootPath = experimentBaseDirectory + "-random-" + randomIndex + "/";

		String tmp = rootPath + subDir;

		//保存每一个集分类器运行时候各个特征选择的个数<特征索引，出现的次数>
		HashMap<String, Integer> hashMap = null;
		int totalCount = 0;

		//针对每一个基分类器
		for (String classifierName : MyTools.getBaseInfo("baseClassifier").split(",")) {
			System.out.println("classifier:\t" + classifierName);

			hashMap = new HashMap<>();
			totalCount = 0;

			//每一个问题文件
			for (File file : new File(tmp).listFiles()) {
				if (file.getName().endsWith(classifierName)) {
					String problemPath = file.toString();
					System.out.println("Statistic:\t" + file.getName());

					for (int i = 0; i < independentRuns; i++) {
						try {
							funIn = new Scanner(new File(problemPath + "/FUN." + i));
							varIn = new Scanner(new File(problemPath + "/VAR." + i));

							//FUN 和 VAR文件的行数是一样的
							while (funIn.hasNextLine()) {
								String fun = funIn.nextLine();
								String var = varIn.nextLine();

								if (fun.trim().equals("")) continue;

								//当前行选取了0个特征，这是不允许的因此需要过滤掉
								if (fun.split(" ")[0].trim().equals("0.0")) {
									continue;
								}

								if (analysisVar(var, hashMap))
									totalCount++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							funIn.close();
							varIn.close();

						}
					}//end for indenpendent*/
				}
			}//end for problem with special classifier


			saveResult(tmp+classifierName+"-FrequentlySelected.csv",hashMap,totalCount);
			System.out.println("completion for:\t" + classifierName);
		}//end for a classifier


	}


	/**
	 * 将每一个分类器的统计结果进行保存
	 *
	 * @param path
	 * @param hashMap
	 * @param totalCount
	 */
	public static void saveResult(String path, HashMap<String, Integer> hashMap,int totalCount) {
		List<Map.Entry<String, Integer>> infoIds =
				new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());

		//排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});

		PrintWriter printWriter=null;
		try {
			printWriter = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
			for (Map.Entry<String, Integer> entry : infoIds) {
				//找到对应的特征名称
				printWriter.println(featureIndex2Name.get(entry.getKey().trim())+","+entry.getValue()+","+totalCount+","+entry.getValue()/Double.valueOf(totalCount));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			printWriter.flush();
			printWriter.close();
		}

	}


	/**
	 * @param var     Var的特征字符串
	 * @param hashMap 已经累计的特征出现次数
	 * @return 表明是否分析成功
	 */
	private static boolean analysisVar(String var, HashMap<String, Integer> hashMap) {
		if (var == null || var.trim().equals(""))
			return false;

		String[] indexs = var.split(" ");

		//我们将特征的索引从0开始计数
		for (int i = 0; i < indexs.length; i++) {
			//当前特征被选择了
			if (indexs[i].trim().equals("1")) {
				hashMap.put(String.valueOf(i),
						hashMap.containsKey(String.valueOf(i)) ? hashMap.get(String.valueOf(i)) + 1 : 1);
			}

		}
		return true;


	}
}
