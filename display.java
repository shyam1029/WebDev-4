import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Base64;

@WebServlet("/display")
public class display extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/project4";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String ALGORITHM = "AES";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String villageID = (String) session.getAttribute("villageID");

        if (villageID == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Village ID not found in session.");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        StringBuilder htmlResponse = new StringBuilder();

        htmlResponse.append("<html><head><meta charset='UTF-8'><title>Grama Panchayat</title>")
                            .append("<link rel='stylesheet' href='stl.css'>")
                            .append("<link href='https://fonts.googleapis.com/css2?family=poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>")
                            .append("<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css'>")
                            .append("</head>")
                            .append("<body><header><div class='navbar'>")
                            .append("<div class='logo'><img src='download.jpg' alt='my company name' style='max-width:80px; height:auto; border-radius:100%;'></a>")
                            .append("<h1><strong>Grama Panchayat</strong></h1></div>")
                            .append("<nav><ul id='MenuItems'><li><button class='dynamic-button'><a href='villageDetails'>Back</a></button></li></ul></nav></div></header>")
                            .append("<div class='containers'><div class='animated-border'><div><h3 align='center'>Notice & Tenders</h3>");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Prepare SQL query to fetch encrypted tender details
            String sql = "SELECT * FROM tender_details";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String encryptedTenderId = resultSet.getString("encrypted_tender_id");
                String encryptedDescription = resultSet.getString("encrypted_description");

                String decryptedTenderId = decrypt(encryptedTenderId, villageID);
                String decryptedDescription = decrypt(encryptedDescription, villageID);

                htmlResponse.append("<div class='card'>")
                            .append("<div class='card-header'>Tender ID: ").append(decryptedTenderId)
                            .append("<div class='card-content'>").append(decryptedDescription).append("</div>")
                            .append("</div>")
                            .append("</div>");
            }

            htmlResponse.append("</div></div></div>")
                        .append("<footer><div class='footer'><div class='logo2'><img src='download.jpg' alt='my company name' style='max-width:80px; height:auto; border-radius:100%;'>")
                        .append("<h1><strong>Grama Panchayat</strong></h1></div></div></footer></body></html>");


            out.print(htmlResponse.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private SecretKey generateKey(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(password.getBytes("UTF-8"));
        return new SecretKeySpec(key, ALGORITHM);
    }

    private String decrypt(String data, String password) throws Exception {
        SecretKey key = generateKey(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(data);
        byte[] decryptedData = cipher.doFinal(decodedValue);
        return new String(decryptedData, "UTF-8");
    }
}
