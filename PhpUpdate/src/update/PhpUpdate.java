package update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PhpUpdate {
	private String rootDir;
	private static int totalChange = 0;
	
	public static int getTotalChange() {
		return totalChange;
	}

	public static void setTotalChange(int totalChange) {
		PhpUpdate.totalChange = totalChange;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public PhpUpdate(String rootdir){
		this.rootDir = rootdir;
	}
	
	public void traverseRoot(){
		File root = new File(rootDir);
		traverseDir(root);
	}
	
	public void traverseDir(File dir){
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++){
			if (fs[i].isDirectory()){
				traverseDir(fs[i]);
			}else{
				if (isPhp(fs[i].getName()) && needUpdate(fs[i])){
					String tempPath = fs[i].getAbsolutePath();
					String tempName = tempPath.substring(0, tempPath.lastIndexOf("."))+".bak";
					tempName = tempName.replace("\\", "\\\\");
					File bakFile = new File(tempName);
					boolean ok = fs[i].renameTo(bakFile);
					if (ok){
						traverseFile(fs[i],bakFile);
						bakFile.delete();
					}
				}
			}
		}
	}
	
	public boolean isPhp(String fileName){
		String docType = fileName.substring(fileName.lastIndexOf(".")+1);
		if (docType.equals("php")){
			return true;
		}else{
			return false;
		}
	}
	
	public void traverseFile(File php, File bak){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(bak));
			BufferedWriter writer = new BufferedWriter(new FileWriter(php,true));
			String temp = null;
			String tempFunc = null;
			boolean hasChanged = false;
			int lineNumber = 0;
			int insideFileChange = 0;
			
			while ((temp = reader.readLine()) != null){
				lineNumber++;
				if (temp.contains("function") && temp.contains("(") && !temp.contains(")")){
						writer.write(temp+"\r\n");
					do{
						tempFunc = reader.readLine();
						lineNumber++;
						writer.write(tempFunc+"\r\n");
					}while (!tempFunc.contains(")"));
					continue;
				}
				
				if (needReplace(temp)){
					hasChanged = true;
					insideFileChange++;
					if (insideFileChange == 1){
						System.out.println(php.getAbsolutePath()+" started.");
					}
					System.out.println("Line:"+lineNumber+"\t"+temp);
					if (temp.contains("&$")){
						temp = temp.replace("&$", "$");
					}
					if (temp.contains("& $")){
						temp = temp.replace("& $", "$");
					}
					System.out.println("Updated:"+lineNumber+"\t"+temp);
					totalChange++;
				}
					writer.write(temp+"\r\n");
			}
			if (hasChanged){
				System.out.println(php.getAbsolutePath()+" finished.\r\n");
			}
			reader.close();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean needReplace(String str){
		boolean flag = false;
		if (!str.contains("function")){
			if ( (str.contains("&$") && !str.contains("&&$")) || (str.contains("& $") && !str.contains("&& $"))){
				flag = true;
				if (str.contains(" = &$")){
					flag = false;
				}
				if (str.contains(" =& $")){
					flag = false;
				}
				if (str.contains("array")){
					if (str.indexOf("array") < str.indexOf("&$")){
						flag = false;
					}
				}
			}
		}
		return flag;
	}
	
	public boolean needUpdate(File file){
		boolean needUpdateFlag = false;
		try {
			BufferedReader checkReader = new BufferedReader(new FileReader(file));
			String temp = null;
			String tempFunc = null;
			while ((temp = checkReader.readLine()) != null){
				if (temp.contains("function") && temp.contains("(") && !temp.contains(")")){
					do{
						tempFunc = checkReader.readLine();
					}while (!tempFunc.contains(")"));
					continue;
				}
				
				if (needReplace(temp)){
					needUpdateFlag = true;
					break;
				}
			}
			checkReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return needUpdateFlag;
	}
}
