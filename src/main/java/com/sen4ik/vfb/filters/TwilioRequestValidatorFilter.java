package com.sen4ik.vfb.filters;

import com.twilio.security.RequestValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// https://www.baeldung.com/spring-boot-add-filter
@Slf4j
public class TwilioRequestValidatorFilter implements Filter {

//    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;
    private RequestValidator requestValidator;

//    @Autowired
//    Environment env;

    @Autowired
    public TwilioRequestValidatorFilter(String AUTH_TOKEN){
        this.AUTH_TOKEN = AUTH_TOKEN;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        AUTH_TOKEN = env.getRequiredProperty("twilio.auth-token");
        requestValidator = new RequestValidator(AUTH_TOKEN);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.info("====================> filtering");

        boolean isValidRequest = false;
        if (request instanceof HttpServletRequest) {
            log.info("====================> working on request");
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Concatenates the request URL with the query string
            String pathAndQueryUrl = getRequestUrlAndQueryString(httpRequest);
            pathAndQueryUrl = pathAndQueryUrl.replace("http://", "https://");

            log.info("pathAndQueryUrl: " + pathAndQueryUrl);
            // Extracts only the POST parameters and converts the parameters Map type
            Map<String, String> postParams = extractPostParams(httpRequest);
            log.info("postParams: " + postParams.toString());
            String signatureHeader = httpRequest.getHeader("X-Twilio-Signature");
            log.info("signatureHeader: " + signatureHeader);

            isValidRequest = requestValidator.validate(
                    pathAndQueryUrl,
                    postParams,
                    signatureHeader);
            log.info("isValidRequest: " + isValidRequest);
        }

        log.info("isValidRequest: " + isValidRequest);

        if(isValidRequest) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    private Map<String, String> extractPostParams(HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, String[]> requestParams = request.getParameterMap();
        List<String> queryStringKeys = getQueryStringKeys(queryString);

        return requestParams.entrySet().stream()
                .filter(e -> !queryStringKeys.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0]));
    }

    private List<String> getQueryStringKeys(String queryString) {
        if(queryString == null || queryString.length() == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(queryString.split("&"))
                    .map(pair -> pair.split("=")[0])
                    .collect(Collectors.toList());
        }
    }

    private String getRequestUrlAndQueryString(HttpServletRequest request) {

        // Scheme below is not https, isSecure is not true. This behavior can happen if you have a load-balancer
        // (or using reverse proxy) in front of the application. Even though requests are done in HTTPS the
        // load-balancer will reissue them as plain http requests which produce this behavior.
        String scheme = request.getScheme();
        log.info("scheme: " + scheme);
        boolean isSecured = request.isSecure();
        log.info("isSecured: " + isSecured);
        // Solution is to have X-Forwarded-Proto in nginx.conf
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        log.info("xForwardedProto: " + xForwardedProto);
        log.info("X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
        log.info("X-Real-IP: " + request.getHeader("X-Real-IP"));
        log.info("Host: " + request.getHeader("Host"));
        log.info("X-WhatsMyScheme: " + request.getHeader("X-WhatsMyScheme"));
        log.info(request.getHeaderNames().toString());



        String queryString = request.getQueryString();
        String requestUrl = request.getRequestURL().toString();
        if(queryString != null && queryString != "") {
            return requestUrl + "?" + queryString;
        }
        return requestUrl;
    }
}
