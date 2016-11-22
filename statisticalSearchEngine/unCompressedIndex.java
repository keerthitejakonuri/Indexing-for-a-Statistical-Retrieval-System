/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Properties;
/**
 *
 * @author Keerthi Teja Konuri
 */
public class unCompressedIndex {

    /**
     * @param args the command line arguments
     */
    TreeMap<String,Set<Integer>> tokens=new TreeMap();
    TreeMap<String,Set<Integer>> stems=new TreeMap();
    TreeMap<String,Set<Byte>> compressedtokens=new TreeMap();
    TreeMap<String,Set<Byte>> compressedstems=new TreeMap();
    TreeMap<String,Integer> tfreq=new TreeMap();
    TreeMap<String,Integer> sfreq=new TreeMap();
    TreeMap<Integer,Frequency> maxtf_doclen=new TreeMap();
    TreeMap<Integer,Frequency> stemmaxtf_doclen=new TreeMap();
    TreeMap<Integer,Frequencycompressed> maxtf_doclencmp=new TreeMap();
    TreeMap<Integer,Frequencycompressed> stemmaxtf_doclencmp=new TreeMap();
    TreeMap<String,TreeMap<Integer,Integer>> tf_doc=new TreeMap();
    TreeMap<String,TreeMap<Integer,Byte>> tf_doccmp=new TreeMap();
    TreeMap<String,TreeMap<Integer,Integer>> sf_doc=new TreeMap();
    TreeMap<String,TreeMap<Integer,Byte>> sf_doccmp=new TreeMap();
    LinkedHashSet<String>compressedversion1=new LinkedHashSet();
    LinkedHashSet<String>compressedversion2=new LinkedHashSet();
    long starttimeuncmpv1,starttimeuncmpv2,endtimeuncmpv1,endtimeuncmpv2;
    public static StanfordCoreNLP pipeline;
    Set<Integer>s1=new TreeSet();
    int totaltokens=0,uniquewords=0,sum=0;
    int uniquestems=0;
    int flag=0,flags=0,flagtf=0,flagt=0;
    public void readFile(){
        
        String line,swords,cwords;
        String[] filename=new String[1400];
        int oncewords=0;
        ArrayList<String>words=new ArrayList();
        ArrayList<String>stopwords=new ArrayList();
        ArrayList<String>commonwords=new ArrayList();
        for(int i=1;i<=1400;i++){
            if(1<=i&&i<10)
                filename[i-1]="cranfield"+"000"+i;
            else if(9<i&&i<100)
                filename[i-1]="cranfield"+"00"+i;
            else if(99<i&&i<1000)
                filename[i-1]="cranfield"+"0"+i;
            else
                filename[i-1]="cranfield"+i;
        }
        try{
            BufferedReader b=new BufferedReader(new FileReader("stopwords"));
            while((swords=b.readLine())!=null){
                stopwords.add(swords);
            }
            BufferedReader b1=new BufferedReader(new FileReader("common_words"));
            while((cwords=b1.readLine())!=null){
                commonwords.add(cwords);
                
            }
            //System.out.println("common words="+commonwords);
            //System.out.println("stop words are"+stopwords);
            for(int i=0;i<1400;i++){
                BufferedReader br=new BufferedReader(new FileReader("Cranfield/"+filename[i]));
                while((line=br.readLine())!=null){
                    line=line.replaceAll("<.*>","");
                    line=line.replaceAll("\\d*","");
                    line=line.replaceAll("\\.", "");
                    line=line.replaceAll("[^a-zA-Z ]","");
                    line=line.replaceAll("^\\s*$","");
                    //line=line.replaceAll("( )+"," "); 
                    line=line.toLowerCase();
                    words.addAll(Arrays.asList(line.split(" ")));
                    words.removeAll(stopwords);
                    words.removeAll(commonwords);
                }
                //System.out.println("1");
                words.removeAll(Arrays.asList("", null));
                totaltokens=totaltokens+words.size();
                //System.out.println("2");
                postings(words,i);
                
                //System.out.println("22");
                //
                //System.out.println("222");
                max(words,(i+1),1);
                stemsindoc(words,(i+1));
                //System.out.println("3");
                words.removeAll(words);
                //System.out.println("4");
            }
        } 
        catch(Exception e){
            System.out.println("error is"+e.getMessage());
        }
       // uniquewords=tokens.size();
       //System.out.println("Tokens and postings="+tokens);
       //System.out.println("total frequency of words in all docs="+tfreq);
    }
    public static  void  StanfordLemmatizer() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
       
    }
    public static String lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // Create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        pipeline.annotate(document);
        // Iterate over all of the sentences found
        String temp="";
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
            	temp=token.get(LemmaAnnotation.class);
            	    lemmas.add(temp);
            
            }
        }
        return lemmas.get(0).toString();
        //System.out.println("lemmas:"+lemmas.get(0));
    }
    public void postings(ArrayList<String> words, int fnum){
        String lemma="";
        for(int i=0;i<words.size();i++){
            lemma=lemmatize(words.get(i));
            //System.out.println("5");
            if(flag==0){
                //System.out.println("6");
                s1.add(fnum+1);
                //System.out.println("s="+s1);
                
                tokens.put(lemma,s1);
                tfreq.put(lemma,1);
                //System.out.println("7");
                flag=1;
            }
            else{
                Set<Integer>s2=new TreeSet();
                //System.out.println("8");
                if(!tokens.containsKey(lemma)){
                    //System.out.println("9");
                    s2.add(fnum+1);
                    tokens.put(lemma,s2);
                    tfreq.put(lemma,1);
                    //System.out.println("10");
                }
                else{
                    //System.out.println("11");
                    Set<Integer>s3=new TreeSet();
                    s3=tokens.get(lemma);
                    s3.add(fnum+1);
                    tokens.put(lemma,s3);
                    tfreq.put(lemma,tfreq.get(lemma)+1);
                }
            }
        }
        //System.out.println(tokens);
    }
    public void tf_doc(TreeMap<String,Integer> wordcount,int filenum){
        Set set = wordcount.entrySet();
        int docid=0;
        
        Iterator i = set.iterator();
        
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            TreeMap<Integer,Integer>freq=new TreeMap();
            //freq=tf_doc.get(me.getKey().toString());
            if(flagtf==0){
                //System.out.println("freq!=null");
                freq.put(filenum,1);
                //System.out.println("freq in tf_doc ="+freq);
                tf_doc.put(me.getKey().toString(),freq);
                //System.out.println("term frequency in each doc in first if="+tf_doc);
                flagtf=1;
            }
            else{
                if(tf_doc.get(me.getKey().toString())!=null){
                    freq=tf_doc.get(me.getKey().toString());
                    //System.out.println("freq in first else ="+freq);
                    if(freq.get(filenum)!=null){
                    //System.out.println("freq.get(filenum)!=null");
                    freq.put(filenum,freq.get(filenum)+1);
                    //System.out.println("freq in tf doc if="+freq);
                    tf_doc.put(me.getKey().toString(), freq);
                    //System.out.println("term frequency in each doc in first else="+tf_doc);
                    }
                    else{
                    //System.out.println("else of freq.get(filenum)!=null");
                    freq.put(filenum,1);
                    //System.out.println("freq in tf doc else="+freq);
                    tf_doc.put(me.getKey().toString(), freq);
                   // System.out.println("term frequency in each docin second else="+tf_doc);
                    
                    }
                }
                else{
                    freq.put(filenum, 1);
                    tf_doc.put(me.getKey().toString(), freq);
                            
                }
            }
            //System.out.println("term frequency in each docin second else="+tf_doc);
        }
        //System.out.println("term frequency in each doc="+tf_doc.get("nasa"));
    }
    public void sf_doc(TreeMap<String,Integer> wordcount,int filenum){
        Set set = wordcount.entrySet();
        int docid=0;
        
        Iterator i = set.iterator();
        
        while(i.hasNext()) {
            TreeMap<Integer,Integer>freq=new TreeMap();
            Map.Entry me = (Map.Entry)i.next();
            //freq=tf_doc.get(me.getKey().toString());
            if(flagt==0){
                //System.out.println("freq!=null");
                freq.put(filenum,1);
                //System.out.println("freq="+freq);
                sf_doc.put(me.getKey().toString(),freq);
                flagt=1;
            }
            else{
                if(sf_doc.get(me.getKey().toString())!=null){
                    freq=sf_doc.get(me.getKey().toString());
                    if(freq.get(filenum)!=null){
                    //System.out.println("freq.get(filenum)!=null");
                    freq.put(filenum,freq.get(filenum)+1);
                    //System.out.println("freq in tf doc if="+freq);
                    sf_doc.put(me.getKey().toString(), freq);
                    //System.out.println("term frequency in each doc in first else="+tf_doc);
                    }
                    else{
                    //System.out.println("else of freq.get(filenum)!=null");
                    freq.put(filenum,1);
                    //System.out.println("freq in tf doc else="+freq);
                    sf_doc.put(me.getKey().toString(), freq);
                   // System.out.println("term frequency in each docin second else="+tf_doc);
                    
                    }
                }
                else{
                    freq.put(filenum, 1);
                    sf_doc.put(me.getKey().toString(), freq);
                            
                }
            }
            
            //freq.clear();
        }
        //System.out.println("term frequency in each doc="+sf_doc);
    }
    public void blockedcompression(){
        Set terms1,doc;
        String documentterms="";
        Set<String> terms = compressedtokens.keySet();
        String termsArray[] = terms.toArray(new String[terms.size()]);
        List<Object> allmetrics=new ArrayList();
        //Set<Byte>temppostings=new TreeSet();
        int currentk=0,k=8;
        LinkedHashSet<Set<Byte>>postings=new LinkedHashSet();
        LinkedHashSet<TreeMap<Integer,Byte>> tfreq1=new LinkedHashSet();
        //LinkedHashMap<Byte,Frequencycompressed> maxtfdoclen=new LinkedHashMap();
        //TreeMap<Integer,Integer> temptf=new TreeMap();
        int docid=0,tf=0,j=0;
        terms1=compressedtokens.entrySet();
        Iterator i = terms1.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if(currentk<k){
                documentterms+=""+(me.getKey().toString()).length()+""+me.getKey().toString();
                postings.add(compressedtokens.get(me.getKey().toString()));
                tfreq1.add(tf_doccmp.get(me.getKey().toString()));
                
            }
            if(currentk==k|| (j + 1) == termsArray.length){
                //System.out.println("in block compressed postings="+postings);
                //allmetrics.add(postings);
                //System.out.println("in block compressed tfreq="+tfreq1);
                //allmetrics.add(tfreq1);
                //System.out.println("in block compressed max tf="+maxtf_doclencmp);
                //allmetrics.add(maxtf_doclencmp);
                compressedversion1.add(documentterms);//, allmetrics);
                currentk=0;
                documentterms="";
                postings.clear();
                tfreq1.clear();
            }
            currentk++;
            j++;
        }
        //System.out.println("compressed version1="+compressedversion1);
    }
    
    public String max(ArrayList<String> words,int filenum,int flag){
        int max=0;
        Porter p=new Porter();
        TreeMap<String, Integer> wordCount = new TreeMap();
        TreeMap<String, Integer> wordCountStem = new TreeMap();
        String stemtemp=new String();
        String maxkey=new String();
        
        for (String read : words) {
            Integer freq = wordCount.get(read);
            wordCount.put(read, (freq == null) ? 1 : freq + 1); //For Each word the count will be incremented in the Hashmap
        }
        /*for (String read : words) {
            Integer freq = wordCountStem.get(read);
            stemtemp=p.stripAffixes(read);
            wordCountStem.put(stemtemp, (freq == null) ? 1 : freq + 1);
        }*/
        //System.out.println("tokensabove in max");
        
        //System.out.println("stemsabove in max");
        
        //System.out.println("stemsabove in max after");
        Set set = wordCount.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            //System.out.println("key, value="+me.getKey()+" "+me.getValue());
            if(((Integer)me.getValue())>max){
                max=(Integer)me.getValue();
                maxkey=me.getKey().toString();
                //System.out.println("key, value="+max+" "+maxkey);
            }
        }
        //System.out.println("max key="+maxkey);
        if(flag==1){
            tf_doc(wordCount,filenum);
            Frequency freq=new Frequency(max,totaltokens);
            endtimeuncmpv1 = System.currentTimeMillis();
            //System.out.println("endtimev1"+endtimeuncmpv1);
            maxtf_doclen.put(filenum, freq);
            Set set1 = maxtf_doclen.entrySet();
            Iterator i1 = set1.iterator();
            while(i1.hasNext()) {
                Frequency f;
                Map.Entry me = (Map.Entry)i1.next();
                f=(Frequency)me.getValue();
            //System.out.println("key, value max_tf="+me.getKey()+" "+f.max_tf);
            }
            //System.out.println("max_tf, doclen of tokens"+maxtf_doclen);
        }
        else if(flag==2){
            sf_doc(wordCount,filenum);
            Frequency freq=new Frequency(max,totaltokens);
            endtimeuncmpv2 = System.currentTimeMillis();
            //System.out.println("endtimev2"+endtimeuncmpv2);
            stemmaxtf_doclen.put(filenum, freq);
            Set set1 = stemmaxtf_doclen.entrySet();
            Iterator i1 = set1.iterator();
            while(i1.hasNext()) {
                Frequency f;
                Map.Entry me = (Map.Entry)i1.next();
                f=(Frequency)me.getValue();
            //System.out.println("key, value max_tf="+me.getKey()+" "+f.max_tf);
            }
            totaltokens=0;
            //System.out.println("max_tf, doclen of stems"+stemmaxtf_doclen);
        }
        
        wordCount.clear();
        return maxkey;
    }
    public void stemsindoc(ArrayList<String>words,int filenum){
        starttimeuncmpv2=System.currentTimeMillis();
        String stemdoc;
        ArrayList<String> stemofdoc=new ArrayList();
        Porter stem=new Porter();
        for(int i=0;i<words.size();i++){
            stemdoc=stem.stripAffixes(words.get(i));
            stemofdoc.add(stemdoc);
        }
        //System.out.println("stemsindoc()");
        max(stemofdoc,filenum,2);
    }
    public void stemmer(){
        
        int count=0;
        Porter stem=new Porter();
        String stemword=new String();
        for(Map.Entry<String,Set<Integer>> entry : tokens.entrySet()) {
            //count++;
            String key = entry.getKey();
            Set value = entry.getValue();
            
            //count=count+value;
            //stems.put(stem.stripAffixes(key),value);
            
            stemword=stem.stripAffixes(key);
            if(!stemword.contentEquals("")){
                if(flags==0){
                    stems.put(stemword, value);
                    sfreq.put(stemword,tfreq.get(key));
                    flags=1;
                }
                else{
                    //System.out.println("8");
                    if(!stems.containsKey(stemword)){
                        //System.out.println("9");
                        //System.out.println("stemword in if1="+stemword);
                        stems.put(stemword,value);
                        sfreq.put(stemword,tfreq.get(key));
                        //System.out.println("10");
                    }
                    else{
                        //System.out.println("stemword in else 2="+stemword);
                        //Set s=stems.get(stemword);
                        //s.add(value);
                        TreeSet<Integer>s=new TreeSet();
                        s=(TreeSet)stems.get(stemword);
                        Iterator i=s.iterator();
                        while(i.hasNext()){
                            value.add((int)i.next());
                        }
                        stems.put(stemword,value);
                        sfreq.put(stemword,tfreq.get(key)+sfreq.get(stemword));
                    }
                }
            }
        }
        //System.out.println("stems and its posting list="+stems);
        //System.out.println("stems frequency in all docs="+sfreq);
        //System.out.println("count="+count);
        //uniquestems=stems.size();
        //onlyonce(stems);
    }
    public byte gammacode(int num){
        String binary=Integer.toBinaryString(num);
        String offset=binary.substring(1);
        String unary=unarycode(offset.length());
        String code=unary+offset;
        byte b=toByte(code);
        return b;
    }
    public byte toByte(String code){
        byte a=0;
        int len=code.length();
        for(int i=0;i<code.length()-1;i++){
            if((code.charAt(len-i-1))=='1'){
		int b=(int)(Math.pow(2,len-i-1));
		a=(byte)(a|b);
            }
            a=(byte)(a|1);
	}
        return a;
    }
    public String unarycode(int len){
        String num=new String();
        for(int i=0;i<len;i++){
            num=num.concat("1");
        }
        num=num.concat("0");
        return num;
    }
    public byte deltacode(int num){
        String binary=Integer.toBinaryString(num);
        String offset=binary.substring(1);
        String unary=unarycode(binary.length());
        String code=unary+offset;
        byte b=toByte(code);
        return b;
    }
    public void compressedtokenIndexes(){
        int a=0,b=0,c=0;
        TreeMap <Integer,Byte> tfnew=new TreeMap();
        for(Map.Entry<String,Set<Integer>> entry : tokens.entrySet()) {
            String key = entry.getKey();
            Set value = entry.getValue();
            Set <Byte> set=new TreeSet();
            Iterator itr = value.iterator();
            while(itr.hasNext())
            {
                c=(int)itr.next();
                a=(c-b);
                //System.out.println("a="+a);
                set.add(gammacode(a));
                b=c;
            }
            b=c=0;
            compressedtokens.put(key,set);
        }
        //System.out.println();
        //System.out.println("compressed postings="+compressedtokens);
        for(Map.Entry<Integer,Frequency> entry : maxtf_doclen.entrySet()) {
            int key = entry.getKey();
            Frequency value = entry.getValue();
            byte maxtf=gammacode(value.max_tf);
            byte dlen=gammacode(value.doclen);
            Frequencycompressed f=new Frequencycompressed(maxtf,dlen); 
            maxtf_doclencmp.put(key, f);
        }
        for(Map.Entry<String,TreeMap<Integer,Integer>> entry : tf_doc.entrySet()) {
            String key = entry.getKey();
            TreeMap<Integer,Integer> value = entry.getValue();
            for(Map.Entry<Integer,Integer> me: value.entrySet()) {
                int k=me.getKey();
                int v=me.getValue();
                tfnew.put(k,gammacode(v));
            }
            tf_doccmp.put(key, tfnew);
        }
    }
    public void compressedstemIndexes(){
        int a=0,b=0,c=0;
        TreeMap <Integer,Byte> tfnew=new TreeMap();
        for(Map.Entry<String,Set<Integer>> entry : stems.entrySet()) {
            String key = entry.getKey();
            Set value = entry.getValue();
            Set <Byte> set=new TreeSet();
            Iterator itr = value.iterator();
            while(itr.hasNext())
            {
                c=(int)itr.next();
                a=c-b;
                set.add(deltacode(a));
                b=c;
            }
            b=c=0;
            compressedstems.put(key,set);
        }
        for(Map.Entry<Integer,Frequency> entry : stemmaxtf_doclen.entrySet()) {
            int key = entry.getKey();
            Frequency value = entry.getValue();
            byte maxtf=gammacode(value.max_tf);
            byte dlen=gammacode(value.doclen);
            Frequencycompressed f=new Frequencycompressed(maxtf,dlen); 
            stemmaxtf_doclencmp.put(key, f);
        }
        //System.out.println();
        //System.out.println("compressed maxtf, doclen="+stemmaxtf_doclencmp);
        for(Map.Entry<String,TreeMap<Integer,Integer>> entry : sf_doc.entrySet()) {
            String key = entry.getKey();
            TreeMap<Integer,Integer> value = entry.getValue();
            for(Map.Entry<Integer,Integer> me: value.entrySet()) {
                int k=me.getKey();
                int v=me.getValue();
                tfnew.put(k,deltacode(v));
            }
            sf_doccmp.put(key, tfnew);
        }
    }
    public static String longestCommonPrefix(List<String> strings){
        if(strings.isEmpty())
            return "";
        int stringArrayLength=strings.size();
        for(int prefixLength=0; prefixLength<strings.get(0).length(); prefixLength++){
            char c= strings.get(0).charAt(prefixLength);
            for(int i=1; i<stringArrayLength; i++){
                if(prefixLength>=strings.get(i).length() || strings.get(i).charAt(prefixLength)!=c){
                    if(!strings.get(i).substring(0, prefixLength).equals("")){
                        return strings.get(i).substring(0, prefixLength);
                    }
                    else{
                        stringArrayLength--;
                        break;
                    }
                }
            }
        }
        return strings.get(0);
    }
    public void frontCoding() {
        List<Object> testIndexList= new ArrayList();
        LinkedHashMap<Integer, Short> termFreqBlock= new LinkedHashMap();
        LinkedHashSet<Set<Byte>> deltaEncodingSet= new LinkedHashSet();
        Set<String> terms = compressedstems.keySet();
        List<String> termsList = new ArrayList();
        String termsArray[] = terms.toArray(new String[terms.size()]);
        int k = 8, currentK = 0;
        String prefix = "";
        String temp = "";
        
        Set s=compressedstems.entrySet();
        Iterator it=s.iterator();
        while(it.hasNext()){
            int i=0;
            Map.Entry m=(Map.Entry)it.next();
            if(currentK<k){
               termsList.add(currentK, m.getKey().toString());
               currentK++; 
            }
            if (currentK == k || i + 1 == termsArray.length) {
                if (!(prefix = longestCommonPrefix(termsList)).equals("")) {
                    temp += "[";
                    for (int j = 0; j < termsList.size(); j++) {
                        if (termsList.get(j).startsWith(prefix)) {
                            if (j == 0)
                                temp += termsList.get(j).length() + prefix + "*" + termsList.get(j).substring(prefix.length());
                            if (j > 0) {
                                temp += termsList.get(j).substring(prefix.length()).length() + "|" + termsList.get(j).substring(prefix.length());
                            }
                        } else {
                            if (j == 0)
                                temp += termsList.get(j).length() + prefix + "*" + termsList.get(j).substring(0);
                            if (j > 0) {
                                temp += termsList.get(j).substring(0).length() + "|" + termsList.get(j).substring(0);
                            }

                        }
                    }
                    temp += "]";
                    //testIndexList.add(0,compressedstems.get(m.getKey().toString()));
                    //testIndexList.add(1, sf_doccmp.get(m.getKey().toString()));
                    //testIndexList.add(2,stemmaxtf_doclencmp);
                    compressedversion2.add(temp);//, testIndexList);
 //                   System.out.println(temp+"="+testIndexList.toString());
                    currentK = 0;testIndexList.clear();termFreqBlock.clear();
                    termsList.clear();temp="";deltaEncodingSet.clear();
                }
            }
            i++;
        }
        //System.out.println("compressed version2="+compressedversion2);
   //     return testIndexFrontCoding;
    }
    public void displayInfo(){
        int len=0;
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            //System.out.println("tokens Size: " + tokens.size());
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(tokens);
                oos.close();
            }
            len=baos.size();
            //System.out.println("len=" + len);
            baos.close();
            long a=memmaxtf();
            long b=memtf();
            System.out.println("Size of uncompressed version 1 Index="+(len/1024+a/1024+b/1024)+"KB");
            
            }catch(IOException e){
            e.getMessage();
        }
    }
    public void memstem(){
        int len=0;
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            //System.out.println("tokens Size: " + tokens.size());
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(stems);
                oos.close();
            }
            len=baos.size();
            //System.out.println("len=" + len);
            baos.close();
            long a=memmaxtfstem();
            long b=memtfstem();
            System.out.println("Size of uncompressed version 2 Index="+(len/1024+a/1024+b/1024)+"KB");
            
            }catch(IOException e){
            e.getMessage();
        }
    }
    public void tokencmp(){
        int len=0;
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            //System.out.println("tokens Size: " + compressedtokens.size());
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(compressedtokens);
                oos.close();
            }
            len=baos.size();
            //System.out.println("len=" + len);
            baos.close();
            long a=memmaxtfcmp();
            long b=memtfcmp();
            System.out.println("Size of compressed version 1 Index="+(len/1024+a/1024+b/1024)+"KB");
            
            }catch(IOException e){
            e.getMessage();
        }
    }
    public void stemcmp(){
        int len=0;
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            //System.out.println("tokens Size: " + compressedtokens.size());
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(compressedstems);
                oos.close();
            }
            len=baos.size();
            //System.out.println("len=" + len);
            baos.close();
            long a=stemmaxtfcmp();
            long b=stemmemtfcmp();
            System.out.println("Size of compressed version 2 Index="+(len/1024+a/1024+b/1024)+"KB");
            
            }catch(IOException e){
            e.getMessage();
        }
    }
    public long memmaxtf(){
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        try{
            
            Frequency f=new Frequency();
            //System.out.println("1");
            Set s=maxtf_doclen.entrySet();
            Iterator it=s.iterator();
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                f=maxtf_doclen.get((int)m.getKey());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos1)) {
                    //System.out.println("2");
                    oos.writeObject(f.doclen);
                    oos.writeObject(f.max_tf);
                    //System.out.println("3");
                    oos.close();
                }
                //System.out.println("4");
                //len=len+baos1.size();
                
                //System.out.println("5");
                baos1.close();
            }
            //System.out.println("len1=" + baos1.size());
        }
        catch(Exception e){
            e.getMessage();
        }
        return baos1.size();
    }
    public long memmaxtfstem(){
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        try{
            
            Frequency f=new Frequency();
            //System.out.println("1");
            Set s=maxtf_doclen.entrySet();
            Iterator it=s.iterator();
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                f=stemmaxtf_doclen.get((int)m.getKey());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos1)) {
                    //System.out.println("2");
                    oos.writeObject(f.doclen);
                    oos.writeObject(f.max_tf);
                    //System.out.println("3");
                    oos.close();
                }
                //System.out.println("4");
                //len=len+baos1.size();
                
                //System.out.println("5");
                baos1.close();
            }
            //System.out.println("len1=" + baos1.size());
        }
        catch(Exception e){
            e.getMessage();
        }
        return baos1.size();
    }
    public long memmaxtfcmp(){
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        try{
            
            Frequencycompressed f=new Frequencycompressed();
            //System.out.println("1");
            Set s=maxtf_doclencmp.entrySet();
            Iterator it=s.iterator();
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                f=maxtf_doclencmp.get((int)m.getKey());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos1)) {
                    //System.out.println("2");
                    oos.writeObject(f.doclen);
                    oos.writeObject(f.max_tf);
                    //System.out.println("3");
                    oos.close();
                }
                //System.out.println("4");
                //len=len+baos1.size();
                
                //System.out.println("5");
                baos1.close();
            }
            //System.out.println("len1=" + baos1.size());
        }
        catch(Exception e){
            e.getMessage();
        }
        return baos1.size();
    }
    public long stemmaxtfcmp(){
        ByteArrayOutputStream baos1=new ByteArrayOutputStream();
        try{
            
            Frequencycompressed f=new Frequencycompressed();
            //System.out.println("1");
            Set s=stemmaxtf_doclencmp.entrySet();
            Iterator it=s.iterator();
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                f=stemmaxtf_doclencmp.get((int)m.getKey());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos1)) {
                    //System.out.println("2");
                    oos.writeObject(f.doclen);
                    oos.writeObject(f.max_tf);
                    //System.out.println("3");
                    oos.close();
                }
                //System.out.println("4");
                //len=len+baos1.size();
                
                //System.out.println("5");
                baos1.close();
            }
            //System.out.println("len1=" + baos1.size());
        }
        catch(Exception e){
            e.getMessage();
        }
        return baos1.size();
    }
    public long memtf(){
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            Set s=tf_doc.entrySet();
            Iterator it=s.iterator();
            
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                termFrequency=tf_doc.get(m.getKey().toString());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(termFrequency);
                }
                //len=len+baos2.size();
                termFrequency.clear();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(tf_doc);
                    oos.close();
            }
            //System.out.println("term frequency size" + baos2.size());
            baos2.close();
        }catch(IOException e){
            e.getMessage();
        }
        return baos2.size();
    }
    public long memtfstem(){
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        TreeMap<Integer,Integer>termFrequency=new TreeMap();
        try{
            Set s=sf_doc.entrySet();
            Iterator it=s.iterator();
            
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                termFrequency=sf_doc.get(m.getKey().toString());
                //System.out.println("term");
                try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(termFrequency);
                }
                //len=len+baos2.size();
                termFrequency.clear();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(sf_doc);
                    oos.close();
            }
            //System.out.println("term frequency size" + baos2.size());
            baos2.close();
        }catch(IOException e){
            e.getMessage();
        }
        return baos2.size();
    }
    public long memtfcmp(){
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        TreeMap<Integer,Byte>termFrequency=new TreeMap();
        try{
            Set s=tf_doccmp.entrySet();
            Iterator it=s.iterator();
            
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                termFrequency=tf_doccmp.get(m.getKey().toString());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(termFrequency);
                }
                //len=len+baos2.size();
                termFrequency.clear();
            }
            /*try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(tf_doccmp);
                    oos.close();
            }*/
            //System.out.println("term frequency size" + baos2.size());
            baos2.close();
        }catch(IOException e){
            e.getMessage();
        }
        return baos2.size();
    }
    public long stemmemtfcmp(){
        ByteArrayOutputStream baos2=new ByteArrayOutputStream();
        TreeMap<Integer,Byte>termFrequency=new TreeMap();
        try{
            Set s=sf_doccmp.entrySet();
            Iterator it=s.iterator();
            
            while(it.hasNext()){
                Map.Entry m=(Map.Entry)it.next();
                termFrequency=sf_doccmp.get(m.getKey().toString());
                try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(termFrequency);
                }
                //len=len+baos2.size();
                termFrequency.clear();
            }
            /*try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                    oos.writeObject(tf_doc);
                    oos.close();
            }*/
            //System.out.println("term frequency size" + baos2.size());
            baos2.close();
        }catch(IOException e){
            e.getMessage();
        }
        return baos2.size();
    }
    public void output(){
        Porter p=new Porter();
        String lemmas="";
        int df=0,len=0,tf=0;
        String[] wordsex={"reynolds","nasa","prandtl","flow","pressure","boundary","shock"};
        System.out.println("Term\tDocFrequency\tTermfrequency\t Postinglist size");
        for(int i=0;i<wordsex.length;i++){
            lemmas=lemmatize(wordsex[i]);
            ByteArrayOutputStream baos2=new ByteArrayOutputStream();
            df=(tokens.get(lemmas)).size();
            tf=tfreq.get(lemmas);
            try (ObjectOutputStream oos = new ObjectOutputStream(baos2)) {
                oos.writeObject(tokens.get(lemmas));
                oos.writeObject(tokens.get(lemmas).size());
                oos.writeObject(lemmas);
            }
            catch(IOException e){
                e.getMessage();
            }
            len=baos2.size();
            System.out.println(wordsex[i]+"\t"+df+"\t"+tf+" \t"+len);
        }
        System.out.println();
        String stemword="";
        //System.out.println("reynolds in stems="+stems.get("reynold"));
        for(int i=0;i<wordsex.length;i++){
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            stemword=p.stripAffixes(wordsex[i]);
            df=(stems.get(stemword)).size();
            tf=sfreq.get(stemword);
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(stems.get(stemword));
                oos.writeObject((stems.get(stemword)).size());
                oos.writeObject(stemword);
            }
            catch(IOException e){
                e.getMessage();
            }
            len=baos.size();
            
            System.out.println(wordsex[i]+"\t"+df+"\t"+tf+" \t"+len);
            
        }
        Set s=tf_doc.entrySet();
        Iterator i=s.iterator();
        while(i.hasNext()){
            Map.Entry m=(Map.Entry)i.next();
            if(m.getKey().equals("nasa")){
                
            }
        }
        TreeSet<Integer> postinglist=(TreeSet)tokens.get("nasa");
        int j=0;
        System.out.println("\n Document length, max_tf, term frequency for first 3 documents of posting list of Nasa");
        System.out.println("DocLen  Max_tf  TermFrequency");
        Iterator it=postinglist.iterator();
            while(it.hasNext()&&j<3){
                 int a=(int)it.next();
                 //System.out.println("a="+a);
                 TreeMap<Integer,Integer> tf1=tf_doc.get("nasa");
                 //int val=(int)tf1.get(a);
                 //System.out.println("tf1="+tf_doc.get("reynolds"));
                 //System.out.println("term frequency in each doc="+tf_doc.get("nasa"));
                 Frequency f=maxtf_doclen.get(a);
                 System.out.println(f.doclen+"      "+f.max_tf+"       1");
                 j++;
            }
            System.out.println("Document frequency for Nasa is"+tokens.get("nasa").size());
            System.out.println("");
        maxdf();
        maxdfstems();
        maxtfdoclen();
    }
    public void maxdf(){
        Set s=tokens.entrySet();
        int j=0;
        //List<Integer> min=new ArrayList();
        //List<Integer> max=new ArrayList();
        int max=0,min=0,count=0;
        String maxterm="",eqterm="";
        String minterm="",eqminterm="";
        Iterator i=s.iterator();
        while(i.hasNext()){
            Map.Entry m=(Map.Entry)i.next();
            int a=tokens.get(m.getKey().toString()).size();
            if(j==0){
                min=a;
                //System.out.println("a="+a);
                //System.out.println("min in j==0="+min);
                j=1;
            }
            if(a>max){
               max=a;
               maxterm=m.getKey().toString();
            }
            if(a==max){
                eqterm=m.getKey().toString();
                count++;
            }
            if(a<min){
                min=a;
                //System.out.println("min in a<min="+min);
                minterm=m.getKey().toString();
            }
            if(a==min){
                eqminterm=m.getKey().toString();
                System.out.println("Term with minimum document frequency from index1 is"+eqminterm);
                //count++;
            }
        }
        System.out.println("\nTerm with maximum document frequency from index1 is "+maxterm);
	System.out.println("");
    }
    public void maxdfstems(){
        int j=0;
        //List<Integer> min=new ArrayList();
        //List<Integer> max=new ArrayList();
        int max=0,min=0,count=0;
        String maxterm="",eqterm="";
        String minterm="",eqminterm="";
        Set s=stems.entrySet();
        Iterator i=s.iterator();
        while(i.hasNext()){
            Map.Entry m=(Map.Entry)i.next();
            int a=stems.get(m.getKey().toString()).size();
            if(j==0){
                min=a;
                //System.out.println("a="+a);
                //System.out.println("min in j==0="+min);
                j=1;
            }
            if(a>max){
               max=a;
               maxterm=m.getKey().toString();
            }
            if(a==max){
                eqterm=m.getKey().toString();
                count++;
            }
            if(a<min){
                min=a;
                //System.out.println("min in a<min="+min);
                minterm=m.getKey().toString();
            }
            if(a==min){
                eqminterm=m.getKey().toString();
                System.out.println("Term with minimum df in stem index2 is "+eqminterm);
                //count++;
            }
        }
        System.out.println("\nTerm with maximum df in stem index2 is "+maxterm);
    }
    public void maxtfdoclen(){
        int j=0;
        //List<Integer> min=new ArrayList();
        //List<Integer> max=new ArrayList();
        int max=0,maxd=0,docid1=0,docid2=0;
        String maxterm="",eqterm="";
        String minterm="",eqminterm="";
        Set s=maxtf_doclen.entrySet();
        Iterator i=s.iterator();
        while(i.hasNext()){
            Map.Entry m=(Map.Entry)i.next();
            Frequency f=maxtf_doclen.get((int)m.getKey());
            if(f.max_tf>max){
                max=f.max_tf;
                docid1=(int)m.getKey();
            }
            if(f.doclen>maxd){
                maxd=f.doclen;
                //System.out.println("maxd="+maxd);
                docid2=(int)m.getKey();
            }
        }
        System.out.println("\nDocument with largest max_tf is "+docid1);
        System.out.println("\nDocument with largest doclen is "+docid2);
    }
    public void writeUncompressedIndex() {
        File uncompressedIndexFile= new File("uncompressedv1");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(uncompressedIndexFile))) {
            objectOutputStream.writeObject(tokens);
            //objectOutputStream.flush();
            objectOutputStream.writeObject(tf_doc);
            //objectOutputStream.writeObject(maxtf_doclen);
            objectOutputStream.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }
    public void writeUncompressedIndex2() {
        File uncompressedIndexFile= new File("uncompressedv2");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(uncompressedIndexFile))) {
            objectOutputStream.writeObject(stems);
            //objectOutputStream.flush();
            objectOutputStream.writeObject(sf_doc);
            //objectOutputStream.writeObject(stemmaxtf_doclencmp);
            objectOutputStream.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }
    public void writecompressedIndex() {
        File uncompressedIndexFile= new File("compressedv1");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(uncompressedIndexFile))) {
            objectOutputStream.writeObject(compressedtokens);
            //objectOutputStream.flush();
            objectOutputStream.writeObject(tf_doccmp);
            //objectOutputStream.flush();
            //objectOutputStream.writeObject(maxtf_doclencmp);
            objectOutputStream.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }
    public void writecompressedIndex2() {
        File uncompressedIndexFile= new File("compressedv2");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(uncompressedIndexFile))) {
            objectOutputStream.writeObject(compressedstems);
            //objectOutputStream.flush();
            objectOutputStream.writeObject(sf_doccmp);
            //objectOutputStream.flush();
            //objectOutputStream.writeObject(stemmaxtf_doclencmp);
            //objectOutputStream.flush();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }
    public static void main(String[] args) {
        Uncmpindex t=new Uncmpindex();
        StanfordLemmatizer();
        long startTime1 = System.currentTimeMillis();
        t.readFile();
        long endtime1 = System.currentTimeMillis();
        //System.out.println("stems="+t.stems);
        long startTime2 = System.currentTimeMillis();
        t.stemmer();
        long endTime2 = System.currentTimeMillis();
        //System.out.println(t.gammacode(3));
        //long endTime   = System.currentTimeMillis();
        long startTime3 = System.currentTimeMillis();
        t.compressedtokenIndexes();
        t.blockedcompression();
        long endTime3 = System.currentTimeMillis();
        long startTime4 = System.currentTimeMillis();
        t.compressedstemIndexes();
        t.frontCoding();
        long endTime4 = System.currentTimeMillis();
        System.out.println("Time taken to build uncompressed index 1 version 1 "+((endtime1-startTime1)/10));
        System.out.println("Time taken to build compressed index 1 version 1 "+(endTime3-startTime3));
        System.out.println("Time taken to build uncompressed index 2 version 1 "+(endTime2-startTime2));
        System.out.println("Time taken to build copressed index 2 version 2 "+(endTime4-startTime4));
        t.displayInfo();
        t.tokencmp();
        t.memstem();
        t.stemcmp();
        t.output();
        t.writeUncompressedIndex();
        t.writecompressedIndex();
        t.writeUncompressedIndex2();
        t.writecompressedIndex2();
    }
    static class Frequency{
        int max_tf,doclen;
        public Frequency(int max_tf,int doclen){
            this.max_tf=max_tf;
            this.doclen=doclen;
        }
        public Frequency(){
            
        }
    }
    static class Frequencycompressed{
        byte max_tf,doclen;
        public Frequencycompressed(byte max_tf,byte doclen){
            this.max_tf=max_tf;
            this.doclen=doclen;
        }
        public Frequencycompressed(){
            
        }
    }
}
