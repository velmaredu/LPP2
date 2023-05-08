import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    // private static Scanner scanner;

    public static void main(String[] args) {

        // // Para pedir por fichero
        if (args.length < 1) {
            System.err.println("Debe especificar el nombre del fichero de entrada.");
            System.exit(1);
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(new File(args[0])))) {
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                parse(line, lineNumber++);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el fichero de entrada: " + e.getMessage());
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Error de sintaxis en la línea " + e.getLineNumber() + ": " + e.getMessage());
            System.exit(1);
        }

        // scanner = new Scanner(System.in);
        // System.out.print("Ingrese una expresión: ");
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
        if (tokens.length < 2) {
            throw new ParseException(lineNumber,"La expresión es demasiado corta.");
        }
        String command = tokens[0];
        String argument = tokens[1];
        switch (command) {
            case "start":
                if (!argument.equals("timer")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'timer'.");
                }
                if (tokens.length < 4 || !tokens[2].equals("seconds") || !tokens[3].equals("to") || tokens.length < 5) {
                    throw new ParseException(lineNumber,"Sintaxis incorrecta para 'start timer'.");
                }
                try {
                    Integer.parseInt(tokens[4]);
                } catch (NumberFormatException e) {
                    throw new ParseException(lineNumber,"Se esperaba un valor numérico para el tiempo de espera.");
                }
                break;
            case "stop":
                if (!argument.equals("timer")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'timer'.");
                }
                if (tokens.length > 2) {
                    throw new ParseException(lineNumber,"Demasiados argumentos para 'stop timer'.");
                }
                break;
            case "set":
                if (tokens.length < 3) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'on' o 'off'.");
                }
                String state = tokens[2];
                if (!state.equals("on") && !state.equals("off")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'on' o 'off'.");
                }
                if (!argument.equals("switch")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'switch'.");
                }
                break;
            case "waiting":
                if (!argument.equals("for")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'for'.");
                }
                if (tokens.length < 4) {
                    throw new ParseException(lineNumber,"Sintaxis incorrecta para 'waiting for'.");
                }
                String event = tokens[3];
                switch (event) {
                    case "timer":
                        if (tokens.length < 5 || !tokens[4].equals("while") && !tokens[4].equals("until")) {
                            throw new ParseException(lineNumber,"Sintaxis incorrecta para 'wait while/until timer'.");
                        }
                        break;
                    case "signal":
                        if (tokens.length < 5 || !tokens[4].equals("event") || tokens.length < 6) {
                            throw new ParseException(lineNumber,"Sintaxis incorrecta para 'wait while/until signal event'.");
                        }
                        String signal = tokens[5];
                        if (!signal.equals("switch")) {
                            throw new ParseException(lineNumber,"Se esperaba el argumento 'switch'.");
                        }
                        break;
                    default:
                        throw new ParseException(lineNumber,"Se esperaba el argumento 'timer' o 'signal'.");
                }
                String condition = tokens[4];
                if (!condition.equals("while") && !condition.equals("until")) {
                    throw new ParseException(lineNumber,"Se esperaba la condición 'while' o 'until'.");
                }
                break;
            case "run":
                if (tokens.length < 2 || !tokens[1].equals("procedure")) {
                    throw new ParseException(lineNumber,"Se esperaba el argumento 'procedure'.");
                }
                break;
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