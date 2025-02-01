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

@WebServlet("/userlogin")
public class  userlogin extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/project4";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String ALGORITHM = "AES";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String villageID = request.getParameter("villageID");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Check user credentials
            String sql = "SELECT * FROM user WHERE EmailID = ? AND EncryptedPassword = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, encrypt(email,password));
            statement.setString(2, encrypt(password, password));
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("villageID", villageID);

                // Fetch tender details
                sql = "SELECT * FROM tender_details";
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();

                // Generate HTML response
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                StringBuilder htmlResponse = new StringBuilder();

                htmlResponse.append("<html><head><meta charset='UTF-8'><title>Grama Panchayat</title>")
                            .append("<link rel='stylesheet' href='stl.css'>")
                            .append("<link href='https://fonts.googleapis.com/css2?family=poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>")
                            .append("<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css'>")
                            .append("</head>")
                            .append("<body><header><div class='navbar'>")
                            .append("<div class='logo'><a href='index.html'><img src='download.jpg' alt='my company name' style='max-width:80px; height:auto; border-radius:100%;'></a>")
                            .append("<h1><strong>Grama Panchayat</strong></h1></div>")
                            .append("<nav><ul id='MenuItems'><li><button class='dynamic-button'><a href='about.html'>About</a></button></li>")
                            .append("<li><button class='dynamic-button'><a href='contact.html'>Contact</a></button></li>")
                            .append("<li><button class='dynamic-button'><a href='index.html'>Logout</a></button></li></ul></nav></div></header>")
                            .append("<div class='containers'><div class='animated-border'><div><h3 align='center'>Notice & Tenders</h3>");

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
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password.");
            }
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

    private String encrypt(String data, String password) throws Exception {
        SecretKey key = generateKey(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedData);
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
