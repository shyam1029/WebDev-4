import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

@WebServlet("/delete")
public class delete extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/project4";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String ALGORITHM = "AES";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tenderId = request.getParameter("tender_id");

        HttpSession session = request.getSession();
        String villageID = (String) session.getAttribute("villageID");

        if (villageID == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Village ID not found in session.");
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Encrypt tenderId for deletion
            String encryptedTenderId = encrypt(tenderId, villageID);

            // Prepare SQL query to delete tender
            String sql = "DELETE FROM tender_details WHERE encrypted_tender_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, encryptedTenderId);
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.sendRedirect("display");
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tender not found.");
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
