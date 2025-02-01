import java.io.IOException;
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

@WebServlet("/login")
public class adminlogin extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/project4";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String ALGORITHM = "AES";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("emailID");
        String villageID = request.getParameter("villageID");
        String password = request.getParameter("password");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Encrypt the entered details
            String encryptedEmail = encrypt(email, password);
            String encryptedVillageID = encrypt(villageID, password);
            String encryptedPassword = encrypt(password, password);

            // Prepare SQL query to check credentials
            String sql = "SELECT * FROM Admin WHERE EmailID = ? AND VillageID = ? AND EncryptedPassword = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, encryptedEmail);
            statement.setString(2, encryptedVillageID);
            statement.setString(3, encryptedPassword);
            resultSet = statement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                // Set session attributes
                HttpSession session = request.getSession();
                session.setAttribute("emailID", email);
                session.setAttribute("villageID", villageID);

                // Redirect to village details servlet
                response.sendRedirect("villageDetails");
            } else {
                // Redirect back to login page with error
                response.setContentType("text/html");
                response.getWriter().println("<script type='text/javascript'>");
                response.getWriter().println("alert('Invalid email, village ID, or password. Please try again.');");
                response.getWriter().println("window.location.href = 'index.html';");
                response.getWriter().println("</script>");
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
}
