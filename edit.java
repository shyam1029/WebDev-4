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

@WebServlet("/edit")
public class edit extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:3306/project4";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String ALGORITHM = "AES";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tenderId = request.getParameter("tender_id");
        String description = request.getParameter("description");
        HttpSession session = request.getSession();
        String villageID = (String) session.getAttribute("villageID");

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Encrypt the entered details
            String encryptedTenderId = encrypt(tenderId, villageID);
            String encryptedVillageID = encrypt(villageID, villageID);
            String encryptedDescription = encrypt(description, villageID);

            // Prepare SQL query to update encrypted details
            String sql = "UPDATE tender_details SET encrypted_description = ? WHERE encrypted_tender_id = ? AND encrypted_village_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, encryptedDescription);
            statement.setString(2, encryptedTenderId);
            statement.setString(3, encryptedVillageID);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                 response.getWriter().println("<script type=\"text/javascript\">");
                
                response.getWriter().println("window.location.href = 'editremove.html';");
                response.getWriter().println("</script>");
            } else {
                  response.getWriter().println("<script type=\"text/javascript\">");
                response.getWriter().println("alert('update was failed.');");
                response.getWriter().println("window.location.href = 'editremove.html';");
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
