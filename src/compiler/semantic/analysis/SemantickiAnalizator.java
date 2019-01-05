package compiler.semantic.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SemantickiAnalizator {
    private static Scanner sc;

    public static void main(String[] args) throws FileNotFoundException {
        String path = "/home/vlado24/Downloads/PPJ_MultiSPRUT_2/Primjeri radnih direktorija/Sema/Testovi/5/5.proba";
        sc = new Scanner(new File(path));
        check_node(load_next_node());

    }

    //----------HELP FUNCTIONS---------
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
                params = slozena_naredba();
                break;

            case "lista_naredbi":
                params = lista_naredbi();
                break;

            case "naredba":
                params = naredba();
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
                params = prijevodna_jedinica();
                break;

            case "vanjska_deklaracija":
                params = vanjska_deklaracija();

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
                params = lista_deklaracija();
                break;

            case "deklaracija":
                params = deklaracija();
                break;

            case "lista_init_deklaratora":
                params = params = lista_init_deklaratora();
                break;

            case "init_deklarator":
                params = params = init_deklarator();
                break;

            case "izravni_deklarator":
                params = izravni_deklarator();
                break;

            case "inicijalizator":
                params = inicijalizator();
                break;

            case "lista_izraza_pridruzivanja":
                params = lista_izraza_pridruzivanja();
                break;
            default:
                System.out.println("There is no function: " + function);
                System.exit(1);
        }

        return params;
    }

    public static String load_next_node(){
        if (!sc.hasNextLine()){
            System.err.println("Finish too early!");
            System.exit(-1);
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

                if (Character.isLetter(testChar.charAt(0))
                        || Character.isDigit(testChar.charAt(0))
                        || testChar.charAt(0) == '"')
                    return true;
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
            String secondNode = load_next_node();

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

    //---------SEMANTIC FUNCTIONS----------
    private static ReturnParameters primarni_izraz() {
        ReturnParameters params = new ReturnParameters();

        String input = load_next_node();
        String keyWord = input.substring(0, input.indexOf(" "));
        String row = input.substring(keyWord.length() + 1, input.indexOf(" ", keyWord.length() + 1));
        String lexWord = input.substring(keyWord.length() + row.length() + 2);

        switch (keyWord){
            case "IDN":
                //TODO: implementirati tablicu i provjeriti ovdje IDN
                break;

            case "BROJ":
                params.type = "int";

                //1.
                long lng = Long.parseLong(lexWord);
                if (lng > 2147483648L || -lng > 2147483648L){
                    String out = keyWord + "(" + row + "," + lexWord + ")";
                    System.out.println("<primarni_izraz> ::= " + out);
                    System.err.println("Error: int je prevelik!");
                    System.exit(-1);
                }
                break;

            case "ZNAK":
                params.type = "char";

                String testChar = lexWord.substring(1, lexWord.length()-1);
                if (!isCharacter(testChar)){
                    //error
                    String out = keyWord + "(" + row + "," + lexWord + ")";
                    System.out.println("<primarni_izraz> ::= " + out);
                    System.err.println("Error: ovo nije char!");
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
                    String out = keyWord + "(" + row + "," + lexWord + ")";
                    System.out.println("<primarni_izraz> ::= " + out);
                    System.err.println("Error: ovo nije string!");
                    System.exit(-1);
                }
                break;

            case "L_ZAGRADA":
                ReturnParameters retPar = check_node(load_next_node());
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
        ReturnParameters retPar = check_node(firstNode);

        if (firstNode.startsWith("<primarni_izraz>")){
            //2.1.
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else {
            //calculate second node end character
            String secondNode = load_next_node();
            String keyWord = secondNode.substring(0, secondNode.indexOf(" "));
            String row = secondNode.substring(keyWord.length() + 1, secondNode.indexOf(" ", keyWord.length() + 1));
            String lexWord = secondNode.substring(keyWord.length() + row.length() + 2);


            if (secondNode.startsWith("L_UGL_ZAGRADA")){
                //TODO 2.2.
                //2.2.


            }else if (secondNode.startsWith("L_ZAGRADA")){
                String thirdNode = load_next_node();

                if (thirdNode.startsWith("D_ZAGRADA")){
                    //2.3.
                    //params.type = TODO: = funkcija(void -> pov)

                }else if (thirdNode.startsWith("<lista_argumenata>")){
                    //TODO
                    //2.4

                }else {
                    System.err.println("Wrong: postfiks_izraz -> <postfiks_izraz> L_ZAGRADA <lista_argumenata>!");
                    System.exit(-1);
                }

            }else if (secondNode.startsWith("OP_")){
                //2.5. & 2.6.
                params.type = "int";

                if (!retPar.l_expression || !isImplicitCastable(retPar.type, "int")){
                    String out = keyWord + "(" + row + "," + lexWord + ")";
                    System.out.println("<postfiks_izraz> ::= <postfiks_izraz> " + out);
                    System.err.println("Error: Nemogouce castati! -> " + retPar.type + " u int");
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
        return params;
    }

    private static ReturnParameters unarni_izraz() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();

        if (firstNode.startsWith("OP_")){
            params.type = "int";

            ReturnParameters retPar = check_node(load_next_node());
            if (!retPar.l_expression || !isImplicitCastable(retPar.type, "int")){

                String out = createErrorEndWord(firstNode);
                System.out.println("<unarni_izraz> ::= " + out + " <unarni_izraz>");
                System.err.println("Error: Nemogouce castati! -> " + retPar.type + " u INT");
                System.exit(-1);
            }

        }else if (firstNode.startsWith("<postfiks_izraz>")){
            //1.1.
            ReturnParameters retPar = check_node(firstNode);
            params.type = retPar.type;
            params.l_expression = retPar.l_expression;

        }else if (firstNode.startsWith("<unarni_operator>")) {
            //2.3.
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
        System.err.println("UNARNI OPERATOR -> NEDOHVATLJIVO!");
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

        String nextNode = load_next_node();
        if (nextNode.startsWith("<")){
            //<ime_tipa> ::= <specifikator_tipa>
            ReturnParameters retPar = check_node(nextNode);
            params.type = retPar.type;

        }else if (nextNode.startsWith("KR_CONST")){
            //<ime_tipa> ::= KR_CONST <specifikator_tipa>
            //1.
            ReturnParameters retPar = check_node(load_next_node());

            //2.
            if (retPar.type.equals("void")){
                System.out.println("<ime_tipa> ::= "+ retPar.name +" <specifikator_tipa>");
                System.err.println("Error: Ne postoji 'const void'!");
                System.exit(-1);
            }
            params.type = "const " + retPar.type;

        }else {
            System.err.println("Error: ime_tipa -> nepostojeca produkcija");
            System.exit(-1);
        }

        return params;
    }

    private static ReturnParameters specifikator_tipa() {
        ReturnParameters params = new ReturnParameters();

        String[] nextNode = load_next_node().split(" ");
        switch (nextNode[0]){
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

        params.name = nextNode[0] + "(" + nextNode[1]+ "," +nextNode[2] + ")";
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

    private static ReturnParameters slozena_naredba() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters lista_naredbi() {
        ReturnParameters params = new ReturnParameters();
        String firstNode = load_next_node();
        check_node(firstNode);

        if (firstNode.startsWith("<lista_naredbi>")){
            check_node(load_next_node());

        }else if (!firstNode.startsWith("<naredba>")){
            System.err.println("Wrong: slozena_naredba");
            System.exit(-1);
        }
        //TODO isto nema returna
        return params;
    }

    private static ReturnParameters naredba() {
        ReturnParameters params = new ReturnParameters();
        check_node(load_next_node());
        //TODO: pobrisat sve osim check_node...
        return params;
    }

    private static ReturnParameters izraz_naredba() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters naredba_grananja() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters naredba_petlje() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters naredba_skoka() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters prijevodna_jedinica() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters vanjska_deklaracija() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters definicija_funkcije() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters lista_parametara() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters deklaracija_parametra() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters lista_deklaracija() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters deklaracija() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters lista_init_deklaratora() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters init_deklarator() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters izravni_deklarator() {
        ReturnParameters params = new ReturnParameters();
        return params;
    }

    private static ReturnParameters inicijalizator() {
        ReturnParameters params = new ReturnParameters();
        return params;

    }

    private static ReturnParameters lista_izraza_pridruzivanja() {
        ReturnParameters params = new ReturnParameters();
        return params;

    }

    static class ReturnParameters {
        public String type;
        public boolean l_expression;
        public String name;

        public ReturnParameters() {
            l_expression = false;
        }
    }

}
