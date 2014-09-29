package common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SSHUtils {
	
	public static String runLinuxCMD(String... cmd){
		String result="";
		try {
			List<String> cmds = new ArrayList<String>(); 
			cmds.add("/bin/sh"); 
			cmds.add("-c"); 
			cmds.addAll(Arrays.asList(cmd));
			ProcessBuilder pb=new ProcessBuilder(cmds); 
			Process p = pb.start();
			result=IOUtils.toString(p.getInputStream());
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void startGameServer(String cmd,String interrupt){
		try {
			List<String> cmds = new ArrayList<String>(); 
			cmds.add("/bin/sh"); 
			cmds.add("-c"); 
			cmds.add(cmd);
			ProcessBuilder pb=new ProcessBuilder(cmds); 
			Process p = pb.start();
			
			String temp="";
			BufferedReader br=new BufferedReader(new InputStreamReader(p.getInputStream()));
			while((temp=br.readLine())!=null){
				if(StringUtils.containsIgnoreCase(temp, interrupt)){
					break;
				}
			}
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String runWindowsCMD(String cmd){
		String result=null;
		try {
			List<String> cmds = new ArrayList<String>(); 
			cmds.add("cmd.exe"); 
			cmds.add("-c"); 
			cmds.add(cmd); 
			ProcessBuilder pb=new ProcessBuilder(cmds); 
			Process p = pb.start();
			result=IOUtils.toString(p.getInputStream());
			p.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
