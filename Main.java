import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static String processRequest(String request) {
        Pattern findQuotes = Pattern.compile("\"[^\"]+\"");


        Matcher m = findQuotes.matcher(request);

        StringBuffer result = new StringBuffer();
        while (m.find())
            m.appendReplacement(result, m.group().replace(" ", "~"));
        m.appendTail(result);

        return result.toString().replace("\"", "");
    }

    public static void main(String[] args) {
        MetroList list = new MetroList(args[0]);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String[] request = processRequest(scanner.nextLine()).split(" ");
            if (request[0].equals("/output")) {
                list.printLine(request[1].replaceAll("~", " "));
            } else if (request[0].equals("/append")) {
                list.addNewStation(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "),
                        Integer.parseInt(request[3].replaceAll("~", " "), 10));
            } else if (request[0].equals("/add-head")) {
                list.addHeadStation(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "),
                        Integer.parseInt(request[3].replaceAll("~", " "), 10));
            } else if (request[0].equals("/remove")) {
                list.removeStation(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "));
            } else if (request[0].equals("/connect")) {
                list.connectStation(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "),
                        request[3].replaceAll("~", " "),
                        request[4].replaceAll("~", " "));
            } else if (request[0].equals("/fastest-route")) {
                list.createRoute(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "),
                        request[3].replaceAll("~", " "),
                        request[4].replaceAll("~", " "),
                        true);
            } else if (request[0].equals("/route")) {
                list.createRoute(request[1].replaceAll("~", " "),
                        request[2].replaceAll("~", " "),
                        request[3].replaceAll("~", " "),
                        request[4].replaceAll("~", " "),
                        false);
            } else if (request[0].equals("/exit")) {
                break;
            }

            // /fastest-route "Victoria line" Brixton "Northern line" Angel
        }

    }
}