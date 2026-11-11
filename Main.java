import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Reads student.xml, prints each student, then POSTs the data
 * to the dummy REST API at jsonplaceholder.typicode.com.
 *
 * Compile and run from this folder (Java 11 or newer):
 *   javac Main.java
 *   java Main
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Open student.xml and turn it into a document we can search
        File xmlFile = new File("student.xml");
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlFile);

        // 2. Find every <student> tag
        NodeList students = document.getElementsByTagName("student");

        // We will also build a simple JSON array to send to the API
        StringBuilder json = new StringBuilder();
        json.append("[");

        System.out.println("Students from student.xml:");

        for (int i = 0; i < students.getLength(); i++) {
            Element student = (Element) students.item(i);

            // Read the text inside <name> and <marks>
            String name = student.getElementsByTagName("name").item(0).getTextContent();
            String marks = student.getElementsByTagName("marks").item(0).getTextContent();

            System.out.println("Name: " + name + ", Marks: " + marks);

            // Add a comma before every student except the first
            if (i > 0) {
                json.append(",");
            }
            json.append("{\"name\":\"").append(name)
                    .append("\",\"marks\":").append(marks).append("}");
        }

        json.append("]");

        // 3. Send the JSON to the dummy API (it accepts the POST and echoes it back)
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println();
        System.out.println("Sent to API. Status code: " + response.statusCode());
        System.out.println("API response: " + response.body());
    }
}
