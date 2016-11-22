Readme File

Language used: Java

Compiling the code: 
export corenlp=/usr/local/corenlp350
javac -cp .:jsoup-1.8.1.jar:$corenlp/joda-time.jar:$corenlp/jollyday.jar:$corenlp/ejml-0.23.jar:$corenlp/xom.jar:$corenlp/javax.json.jar:$corenlp/stanford-corenlp-3.5.0.jar:$corenlp/stanford-corenlp-3.5.0-models.jar unCompressedIndex.java

Running the code: 
java -cp .:jsoup-1.8.1.jar:$corenlp/joda-time.jar:$corenlp/jollyday.jar:$corenlp/ejml-0.23.jar:$corenlp/xom.jar:$corenlp/javax.json.jar:$corenlp/stanford-corenlp-3.5.0.jar:$corenlp/stanford-corenlp-3.5.0-models.jar unCompressedIndex

The file containd two java class files. 1. Uncmpindex.java 2. Porter.java
Porter.java is an open source porter-stemmer used to stem the tokens.

StanfordCoreNLP is used to lemmatize the tokens.

The output is in the description file

