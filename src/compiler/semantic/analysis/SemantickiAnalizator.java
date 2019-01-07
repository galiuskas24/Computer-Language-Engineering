package compiler.semantic.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

public class SemantickiAnalizator {
    private static Scanner sc;
    private static LinkedList<String> bufferRow = new LinkedList<>();
    private static LinkedList<Context> manager = new LinkedList<>();
    private static TreeSet<String> declaredFunctions = new TreeSet<>();
    private static int loopDepth = 0;


    public static void main(String[] args) throws FileNotFoundException {
        String path = "/home/vlado24/Downloads/PPJ_MultiSPRUT_2/Primjeri radnih direktorija/Sema/Testovi/7/7.in";
        sc = new Scanner(new File(path));

        Context globalContext = new Context();
        globalContext.locaclVars = new ArrayList<>();
        manager.add(globalContext);
        check_node(load_next_node());

        //main exist
        check_main(globalContext);


        //all function
        check_functions(globalContext);
    }



    //----------HELP FUNCTIONS---------
    public static void check_main(Context globalContext){
        boolean mainExist = false;

        for (MyIDN func : globalContext.locaclVars){
            if (func.type.equals("main(void->int)")){
                mainExist = true;
                break;
            }
        }

        if (!mainExist) System.out.println("main");
    }

    private static void check_functions(Context globalContext) {
        for (String declFunction: declaredFunctions) {
            boolean exist = false;

            for (MyIDN func: globalContext.locaclVars) {
                if (declFunction.equals(func.type)){
                    if (func.definedFunction){
                        exist = true;
                        break;
                    }
                }
            }

            if (!exist) System.out.println("funkcija");
        }
    }

    public static ReturnParameters check_node(String function){
        function = function.substring(1, function.length()-1); //remove < & >
        ReturnParameters params = new ReturnParameters();

        switch (function){
            case "primarni_izraz":
                params = primarni_izraz();
                break;

            case "postfiks_izraz":
                params = postfiks_izraz();
                break;
            case "lista_argumenata":
                params = lista_argumenata();
                break;
            case "unarni_izraz":
                params = unarni_izraz();
                break;

            case "unarni_operator":
                params = unarni_operator();
                break;

            case "cast_izraz":
                params = cast_izraz();
                break;

            case "ime_tipa":
                params = ime_tipa();
                break;

            case "specifikator_tipa":
                params = specifikator_tipa();
                break;

            case "multiplikativni_izraz":
                params = multiplikativni_izraz();
                break;

            case "aditivni_izraz":
                params = aditivni_izraz();
                break;

            case "odnosni_izraz":
                params = odnosni_izraz();
                break;

            case "jednakosni_izraz":
                params = jednakosni_izraz();
                break;

            case "bin_i_izraz":
                params = bin_i_izraz();
                break;

            case "bin_xili_izraz":
                params = bin_xili_izraz();
                break;

            case "bin_ili_izraz":
                params = bin_ili_izraz();
                break;

            case "log_i_izraz":
                params = log_i_izraz();
                break;

            case "log_ili_izraz":
                params = log_ili_izraz();
                break;

            case "izraz_pridruzivanja":
                params = izraz_pridruzivanja();
                break;

            case "izraz":
                params = izraz();
                break;

            case "slozena_naredba":
                params = slozena_naredba(null, "");
                break;

            case "lista_naredbi":
                lista_naredbi();
                break;

            case "naredba":
                naredba();
                break;

            case "izraz_naredba":
                params = izraz_naredba();
                break;

            case "naredba_grananja":
                params = naredba_grananja();
                break;

            case "naredba_petlje":
                params = naredba_petlje();
                break;

            case "naredba_skoka":
                params = naredba_skoka();
                break;

            case "prijevodna_jedinica":
                prijevodna_jedinica();
                break;

            case "vanjska_deklaracija":
                vanjska_deklaracija();

                break;
            case "definicija_funkcije":
                params = definicija_funkcije();
                break;

            case "lista_parametara":
                params = lista_parametara();
                break;

            case "deklaracija_parametra":
                params = deklaracija_parametra();
                break;

            case "lista_deklaracija":
                lista_deklaracija();
                break;

            case "deklaracija":
                deklaracija();
                break;

            case "lista_init_deklaratora":
                lista_init_deklaratora("");
                break;

            case "init_deklarator":
                init_deklarator("");
                break;

            case "izravni_deklarator":
                params = izravni_deklarator("");
                break;

            case "inicijalizator":
                params = inicijalizator();
                break;

            case "lista_izraza_pridruzivanja":
                params = lista_izraza_pridruzivanja();
                break;
            default:
                ArrayList<Integer> aa = new ArrayList<>();
                aa.get(1);
                System.out.println("There is no function: " + function);
                System.exit(1);
        }

        return params;
    }

    public static String load_next_node(){
        if (!bufferRow.isEmpty()){
            String returnRow = bufferRow.getFirst();
            bufferRow.removeFirst();
            return returnRow;
        }

        if (!sc.hasNextLine()){
            System.err.println("Finish too early!");
            //System.exit(-1);
            return "";
        }

        return sc.nextLine().replaceAll("^\\s+", "");
    }

    public static boolean isCharacter(String testChar){
        if (testChar.length() < 3){

            if (testChar.length() == 2) {
                if (testChar.charAt(0) == '\\'){
                    if (testChar.charAt(1) == 't'
                            || testChar.charAt(1) == 'n'
                            || testChar.charAt(1) == '0'
                            || testChar.charAt(1) == '\''
                            || testChar.charAt(1) == '\"'
                            || testChar.charAt(1) == '\\'){
                        return true;
                    }
                }
            } else if (testChar.length() == 1) {
                int numericValue = (int) testChar.charAt(0);
                if (numericValue >= 0 && numericValue <= 127) return true;
            }
        }

        return false;
    }

    public static String createErrorEndWord(String inputLine){
        String keyWord = inputLine.substring(0, inputLine.indexOf(" "));
        String row = inputLine.substring(keyWord.length() + 1, inputLine.indexOf(" ", keyWord.length() + 1));
        String lexWord = inputLine.substring(keyWord.length() + row.length() + 2);

        return keyWord + "(" + row + "," + lexWord + ")";
    }

