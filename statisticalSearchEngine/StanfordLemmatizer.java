import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordLemmatizer {

    protected StanfordCoreNLP pipeline;

    public StanfordLemmatizer() {
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
        System.out.println("Starting Stanford Lemmatizer");
        String text = "";
        StanfordLemmatizer slem = new StanfordLemmatizer();
    	String sCurrentLine;
		File folder = new File("K:/Master's/2ndSEM/Information Retrieval/Projects/project1/cranfielddatabase");
		File folder1 = new File("K:/Master's/2ndSEM/Information Retrieval/Projects/project2/stopwords");
	
	//	File folder = new File("C:\\Users\\Kiran\\Desktop\\study\\spring2015\\ir\\Cranfield\\ram\\");
    	try{
    		BufferedReader br1 = new BufferedReader(
    				new FileReader(folder1));
    		while ((sCurrentLine = br1.readLine()) != null) {
    			al.add(sCurrentLine);
    		}
    	//	System.out.println(al);
    		
	//	File folder = new File("/people/cs/s/sanda/cs6322/Cranfield");
		File[] listOfFiles = folder.listFiles();BufferedReader br = null;
	HashMap<String,HashMap<Integer,Integer>> hm=new HashMap<String,HashMap<Integer,Integer>>();
	HashMap<String,HashMap<Integer,Integer>> hmStem=new HashMap<String,HashMap<Integer,Integer>>();
		int numOfFiles=listOfFiles.length;
		  long time1=System.currentTimeMillis();
	  for (int i = 0; i < listOfFiles.length; i++) {
		br = new BufferedReader(
				new FileReader(listOfFiles[i]));
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sCurrentLine = sCurrentLine.replaceAll("\\<.*?>", ""); //remove sgml tags
			sCurrentLine = sCurrentLine.replaceAll("\\/", " ");    //remove /
			sCurrentLine = sCurrentLine.replaceAll("\\d+.*", ""); //remove digits
			sCurrentLine = sCurrentLine.replaceAll("\\,", " ");   //remove ,
			sCurrentLine = sCurrentLine.replaceAll("\\.", "");    //remove .
			sCurrentLine = sCurrentLine.replaceAll("\\-", " ");    // remove -
			sCurrentLine = sCurrentLine.toLowerCase();            //to lower case
			sCurrentLine = sCurrentLine.replaceAll("\\'s", "");  // remove possessives
			sCurrentLine = sCurrentLine.replaceAll("\\'", ""); 
			sCurrentLine = sCurrentLine.replaceAll("\\(", ""); 
			sCurrentLine = sCurrentLine.replaceAll("\\)", "");
			sb.append(sCurrentLine);
			sb.append(System.getProperty("line.separator"));
		}
		 text=sb.toString();
		 Integer j=i;
	     //   System.out.println(slem.lemmatize(text));
	        List<String> l=slem.lemmatize(text);
			StringTokenizer itr = new StringTokenizer(sb.toString()); 
			String token;
			
			
			
	     Iterator<String> it=l.iterator();
	     while(it.hasNext()){
	    	 String key=(String)it.next();
	    	 if(hm.containsKey(key)){
	    		 HashMap<Integer,Integer> h=hm.get(key); 
	    		 if(h.containsKey(j)){
	    			h.put(j, (h.get(j)+1));
	    			hm.put(key,h);
	    		 }
	    		 else{
		    		 h.put(j, 1);
		    		 hm.put(key, h);
	    		 }
	    	 } 
	    	 else{
	    		 HashMap<Integer,Integer> h=new HashMap<Integer,Integer>();
	    		 h.put(j, 1);
	    		 hm.put(key, h);
	    	 }
	     }
	   
		}
//	  System.out.println(hm);
	  System.out.println(hmStem);
     
    }
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }

}