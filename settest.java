import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/setSession")
public class settest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("testAttribute", "testValue");

        response.setContentType("text/html");
        response.getWriter().println("<html><body>");
        response.getWriter().println("Session attribute 'testAttribute' set to 'testValue'.<br>");
        response.getWriter().println("<a href='getSession'>Get Session Attribute</a>");
        response.getWriter().println("</body></html>");
    }
}