    public static boolean isImplicitCastable(String from, String to){

        //TODO: provjerit castove za CONST
        if (from.equals(to)
                || (to.equals("int") && (from.equals("char") || from.equals("const char") || from.equals("const int")))
                || (to.equals("char") && from.equals("const char"))
                || (to.equals("const int") && (from.equals("char") || from.equals("const char") || from.equals("int")))
                || (to.equals("const char") && from.equals("char")))
            return true;

        return false;
    }

    public static ReturnParameters genericLogicalFunction(String first, String second){
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);

        if (firstNode.startsWith(first)){
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith(second)){
            params.type = "int";
            String endWord = createErrorEndWord(load_next_node());
            String secondNode = load_next_node(); //<cast_izraz>

            if (!isImplicitCastable(retPar.type, "int")){
                System.out.println(second + " ::= " + second + " " + endWord + " " + first);
                System.err.println("Error: Potreban tip podataka INT a ne: " + retPar.type );
                System.exit(-1);
            }

            ReturnParameters retPar2 = check_node(secondNode);

            if (!isImplicitCastable(retPar2.type, "int")){
                System.out.println(second + " ::= " + second + " " + endWord + " " + first);
                System.err.println("Error: Potreban tip podataka INT a ne: " + retPar2.type );
                System.exit(-1);
            }

        }else {
            System.err.println("Wrong: " + second);
            System.exit(-1);
        }

