
#### 1) Stanford Named Entity Recognizer (NER)

[Download NER](https://nlp.stanford.edu/software/CRF-NER.html#Download). Use existing `Symptoms` data to tokenize from the command below
or use the `Java` method below to populate the same data on command line. Once tokenized. Tag the tokens.

    java -cp stanford-ner.jar edu.stanford.nlp.process.PTBTokenizer medical-symptoms.txt > medical-symptoms.tok

This mechanism is better approach it take cares of everything in a single operation. Create `medical-symptoms.tok` file 
from existing data `MedicalDataStatic.java` in Android

    public static void main(String[] args) {
        Set<TokenizeData> words = new HashSet<>();

        MedicalDataStatic medicalDataStatic = new MedicalDataStatic();
        medicalDataStatic.splitToTokens(Gynae.getSymptoms(), MedicalDepartmentEnum.OGY, words);
        medicalDataStatic.splitToTokens(Gynae.getObstetrics(), MedicalDepartmentEnum.OGY, words);
        medicalDataStatic.splitToTokens(Pediatrician.getSymptoms(), MedicalDepartmentEnum.PAE, words);
        medicalDataStatic.splitToTokens(Surgeon.getSymptoms(), MedicalDepartmentEnum.GSR, words);
        medicalDataStatic.splitToTokens(Ortho.getSymptoms(), MedicalDepartmentEnum.ORT, words);
        medicalDataStatic.splitToTokens(Dental.getSymptoms(), MedicalDepartmentEnum.DNT, words);
        medicalDataStatic.splitToTokens(Physician.getSymptoms(), MedicalDepartmentEnum.GPY, words);


        for (TokenizeData word : words) {
            System.out.format("%s\t%s\n", word.getToken(), word.getMedicalDepartment().name());
        }
    }

    private void splitToTokens(List<DataObj> symptoms, MedicalDepartmentEnum medicalDepartment, Set<TokenizeData> words) {
        StringTokenizer stringTokenizer;
        for (DataObj symptom : symptoms) {
            stringTokenizer = new StringTokenizer(symptom.getFullName(), "() ");
            writeToken(medicalDepartment, stringTokenizer, words);

            stringTokenizer = new StringTokenizer(symptom.getShortName(), "() ");
            writeToken(medicalDepartment, stringTokenizer, words);
        }
    }

    private void writeToken(MedicalDepartmentEnum medicalDepartment, StringTokenizer stringTokenizer, Set<TokenizeData> words) {
        while (stringTokenizer.hasMoreTokens()) {
            String wordLowerCase = stringTokenizer.nextToken().toLowerCase();
            switch (wordLowerCase) {
                case "of":
                case "in":
                case "at":
                case "all":
                case "to":
                case "angle":
                case "left":
                case "right":
                case "&":
                case "from":
                case "both":
                case "is":
                case "i":
                case "+":
                case "-":
                case "on":
                case "over":
                case "lover":
                case "altered":
                case "for":
                case "/":
                case "\\":
                case "or":
                case "checkup":
                    words.add(new TokenizeData("be", medicalDepartment));
                    break;
                default:
                    words.add(new TokenizeData(wordLowerCase, medicalDepartment));
            }
        }
    }

    private class TokenizeData {
        private String token;
        private MedicalDepartmentEnum medicalDepartment;

        TokenizeData(String token, MedicalDepartmentEnum medicalDepartment) {
            this.token = token;
            this.medicalDepartment = medicalDepartment;
        }

        String getToken() {
            return token;
        }

        MedicalDepartmentEnum getMedicalDepartment() {
            return medicalDepartment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TokenizeData that = (TokenizeData) o;
            return token.equals(that.token) &&
                    medicalDepartment == that.medicalDepartment;
        }

        @Override
        public int hashCode() {
            return Objects.hash(token, medicalDepartment);
        }
    }

Once the tokenized data is created. Create a `NER model`. 

#### 2) NER Model

Here's a sample NER properties file: [Refer](https://nlp.stanford.edu/software/crf-faq.html#a). Save the content
as `medical-symptoms.properties` files. And then execute the code below. Whereas for fine tuning 
[Fine Grained NER](https://stanfordnlp.github.io/CoreNLP/ner.html#customizing-the-fine-grained-ner)

    java -cp stanford-ner.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop medical-symptoms.properties

`medical-symptoms.properties` content below. Refer [Properties Feature](https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/ie/NERFeatureFactory.html)

    #trainFile = training-data.col
    trainFile = medical-symptoms.tok
    #serializeTo = ner-model.ser.gz
    serializeTo = medical-symptoms-ner-model.ser.gz
    map = word=0,answer=1
    
    useClassFeature=true
    useWord=true
    useNGrams=true
    noMidNGrams=true
    maxNGramLeng=6
    usePrev=true
    useNext=true
    useSequences=true
    usePrevSequences=true
    maxLeft=1
    useTypeSeqs=true
    useTypeSeqs2=true
    useTypeySequences=true
    wordShape=chris2useLC
    useDisjunctive=true 
    
Above file explained below. 

    # location of the training file
    trainFile = jane-austen-emma-ch1.tsv
    # location where you would like to save (serialize) your
    # classifier; adding .gz at the end automatically gzips the file,
    # making it smaller, and faster to load
    serializeTo = ner-model.ser.gz
    
    # structure of your training file; this tells the classifier that
    # the word is in column 0 and the correct answer is in column 1
    map = word=0,answer=1
    
    # This specifies the order of the CRF: order 1 means that features
    # apply at most to a class pair of previous class and current class
    # or current class and next class.
    maxLeft=1
    
    # these are the features we'd like to train with
    # some are discussed below, the rest can be
    # understood by looking at NERFeatureFactory
    useClassFeature=true
    useWord=true
    # word character ngrams will be included up to length 6 as prefixes
    # and suffixes only 
    useNGrams=true
    noMidNGrams=true
    maxNGramLeng=6
    usePrev=true
    useNext=true
    useDisjunctive=true
    useSequences=true
    usePrevSequences=true
    # the last 4 properties deal with word shape features
    useTypeSeqs=true
    useTypeSeqs2=true
    useTypeySequences=true
    wordShape=chris2useLC 
      
#### 3) Penn Part of Speech Tags

Note:  these are the 'modified' tags used for Penn tree banking; these are the tags used in the Jet system. NP, NPS, PP, and PP$ from the original Penn part-of-speech tagging were changed to NNP, NNPS, PRP, and PRP$ to avoid clashes with standard syntactic categories.

    1.	CC	Coordinating conjunction
    2.	CD	Cardinal number
    3.	DT	Determiner
    4.	EX	Existential there
    5.	FW	Foreign word
    6.	IN	Preposition or subordinating conjunction
    7.	JJ	Adjective
    8.	JJR	Adjective, comparative
    9.	JJS	Adjective, superlative
    10.	LS	List item marker
    11.	MD	Modal
    12.	NN	Noun, singular or mass
    13.	NNS	Noun, plural
    14.	NNP	Proper noun, singular
    15.	NNPS	Proper noun, plural
    16.	PDT	Predeterminer
    17.	POS	Possessive ending
    18.	PRP	Personal pronoun
    19.	PRP$	Possessive pronoun
    20.	RB	Adverb
    21.	RBR	Adverb, comparative
    22.	RBS	Adverb, superlative
    23.	RP	Particle
    24.	SYM	Symbol
    25.	TO	to
    26.	UH	Interjection
    27.	VB	Verb, base form
    28.	VBD	Verb, past tense
    29.	VBG	Verb, gerund or present participle
    30.	VBN	Verb, past participle
    31.	VBP	Verb, non-3rd person singular present
    32.	VBZ	Verb, 3rd person singular present
    33.	WDT	Wh-determiner
    34.	WP	Wh-pronoun
    35.	WP$	Possessive wh-pronoun
    36.	WRB	Wh-adverb






