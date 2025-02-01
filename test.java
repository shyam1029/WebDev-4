import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/test")
public class test extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Do not create a new session if one does not exist
        response.setContentType("text/html");
        response.getWriter().println("<html><body>");

        if (session != null) {
            String testValue = (String) session.getAttribute("testAttribute");
            response.getWriter().println("Session attribute 'testAttribute' has value: " + testValue + "<br>");
        } else {
            response.getWriter().println("No session found.<br>");
        }

        response.getWriter().println("<a href='setSession'>Set Session Attribute</a>");
        response.getWriter().println("</body></html>");
    }
}