        return params;
    }

    public static MyIDN getVarIfDeclared(String IDNname){

        for (int i = manager.size()-1; i >= 0; i--){
            for (MyIDN myVar: manager.get(i).locaclVars) {
                if (myVar.name.equals(IDNname)) return myVar;
            }
        }

        return null;
    }

    public static int isArrayCharInFuture(){
        if (!bufferRow.isEmpty()) System.err.println("ERROR: isArrayCharInFuture");

        String firstLine = sc.nextLine();
        bufferRow.addLast(firstLine.replaceAll("^\\s+", ""));
        int spaceLenght = firstLine.length() - firstLine.replaceAll("^\\s+", "").length();

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String shortLine = line.replaceAll("^\\s+", "");
            bufferRow.addLast(shortLine);

            if ((line.length()-shortLine.length()) < spaceLenght) return 0;
            else if (shortLine.startsWith("NIZ_ZNAKOVA")){
                String str = createErrorEndWord(shortLine);
                str = str.substring(str.indexOf(",")+2, str.length()-2);
                return str.length();
            }
        }

        return 0;
    }

    public static String arrayToString(ArrayList<String> array){
        String funParams = "";

        for (String parType: array) funParams += parType + ",";
        funParams = funParams.substring(0, funParams.length()-1);

        return "[" + funParams + "]";
    }


    public static void errorLoadNode(){
        String line = sc.nextLine();
        String node = line.replaceAll("^\\s+", "");
        int spacePrefix = line.length() - node.length();

        while (sc.hasNextLine()){
            String newLine = sc.nextLine();
            String nodeLine = newLine.replaceAll("^\\s+", "");
            if ((newLine.length()-nodeLine.length()) > spacePrefix){
                bufferRow.add(nodeLine);
                break;
            }
        }
    }

    //---------SEMANTIC FUNCTIONS----------
    private static ReturnParameters primarni_izraz() {
        ReturnParameters params = new ReturnParameters();

        String firstNode = load_next_node();
        String keyWord = firstNode.substring(0, firstNode.indexOf(" "));
        String row = firstNode.substring(keyWord.length() + 1, firstNode.indexOf(" ", keyWord.length() + 1));
        String lexWord = firstNode.substring(keyWord.length() + row.length() + 2);

        switch (keyWord){

            case "IDN":
                MyIDN variable = getVarIfDeclared(lexWord);
                if (variable == null){
                    System.out.println("<primarni_izraz> ::= " + createErrorEndWord(firstNode));
                    System.err.println("Error: IDN is not declared!");
                    System.exit(-1);

                }else {
                    params.type = variable.type;
                    params.l_expression = variable.l_expression;
                }
                break;

            case "BROJ":
                params.type = "int";

                //1.
                if (lexWord.length() < 10){
                    long lng = Long.parseLong(lexWord);
                    if (lng <= 2147483648L && lng >= -2147483648L) break;
                }

                //error
                System.out.println("<primarni_izraz> ::= " + createErrorEndWord(firstNode));
                System.err.println("Error: int je prevelik!");
                System.exit(-1);
                break;

            case "ZNAK":
                params.type = "char";

                String testChar = lexWord.substring(1, lexWord.length()-1);
                if (!isCharacter(testChar)){
                    //error
                    System.out.println("<primarni_izraz> ::= " + createErrorEndWord(firstNode));
                    System.err.println("Error: This is not char!");
                    System.exit(-1);
                }
                break;

            case "NIZ_ZNAKOVA":
                params.type = "array const char";

                String testString = lexWord.substring(1, lexWord.length()-1);
                String buffer = "";
                boolean isString = true;

                for (char ch: testString.toCharArray()) {
                    buffer += ch;

                    if (ch == '\\' && isString){
                        isString = false;
                        continue;
                    }

                    if (!isCharacter(buffer) || buffer.equals("\"")) {
                        isString = false;
                        break;
                    }

                    buffer = "";
                    isString = true;
                }

                //error
                if (!isString){
                    System.out.println("<primarni_izraz> ::= " + createErrorEndWord(firstNode));
                    System.err.println("Error: This is not string!");
                    System.exit(-1);
                }
                break;

            case "L_ZAGRADA":
                ReturnParameters retPar = check_node(load_next_node()); // <izraz>
                load_next_node(); //D_ZAGRADA

                params.type = retPar.type;
                params.l_expression = retPar.l_expression;
                break;

            default:
                System.err.println("Wrong: primarni izraz!");
                System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters postfiks_izraz() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        //1.
        ReturnParameters retPar = check_node(firstNode); // firstNode

        if (firstNode.startsWith("<primarni_izraz>")){
            //2.1.
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else {
            //calculate second node end character
            String firstEndWord = load_next_node();

            if (firstEndWord.startsWith("L_UGL_ZAGRADA")){
                //2.2.
                String nextNode = load_next_node(); //<izraz>
                String funcType = retPar.type;

                if (!funcType.startsWith("array")){
                    //error
                    errorLoadNode();
                    String lastEndWord = createErrorEndWord(load_next_node());
                    firstEndWord = createErrorEndWord(firstEndWord);
                    System.out.println("<postfiks_izraz> ::= <postfiks_izraz> "+firstEndWord+" <izraz> "+lastEndWord);
                    System.err.println("Error: Must be array!");
                    System.exit(-1);

                }else {
                    funcType = funcType.substring(6);
                    params.type = funcType;
                    params.l_expression = !funcType.startsWith("const");
                }

                ReturnParameters retPar2 = check_node(nextNode);

                if (!isImplicitCastable(retPar2.type, "int")){
                    //error
                    firstEndWord = createErrorEndWord(firstEndWord);
                    String lastEndWord = createErrorEndWord(load_next_node());
                    System.out.println("<postfiks_izraz> ::= <postfiks_izraz> "+firstEndWord+" <izraz> "+lastEndWord);
                    System.err.println("Error: Number must be INT!");
                    System.exit(-1);
                }
                load_next_node();//D_UGL_ZAGRADA

            }else if (firstEndWord.startsWith("L_ZAGRADA")){
                String thirdNode = load_next_node();

                if (thirdNode.startsWith("D_ZAGRADA")){
                    //2.3.
                    String func = retPar.type;
                    params.type = func.substring(func.indexOf("->") + 2, func.length()-1);
                    String parType = func.substring(func.indexOf("(") + 1, func.indexOf("->"));

                    if (!parType.equals("void")){
                        //error
                        firstEndWord = createErrorEndWord(firstEndWord);
                        String secondEndWord = createErrorEndWord(thirdNode);
                        System.out.println("<postfiks_izraz> ::= <postfiks_izraz> "+firstEndWord+" "+ secondEndWord);
                        System.err.println("Error: Wrong parameter type! Expected: void but" + parType);
                        System.exit(-1);
                    }

                }else if (thirdNode.startsWith("<lista_argumenata>")){
                    ReturnParameters retPar2 = check_node(thirdNode);

                    String func = retPar.type;
                    params.type = func.substring(func.indexOf("->") + 2, func.length()-1);
                    String parTypes = func.substring(func.indexOf("[") + 1, func.indexOf("]"));
                    String[] parameters = parTypes.split(",");


                    for (int i = 0; i < parameters.length; i++) {
                        if (!isImplicitCastable(retPar2.types.get(i), parameters[i])){
                            //error
                            firstEndWord = createErrorEndWord(firstEndWord);
                            String lastEndWord = createErrorEndWord(load_next_node());
                            System.out.println("<postfiks_izraz> ::= <postfiks_izraz> "+firstEndWord+" <lista_argumenata> "+lastEndWord);
                            System.err.println("Error: wrong types in lista_argumenata!");
                            System.exit(-1);
                        }
                    }

                    load_next_node(); //D_ZAGRADA

                }else {
                    System.err.println("Wrong: postfiks_izraz -> <postfiks_izraz> L_ZAGRADA <lista_argumenata>!");
                    System.exit(-1);
                }

            }else if (firstEndWord.startsWith("OP_")){
                params.type = "int";

                if (!retPar.l_expression || !isImplicitCastable(retPar.type, "int")){
                    System.out.println("<postfiks_izraz> ::= <postfiks_izraz> " + createErrorEndWord(firstEndWord));
                    System.err.println("Error: Nemogouce castati! -> " + retPar.type + " u INT");
                    System.exit(-1);
                }

            }else {
                System.err.println("Wrong: postfiks_izraz!");
                System.exit(-1);
            }
        }

        return params;
    }

    private static ReturnParameters lista_argumenata() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);

        if (firstNode.startsWith("<izraz_pridruzivanja>")){
            params.types = new ArrayList<>();
            params.types.add(retPar.type);

        }else if (firstNode.startsWith("<lista_argumenata>")){
            load_next_node(); //ZAREZ
            ReturnParameters retPar2 = check_node(load_next_node());

            params.types = new ArrayList<>(retPar.types);
            params.types.add(retPar2.type);
        }

        return params;
    }

    private static ReturnParameters unarni_izraz() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        if (firstNode.startsWith("OP_")){
            params.type = "int";

            ReturnParameters retPar = check_node(load_next_node());
            if (!retPar.l_expression || !isImplicitCastable(retPar.type, "int")){
                //error
                System.out.println("<unarni_izraz> ::= " + createErrorEndWord(firstNode) + " <unarni_izraz>");
                System.err.println("Error: Nemogouce castati! -> " + retPar.type + " u INT");
                System.exit(-1);
            }

        }else if (firstNode.startsWith("<postfiks_izraz>")){
            //1.1.
            ReturnParameters retPar = check_node(firstNode);
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith("<unarni_operator>")) {
            params.type = "int";

            ReturnParameters retPar = check_node(load_next_node());
            if (!isImplicitCastable(retPar.type, "int")){
                System.out.println("<unarni_izraz> ::= <unarni_operator> <cast_izraz>");
                System.err.println("Error: Nemogouce castati <cast_izraz> u int!");
                System.exit(-1);
            }

        } else {
            System.err.println("Wrong: unarni_izraz!");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters unarni_operator() {
        ReturnParameters params = new ReturnParameters();
        System.err.println("UNARNI OPERATOR -> NEDOHVATLJIVO!");//TODO: KRAJ _>pobrisat
        return params;
    }

    private static ReturnParameters cast_izraz() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        if (firstNode.startsWith("<unarni_izraz>")){
            ReturnParameters retPar = check_node(firstNode);
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith("L_ZAGRADA")){
            ReturnParameters retPar1 = check_node(load_next_node());
            String secondEndWord = createErrorEndWord(load_next_node()); //D_ZAGRADA
            ReturnParameters retPar2 = check_node(load_next_node());
            //TODO: implementirat castanje retPar2 i retpar1


            params.type = retPar1.type;
        }else {
            System.err.println("Wrong: cast_izraz!");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters ime_tipa() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        if (firstNode.startsWith("<")){
            ReturnParameters retPar = check_node(firstNode);
            params.type = retPar.type;

        }else if (firstNode.startsWith("KR_CONST")){
            ReturnParameters retPar = check_node(load_next_node()); //<specifikator_tipa>
            params.type = "const " + retPar.type;

            //2.
            if (retPar.type.equals("void")){
                System.out.println("<ime_tipa> ::= "+ createErrorEndWord(firstNode) +" <specifikator_tipa>");
                System.err.println("Error: Ne postoji 'const void'!");
                System.exit(-1);
            }

        }else {
            System.err.println("Error: ime_tipa");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters specifikator_tipa() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = createErrorEndWord(load_next_node());
        String wordType = firstNode.substring(0, firstNode.indexOf("("));

        switch (wordType){
            case "KR_VOID":
                params.type = "void";
                break;

            case "KR_CHAR":
                params.type = "char";
                break;

            case "KR_INT":
                params.type = "int";
                break;

            default:
                System.err.println("Wrong: specifikator_tipa!");
                System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters multiplikativni_izraz() {
        return genericLogicalFunction("<cast_izraz>", "<multiplikativni_izraz>");
    }

    private static ReturnParameters aditivni_izraz() {
        return genericLogicalFunction("<multiplikativni_izraz>", "<aditivni_izraz>");
    }

    private static ReturnParameters odnosni_izraz() {
        return genericLogicalFunction("<aditivni_izraz>", "<odnosni_izraz>");
    }

    private static ReturnParameters jednakosni_izraz() {
        return genericLogicalFunction("<odnosni_izraz>", "<jednakosni_izraz>");
    }

    private static ReturnParameters bin_i_izraz() {
        return genericLogicalFunction("<jednakosni_izraz>", "<bin_i_izraz>");
    }

    private static ReturnParameters bin_xili_izraz() {
        return genericLogicalFunction("<bin_i_izraz>", "<bin_xili_izraz>");
    }

    private static ReturnParameters bin_ili_izraz() {
        return genericLogicalFunction("<bin_xili_izraz>", "<bin_ili_izraz>");
    }

    private static ReturnParameters log_i_izraz() {
        return genericLogicalFunction("<bin_ili_izraz>", "<log_i_izraz>");
    }

    private static ReturnParameters log_ili_izraz() {
        return genericLogicalFunction("<log_i_izraz>", "<log_ili_izraz>");
    }

    private static ReturnParameters izraz_pridruzivanja() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);

        if (firstNode.startsWith("<log_ili_izraz>")){
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith("<postfiks_izraz>")){
            params.type = retPar.type;

            String endWord = createErrorEndWord(load_next_node());
            String secondNode = load_next_node();

            if (!retPar.l_expression){
                System.out.println("<izraz_pridruzivanja> ::= <postfiks_izraz> " + endWord + " <izraz_pridruzivanja>");
                System.err.println("Error: Mora biti pridruzivo!");
                System.exit(-1);
            }

            ReturnParameters retPar2 = check_node(secondNode);

            if (!isImplicitCastable(retPar2.type, retPar.type)){
                System.out.println("<izraz_pridruzivanja> ::= <postfiks_izraz> " + endWord + " <izraz_pridruzivanja>");
                System.err.println("Error: Različiti tipovi pridruživanja!" + retPar2.type + "->" + retPar.type);
                System.exit(-1);
            }

        }else {
            System.err.println("Wrong: izraz_pridruzivanja");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters izraz() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);

        if (firstNode.startsWith("<izraz_pridruzivanja>")){
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith("<izraz>")){
            load_next_node(); //ZAREZ
            ReturnParameters retPar2 = check_node(load_next_node());
            params.type = retPar2.type;

        }else {
            System.err.println("Wrong: izraz");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters slozena_naredba(ReturnParameters parameters, String returnType) {
        ReturnParameters params = new ReturnParameters();

        //add new context TODO: možda samo za jedan treba
        Context cnx = new Context();
        cnx.locaclVars = new ArrayList<>();

        if (!returnType.equals("")) cnx.returnType = returnType;

        if (parameters != null){
            for (int i = 0; i < parameters.types.size(); i++){
                MyIDN newIDN = new MyIDN();
                newIDN.name = parameters.names.get(i);
                newIDN.type = parameters.types.get(i);
                cnx.locaclVars.add(newIDN);
            }
        }
        manager.addLast(cnx);

        //main part
        load_next_node(); // L_VIT_ZAGRADA
        check_node(load_next_node());

        String nextNode = load_next_node();

        if (nextNode.startsWith("<lista_naredbi>")){
            check_node(nextNode);
            load_next_node();

        }else if (!nextNode.startsWith("D_VIT_ZAGRADA")){
            System.err.println("Error: slozena_naredba");
            System.exit(-1);
        }

        //remove context
        manager.removeLast();
        return params;
    }

    private static void lista_naredbi() {
        String firstNode = load_next_node();
        check_node(firstNode);

        if (firstNode.startsWith("<lista_naredbi>")){
            check_node(load_next_node());

        }else if (!firstNode.startsWith("<naredba>")){
            System.err.println("Wrong: slozena_naredba");
            System.exit(-1);
        }
    }

    private static void naredba() {
        check_node(load_next_node());
    }

    private static ReturnParameters izraz_naredba() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        if (firstNode.startsWith("<izraz>")){
            ReturnParameters retPar = check_node(firstNode);
            load_next_node(); //TOCKAZAREZ
            params.type = retPar.type;

        }else if (firstNode.startsWith("TOCKAZAREZ")){
            params.type = "int";

        }else {
            System.err.println("Wrong: izraz_naredba");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters naredba_grananja() {
        ReturnParameters params = new ReturnParameters();
        String endWordIF = createErrorEndWord(load_next_node()); //KR_IF
        String endWordLZAGRADA = createErrorEndWord(load_next_node()); //L_ZAGRADA
        ReturnParameters retPar = check_node(load_next_node()); //<izraz>
        String endWordDZAGRADA = createErrorEndWord(load_next_node()); //D_ZAGRADA

        if (!isImplicitCastable(retPar.type, "int")){
            //error
            load_next_node(); // <naredba>
            errorLoadNode();
            String desNode = load_next_node();
            String out = "<naredba_grananja> ::= "+endWordIF+" "+endWordLZAGRADA+" <izraz> "+endWordDZAGRADA+" <naredba>";

            if (desNode.startsWith("KR_ELSE")) out += " " + createErrorEndWord(desNode) +" <naredba>";

            System.out.println(out);
            System.err.println("Error: for expression is not INT!");
            System.exit(-1);
        }
        check_node(load_next_node()); // before else (<naredba>)

        //else-part
        String nextNode = load_next_node();

        if (nextNode.startsWith("KR_ELSE")) check_node(load_next_node()); // after else
        else bufferRow.addLast(nextNode);

        return params;
    }

    private static ReturnParameters naredba_petlje() {
        ReturnParameters params = new ReturnParameters();
        loopDepth++;

        String firstEndWord = createErrorEndWord(load_next_node());
        String sedoncEndWord = createErrorEndWord(load_next_node());
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);

        if (firstEndWord.startsWith("KR_WHILE")){
            String thirdEndWord = load_next_node(); // D_ZAGRADA

            if (isImplicitCastable(retPar.type, "int")){
                //error
                String out = "<naredba_petlje> ::= "+ firstEndWord + " " + sedoncEndWord + " <izraz> "+ thirdEndWord +" <naredba>";
                System.out.println(out);
                System.err.println("Expected int type!");
                System.exit(-1);
            }
            check_node(load_next_node());

        }else {
            ReturnParameters retPar2 = check_node(load_next_node()); // second node
            String desNode = load_next_node();

            if (!isImplicitCastable(retPar2.type, "int")){
                if (desNode.startsWith("D_ZAGRADA")){
                    String lastEndWord = createErrorEndWord(desNode);
                    String out = "<naredba_petlje> ::= "+ firstEndWord + " " + sedoncEndWord +
                            " <izraz_naredba> <izraz_naredba> "+ lastEndWord +" <naredba>";
                    System.out.println(out);
                    System.err.println("Expected int type!");
                    System.exit(-1);

                }else if (desNode.startsWith("<izraz>")){
                    //TODO: ako je error u izraz -> onda nije dobar ispis
                    check_node(desNode);
                    String lastEndWord = createErrorEndWord(desNode);
                    String out = "<naredba_petlje> ::= "+ firstEndWord + " " + sedoncEndWord +
                            " <izraz_naredba> <izraz_naredba> <izraz> "+ lastEndWord +" <naredba>";
                    System.out.println(out);
                    System.err.println("Expected int type!");
                    System.exit(-1);
                }
            }

            if (desNode.startsWith("D_ZAGRADA")){
                check_node(load_next_node());

            }else if (desNode.startsWith("<izraz>")){
                check_node(desNode);
                check_node(load_next_node());
            }

        }

        loopDepth--;
        return params;
    }

    private static ReturnParameters naredba_skoka() {
        ReturnParameters params = new ReturnParameters();
        String firstEndWord = createErrorEndWord(load_next_node());
        String secondNode = load_next_node();

        if (firstEndWord.startsWith("KR_RETURN")){
            if (secondNode.startsWith("<izraz>")){

                ReturnParameters retPar = check_node(secondNode);
                String lastEndWord = createErrorEndWord(load_next_node()); //TOCKAZAREZ
                String retTypeOfFunc = manager.getLast().returnType;

                if (!isImplicitCastable(retPar.type, retTypeOfFunc)){
                    //error
                    System.out.println("<naredba_skoka> ::= " + firstEndWord + " <izraz> " + lastEndWord);
                    System.err.println("Error: Wrong return type!");
                    System.exit(-1);
                }

            }else if (secondNode.startsWith("TOCKAZAREZ")){
                String secondEndWord = createErrorEndWord(secondNode);

                if (!manager.getLast().returnType.equals("void")){
                    //error
                    System.out.println("<naredba_skoka> ::= " + firstEndWord + " " + secondEndWord);
                    System.err.println("Error: return type must be void!");
                    System.exit(-1);
                }
            }

        }else {
            if (loopDepth < 1){
                //error
                System.out.println("<naredba_skoka> ::= " + firstEndWord + " " + createErrorEndWord(secondNode));
                System.err.println("Error: Out of loop!");
                System.exit(-1);
            }
        }

        return params;
    }

    private static void prijevodna_jedinica() {
        String firstNode = load_next_node();
        check_node(firstNode);

        if (firstNode.startsWith("<prijevodna_jedinica>")){
            check_node(load_next_node());

        }else if (!firstNode.startsWith("<vanjska_deklaracija>")){
            System.err.println("Wrong: prijevodna_jedinica");
            System.exit(-1);
        }
    }

    private static void vanjska_deklaracija() {
        check_node(load_next_node());
    }

    private static ReturnParameters definicija_funkcije() {
        ReturnParameters params = new ReturnParameters();
        ReturnParameters retPar = check_node(load_next_node()); //<ime_tipa>
        String idn = load_next_node();
        String l_zagrada = load_next_node();
        String desNode = load_next_node();

        //2. both
        if (retPar.type.startsWith("const")){
            idn = createErrorEndWord(idn);
            l_zagrada = createErrorEndWord(l_zagrada);
            String out;

            if (desNode.startsWith("KR_VOID")){
                desNode = createErrorEndWord(desNode);
                String lastEndWord = createErrorEndWord(load_next_node());
                out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" "+desNode+" "+lastEndWord+" <slozena_naredba>";

            }else {
                errorLoadNode();
                String lastEndWord = createErrorEndWord(load_next_node());
                out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" <lista_parametara> "+lastEndWord+" <slozena_naredba>";

            }

            System.out.println(out);
            System.err.println("Error: Type is not const!");
            System.exit(-1);
        }



        if (desNode.startsWith("KR_VOID")){
            String funcName = createErrorEndWord(idn);
            String idnName = funcName.substring(funcName.indexOf(",") + 1, funcName.length()-1);
             funcName = idnName + "(void->"+ retPar.type + ")";

            //3.
            for (MyIDN function: manager.getFirst().locaclVars) {
                if (function.type.startsWith(idnName)){
                    if (function.definedFunction){
                        //error 3.
                        idn = createErrorEndWord(idn);
                        l_zagrada = createErrorEndWord(l_zagrada);
                        desNode = createErrorEndWord(desNode);
                        String lastEndWord = createErrorEndWord(load_next_node());
                        String out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" "+desNode+" "+lastEndWord+" <slozena_naredba>";
                        System.out.println(out);
                        System.err.println("Error: Function already defined!");
                        System.exit(-1);

                    }else {
                        if (!function.type.equals(funcName)){
                            //Error 4.
                            idn = createErrorEndWord(idn);
                            l_zagrada = createErrorEndWord(l_zagrada);
                            desNode = createErrorEndWord(desNode);
                            String lastEndWord = createErrorEndWord(load_next_node());
                            String out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" "+desNode+" "+lastEndWord+" <slozena_naredba>";
                            System.out.println(out);
                            System.err.println("Error: Declaration wrong!");
                            System.exit(-1);
                        }

                    }
                }
            }

            //5.
            MyIDN newIDN = new MyIDN();
            newIDN.type = funcName;
            newIDN.name = idnName;
            newIDN.definedFunction = true;
            manager.getFirst().locaclVars.add(newIDN);

            load_next_node();//D_ZAGRADA
            load_next_node(); //slozena_naredba
            slozena_naredba(null, retPar.type);

        }else if (desNode.startsWith("<lista_parametara>")){
            String funcName = createErrorEndWord(idn);
            funcName = funcName.substring(funcName.indexOf(",") + 1, funcName.length()-1);
            String idnName = funcName;

            //3.
            for (MyIDN function: manager.getFirst().locaclVars) {
                if (function.type.startsWith(idnName)){
                    if (function.definedFunction){
                        //error 3.
                        idn = createErrorEndWord(idn);
                        l_zagrada = createErrorEndWord(l_zagrada);
                        errorLoadNode(); //for <lista_parametara>
                        String lastEndWord = createErrorEndWord(load_next_node());
                        String out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" <lista_parametara> "+lastEndWord+" <slozena_naredba>";
                        System.out.println(out);
                        System.err.println("Error: Function already defined!");
                        System.exit(-1);
                    }
                }
            }

            ReturnParameters retPar2 = check_node(desNode);
            funcName += "(" + arrayToString(retPar2.types) + "->" + retPar.type + ")";

            for (MyIDN function: manager.getFirst().locaclVars) {
                if (function.type.startsWith(idnName)){
                    if (!function.type.equals(funcName)){
                        //error
                        idn = createErrorEndWord(idn);
                        l_zagrada = createErrorEndWord(l_zagrada);
                        String lastEndWord = createErrorEndWord(load_next_node());
                        String out = "<definicija_funkcije> ::= <ime_tipa> "+idn+" "+l_zagrada+" <lista_parametara> "+lastEndWord+" <slozena_naredba>";
                        System.out.println(out);
                        System.err.println("Error: Wrong declaration!");
                        System.exit(-1);
                    }
                }
            }

            //5.
            MyIDN newIDN = new MyIDN();
            newIDN.type = funcName;
            newIDN.name = idnName;
            newIDN.definedFunction = true;
            manager.getFirst().locaclVars.add(newIDN);

            load_next_node();//D_ZAGRADA
            load_next_node(); //slozena_naredba
            slozena_naredba(retPar2, retPar.type);

        }else{
            System.err.println("Error: definicija_funkcije");
            System.exit(-1);
        }


        return params;
    }

    private static ReturnParameters lista_parametara() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);
        params.names = new ArrayList<>();
        params.types = new ArrayList<>();

        if (firstNode.startsWith("<deklaracija_parametra>")){
            params.names.add(retPar.name);
            params.types.add(retPar.type);

        }else if (firstNode.startsWith("<lista_parametara>")){
            String firstEndWord = createErrorEndWord(load_next_node()); // ZAREZ
            ReturnParameters retPar2 = check_node(load_next_node());

            params.names = new ArrayList<>(retPar.names);
            params.names.add(retPar2.name);

            params.types = new ArrayList<>(retPar.types);
            params.types.add(retPar2.type);

            if (retPar.names.contains(retPar2.name)){
                //error
                System.out.println("<lista_parametara> ::= <lista_parametara> "+firstEndWord+" <deklaracija_parametra>");
                System.err.println("Error: Same name in lista_parametara!");
                System.exit(-1);
            }

        }else {
            System.err.println("Wrong: lista_parametara");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters deklaracija_parametra() {
        ReturnParameters params = new ReturnParameters();
        ReturnParameters retPar = check_node(load_next_node());
        String firstEndWord = createErrorEndWord(load_next_node());

        String nextNode1 = load_next_node();
        String nextNode2 = load_next_node();

        if (nextNode1.startsWith("L_UGL_ZAGRADA") && nextNode2.startsWith("D_UGL_ZAGRADA")){
            params.type = "array " + retPar.type;
            params.name = firstEndWord.substring(firstEndWord.indexOf(",") + 1, firstEndWord.length()-1);

            if (retPar.type.equals("void")){
                String secEndWord = createErrorEndWord(nextNode1);
                String thirdEndWord = createErrorEndWord(nextNode2);
                System.out.println("<deklaracija_parametra> ::= <ime_tipa> " +firstEndWord+" "+secEndWord+" "+thirdEndWord);
                System.err.println("Error: Type can not be void!");
                System.exit(-1);
            }

        }else{
            bufferRow.addLast(nextNode1);
            bufferRow.addLast(nextNode2);

            params.type = retPar.type;
            params.name = firstEndWord.substring(firstEndWord.indexOf(",") + 1, firstEndWord.length()-1);

            if (retPar.type.equals("void")){
                //error
                System.out.println("<deklaracija_parametra> ::= <ime_tipa> " + firstEndWord);
                System.err.println("Error: Type can not be void!");
                System.exit(-1);
            }
        }

        return params;
    }

    private static void lista_deklaracija() {
        String firstNode = load_next_node();
        check_node(firstNode);

        if (firstNode.startsWith("<lista_deklaracija>")){
            check_node(load_next_node());

        }else if (!firstNode.startsWith("<deklaracija>")){
            System.err.println("Error: lista_deklaracija");
            System.exit(-1);
        }

    }

    private static void deklaracija() {
        ReturnParameters retPar = check_node(load_next_node());
        load_next_node();
        lista_init_deklaratora(retPar.type);
        load_next_node(); //TOCKAZAREZ
    }

    private static void lista_init_deklaratora(String type) {
        String firstNode = load_next_node();

        if (firstNode.startsWith("<init_deklarator>")){
            init_deklarator(type);

        }else if (firstNode.startsWith("<lista_init_deklaratora>")){
            lista_init_deklaratora(type);
            load_next_node(); // ZAREZ
            load_next_node(); // <init_deklarator>
            init_deklarator(type);
        }
    }

    private static void init_deklarator(String type) {
        load_next_node(); //firstnode
        ReturnParameters retParIzDecl = izravni_deklarator(type);
        String nextNode = load_next_node();

        if (nextNode.startsWith("OP_PRIDRUZI")){
            String firstEndWord = createErrorEndWord(nextNode); // OP_PRIDRUZI
            ReturnParameters retPar = check_node(load_next_node()); //<inicijalizator>

            if (retParIzDecl.type.startsWith("array")){

                if (retPar.numOfElem > retParIzDecl.numOfElem){
                    //error
                    System.out.println("<init_deklarator> ::= <izravni_deklarator> "+ firstEndWord+" <inicijalizator>");
                    System.err.println("Error: inicijalizator.br > declarator.br");
                    System.exit(-1);
                }

                String leftType = retParIzDecl.type.substring(6);
                for (int i = 0; i <  retPar.types.size(); i++) {

                    if (!isImplicitCastable(retPar.types.get(i), leftType)){
                        //error
                        System.out.println("<init_deklarator> ::= <izravni_deklarator> "+ firstEndWord+" <inicijalizator>");
                        System.err.println("Error: types not castable! " + retPar.types.get(i) + "->" + leftType);
                        System.exit(-1);
                    }
                }

            }else {
                if (!isImplicitCastable(retPar.type, retParIzDecl.type)){
                    //error
                    System.out.println("<init_deklarator> ::= <izravni_deklarator> "+ firstEndWord+" <inicijalizator>");
                    System.err.println("Error: types not castable! " + retPar.type + "->" + retParIzDecl.type);
                    System.exit(-1);
                }
            }

        }else {
            bufferRow.addLast(nextNode);
            if (retParIzDecl.type.startsWith("const") || retParIzDecl.type.startsWith("array const")){
                //error
                System.out.println("<init_deklarator> ::= <izravni_deklarator>");
                System.err.println("Error: init_deklarator can not be const!");
                System.exit(-1);
            }
        }
    }

    private static ReturnParameters izravni_deklarator(String type) {
        ReturnParameters params = new ReturnParameters();

        String firstEndWord = load_next_node(); //IDN
        String keyWord = firstEndWord.substring(0, firstEndWord.indexOf(" "));
        String row = firstEndWord.substring(keyWord.length() + 1, firstEndWord.indexOf(" ", keyWord.length() + 1));
        String lexWord = firstEndWord.substring(keyWord.length() + row.length() + 2);

        String nextNode = load_next_node();

        if (nextNode.startsWith("L_UGL_ZAGRADA")){
            firstEndWord = createErrorEndWord(firstEndWord); //IDN
            String secondEndWord = createErrorEndWord(nextNode); //L_UGL_ZAGRADA
            String thirdEndWord = createErrorEndWord(load_next_node()); //BROJ
            String fourthEndWord = createErrorEndWord(load_next_node()); //D_UGL_ZAGRADA

            params.type = "array " + type;
            String num  =thirdEndWord.substring(thirdEndWord.indexOf(",") + 1, thirdEndWord.length()-1);
            params.numOfElem = Integer.parseInt(num);

            //1.
            if (type.equals("void")){
                //error
                System.out.println("<izravni_deklarator> ::= "+firstEndWord+" "+secondEndWord+" "+thirdEndWord+" "+fourthEndWord);
                System.err.println("Error: IDN can not be void!");
                System.exit(-1);

            }

            //2.
            for (MyIDN idn : manager.getLast().locaclVars){
                if (idn.name.equals(lexWord)){
                    //error
                    System.out.println("<izravni_deklarator> ::= "+firstEndWord+" "+secondEndWord+" "+thirdEndWord+" "+fourthEndWord);
                    System.err.println("Error: IDN already declared!");
                    System.exit(-1);
                }
            }

            //3.
            if (params.numOfElem < 1 || params.numOfElem > 1024){
                //error
                System.out.println("<izravni_deklarator> ::= "+firstEndWord+" "+secondEndWord+" "+thirdEndWord+" "+fourthEndWord);
                System.err.println("Error: BROJ out of range! -> " + params.numOfElem);
                System.exit(-1);
            }

            //4.
            MyIDN newIDN = new MyIDN();
            newIDN.name = lexWord;
            newIDN.type = params.type; //TODO: mozda bez array
            manager.getLast().locaclVars.add(newIDN);


        }else if (nextNode.startsWith("L_ZAGRADA")) {
            String thirdNode = load_next_node();

            if (thirdNode.startsWith("KR_VOID")){
                String fourthEndWord = createErrorEndWord(load_next_node()); //D_ZAGRADA
                String funcName = lexWord + "(void->" + type +")";
                declaredFunctions.add(funcName);
                params.type = funcName;
                params.name = lexWord;
                boolean alreadyDeclared = false;

                //1.
                for (MyIDN func: manager.getLast().locaclVars) {
                    if (func.type.startsWith(lexWord)){
                        if (!func.type.equals(funcName)){
                            //error
                            firstEndWord = createErrorEndWord(firstEndWord);
                            String secondEndWord = createErrorEndWord(nextNode);
                            String thirdEndWord = createErrorEndWord(thirdNode);
                            System.out.println("<izravni_deklarator> ::= "+firstEndWord+" "+secondEndWord+" "+thirdEndWord+" "+fourthEndWord);
                            System.err.println("Error: Wrong declaration!");
                            System.exit(-1);
                        }else {
                            alreadyDeclared = true;
                            break;
                        }

                    }
                }

                //2.
                if (!alreadyDeclared){
                    MyIDN newIDN = new MyIDN();
                    newIDN.type = funcName;
                    newIDN.name = lexWord;
                    manager.getLast().locaclVars.add(newIDN);
                }

            }else if (thirdNode.startsWith("<lista_parametara>")){
                ReturnParameters retPar = check_node(thirdNode);
                String lastEndWord = load_next_node();


                String funcName = lexWord + "("+ arrayToString(retPar.types) +"->" + type +")";
                declaredFunctions.add(funcName);
                params.type = funcName;
                params.name = lexWord;

                boolean alreadyDeclared = false;
                //1.
                for (MyIDN func: manager.getLast().locaclVars) {
                    if (func.type.startsWith(lexWord)){
                        if (!func.type.equals(funcName)){
                            //error
                            firstEndWord = createErrorEndWord(firstEndWord);
                            String secondEndWord = createErrorEndWord(nextNode);
                            String thirdEndWord = createErrorEndWord(lastEndWord);
                            System.out.println("<izravni_deklarator> ::= "+firstEndWord+" "+secondEndWord+" <lista_parametara> "+thirdEndWord);
                            System.err.println("Error: Wrong declaration!");
                            System.exit(-1);

                        }else {
                            alreadyDeclared = true;
                            break;
                        }
                    }
                }

                //2.
                if (!alreadyDeclared){
                    MyIDN newIDN = new MyIDN();
                    newIDN.type = funcName;
                    newIDN.name = lexWord;
                    manager.getLast().locaclVars.add(newIDN);
                }


            }else {
                //error
                System.err.println("Error: izdravni_deklarator");
                System.exit(-1);
            }


        }else{
            bufferRow.addLast(nextNode);
            params.type = type;

            //1.
            if (type.equals("void")){
                //error
                System.out.println("<izravni_deklarator> ::= " + createErrorEndWord(firstEndWord));
                System.err.println("Error: IDN can not be void!");
                System.exit(-1);
            }

            //2.
            for (MyIDN idn : manager.getLast().locaclVars){
                if (idn.name.equals(lexWord)){
                    //error
                    System.out.println("<izravni_deklarator> ::= " + createErrorEndWord(firstEndWord));
                    System.err.println("Error: IDN already declared in local area!");
                    System.exit(-1);
                }
            }

            //3.
            MyIDN newIDN = new MyIDN();
            newIDN.name = lexWord;
            newIDN.type = type;
            manager.getLast().locaclVars.add(newIDN);
        }


        return params;
    }

    private static ReturnParameters inicijalizator() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();


        if (firstNode.startsWith("<izraz_pridruzivanja>")){
            int arrayCharLength = isArrayCharInFuture();
            if (arrayCharLength > 0){
                ReturnParameters retPar = check_node(firstNode);
                String toFill = retPar.type.startsWith("array ")? retPar.type.substring(6): retPar.type;

                params.types = new ArrayList<>();
                for (int i = 0; i < arrayCharLength; i++) params.types.add(toFill);

                params.numOfElem = params.types.size(); //TODO: ovo tu je upitno malo dal ide +1

            }else {
                ReturnParameters retPar = check_node(firstNode);
                params.type = retPar.type;
            }


        }else if (firstNode.startsWith("L_VIT_ZAGRADA")){
            ReturnParameters retPar = check_node(load_next_node()); //<lista_izraza_pridruzivanja>
            load_next_node(); //D_VIT_ZAGRADA

            params.numOfElem = retPar.numOfElem;
            params.types = new ArrayList<>(retPar.types);

        }else {
            //error
            System.err.println("Wrong: inicijalizator!");
            System.exit(-1);
        }

        return params;

    }

    private static ReturnParameters lista_izraza_pridruzivanja() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        ReturnParameters retPar = check_node(firstNode);
        params.types = new ArrayList<>();

        if (firstNode.startsWith("<izraz_pridruzivanja>")){
            params.types.add(retPar.type);
            params.numOfElem = 1;

        }else if (firstNode.startsWith("<lista_izraza_pridruzivanja>")){
            load_next_node(); // ZAREZ
            ReturnParameters retPar2 = check_node(load_next_node()); //<izraz_pridruzivanja>

            params.types = new ArrayList<>(retPar.types);
            params.types.add(retPar2.type);

            params.numOfElem = params.types.size();

        }else {
            System.err.println("Error: lista_izraza_pridruzivanja");
            System.exit(-1);
        }

        return params;
    }

    static class ReturnParameters {
        public String type;
        public boolean l_expression;
        public String name;
        public int numOfElem;
        public ArrayList<String> types;
        public ArrayList<String> names;

        public ReturnParameters() {
            l_expression = false;
        }
    }

    static class Context{
        public ArrayList<MyIDN> locaclVars;
        public String name;
        public String returnType;

        public Context() {
            this.name = "";
            this.returnType = "";
            this.locaclVars = new ArrayList<>();
        }
    }

    static class MyIDN {
        public String type;
        public String name;
        public boolean l_expression;
        public boolean definedFunction;
        public int numOfElem;

        public MyIDN() {
            type = name = "";
            l_expression = false;
            definedFunction = false;
            numOfElem = 0;
        }
    }

}
