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
@WebServlet("/userloegin")
public class ulogin extends HttpServlet {
    // Existing code...

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String villageID = request.getParameter("villageID");
        String password = request.getParameter("password");

        // Check if fields are null or empty
        if (email == null || villageID == null || password == null ||
            email.isEmpty() || villageID.isEmpty() || password.isEmpty()) {
            response.sendRedirect("index1.html?error=missing_fields");
            return;
        }

        // Existing database and encryption code...

        // If login is successful
        HttpSession session = request.getSession(true); // Ensure session is created if it doesn't exist
        session.setAttribute("email", email);
        session.setAttribute("villageID", villageID);

        // Redirect to the next page
        response.sendRedirect("register.html");
    }
}
