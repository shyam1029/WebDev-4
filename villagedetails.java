import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.logging.Logger;

@WebServlet("/villageDetails")
public class villagedetails extends HttpServlet {
    private static final Logger logger = Logger.getLogger(villagedetails.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("emailID") == null) {
            logger.warning("User not authenticated. Redirecting to login page.");
            // Redirect to login page if not authenticated
            response.sendRedirect("index.html");
            return;
        }

        // Set content type to HTML
        response.setContentType("text/html");

        // Write HTML content
        try (var writer = response.getWriter()) {
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<meta charset=\"UTF-8\">");
            writer.println("<title>Grama Panchayat</title>");
            writer.println("<link rel=\"stylesheet\" href=\"stl.css\">");
            writer.println("<link href=\"https://fonts.googleapis.com/css2?family=poppins:wght@300;400;500;600;700&display=swap\" rel=\"stylesheet\">");
            writer.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css\">");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<header>");
            writer.println("<div class=\"navbar\">");
            writer.println("<div class=\"logo\">");
            writer.println("<a href=\"index.html\"> <img src=\"download.jpg\" alt=\"Company Logo\" style=\"max-width:80px;height:auto;border-radius:100%;\"></a>");
            writer.println("<h1><strong>Grama Panchayat</strong></h1>");
            writer.println("</div>");
            writer.println("<nav>");
            writer.println("<ul id=\"MenuItems\">");
            writer.println("<li><button class=\"dynamic-button\"><a href=\"editremove.html\">Add & Edit</a></button></li>");
            writer.println("<li><button class=\"dynamic-button\"><a href=\"register.html\">New Register</a></button></li>");
            writer.println("<li><button class=\"dynamic-button\"><a href=\"display\">View all tenders</a></button></li>");
            writer.println("<li><button class=\"dynamic-button\"><a href=\"index.html\">Logout</a></button></li>");
            writer.println("</ul>");
            writer.println("</nav>");
            writer.println("</div>");
            writer.println("</header>");
            writer.println("<div class=\"containers\">");
            writer.println("<div class=\"animated-border\">");
            writer.println("<div>");
            writer.println("<h3 align=\"center\">Village Details</h3>");
            writer.println("<table>");
            writer.println("<tr><td>Revenue Division</td><td>Bobbili</td></tr>");
            writer.println("<tr><td>Mandal</td><td>Gajapathinagaram</td></tr>");
            writer.println("<tr><td>No. of Villages</td><td>35</td></tr>");
            writer.println("<tr><td>No. of Grama Panchayats</td><td>35</td></tr>");
            writer.println("<tr><td>No. of Secretariats</td><td>21</td></tr>");
            writer.println("</table><br><br>");
            writer.println("<h3 align=\"center\">Treasury Details</h3><br>");
            writer.println("<p>The Treasuries and Accounts Department is the first department to computerize its activities up to the lowest level offices. The department has always upgraded the systems to suit the current era technologies and process re-engineering to deliver services efficiently and effectively.</p><br>");
            writer.println("<h3>Panchayat</h3>");
            writer.println("<table>");
            writer.println("<tr><td>Mandal Name</td><td>Bobbili</td></tr>");
            writer.println("<tr><td>Panchayat Name</td><td>Alajangi</td></tr>");
            writer.println("<tr><td>Secretary Name</td><td>G. Saraswathi</td></tr>");
            writer.println("<tr><td>Mobile No:</td><td>9866555978</td></tr>");
            writer.println("</table><br><br>");
            writer.println("<h3>Current Tenders & Notices</h3><br>");
            writer.println("<p>The Panchayati Secretaries play a crucial role in the administration of Gram Panchayats. Here are the names and contact details of some Panchayati Secretaries in Bobbili:</p>");
            writer.println("<p>ROUTHU VENUGOPALARAO, Mobile: 9491666137</p>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("<footer>");
            writer.println("<div class=\"footer\">");
            writer.println("<div>");
            writer.println("<div class=\"footer-col-2\">");
            writer.println("<img src=\"download.jpg\" alt=\"Company Logo\" style=\"max-width:80px;height:auto;border-radius:100%;\" id=\"first\">");
            writer.println("<h1><strong>Grama Panchayat</strong></h1>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");
            writer.println("</footer>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
