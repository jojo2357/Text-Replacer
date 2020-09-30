import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main{

	private static int changesMade = 0;
	private static int filesInspected = 0;
	private static boolean regExMode;

	/* args:
	-r <replace string> (-s/-x) <string or regex, repectively, to use> @optional -i <file type (include '.')>
	*/
	public static void main(String[] args){
		if (args.length == 0){
			System.out.println("LEARN ARGS DAMMIT");
			throw new IllegalArgumentException("Ur a dummy hed. Learn args");
		}
		String argLine = "";
		for (String str : args){
			argLine += str + " ";
		}
		if (!(argLine.contains("-s") ^ argLine.contains("-x")))
			throw new IllegalArgumentException("Cant do regex and simple search");
		regExMode = argLine.contains("-x");
		System.out.println(regExMode);
		System.out.println(3 + Math.max(argLine.indexOf("-s"), argLine.indexOf("-x")) + " " + (argLine.length() - (argLine.contains("-i") ? argLine.indexOf("-i") + 3: argLine.length())));
		String replace = argLine.substring(argLine.indexOf("-r") + 3, Math.max(argLine.indexOf("-s"), argLine.indexOf("-x")) - argLine.indexOf("-r") - 1).trim();
		String find = argLine.substring(3 + Math.max(argLine.indexOf("-s"), argLine.indexOf("-x")), 3 + Math.max(argLine.indexOf("-s"), argLine.indexOf("-x"))+ argLine.length() - (argLine.contains("-i") ? argLine.indexOf("-i") + 3: argLine.length())).trim();
		String include = argLine.contains("-i") ? argLine.substring(argLine.indexOf("-i") + 3).trim() : "";
		findFiles(System.getProperty("user.dir"), find, replace, include);
		System.out.println(filesInspected + " |" + find + "|" + replace + "|" + include + "|");
	}

	private static void findFiles(String absolutePath, String find, String replace, String filter){
		File thisFolder = new File(absolutePath);
		String[] directories = thisFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		for (String str : directories){
			findFiles(absolutePath + "\\" + str, find, replace, filter);
		}
		directories = new String[0];
		String[] files = thisFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return !(new File(current, name).isDirectory()) && (filter.equals("") || name.substring(name.length() - filter.length()).equals(filter));
			}
		});
		for (String str : files){
			replaceText(new File(absolutePath + "\\" + str), find, replace);
		}
	}

	private static void replaceText(File file, String find, String replace){
		filesInspected++;
		if (thisFileContains(file, find, replace))
			System.out.println(file.getAbsolutePath() + " : " + (++changesMade));
	}

	private static boolean thisFileContains(File file, String find, String replace){
		try{
		Scanner reader = new Scanner(file);
		String fileText = "";
		while(reader.hasNextLine()){
			fileText += reader.nextLine() + "\n";
		}
		reader.close();
		boolean returnVal = regExMode ? Pattern.compile(find).matcher(fileText).find() : fileText.contains(find);
		System.out.println(find + " " + replace + " " + returnVal);
		System.out.println(fileText);
		if (returnVal){
			fileText = fileText.replaceAll(find, replace);
			FileWriter outWriter = new FileWriter(file);
			outWriter.append(fileText);
			outWriter.close();
		}
		return returnVal;
		}catch(Exception e){}
		return false;
	}
}