import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanforLemmatizer {

    protected StanfordCoreNLP pipeline;

    public StanforLemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        /*
         * This is a pipeline that takes in a string and returns various analyzed linguistic forms. 
         * The String is tokenized via a tokenizer (such as PTBTokenizerAnnotator), 
         * and then other sequence mod el style annotation can be used to add things like lemmas, 
         * POS tags, and named entities. These are returned as a list of CoreLabels. 
         * Other analysis components build and store parse trees, dependency graphs, etc. 
         * 
         * This class is designed to apply multiple Annotators to an Annotation. 
         * The idea is that you first build up the pipeline by adding Annotators, 
         * and then you take the objects you wish to annotate and pass them in and 
         * get in return a fully annotated object.
         * 
         *  StanfordCoreNLP loads a lot of models, so you probably
         *  only want to do this once per execution
         */
        this.pipeline = new StanfordCoreNLP(props);
    }
    
  static  ArrayList<String> al=new ArrayList<String>();
 static  TreeMap<String,TreeMap<Integer,Integer>> hm=new TreeMap<String,TreeMap<Integer,Integer>>();	 
	 static Map<Integer,MaxDocument> termFrequencyMapLemmaForDoc=new TreeMap<Integer,MaxDocument>();
	 static  TreeMap<Integer,TreeMap<Integer,Double>> QueryWeights=new TreeMap<Integer,TreeMap<Integer,Double>>();	
	 static  TreeMap<Integer,TreeMap<Integer,Double>> QueryWeights2=new TreeMap<Integer,TreeMap<Integer,Double>>();
	 static TreeMap<Integer,String> qToMap=new TreeMap<Integer,String>();
	 static HashMap<Integer,String> docToTile=new HashMap<Integer,String>();
		static int tokenCount,collectionsize,wordcount = 0;
    public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        String temp="";
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
            	temp=token.get(LemmaAnnotation.class);
            	if(!al.contains(temp)){
            	    lemmas.add(temp);
            	}
            
            }
        }
        return lemmas;
    }


    public static void main(String[] args) {
     //   System.out.println("Starting Stanford Lemmatizer");
        String text ;
        StanforLemmatizer slem = new StanforLemmatizer();
    	String sCurrentLine;
	File folder = new File("cranfielddatabase");
		//File folder = new File("K:/Master's/2ndSEM/Information Retrieval/Projects/project1/cranfielddatabase");
    	try{
    		stopWords();
    	//	System.out.println(al);
	//	
		File[] listOfFiles = folder.listFiles();BufferedReader br = null;
		Arrays.sort(listOfFiles);
		//System.out.println(listOfFiles);
		collectionsize=listOfFiles.length;
	  for (int i = 0; i < listOfFiles.length; i++) {
		br = new BufferedReader(
				new FileReader(listOfFiles[i]));
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(replaceSpecialCharacters(sCurrentLine));
			sb.append(System.getProperty("line.separator"));
		}
		 text=sb.toString();
		 LemmaGenerator(text,i,slem);
		}
	//  System.out.println(hm);
	  readTitle();
	  writeTermData();
	  readQuery();
	  

System.out.println("-----------------------------------------------------------------");
System.out.println("-----------------By Weighing schema W1---------------------------");
System.out.println("-----------------------------------------------------------------");
for (Map.Entry<Integer,TreeMap<Integer,Double>> entry : QueryWeights.entrySet())
{
	System.out.println( "For Query "+entry.getKey() +" "+qToMap.get(entry.getKey()));int count=0;
    TreeMap<Integer,Double> t =(TreeMap<Integer,Double>) entry.getValue();
    System.out.println("Rank"+"	"+"Doc id"+"    "+"Score"+"    		"+"Doc title");
    for(Map.Entry<Integer,Double> entry1 : t.entrySet()) {
    	count++;
    	if(count>5){
    		break;
    	}
    	System.out.println(count+"	"+entry1.getKey()+"    "+entry1.getValue()+"	"+docToTile.get(entry1.getKey()));
    }
    
}
System.out.println("-----------------------------------------------------------------");
System.out.println("-----------------By Weighing schema W2---------------------------");
System.out.println("-----------------------------------------------------------------");
for (Map.Entry<Integer,TreeMap<Integer,Double>> entry : QueryWeights2.entrySet())
{
	System.out.println( "For Query "+entry.getKey()+" "+qToMap.get(entry.getKey()));int count=0;
    TreeMap<Integer,Double> t =(TreeMap<Integer,Double>) entry.getValue();
    System.out.println("Rank"+"	"+"Doc id"+"    "+"Score"+"    		"+"Doc title");
    for(Map.Entry<Integer,Double> entry1 : t.entrySet()) {
    	count++;
    	if(count>5){
    		break;
    	}
    	System.out.println(count+"	"+entry1.getKey()+"    "+entry1.getValue()+"	"+docToTile.get(entry1.getKey()));
    }
    
}
    }
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    public  static void stopWords(){
    	BufferedReader br1=null;
    	try{
    	br1 = new BufferedReader(
    			new FileReader("stopwords"));
				//new FileReader("K:\\Master's\\2ndSEM\\Information Retrieval\\Projects\\project2\\stopwords")); 
    	String sCurrentLine;
    			
		while ((sCurrentLine = br1.readLine()) != null) {
			al.add(sCurrentLine);
		}
		br1.close();
    	}
    	catch(Exception e){
    		
    		e.printStackTrace();
    	}
    }
    
    public static void readTitle() {
		try {
			for(Integer i=1;i<1401;i++){
				String id = i.toString();
				switch(id.length())
				{
					case 1:id = "000" + id;
					break;
					case 2:id = "00" + id;
					break;
					case 3: id = "0" + id;
					break;
					case 4: id = id;
					break;
				}
			String filePath = "cranfielddatabase/cranfield"+id;
	//String filePath = "K:/Master's/2ndSEM/Information Retrieval/Projects/project1/cranfielddatabase/cranfield"+id;
			File fileName = new File(filePath );
			FileInputStream fileInputStream = new FileInputStream(fileName);
			byte[] bytesLength = new byte[(int)(long) fileName.length()];
			fileInputStream.read(bytesLength);
			fileInputStream.close();
			String docData = new String(bytesLength, "UTF-8");
			String title = docData.substring(docData.indexOf("<TITLE>"),
					docData.lastIndexOf("</TITLE>"));
			String formattedTitle = title.replace("\n", "").replace("\r", "");
			formattedTitle = formattedTitle.replaceAll("<TITLE>", "");
		//	System.out.println("     " + "Title: " + formattedTitle);
			docToTile.put(i, formattedTitle);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    public static String replaceSpecialCharacters(String sCurrentLine){
//    	sCurrentLine = sCurrentLine.replaceAll("\\<.*?>", ""); //remove sgml tags
//		sCurrentLine = sCurrentLine.replaceAll("\\/", " ");    //remove /
//		sCurrentLine = sCurrentLine.replaceAll("\\d+.*", ""); //remove digits
//		sCurrentLine = sCurrentLine.replaceAll("\\,", " ");   //remove ,
//		sCurrentLine = sCurrentLine.replaceAll("\\.", "");    //remove .
//		sCurrentLine = sCurrentLine.replaceAll("\\-", " ");    // remove -
//		sCurrentLine = sCurrentLine.toLowerCase();            //to lower case
//		sCurrentLine = sCurrentLine.replaceAll("\\'s", "");  // remove possessives
//		sCurrentLine = sCurrentLine.replaceAll("\\'", ""); 
//		sCurrentLine = sCurrentLine.replaceAll("\\(", ""); 
//		sCurrentLine = sCurrentLine.replaceAll("\\)", "");
//		sCurrentLine = sCurrentLine.replaceAll("\\*", "");
//		sCurrentLine = sCurrentLine.replaceAll("\\+", "");
//		sCurrentLine = sCurrentLine.replaceAll("\\=", "");
//		sCurrentLine = sCurrentLine.replaceAll("\\?", "");
    	
    	
    	sCurrentLine=sCurrentLine.toString().replaceAll("\\<.*?>",""); 
	      /* Clear the possessives 's  */
    	sCurrentLine=sCurrentLine.replaceAll("'s", ""); 
	     
	     /* Clear the special symbols other than - */
    	sCurrentLine=sCurrentLine.replaceAll("[+^:,?';=%#&~`$!@*_)/(}{]","");
	     
	     /* Clear - and replace it with space */
    	sCurrentLine=sCurrentLine.replaceAll("-","\t");
	     
	     /* Clear full stops and acronyms */
    	sCurrentLine=sCurrentLine.replaceAll("\\.", "");
	  
	     /* Clear numbers */
    	sCurrentLine=sCurrentLine.replaceAll("[0-9]+", ""); 
    	return sCurrentLine;
    	
    }
    
    
   
    
    public static void LemmaGenerator(String text,int j,StanforLemmatizer slem){
    	j++;
    	 TreeMap<String,Integer> termFrequencyMapLemma=new TreeMap<String,Integer>();
   	 List<String> l=slem.lemmatize(text);
     Iterator<String> it=l.iterator();//lemmatizor
     int numberOfTokens=0;
     Integer tf=1;
     while(it.hasNext()){
    	 numberOfTokens++;
    	 String key=(String)it.next();
    	 if(!al.contains(key)){
    	 if(hm.containsKey(key)){
    		 	TreeMap<Integer,Integer> h=hm.get(key); 
    		 	if(h.containsKey(j)){
    			h.put(j, (h.get(j)+1));
    			hm.put(key,h);
    			termFrequencyMapLemma.put(key,termFrequencyMapLemma.get(key)+tf );
    		 	}
    		 	else{
    		 		h.put(j, 1);
    		 		hm.put(key, h);
    		 		termFrequencyMapLemma.put(key,tf );
    		 	}
			 
    	 } 
    	 else{
    		 TreeMap<Integer,Integer> h=new TreeMap<Integer,Integer>();
    		 h.put(j, 1);
    		 hm.put(key, h);
    		 termFrequencyMapLemma.put(key,tf );
    	 }
     }
     } 
     
     ValueComparator bvc = new ValueComparator(termFrequencyMapLemma);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);
		sorted_map.putAll(termFrequencyMapLemma);
//		System.out.println("------------------------------ termfrequencymapstem_sorted-------------------");
//		System.out.println(sorted_map);
		 Entry<String,Integer> ent =sorted_map.firstEntry();
		MaxDocument md=new MaxDocument();
		 md.setMax_tf(ent.getValue());md.setMostFrequentStem(ent.getKey());
		 md.setDocumentId(j);md.setDoclen(numberOfTokens);wordcount=wordcount+numberOfTokens;
		 termFrequencyMapLemmaForDoc.put(j,md);
    }
    
  
    
   
    
    public static void writeTermData()
    {
    	try{
    	 File file1 = new File("maxTF_DocLen");
         Writer output1;
         output1 = new BufferedWriter(new FileWriter(file1));
         
         Iterator it = termFrequencyMapLemmaForDoc.entrySet().iterator(); 
         MaxDocument h=null;
         while (it.hasNext()) {
             Map.Entry<Integer,MaxDocument> pair = (Map.Entry<Integer,MaxDocument>)it.next();
             h=pair.getValue();
               output1.write("Doc : "+h.getDocumentId());output1.write("  Max_tf : "+h.getMostFrequentStem()+" "+h.getMax_tf());
               output1.write("  doclen : "+h.doclen);
               output1.write("\n");
    }
         
         output1.flush();output1.close();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	
  //  	System.out.println("-----------------maxTF_DocLen.txt created with frequency of the most frequent stem in each document(max_tf), and the total number of word occurrences in the document (doclen). ");
    }
    
    public static Double CalcWeight1(Integer tf, Integer maxtf, Integer df, Integer doclen) {
		Double Weight1 = 0.0;
		Weight1 = (0.4 + 0.6 * Math.log((double)tf + 0.5) / Math.log((double)maxtf + 1.0))
				* (Math.log((double)collectionsize / df) / Math.log((double)collectionsize));
		return Weight1;
	}

	public static Double CalcWeight2(Integer tf, Integer maxtf,Integer wordCt, Integer df, Integer doclen) {
		Double Weight2 = 0.0;
		Weight2 = (0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 * (doclen / (wordCt/collectionsize))))
				* Math.log((double)collectionsize / df) / Math.log((double)collectionsize));
		return Weight2;
	}    
	
	
	public static void readQuery()
	{
		BufferedReader br1=null;
		try{
			//stopWords();
		br1 = new BufferedReader(
	new FileReader("hw3.queries"));
				//new FileReader("K:\\Master's\\2ndSEM\\Information Retrieval\\Projects\\hw3.queries"));
                                
		int QuestionNumber=0;
				while (br1.ready())
				{
					br1.readLine();
					String Question="";
					while (br1.ready())
					{
						String sCurrentLine = br1.readLine();
						if (sCurrentLine.trim().length() == 0)
						{
							
							break;
						}
						else
						{
							Question = Question + sCurrentLine + " ";
						}
					}QuestionNumber++;
					//System.out.println("The Query is "+QuestionNumber+" . " + Question);
					
					StanfordLemmatizer alem=new StanfordLemmatizer();
					Question=replaceSpecialCharacters(Question);
					List<String> l=alem.lemmatize(Question);
					List<String> QuestionLemmas=new ArrayList<String>();
				     Iterator<String> it=l.iterator();
				     while(it.hasNext()){
				    	 String key=(String)it.next();
				    	 if(!al.contains(key)){
				    		 QuestionLemmas.add(key);
				    	 }
				     }
				     qToMap.put(QuestionNumber,QuestionLemmas.toString());
				    processQuery(QuestionLemmas,QuestionNumber); 
				    processQuery2(QuestionLemmas,QuestionNumber); 
				     
				    	 }
		br1.close();
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
	}
	
	
	public static void processQuery(List QuestionLemmas,Integer QuestionNumber){
		  TreeMap<Integer,Double> WeightForQueries=new TreeMap<Integer,Double>();
	//	System.out.println(QuestionLemmas);
		for (int i = 0; i < QuestionLemmas.size(); i++)
		{
			try
			{
				String text=(String)QuestionLemmas.get(i);
                                //System.out.println(text);
			TreeMap<Integer, Integer>	t=hm.get(text);
			if(t!=null){
				  Iterator it1 = t.entrySet().iterator();
			        while (it1.hasNext()) {
			            Map.Entry<Integer,Integer> pair = (Map.Entry<Integer,Integer>)it1.next();
			            int tf=pair.getValue();
                                    //System.out.println("Term Frequency is" + tf );
			            MaxDocument m = termFrequencyMapLemmaForDoc.get(pair.getKey());
			double weight =	CalcWeight1(tf, m.getMax_tf(), t.size(), m.getDoclen());
		//	System.out.println(weight);
			 	if(WeightForQueries.containsKey(pair.getKey())){
			 		WeightForQueries.put(pair.getKey(), (WeightForQueries.get(pair.getKey())+weight));
		 	}
		 	else{
		 		WeightForQueries.put(pair.getKey(), weight);
		 	}
			        }
			}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		ValueComparator1 bvc = new ValueComparator1(WeightForQueries);
		TreeMap< Integer,Double> sorted_map = new TreeMap<Integer,Double>(bvc);
		sorted_map.putAll(WeightForQueries);
		QueryWeights.put(QuestionNumber, sorted_map);
	//	hm.
	}
	
	
	public static void processQuery2(List QuestionLemmas,Integer QuestionNumber){
		  TreeMap<Integer,Double> WeightForQueries=new TreeMap<Integer,Double>();
			//	System.out.println(QuestionLemmas);
				for (int i = 0; i < QuestionLemmas.size(); i++)
				{
					try
					{
						String text=(String)QuestionLemmas.get(i);
					TreeMap<Integer, Integer>	t=hm.get(text);
					if(t!=null){
						  Iterator it1 = t.entrySet().iterator();
					        while (it1.hasNext()) {
					            Map.Entry<Integer,Integer> pair = (Map.Entry<Integer,Integer>)it1.next();
					            int tf=pair.getValue();
					            MaxDocument m=termFrequencyMapLemmaForDoc.get(pair.getKey());
					double weight=	CalcWeight2(tf, m.getMax_tf(), wordcount, t.size(), m.getDoclen());
				//	System.out.println(weight);
					 	if(WeightForQueries.containsKey(pair.getKey())){
					 		WeightForQueries.put(pair.getKey(), (WeightForQueries.get(pair.getKey())+weight));
				 	}
				 	else{
				 		WeightForQueries.put(pair.getKey(), weight);
				 	}
					        }
					}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				ValueComparator1 bvc = new ValueComparator1(WeightForQueries);
				TreeMap< Integer,Double> sorted_map = new TreeMap<Integer,Double>(bvc);
				sorted_map.putAll(WeightForQueries);
				QueryWeights2.put(QuestionNumber, sorted_map);
		
	}
}

class MaxDocument{
String mostFrequentStem;
int documentId;
int max_tf;
int doclen;
public String getMostFrequentStem() {
	return mostFrequentStem;
}
public void setMostFrequentStem(String mostFrequentStem) {
	this.mostFrequentStem = mostFrequentStem;
}
public int getDocumentId() {
	return documentId;
}
public void setDocumentId(int documentId) {
	this.documentId = documentId;
}
public int getMax_tf() {
	return max_tf;
}
public void setMax_tf(int max_tf) {
	this.max_tf = max_tf;
}
public int getDoclen() {
	return doclen;
}
public void setDoclen(int doclen) {
	this.doclen = doclen;
}

@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "	"+this.mostFrequentStem+"	"+this.max_tf+"		"+this.doclen;
	}

}

class ValueComparator implements Comparator<String> {

	Map<String,Integer> base;

	public ValueComparator(Map<String,Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}


class ValueComparator1 implements Comparator<Integer> {

	Map<Integer,Double> base;

	public ValueComparator1(Map<Integer,Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(Integer a, Integer b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}





