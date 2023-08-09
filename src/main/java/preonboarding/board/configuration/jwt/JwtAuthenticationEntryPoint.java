package preonboarding.board.configuration.jwt;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        TokenError tokenError = (TokenError)request.getAttribute("exception");
        response.setContentType("application/json;charset=UTF-8");
        setErrorResponse(response, tokenError.getStatus(), tokenError.getMessage());
    }


    private void setErrorResponse(
            HttpServletResponse response,
            int errorCode,
            String message
    ){
        response.setStatus(errorCode);
        try{
            JSONObject responseJson = new JSONObject();
            responseJson.put("message", message);
            responseJson.put("code", errorCode);
            response.getWriter().print(responseJson);

        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
    }
}
