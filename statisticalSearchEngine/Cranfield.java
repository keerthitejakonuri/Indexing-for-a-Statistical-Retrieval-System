import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
public class Cranfield {
	private static File cfFolder = null;
	private static int noOfFiles = 0;
	private static int noOfTokens = 0;
	private static HashMap<String, Integer> word_freq_Map = new HashMap<String, Integer>();
	public static void main(String[] args) throws IOException {
		//for execution time calculation
		long startTime = System.currentTimeMillis();
		//Create object for the CranField Class
		Cranfield cf = new Cranfield();
		//get the CranField folder
      String path = "K:" + File.separator + "Master's" + File.separator + "2ndSEM"+ File.separator + "Information Retrieval" + File.separator + "Projects" + File.separator + "project1" + File.separator + "cranfielddatabase";
     //String path = args[0];
		cfFolder = cf.getCFfolder(path);
		//count the no of files
		noOfFiles = cf.getNoOfFiles();
		System.out.println("Total No Of Files: " + noOfFiles);
		//parse the files
		cf.parseFiles();
		System.out.println("No of Tokens: " + noOfTokens);
		System.out.println("No of Words occured only once: " + cf.calcSingleFreqWords());
		System.out.println("No of Words Unique are:" + word_freq_Map.size());
		System.out.println("The average number of word tokens per document: "+ noOfTokens/noOfFiles);
		//print the top 30 words
		cf.printTopThirtyWords();
		// write output to a file
		cf.printWordsToFile(word_freq_Map);
		long endTime = System.currentTimeMillis();
        long totalTimeTakenToFetchResults = endTime-startTime;
        System.out.println("Total Time Taken To Fetch Results: " + totalTimeTakenToFetchResults + " ms");
	}
	
	private File getCFfolder(String filePath){
		File folder = null;    
		folder = new File(filePath);//Cranfield
		return folder;
	}
	
	private int getNoOfFiles() {
		return cfFolder.list().length;
	}
	
	private void parseFiles() throws IOException {
		FileReader fReader;
		for(File file: cfFolder.listFiles()) {
			fReader = new FileReader(file);
			BufferedReader br=new BufferedReader(fReader);
			String word;
			String token;
			StringTokenizer st;
			while((word=br.readLine())!=null) {
				word = word.replaceAll("\\<.*?>"," ");
				st= new StringTokenizer(word, " \n\t,:;?{}()[]+ ");
				while(st.hasMoreTokens()) {
					token=st.nextToken();
					parseTokens(token);
				}
			}
			br.close();
		}
	}
	
	private void parseTokens(String word) {
		word = word.trim();
		if(!word.equals(null) && !word.equals("")){
			word=word.toLowerCase();
			if(word.contains("-")) {
				handleHyphen(word);
			}
			else if(word.contains("'")) {
				handlePossessives(word);
			}
			else if(word.contains(".")) {
				handlePunctuation(word);
			}
			else{
				storeToken(word);
			}
		}
	}
	private void handlePunctuation(String word) {
		if(word.length()==1) {
			return;
		}
		else if(word.indexOf(".")==word.length()-1) {
			word = word.substring(0, word.length()-2);
			storeToken(word);
		}
		else{
			word.replace(".","");
		}
	}
	private void handlePossessives(String word) {
		if(word.indexOf("'")==word.length()-1 && word.indexOf("'")== 0) {
			word=word.replaceAll("'", "");
		}
		else if(word.indexOf("'")==word.length()-2) {
			word = word.substring(0, word.length()-3);
		}
		storeToken(word);
	}
	private void handleHyphen(String word) {
		String [] array;
		array=word.split("-");
		for(int i=0;i<array.length; i++) {
			if(array[i].length()>1) {
				storeToken(array[i]);
			}
		}
	}
	private void storeToken(String word) {
		if(!word.equals("")){
			if(word_freq_Map.containsKey(word)) {
				word_freq_Map.put(word, word_freq_Map.get(word)+1);
			} 
			else {
				word_freq_Map.put(word,1);
			}
			noOfTokens++;
		}
	}
	
	private  int calcSingleFreqWords() {
		int singleFreqWords = 0;
		for(String key: word_freq_Map.keySet()) {
			if(word_freq_Map.get(key)==1){
				singleFreqWords++;
			}
		}
		return singleFreqWords;
	}
	
	private void printTopThirtyWords() {
		System.out.println("Top 30 frequent words: ");
		HashMap<String, Integer> newmap = word_freq_Map;
        for(int i=0; i<newmap.size(); i++) {            
            Map.Entry<String, Integer> max = null;  
            for (Map.Entry<String, Integer> entry : newmap.entrySet()) {
                if (max == null || entry.getValue().compareTo(max.getValue()) > 0) {
                    max = entry;
                }
            }
            System.out.println(max.getKey()+": "+max.getValue());
            newmap.remove(max.getKey());
        }
	}
	
	public void printWordsToFile(HashMap<String,Integer> input) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter("AllTokens.txt"));
		PrintWriter out2 = new PrintWriter(new FileWriter("Frequencies.txt"));
		for(int i=0;i<input.size();i++)
		{
			out.println(input.keySet().toArray()[i]);
			out2.println(input.get(input.keySet().toArray()[i].toString()));
		}
		
	}
}