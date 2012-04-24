import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Merge {
	
	private ArrayList<String> compress = new ArrayList<String>();
	
	private String styleServerIP ;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Merge m = new Merge();

		int jsIndex = 0;
		int cssIndex = 0;
		
		// 读取cmd文件中的压缩信息
		if(args.length > 2){
			int length = args.length - 2;
			for(int i = 0 ; i < length ; i++){
				m.setCompress(args[i + 2]);
			}
		// 读取style来源的ip地址
		}
		m.setStyleServerIP(args[1]);
		
		System.out.println("你正在从" + m.getStyleServerIP() + "服务器合并代码");
		if(args[0].contains("css")){
			m.mergeCss(args[0],cssIndex);
			m.mergeJS(args[0].replaceFirst("css", "js"),jsIndex);
		}else if (args[0].contains("js")){
			m.mergeJS(args[0],jsIndex);
			m.mergeCss(args[0].replaceFirst("js", "css"),cssIndex);
		}else {
			System.out.println("Wrong path 1");
		}
		
	}
	public void makeNewTimeStampFiles(String path ,String timestamp){
		String parentPath = new File(path).getParentFile().getPath();
		String newPath = parentPath + "\\" + timestamp;
		if(new File(newPath).mkdir()){
			copyFiles(path,newPath);
			
			
		}else{
			System.out.println("you have new timestamp");
		}
		
		if(path.contains("css")){
			if(new File(newPath.replaceFirst("css", "js")).mkdir()){
				copyFiles(path.replaceFirst("css", "js"),newPath.replaceFirst("css", "js"));
			}
		}else if (path.contains("js")){
			if(new File(newPath.replaceFirst("js", "css")).mkdir()){
				copyFiles(path.replaceFirst("js", "css"),newPath.replaceFirst("js", "css"));
			}
		}
	}
	public void copyFiles(String path,String newPath){
		try {
			File file = new File(path);   
			if (file.isDirectory()) { // 如果是目录， 遍历所有子目录取出所有文件名   
				String[] filelist = file.list();   
				for (int i = 0; i < filelist.length; i++) {   
					if(!filelist[i].contains(".svn")){
						File oldFile = new File(path + "\\" + filelist[i]);
						FileInputStream fi = new FileInputStream(oldFile);
						InputStreamReader isr = new InputStreamReader(fi, "GBk");
						BufferedReader reader = new BufferedReader(isr);
						StringBuffer string = new StringBuffer();
						for (String line; (line = reader.readLine()) != null;) {
							string.append(line);
							string.append("\n");
						}
						reader.close();
						File newFile = new File(newPath + "\\" + filelist[i]);
						BufferedWriter ow = new BufferedWriter(new FileWriter(newFile));
						ow.write(string.toString());
						ow.flush();
						ow.close();
					}
					
				}   
			}   
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public String beDual(String s){
		if(s.length() < 2){
			s = "0" + s;
		}
		return s;
	}
	
	public void setCompress(String s){
		this.compress.add(s);
	}
	
	public String getCompress(int index){
		return this.compress.get(index);
	}
	
	public int getCompressLength (){
		return this.compress.size();
	}
	
	public String getStyleServerIP() {
		return styleServerIP;
	}
	public void setStyleServerIP(String styleServerIP) {
		this.styleServerIP = styleServerIP;
	}
	
	public void mergeCss (String path,int cssIndex){
		System.out.println("merge css");
		try {   
			Map<Integer, String> map = readfile(path, null);
			boolean flag = true;
			for(int i=0 ; i < map.size(); i++) {  
				StringBuffer str = new StringBuffer();
				BufferedReader reader = null;
				cssIndex = 0;
				try {
					// TODO Auto-generated method stub
					File a = new File(map.get(i));
					if(a.exists()){
						FileInputStream fi = new FileInputStream(a);
						InputStreamReader isr = new InputStreamReader(fi, "GBk");
						reader = new BufferedReader(isr);
		            }else{
		            	System.out.println("file not exit");
		            }
					for (String line; (line = reader.readLine()) != null;) {
						if(line.contains("/* Merged */")) return ;
						if(line.contains("/*")){
							flag = false;
						}
						
						if(line.contains("import") && flag){
							if(cssIndex == 0){
								str = this.getMergeInfo(str);						
							}
							cssIndex++;
							int start = line.indexOf("\"");
							int end = line.indexOf("\"", start+1);
							String cssUrl = line.substring(start+1, end);
							str.append(getPageContent(cssUrl));
							
						}
						if(flag == false && line.contains("*/")){
							flag = true;
						}
					}
					writeFile(str,map.get(i));
					reader.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}  
		}catch (Exception ex) {
			System.out.println("wrong css path");
		} 
	}
	public void mergeJS (String path,int jsIndex){
		System.out.println("merge js");
		try {
			Map<Integer, String> map = readfile(path, null);
			for(int i=0 ; i < map.size(); i++) {
				boolean flag = true;
				StringBuffer str = new StringBuffer();
				BufferedReader reader = null;
				jsIndex = 0;
				try {
					File a = new File(map.get(i));
					if(a.exists()){
						FileInputStream fi = new FileInputStream(a);
						InputStreamReader isr = new InputStreamReader(fi, "GBk");
						reader = new BufferedReader(isr);
		            }else{
		            	System.out.println("file not exit");
		            }
					for (String line; (line = reader.readLine()) != null;) {
						if(line.contains("/* Merged */")){
							return;
						}
						if(line.contains("/*")){
							flag = false;
						}
						
						if(!line.startsWith("//") && line.contains("ImportJavscript") && flag){
							if(jsIndex == 0){
								str = this.getMergeInfo(str);
							}
							jsIndex++;
							int start = line.indexOf("\"");
							String jsUrl;
							if(start == -1){
								int start1 = line.indexOf("\'");
								if(start1 == -1) continue;
								int end1 = line.indexOf("\'", start1+1);
								jsUrl = line.substring(start1+1, end1);
								str.append(getPageContent(jsUrl));
							}else{
								int end = line.indexOf("\"", start+1);
								jsUrl = line.substring(start+1, end);
								str.append(getPageContent(jsUrl));
							}
						}
						if(flag == false && line.contains("*/")){
							flag = true;
						}
					}
					writeFile(str,map.get(i));
					reader.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}  
		}catch (Exception ex) {
			System.out.println("wrong js path");
		} 
	}
	public Map<Integer, String> readfile(String filepath, Map<Integer, String> pathMap) throws Exception {  
		if (pathMap == null) {  
			pathMap = new HashMap<Integer, String>();  
		}  
		
		File file = new File(filepath);   
			if (!file.isDirectory()) {   
				pathMap.put(pathMap.size(), file.getPath());  
			} else if (file.isDirectory()) { // 如果是目录， 遍历所有子目录取出所有文件名   
				String[] filelist = file.list();   
				for (int i = 0; i < filelist.length; i++) {   
					File readfile = new File(filepath + "/" + filelist[i]);
					System.out.println(filepath + "/" + filelist[i]);
					if (!readfile.isDirectory()) {  
						pathMap.put(pathMap.size(), readfile.getPath());  
					} 
				}   
			}   
		return pathMap;   
	} 
	public StringBuffer getPageContent(String targetUrl){
		StringBuffer str = new StringBuffer();
		try {
			// TODO Auto-generated method stub
			URL url = new URL(targetUrl.replace("style.china.alibaba.com",this.getStyleServerIP()));
			InputStream response = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(response));
			int s;
			while((s = reader.read())!=-1){
			    str.append((char)s);
			}
			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return str;
	}
	public void writeFile(StringBuffer str,String name) throws IOException{
		try {
			File file = new File(name);
			BufferedWriter ow = new BufferedWriter(new FileWriter(file));
			//Writer writer = new OutputStreamWriter(new FileOutputStream(file), "GBK"); 
			//BufferedWriter ow = new BufferedWriter(writer);
			ow.write(str.toString());
			System.out.println("you changed :" + name);
			ow.flush();
			ow.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	public StringBuffer getMergeInfo(StringBuffer str){
		str.append("/* Merged */\n");
		if(this.getCompressLength() > 0){
			for (int j = 0 ; j < this.getCompressLength() ; j++){
				str.append("/* " + this.getCompress(j) +  " */\n");
			}
		}
		return str;
	}
}
