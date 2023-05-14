import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    // private static Scanner scanner;
    static ArrayList<String> procedures = new ArrayList<>();
    static ArrayList<String> timers = new ArrayList<>();
    static boolean procedure = false;
    public static void main(String[] args) {
        // Para pedir por fichero
        if (args.length < 1) {
            System.err.println("Debe especificar el nombre del fichero de entrada.");
            System.exit(1);
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(new File(args[0])))) {
        //try (BufferedReader br = new BufferedReader(new FileReader(new File("E:\\Universidad\\2022-2023\\SegundoCuatri\\LP\\Laboratorio\\Practica2\\LPP2\\example03.txt")))){
            String line;
            boolean puntoFinal = false;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if(puntoFinal){
                    throw new ParseException(lineNumber,"No se esperaba un '.'.");
                }
                if(!line.equals("") && !line.equals(".")){
                    parse(line, lineNumber++);
                }
                if(line.equals(".")){
                    puntoFinal = true;
                }
            }
            if (puntoFinal){
                System.out.println("El codigo es correcto");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el fichero de entrada: " + e.getMessage());
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Error de sintaxis en la línea " + e.getLineNumber() + ": " + e.getMessage());
            System.exit(1);
        }

        //scanner = new Scanner(System.in);
        //System.out.print("Ingrese una expresión: ");
        // String input = scanner.nextLine();
        // try {
        //     parse(input, 0);
        //     System.out.println("Código válido.");
        // } catch (ParseException e) {
        //     System.out.println("Error de sintaxis: " + e.getMessage());
        // }
    }

    private static void parse(String input, int lineNumber) throws ParseException {
        String[] tokens = input.split(" ");
        if (tokens.length < 2 && !tokens[0].equals(";") && !tokens[0].equals(".") && !tokens[0].equals("")) {
            throw new ParseException(lineNumber,"La expresión es demasiado corta.");
        }
        String command = tokens[0];
        String argument = "";
        if(tokens.length>=2){
            argument = tokens[1];
        }
        switch (command) {
            case "start":
                if (!argument.contains("timer")){
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'timer'.");
                }
                try {
                    Integer.parseInt(tokens[2]);
                    timers.add(tokens[1]);
                } catch (NumberFormatException e) {
                    throw new ParseException(lineNumber,"Se esperaba un valor numérico para el tiempo de espera.");
                }
                if(tokens[tokens.length-1].equals(";") || tokens[tokens.length-1].contains(";")){
                    procedure=false;
                }
                break;
            case "stop":
                if (!argument.contains("timer")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'timer'.");
                }
                if (tokens.length > 2 && !tokens[tokens.length-1].equals(";")) {
                    throw new ParseException(lineNumber,"Demasiados argumentos para 'stop timer'.");
                }
                if(tokens[tokens.length-1].equals(";") || tokens[tokens.length-1].contains(";")){
                    procedure=false;
                }
                break;
            case "set":
                //Revisar contador y mensaje de la excepción
                if (tokens.length < 3) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'on' o 'off'.");
                }
                if (!argument.equals("on") && !argument.equals("off")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'on' o 'off'.");
                }
                if (!tokens[2].contains("light")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'light'.");
                }
                if(tokens[tokens.length-1].equals(";") || tokens[tokens.length-1].contains(";")){
                    procedure=false;
                }
                break;
            case "wait":
                if (!argument.equals("while") && !argument.equals("until")){
                    throw new ParseException(lineNumber,"Se esperab un argumento 'while' o un argumento 'until'.");
                }else{
                   if (timers.contains(tokens[2])){
                       if(tokens[tokens.length-1].equals(";")){
                           procedure=false;
                       }
                       break;
                   }else{
                       if( tokens[2].equals("signal")){
                           if (!tokens[3].equals(" ")){
                               try {
                                   Integer.parseInt(tokens[3]);
                                   throw new ParseException(lineNumber,"Se esperab un argumento 'signal' o 'timer'.");
                               }catch (NumberFormatException e){
                                   if(tokens[tokens.length-1].equals(";")){
                                       procedure=false;
                                   }
                                   break;
                               }
                           }
                       }
                       else{
                           throw new ParseException(lineNumber,"Se esperab un argumento 'signal' o 'timer'.");
                       }
                   }
                }
                if(tokens[tokens.length-1].equals(";") || tokens[tokens.length-1].contains(";")){
                    procedure=false;
                }
                break;
            case "run":
                if (procedure){
                    throw new ParseException(lineNumber,"No se esperaba un argumento 'run'.");
                }
                //Mirar numero de tokesn
                if (tokens.length < 2 || !procedures.contains(tokens[1])) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'procedure'.");
                }
                if(tokens[tokens.length-1].equals(".") || tokens[tokens.length-1].contains(";")){
                    System.out.println("El codigo es correcto");
                }
                break;
            case "procedure":
                if(argument == null){
                    throw new ParseException(lineNumber,"Se esperaba un nombre.");
                }else{
                    if(procedures.contains(argument)){
                        throw new ParseException(lineNumber,"No se pueden redefinir procedimientos.");
                    }
                    procedures.add(argument);
                    procedure = true;
                }
                if(tokens[tokens.length-1].equals(";")){
                    procedure=false;
                }
                break;
            case ";":
                procedure = false;
                break;
            case "":
                if(tokens[1].equals("")){
                    String nueva ="";
                    for(int i = 2; i<tokens.length;i++){
                        nueva = nueva+tokens[i]+" ";
                    }
                    parse(nueva,lineNumber);
                    break;
                }else{
                    throw new ParseException(lineNumber,"Se esperaba otro formato.");
                }
            default:
                throw new ParseException(lineNumber,"Comando no reconocido: " + command);
        }
    }
}

class ParseException extends Exception {
    private int lineNumber;

    public ParseException(int lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}